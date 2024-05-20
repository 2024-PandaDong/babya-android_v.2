package kr.pandadong2024.babya.home.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import kr.pandadong2024.babya.databinding.FragmentMainBinding
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.responses.BannerResponses
import java.time.Duration
import kotlin.math.ceil

class MainFragment : Fragment() {
    val TAG = "MainFragment"
    private lateinit var bannerList : List<BannerResponses>
    private lateinit var bannerAdapter : BannerAdapter
    private var _binding: FragmentMainBinding? = null
    private var bannerPosition = 0
    private val binding get() = _binding!!
//    private var bannerList = mutableListOf<BannerResponses>()
    init {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching{
                bannerList = getBanner()
            }.onSuccess {
                Log.d(TAG, "succeed")
            }.onFailure {e ->
                Log.e(TAG,  e.message!!)
                val test = "https://dszw1qtcnsa5e.cloudfront.net/community/20240425/6ef4f9e2-505d-4c14-8a54-f72beb794512/BAGS%EC%A0%9C%ED%9C%B4%EC%BB%A4%EB%AE%A4%EB%8B%88%ED%8B%B0%EB%A9%94%EC%9D%B8MO750x522.png"
                bannerList = mutableListOf(
                    BannerResponses(test,"12/12","ㅁ?ㄹ","1","서울","https://www.netflix.com/kr/"),
                    BannerResponses(test,"12/12","ㅁ?ㄹ","2","대구","https://www.netflix.com/kr/"),
                    BannerResponses(test,"12/12","ㅁ?ㄹ","3","여가","https://www.netflix.com/kr/")
                )
                Log.e(TAG,  bannerList.toString())

            }
        }
    }

    private suspend fun getBanner() : List<BannerResponses>{
        return RetrofitBuilder.getMainService().getBanner().data
    }
    lateinit var job : Job
    fun scrollJobCreate() {
        job = lifecycleScope.launchWhenResumed {
            val duration = Duration.ofMillis(1500)
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
        bannerPosition = Int.MAX_VALUE / 2 - ceil(bannerList.size.toDouble() / 2).toInt()
        initViewPager()
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

    private fun initViewPager(){
        bannerAdapter = BannerAdapter()
        bannerAdapter.setList(bannerList)
        bannerAdapter.notifyItemRemoved(0)
        with(binding) {
            bannerViewPager.adapter = bannerAdapter
            bannerViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
//            ciIndicator.setViewPager(bannerAdapter)
        }
    }
}