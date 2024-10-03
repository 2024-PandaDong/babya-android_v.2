package kr.pandadong2024.babya.home.diary

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentDiaryBinding
import kr.pandadong2024.babya.home.diary.diaryadapters.DiaryMainGridViewAdapter
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryDataResponses
import kr.pandadong2024.babya.util.BottomControllable

class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private var diaryList: List<DiaryDataResponses>? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<DiaryViewModel>()
    private lateinit var tokenDao: TokenDAO
    private var isPublic = true

    private lateinit var diaryMainGridViewAdapter: DiaryMainGridViewAdapter

    private var myEmail: String = ""

    private val TAG = "DiaryFragment"

    init {
        isPublic = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_diaryFragment_to_mainFragment)
        }

        binding.swipeRefreshLayout.setOnRefreshListener (
            SwipeRefreshLayout.OnRefreshListener {
                val type = if(viewModel.isPublic.value!!){
                    2
                }
                else{
                    1
                }

                getDiaryData(
                    page = 1,
                    size=100,
                    type = type
                )
            }
        )

        binding.diaryTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.d(TAG, "tab : ${tab!!.text}")
                when (tab.text) {
                    "공개" -> {
                        viewModel.isPublic.value = true
                    }
                    "비공개" -> {
                        viewModel.isPublic.value = false
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        binding.diaryEditFloatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_diaryFragment_to_editDiaryFragment)
        }
        viewModel.isPublic.value = true
        viewModel.isPublic.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    getDiaryData(1, 100, 2)
                }

                false -> {
                    getDiaryData(1, 100, 1)
                }
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_diaryFragment_to_mainFragment)
        }


        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }

    private fun initDiaryGridView() {

        diaryMainGridViewAdapter = DiaryMainGridViewAdapter(diaryList!!) { diaryId, memberId ->
            lifecycleScope.launch(Dispatchers.Main) {
                kotlin.runCatching {
                    viewModel.diaryId.value = diaryId
                    if (memberId == myEmail) {
                        findNavController().navigate(R.id.action_diaryFragment_to_detailWriterFragment)
                    } else {
                        findNavController().navigate(R.id.action_diaryFragment_to_detailPublicFragment)
                    }
                }.onFailure {
                    it.printStackTrace()
                }
            }


        }
        diaryMainGridViewAdapter.notifyDataSetChanged()
        binding.DiaryGridView.adapter = diaryMainGridViewAdapter
    }

    private fun changeGridView() {
        val changeList = mutableListOf<DiaryDataResponses>()

        isPublic = isPublic.not()
        Log.d(TAG, "list : $changeList")
        diaryMainGridViewAdapter.setDiaryList(diaryList!!.toMutableList())
        diaryMainGridViewAdapter.notifyDataSetChanged()
    }


    // type | 1 : my | 2 : all
    private fun getDiaryData(
        page: Int,
        size: Int,
        type: Int
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                var diaryData: BaseResponse<List<DiaryDataResponses>>? = null
                myEmail = tokenDao.getMembers().email
                Log.d(TAG, "email : ${tokenDao.getMembers().email}")

                when (type) {
                    1 -> {
                        diaryData = RetrofitBuilder.getDiaryService().getMyDiaryData(
                            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                            page = page,
                            size = size
                        )

                    }

                    2 -> {
                        isPublic = true
                        diaryData = RetrofitBuilder.getDiaryService().getDiaryList(
                            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                            page = page,
                            size = size

                        )
                    }

                    else -> {
                        RetrofitBuilder.getDiaryService().getDiaryList(
                            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                            page = page,
                            size = size
                        )
                    }
                }
                Log.d(TAG, "status : ${diaryData?.status}, message : ${diaryData?.message}")
                diaryList = diaryData?.data
            }.onFailure {
                Log.d(TAG, "${it.message}")
                diaryList = listOf(
                    DiaryDataResponses()
                )
                lifecycleScope.launch(Dispatchers.Main) {
                    initDiaryGridView()
                }
            }.onSuccess {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.swipeRefreshLayout.isRefreshing = false
                    initDiaryGridView()
                    if (type == 1) {
                        delay(1)
                        changeGridView()
                    }
                }
            }
        }
    }
}