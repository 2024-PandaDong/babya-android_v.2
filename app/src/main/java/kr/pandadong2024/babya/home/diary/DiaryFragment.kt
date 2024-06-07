package kr.pandadong2024.babya.home.diary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.databinding.FragmentDiaryBinding
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.home.main.MainBannerAdapter
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.remote.responses.diary.Diary
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryData
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryFile
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryStatus

class DiaryFragment : Fragment() {
    private var _binding:FragmentDiaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewmodel : DiaryViewModel

    private lateinit var diaryMainGridViewAdapter : DiaryMainGridViewAdapter
    private lateinit var diaryBannerAdapter: DiaryBannerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        initDiaryViewpagerView()
        binding.radioGroup.setOnCheckedChangeListener { radioGroup, checkId ->
            when(checkId){
                binding.allRadio.id ->{
                    initDiaryGridView(getAllDiaryData(1, 1))
                }
                binding.myRadio.id -> {
                    getMyDiaryData(1, 1)

                }

            }
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun initDiaryGridView(diaryDataList : DiaryStatus){
        diaryMainGridViewAdapter = DiaryMainGridViewAdapter(diaryDataList)
        binding.DiaryGridView.adapter = diaryMainGridViewAdapter
    }

    fun initDiaryViewpagerView(){
        diaryBannerAdapter = DiaryBannerAdapter(listOf(""), requireContext())
        diaryBannerAdapter.notifyItemRemoved(0)
        with(binding) {
            binding.diaryBannerViewPager.adapter = diaryBannerAdapter
            diaryBannerViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
    }


    fun getMyDiaryData(
         page : Int,
         size : Int
    ) : DiaryStatus{
        var diaryList : List<DiaryData>? = null
        var statuse : Boolean = false
        lifecycleScope.launch (Dispatchers.IO){
            kotlin.runCatching {
                diaryList =  RetrofitBuilder.getMainService().getMyDiaryData(page=page, size=size).data
            }.onSuccess {
                statuse = true
            }.onFailure {
                statuse = false
                diaryList = listOf(DiaryData(Diary(), listOf(DiaryFile())))
            }
        }

        return DiaryStatus(
            status = statuse,
            data = diaryList
        )
    }

    fun getAllDiaryData(
        page : Int,
        size : Int
    ) : DiaryStatus {
        var diaryList : List<DiaryData>? = null
        var statuse : Boolean = false
        lifecycleScope.launch (Dispatchers.IO){
            kotlin.runCatching {
                diaryList =  RetrofitBuilder.getMainService().getAllDiaryData(page=page, size=size).data
            }.onSuccess {
                statuse = true
            }.onFailure {
                statuse = false
            }
        }

        return DiaryStatus(
            status = statuse,
            data = diaryList
        )

    }


}