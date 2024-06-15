package kr.pandadong2024.babya.home.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import kr.pandadong2024.babya.databinding.FragmentMainBinding
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.remote.responses.BannerResponses
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.CompanyDataResponses
import kr.pandadong2024.babya.server.remote.responses.PageRequest
import java.time.Duration
import kotlin.math.ceil

class MainFragment : Fragment() {
    val TAG = "MainFragment"
    private lateinit var bannerList : List<BannerResponses>
    private lateinit var companyList : List<CompanyDataResponses>
    private lateinit var companyData : BaseResponse<List<CompanyDataResponses>>
    private lateinit var bannerAdapter : MainBannerAdapter
    private lateinit var rankAdapter : CompanyRankAdapter

    private lateinit var tokenDao: TokenDAO
    private lateinit var statusAdapter : StatusAdapter
    private var _binding: FragmentMainBinding? = null
    private var bannerPosition = 0
    private val binding get() = _binding!!
    private var form = 1 // 임산부인지 지역별인지



    private suspend fun getBanner() : List<BannerResponses>{
        Log.d(TAG, "mainBanner : $form")
        val db = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()
        val response = RetrofitBuilder.getHttpMainService().getBanner(
            accessToken = "Bearer ${db?.getMembers()?.accessToken}",
            lc ="my",
            type = "$form")
        Log.d(TAG, "mainBanner : ${response.data}")
        Log.d(TAG, "mainBanner : ${response.status}")
        Log.d(TAG, "mainBanner : ${response.message}")
        return response.data
    }
    lateinit var job : Job
    fun scrollJobCreate() {
        job = lifecycleScope.launchWhenResumed {
            val duration = Duration.ofMillis(2000)
            delay(duration)
            binding.bannerViewPager.setCurrentItem(++bannerPosition, true)
        }
    }


    override fun onResume() {
        super.onResume()
        scrollJobCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!

        binding.radioGroup.setOnCheckedChangeListener { radioGroup, checkId ->
            Log.d(TAG, "in setOnCheckedChangeListener")
            when (checkId) {
                binding.LocaleInfoRadioButton.id -> {
                    form = 2
                    initBannerData()
                }

                binding.maternityInfoRadioButton.id -> {
                    form = 1
                    initBannerData()
                }
            }
        }
        initStatusViewPager()
//        initCompanyList()
        initBannerData()

        binding.radioGroup.check(binding.maternityInfoRadioButton.id)
        binding.bannerViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {  //사용자가 스크롤 했을때 position 수정
                super.onPageSelected(position)
                bannerPosition = position

            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                when (state) {
                    ViewPager2.SCROLL_STATE_IDLE ->{
                        if (!job.isActive) scrollJobCreate()
                    }

                    ViewPager2.SCROLL_STATE_DRAGGING -> job.cancel()

                    ViewPager2.SCROLL_STATE_SETTLING -> {}
                }
            }
        })

        return binding.root
    }

    private fun initStatusViewPager() {
        statusAdapter = StatusAdapter()
        statusAdapter.notifyItemRemoved(0)
        with(binding) {
            statusViewPager.adapter = statusAdapter
            statusViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            ciIndicator.setViewPager(statusViewPager)
        }
    }

    private fun setBannerViewPager(){
        bannerAdapter = MainBannerAdapter(
            context = requireContext(),
            bannerList = bannerList
        )
        bannerAdapter.notifyItemRemoved(0)
        with(binding) {
            bannerViewPager.adapter = bannerAdapter
            bannerViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            bannerIndicator
        }
    }
    private fun setCompanyRecyclerView(){
        rankAdapter = CompanyRankAdapter({
            position ->
            //TODO 프레그먼트로 띄우게 하기
        })
        rankAdapter.setCompanyList(companyList)
        rankAdapter.notifyItemRemoved(0)
        with(binding){
            companyRankRecyclerView.adapter = rankAdapter
            companyRankRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            companyRankRecyclerView.addItemDecoration(CompanyOffsetItemDecoration(25))
        }

    }

    private fun initBannerData(){
        lifecycleScope.launch(Dispatchers.IO){
            kotlin.runCatching {
                Log.d(TAG, "token : ${tokenDao.getMembers().accessToken}")
                bannerList = getBanner()

            }.onSuccess {
                Log.d(TAG, "succeed")
                launch(Dispatchers.Main) {
                    bannerPosition = Int.MAX_VALUE / 2 - ceil(bannerList.size.toDouble() / 2).toInt()
                    setBannerViewPager()
                }
            }.onFailure {
                    e -> Log.e(TAG, "$e")
                e.stackTrace
                Log.e(TAG,  e.message!!)
                launch(Dispatchers.Main) {
                    bannerList = listOf(BannerResponses())
                    bannerPosition = 0
                    setBannerViewPager()
                }
            }
        }
    }

    private fun initCompanyList() {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                companyData = RetrofitBuilder.getMainService().getCompanyData(PageRequest(0, 3))
                Log.e(TAG, "initCompanyRecyclerView: ${companyData.status}")
                Log.e(TAG, "initCompanyRecyclerView: ${companyData.message}")
                companyList = companyData.data
            }.onSuccess {
                setCompanyRecyclerView()
            }.onFailure {
                it.stackTrace
                companyList = listOf()
                Log.e(TAG, "initCompanyRecyclerView: ${it.message.toString()}")
                setCompanyRecyclerView()
            }
        }
    }

}