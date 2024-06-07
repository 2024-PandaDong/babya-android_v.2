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
import kr.pandadong2024.babya.server.remote.responses.CompanyDataResponses
import java.time.Duration
import kotlin.math.ceil

class MainFragment : Fragment() {
    val TAG = "MainFragment"
    private lateinit var bannerList : List<BannerResponses>
    private lateinit var bannerAdapter : MainBannerAdapter
    private lateinit var rankAdapter : CompanyRankAdapter

    private lateinit var tokenDao: TokenDAO
    private lateinit var statusAdapter : StatusAdapter
    private var _binding: FragmentMainBinding? = null
    private var bannerPosition = 0
    private lateinit var companyList : List<CompanyDataResponses>
    private val binding get() = _binding!!



    private suspend fun getBanner() : List<BannerResponses>{
        Log.d(TAG, "in func")
        val db = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()
        val response = RetrofitBuilder.getMainService().getBanner("Bearer ${db?.getMembers()?.accessToken}", lc ="my", "출산 전")
        Log.d(TAG, "${response.data}")
        Log.d(TAG, response.status.toString())
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

        initStatusViewPager()
        getCompanyList()
        getBannerData()
//        initCompanyRecyclerView()

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

    private fun initBannerViewPager(){
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
    private fun initCompanyRecyclerView(){
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

    private fun getBannerData(){
        lifecycleScope.launch(Dispatchers.IO){
            kotlin.runCatching {
                Log.d(TAG, "token : ${tokenDao.getMembers().accessToken}")
                bannerList = getBanner()

            }.onSuccess {
                Log.d(TAG, "succeed")
                launch(Dispatchers.Main) {
                    bannerPosition = Int.MAX_VALUE / 2 - ceil(bannerList.size.toDouble() / 2).toInt()
                    initBannerViewPager()
                }
            }.onFailure {
                    e -> Log.e(TAG, "$e")
                e.stackTrace
                Log.e(TAG,  e.message!!)
                launch(Dispatchers.Main) {
                    bannerPosition = Int.MAX_VALUE / 2 - ceil(bannerList.size.toDouble() / 2).toInt()
                    initBannerViewPager()
                }
            }
        }
    }

    private fun getCompanyList(){
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                companyList =  RetrofitBuilder.getMainService().getCompanyData(0, 0).data
            }.onSuccess {
                initCompanyRecyclerView()
            }.onFailure {
                companyList = listOf()
                it.stackTrace
                Log.e(TAG, it.message.toString())
            }
        }
    }

}