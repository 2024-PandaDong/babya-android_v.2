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
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentMainBinding
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.remote.responses.BannerImageResponses
import kr.pandadong2024.babya.server.remote.responses.BannerResponses
import java.time.Duration
import kotlin.math.ceil

class MainFragment : Fragment() {
    val TAG = "MainFragment"
    private lateinit var bannerList : List<BannerResponses>
    private lateinit var bannerAdapter : BannerAdapter
    private lateinit var rankAdapter : CompanyRankAdapter

    private lateinit var tokenDao: TokenDAO
    private lateinit var statusAdapter : StatusAdapter
    private var _binding: FragmentMainBinding? = null
    private var bannerPosition = 0
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
        getBannerData()
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        initStatusViewPager()
        initCompanyRecyclerView()

        binding.radioGroup.check(binding.maternityInfo.id)
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
//            ciIndicator.setViewPager(bannerAdapter)
        }
    }

    private fun initBannerViewPager(){
        bannerAdapter = BannerAdapter(requireContext())
        bannerAdapter.setList(bannerList)
        bannerAdapter.notifyItemRemoved(0)
        with(binding) {
            bannerViewPager.adapter = bannerAdapter
            bannerViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
//            ciIndicator.setViewPager(bannerAdapter)
        }
    }
    private fun initCompanyRecyclerView(){
        rankAdapter = CompanyRankAdapter()
        rankAdapter.setCompanyList(mutableListOf("test1", "test2", "test3"))
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
                tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!
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
                val test = "https://dszw1qtcnsa5e.cloudfront.net/community/20240425/6ef4f9e2-505d-4c14-8a54-f72beb794512/BAGS%EC%A0%9C%ED%9C%B4%EC%BB%A4%EB%AE%A4%EB%8B%88%ED%8B%B0%EB%A9%94%EC%9D%B8MO750x522.png"
                bannerList = mutableListOf(
                    BannerResponses( "ㅁ?ㄹ",test,"서울", BannerImageResponses("point", 1, "test", 1000, "https://www.netflix.com/kr/")),
                )
                launch(Dispatchers.Main) {
                    bannerPosition = Int.MAX_VALUE / 2 - ceil(bannerList.size.toDouble() / 2).toInt()
                    initBannerViewPager()
                }
            }

        }

    }
}