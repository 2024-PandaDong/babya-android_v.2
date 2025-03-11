package kr.pandadong2024.babya.home.diary

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentDiaryBinding
import kr.pandadong2024.babya.home.diary.diaryadapters.DiaryViewAdapter
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.DAO.TokenDAO
import kr.pandadong2024.babya.util.BottomControllable
import kr.pandadong2024.babya.util.setOnSingleClickListener

@AndroidEntryPoint
class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!
    private val viewModel : DiaryViewModel by viewModels()
    private lateinit var diaryMainGridViewAdapter: DiaryViewAdapter
    private var myEmail: String = ""

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
//                viewModel.initKeyword()
                viewModel.initDiaryList()
                viewModel.getDiaryData(keyword = it)
            }
        }

        viewModel.diaryList.observe(viewLifecycleOwner) {
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
        }
        viewModel.isDiaryScrolled.observe(viewLifecycleOwner) {
            binding.diaryGridView.scrollToPosition(
                viewModel.pagingSize - ((viewModel.diaryList.value?.size
                    ?: 1) - viewModel.pagingSize)
            )
        }

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
            if (
                viewModel.publicDiaryList.value?.isEmpty() == true
                || viewModel.privateDiaryList.value?.isEmpty() == true
            ) {
                viewModel.getDiaryData(it)
            } else {
                viewModel.changeList(it)
            }
        }

        binding.backButton.setOnClickListener {
            viewModel.initDiaryList()
            findNavController().navigate(R.id.action_diaryFragment_to_mainFragment)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            onBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.initDiaryList()
                    if (isAdded && activity != null) {
                        requireActivity().supportFragmentManager.popBackStack()
                    }
//                    findNavController().navigate(R.id.action_diaryFragment_to_mainFragment)
                }

            }
        )

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }
}