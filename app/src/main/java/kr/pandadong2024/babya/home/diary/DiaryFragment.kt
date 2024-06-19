package kr.pandadong2024.babya.home.diary

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentDiaryBinding
import kr.pandadong2024.babya.home.diary.diaryadapters.DiaryBannerAdapter
import kr.pandadong2024.babya.home.diary.diaryadapters.DiaryMainGridViewAdapter
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.PageRequest
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryDataResponses
import kr.pandadong2024.babya.util.BottomControllable

class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private var diaryList: List<DiaryDataResponses>? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DiaryViewModel
    private lateinit var tokenDao: TokenDAO

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

        tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!


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
                    getDiaryData(1, 10, 2)
                }

                binding.diaryMyRadio.id -> {
                    binding.diaryDisclosureButton.visibility = View.VISIBLE
                    getDiaryData(1, 10, 1)
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


    private fun initDiaryGridView() {
        diaryMainGridViewAdapter = DiaryMainGridViewAdapter(diaryList!!) { data ->
            Log.d("TAG", "in data")
            findNavController().navigate(R.id.action_diaryFragment_to_detailPublicFragment)
        }
        diaryMainGridViewAdapter.notifyDataSetChanged()

        binding.DiaryGridView.adapter = diaryMainGridViewAdapter
    }

    private fun initDiaryBannerView() {
        diaryBannerAdapter = DiaryBannerAdapter(listOf(""), requireContext())
        diaryBannerAdapter.notifyItemRemoved(0)
        with(binding) {
            binding.diaryBannerViewPager.adapter = diaryBannerAdapter
            diaryBannerViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
    }


    // type | 1 : my | 2 : all
    private fun getDiaryData(
        page: Int,
        size: Int,
        type : Int
    ) {
        Log.d(TAG, "in getMyDiaryData")
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                Log.d(TAG, "in runCahting")
                var DiaryData : BaseResponse<List<DiaryDataResponses>>? = null

                Log.d(TAG, "ㅅㄷㄴㅅ = ${RetrofitBuilder.getDiaryService().getMyDiaryData(accessToken = tokenDao.getMembers ().accessToken, pageRequest = PageRequest(page = page, size= size)).message!!}")
                when(type){
                    1 -> {
                        DiaryData = RetrofitBuilder.getDiaryService().getMyDiaryData(
                            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                            pageRequest = PageRequest(page, size)
                        )
                        Log.d(TAG, "Test1")
                        Log.e(TAG, "status : ${DiaryData.status}, message : ${DiaryData.message}")
                        Log.d(TAG, "Test2")

                    }
                    2 -> {
                        DiaryData = RetrofitBuilder.getDiaryService().getOtherDiaryList(
                            accessToken = tokenDao.getMembers().accessToken,
                            pageRequest = PageRequest(
                                page = page,
                                size= size
                            )
                        )
                        Log.d(TAG, "Test1")
                        Log.e(TAG, "status : ${DiaryData.status}, message : ${DiaryData.message}")
                        Log.d(TAG, "Test2")
                    }

                    else -> {
                        RetrofitBuilder.getDiaryService().getOtherDiaryList(
                            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                            pageRequest = PageRequest(
                                page = page,
                                size= size
                            )
                        )
                    }
                }
                Log.d(TAG, "Test12123123213")
                Log.e(TAG, "status : ${DiaryData?.status}, message : ${DiaryData?.message}")
                diaryList = DiaryData?.data
            }.onFailure {
                Log.d(TAG, "Test123")
                diaryList = listOf(
                    DiaryDataResponses()
                )
                lifecycleScope.launch(Dispatchers.Main) {
                    initDiaryGridView()
                }
            }.onSuccess {
                Log.d(TAG, "Test3")
                lifecycleScope.launch(Dispatchers.Main) {
                    initDiaryGridView()
                }
            }
        }

    }
}