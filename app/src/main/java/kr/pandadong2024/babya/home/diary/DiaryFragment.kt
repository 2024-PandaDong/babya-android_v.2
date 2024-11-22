package kr.pandadong2024.babya.home.diary

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentDiaryBinding
import kr.pandadong2024.babya.home.diary.diaryadapters.DiaryViewAdapter
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.DAO.TokenDAO
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryDataResponses
import kr.pandadong2024.babya.util.BottomControllable
import kr.pandadong2024.babya.util.setOnSingleClickListener

class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private var diaryList: List<DiaryDataResponses>? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<DiaryViewModel>()
    private lateinit var tokenDao: TokenDAO
    private var isPublic = true
    private lateinit var diaryMainGridViewAdapter: DiaryViewAdapter
    private var myEmail: String = ""
    private val TAG = "DiaryFragment"

    private var isSearchActivated = false

    private var searchKeyWord: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel.isPublic.value = true
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)


        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_diaryFragment_to_mainFragment)
        }

        binding.diaryGridView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                     if (
                        !binding.diaryGridView.canScrollVertically(1) &&
                        newState == RecyclerView.SCROLL_STATE_IDLE
                    ) {
                        viewModel.addDiaryPage()
                    }
                }
            }
        )

        viewModel.isOpenSearchView.observe(viewLifecycleOwner) {
            isSearchActivated = it
            if (it) {
                binding.searchEditText.visibility = View.VISIBLE
            } else {
                binding.searchEditText.visibility = View.GONE
            }
        }

        viewModel.diarySearchKeyWord.observe(viewLifecycleOwner) {
            if (it != "") {
                searchKeyWord = it
                val keyword = binding.searchEditText.text.toString()
                val searchList = mutableListOf<DiaryDataResponses>()
                getDiaryData(page = 1, size = 100, type = 1, keyword = it)
            }
        }

        viewModel.diaryList.observe(viewLifecycleOwner){
            diaryMainGridViewAdapter =
                DiaryViewAdapter(it) { diaryId, memberId ->
                        kotlin.runCatching {
                            viewModel.setDiaryId(diaryId)
                            if (memberId == myEmail) {
                                findNavController().navigate(R.id.action_diaryFragment_to_detailWriterFragment)
                            } else {
                                findNavController().navigate(R.id.action_diaryFragment_to_detailPublicFragment)
                            }
                        }.onFailure {
                            it.printStackTrace()
                        }
                }
            binding.diaryGridView.adapter = diaryMainGridViewAdapter
            binding.swipeRefreshLayout.isRefreshing = false
            diaryMainGridViewAdapter.updateDiaryList(it.toMutableList())
            diaryMainGridViewAdapter.notifyDataSetChanged()
            binding.diaryGridView.scrollToPosition(it.size  - viewModel.pagingSize)
        }

//        binding.searchButton.setOnClickListener {
//            if (isSearchActivated && binding.searchEditText.text.isNotBlank()) {
//                viewModel.setDiarySearchKeyWord(binding.searchEditText.text.toString())
//            } else {
//                viewModel.changeOpenSearchView()
//            }
//        }

        binding.searchButton.setOnSingleClickListener {
            if (isSearchActivated && binding.searchEditText.text.isNotBlank()) {
                viewModel.setDiarySearchKeyWord(binding.searchEditText.text.toString())
            } else {
                viewModel.changeOpenSearchView()
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener(
            SwipeRefreshLayout.OnRefreshListener {
                viewModel.initDiary()
            }
        )

        binding.diaryTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.initKeyword()
                binding.searchEditText.setText("")
                val imm: InputMethodManager =
                    requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
                when (tab?.text) {
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
        viewModel.isPublic.observe(viewLifecycleOwner) {
            Log.d("est", "saffasfasfdsafasdf1")
            if (
                viewModel.publicDiaryList.value?.isEmpty() == true
                || viewModel.privateDiaryList.value?.isEmpty() == true)
            {
                    viewModel.getDiaryData(it)
            }else{
                viewModel.changeList(it)
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
        Log.d("testlife", "onCreate")

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
        Log.d("testlife", "onDestroy")

    }

    override fun onStop() {
        super.onStop()
        Log.d("testlife", "stop")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("testlife", "onSaveInstanceState")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("test", "onDestroyView")
    }

    private fun initDiaryGridView() {
        diaryMainGridViewAdapter =
            DiaryViewAdapter(diaryList ?: listOf()) { diaryId, memberId ->
                lifecycleScope.launch(Dispatchers.Main) {
                    kotlin.runCatching {
                        viewModel.setDiaryId(diaryId)
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
        binding.diaryGridView.adapter = diaryMainGridViewAdapter
        isPublic = isPublic.not()
        diaryMainGridViewAdapter.updateDiaryList(diaryList?.toMutableList() ?: mutableListOf())
        diaryMainGridViewAdapter.notifyDataSetChanged()
    }


    // type | 1 : my | 2 : all
    private fun getDiaryData(
        page: Int,
        size: Int,
        keyword: String = "",
        type: Int
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                var diaryData: BaseResponse<List<DiaryDataResponses>>? = null
                myEmail = tokenDao.getMembers().email

                when (type) {
                    1 -> {
                        diaryData = RetrofitBuilder.getDiaryService().getMyDiaryData(
                            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                            page = page,
                            size = size,
                            keyword = keyword
                        )

                    }

                    2 -> {
                        isPublic = true
                        diaryData = RetrofitBuilder.getDiaryService().getDiaryList(
                            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                            page = page,
                            size = size,
                            keyword = keyword

                        )
                    }

                    else -> {
                        RetrofitBuilder.getDiaryService().getDiaryList(
                            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                            page = page,
                            size = size,
                            keyword = keyword
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
                    }
                }
            }
        }
    }
}