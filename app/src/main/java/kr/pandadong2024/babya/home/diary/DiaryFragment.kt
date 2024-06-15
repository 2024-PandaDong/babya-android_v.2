package kr.pandadong2024.babya.home.diary

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentDiaryBinding
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryData
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryPostData
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryFile
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryStatus
import kr.pandadong2024.babya.util.BottomControllable

class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewmodel: DiaryViewModel

    private lateinit var diaryMainGridViewAdapter: DiaryMainGridViewAdapter
    private lateinit var diaryBannerAdapter: DiaryBannerAdapter

    private val TAG = "DiaryFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        initDiaryBannerView()

        Log.d(TAG, getAllDiaryData(1, 1).toString())
        initDiaryGridView(getAllDiaryData(1, 1))

        binding.diaryEditFloatingActionButton.setOnClickListener{
            findNavController().navigate(R.id.action_diaryFragment_to_editDiaryFragment)
        }
        binding.diaryBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_diaryFragment_to_mainFragment)
        }
        binding.radioGroup.check(R.id.diaryAllRadio)
        binding.radioGroup.setOnCheckedChangeListener { radioGroup, checkId ->
            Log.d(TAG, "in setOnCheckedChangeListener")
            when (checkId) {
                binding.diaryAllRadio.id -> {
                    binding.diaryDisclosureButton.visibility = View.GONE
                    initDiaryGridView(getAllDiaryData(1, 1))
                }

                binding.diaryMyRadio.id -> {
                    binding.diaryDisclosureButton.visibility = View.VISIBLE
                    initDiaryGridView(getAllDiaryData(1, 1))
                }
            }
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }

    fun initDiaryGridView(diaryDataList: DiaryStatus) {
        diaryMainGridViewAdapter = DiaryMainGridViewAdapter(diaryDataList) { data ->
            Log.d("TAG", "in data")
            findNavController().navigate(R.id.action_diaryFragment_to_detailPublicFragment)
        }
        diaryMainGridViewAdapter.notifyDataSetChanged()

        binding.DiaryGridView.adapter = diaryMainGridViewAdapter
    }

    fun initDiaryBannerView() {
        diaryBannerAdapter = DiaryBannerAdapter(listOf(""), requireContext())
        diaryBannerAdapter.notifyItemRemoved(0)
        with(binding) {
            binding.diaryBannerViewPager.adapter = diaryBannerAdapter
            diaryBannerViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
    }


    fun getMyDiaryData(
        page: Int,
        size: Int,
    ): DiaryStatus = runBlocking {
        var diaryList: List<DiaryPostData>? = null
        var statuse: Boolean = false
        withContext(Dispatchers.IO) {
            kotlin.runCatching {
                diaryList =
                    RetrofitBuilder.getHttpMainService().getMyDiaryData(page = page, size = size).data
            }.onSuccess {
                statuse = true
            }.onFailure {
                statuse = false
                diaryList = listOf(
                    DiaryPostData(DiaryData(), listOf(DiaryFile())),
                    DiaryPostData(DiaryData(), listOf(DiaryFile()))
                )
            }
        }

        return@runBlocking DiaryStatus(
            status = statuse,
            data = diaryList
        )
    }

    fun getAllDiaryData(
        page: Int,
        size: Int,
    ): DiaryStatus = runBlocking {
        var diaryList: List<DiaryPostData>? = null
        var status: Boolean = false

        withContext(Dispatchers.IO) {
            kotlin.runCatching {
                diaryList =
                    RetrofitBuilder.getHttpMainService().getAllDiaryData(page = page, size = size).data
            }.onSuccess {
                status = true
            }.onFailure {
                status = false
                diaryList = listOf(
                    DiaryPostData(DiaryData(title = "titledesu"), listOf(DiaryFile())),
                    DiaryPostData(DiaryData(), listOf(DiaryFile())),
                    DiaryPostData(DiaryData(), listOf(DiaryFile())),
                    DiaryPostData(DiaryData(), listOf(DiaryFile())),
                    DiaryPostData(DiaryData(), listOf(DiaryFile())),
                    DiaryPostData(DiaryData(), listOf(DiaryFile())),
                    DiaryPostData(DiaryData(), listOf(DiaryFile())),
                    DiaryPostData(DiaryData(), listOf(DiaryFile())),
                    DiaryPostData(DiaryData(), listOf(DiaryFile())),
                    DiaryPostData(DiaryData(), listOf(DiaryFile())),
                    DiaryPostData(DiaryData(), listOf(DiaryFile())),
                    DiaryPostData(DiaryData(), listOf(DiaryFile())),
                    DiaryPostData(DiaryData(), listOf(DiaryFile())),
                    DiaryPostData(DiaryData(), listOf(DiaryFile())),
                    DiaryPostData(DiaryData(), listOf(DiaryFile()))
                )
            }
        }

        return@runBlocking DiaryStatus(
            status = status,
            data = diaryList
        )
    }


}