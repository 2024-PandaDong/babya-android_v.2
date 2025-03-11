package kr.pandadong2024.babya.home.diary.detil

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentDetailPublicBinding
import kr.pandadong2024.babya.home.diary.bottomsheet.CommentBottomSheet
import kr.pandadong2024.babya.home.diary.diaryadapters.CommentsAdapter
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.DAO.TokenDAO
import kr.pandadong2024.babya.server.remote.request.SubCommentRequest
import kr.pandadong2024.babya.util.BottomControllable
import kotlin.properties.Delegates

@AndroidEntryPoint
class DetailPublicFragment : Fragment() {
    private var _binding: FragmentDetailPublicBinding? = null
    private val binding get() = _binding!!

    private var selectedCommentId: Int? = null

    private val viewModel :DiaryViewModel by viewModels()
    private lateinit var commentsAdapter: CommentsAdapter
    private val TAG = "DetailPublicFragment"
    private var diaryId by Delegates.notNull<Int>()
    private lateinit var token : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        diaryId = viewModel.diaryId.value ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        _binding = FragmentDetailPublicBinding.inflate(inflater, container, false)
        if (isAdded && activity != null) {
            lifecycleScope.launch {
                token = viewModel.getToken()?.accessToken.toString()
            }
        }
        initView()
        viewModel.initPublicDiary()

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_detailPublicFragment_to_diaryFragment)
        }

        binding.constraintLayout.setOnClickListener {
            selectedCommentId = null
        }

        binding.publicMoreButton.setOnClickListener { view ->
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.inflate(R.menu.public_diary_menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.report -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage("이 산모일기를 신고하시겠습니까?")
                            .setNegativeButton("취소") { dialog, which ->
                                // 취소 버튼을 누르면 다이얼로그를 닫음
                                dialog.dismiss()
                            }
                            .setPositiveButton("신고") { dialog, which ->
                                viewModel.reportDiary(diaryId = diaryId) {
                                    findNavController().navigate(R.id.action_detailPublicFragment_to_diaryFragment)
                                }
                            }
                            .show()
                    }
                }
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }

        binding.sendButton.setOnClickListener {
            if (selectedCommentId == null) {
                if (binding.editCommentEditText.text.toString().isNotBlank()) {
                    postComment()
                    val inputManager: InputMethodManager =
                        requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(
                        requireActivity().currentFocus?.windowToken,
                        0
                    )
                    binding.editCommentEditText.setText("")
                }
            } else {
                if (binding.editCommentEditText.text.toString().isNotBlank()) {
                    postSubComment(selectedCommentId ?: -1)
                    val inputManager: InputMethodManager =
                        requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(
                        requireActivity().currentFocus?.windowToken,
                        0
                    )
                    binding.editCommentEditText.setText("")
                    selectedCommentId = null
                }
            }

        }

        viewModel.commentList.observe(viewLifecycleOwner) {

            commentsAdapter = CommentsAdapter(
                commentsList = it.reversed()
            ) { id ->
                val commentBottomSheet = CommentBottomSheet(id)
                commentBottomSheet.show(
                    requireActivity().supportFragmentManager,
                    commentBottomSheet.tag
                )

            }
            commentsAdapter.notifyItemRemoved(0)
            with(binding) {
                commentRecyclerView.adapter = commentsAdapter
            }
        }

        binding.commentRecyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!binding.commentRecyclerView.canScrollVertically(1)
                        && newState == RecyclerView.SCROLL_STATE_IDLE
                    ) {
                        viewModel.addCommentPage()
                    }
                }
            }
        )

        return binding.root
    }

    private fun postSubComment(parentCommentId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getDiaryService().postComment(
                    accessToken = "Bearer ${token}",
                    body = SubCommentRequest(
                        comment = binding.editCommentEditText.text.toString(),
                        diaryId = diaryId,
                        parentCommentId = parentCommentId
                    )
                )
            }.onSuccess { result ->
                Log.d(TAG, "message : ${result.message}")
                Log.d(TAG, "status : ${result.status}")
                Log.d(TAG, "data : ${result.data}")
                delay(500)
                viewModel.getCommentList()
            }.onFailure { result ->
                Log.e(TAG, "result : ${result.message}")
                result.printStackTrace()
            }

        }
    }

    private fun postComment() {

        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getDiaryService().postComment(
                    accessToken = "Bearer ${token}",
                    body = SubCommentRequest(
                        comment = binding.editCommentEditText.text.toString(),
                        diaryId = diaryId,
                        parentCommentId = 0
                    )
                )
            }.onSuccess { result ->
                Log.d(TAG, "message : ${result.message}")
                Log.d(TAG, "status : ${result.status}")
                Log.d(TAG, "data : ${result.data}")
                delay(500)
                viewModel.getCommentList()
            }.onFailure { result ->
                Log.e(TAG, "result : ${result.message}")
                result.printStackTrace()
            }

        }
    }

    private fun initView() {
        lifecycleScope.launch() {
//                kotlin.runCatching {
//                    RetrofitBuilder.getCommonService().getProfile(
//                        accessToken = "Bearer ${viewModel.accessToken}",
//                        email = "my"
//                    )
//                }.onSuccess { result ->
//                    Log.e(TAG, "profile result : ${result.message}")
//                    Log.e(TAG, "profile result : ${result.data}")
//                    Log.e(TAG, "profile result : ${result.status}")
//                    launch(Dispatchers.Main) {
////                        binding.profileImage.load(result.data?.profileImg)
//                    }
//
//                }.onFailure { result ->
//                    Log.e(TAG, "result : ${result.message}")
//                    result.printStackTrace()
//                }
            Log.d("token in diary", "token : ${viewModel.accessToken.value}")
            kotlin.runCatching {
                RetrofitBuilder.getDiaryService().getDiaryData(
                    accessToken = "Bearer ${viewModel.accessToken.value}",
                    id = viewModel.diaryId.value ?: -1
                )
            }.onSuccess { result ->
                Log.e(TAG, "result : ${result.message}")
                val diaryData = result.data
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.writerText.text = diaryData?.nickname
                    binding.publicTitleText.text = diaryData?.title
                    if (diaryData?.files?.get(0)?.url.isNullOrBlank()) {
                        binding.mainImage.visibility = View.GONE
                    } else {
                        binding.mainImage.load(diaryData?.files?.get(0)?.url)
                    }

                    binding.contentText.text = diaryData?.content
                    binding.dateText.text = diaryData?.writtenDt?.replace('-', '/')
                }

            }.onFailure { result ->
                Log.e(TAG, "result : ${result.message}")
                result.printStackTrace()
            }
        }
    }

//    private fun initCommentRecyclerView(page: Int, size: Int, parentId: Int) {
//        lifecycleScope.launch(Dispatchers.IO) {
//            kotlin.runCatching {
//                RetrofitBuilder.getDiaryService().getComment(
//                    accessToken = "Bearer ${viewModel.accessToken}",
//                    page = page,
//                    size = size,
//                    diaryId = parentId
//                )
//            }.onSuccess { result ->
//                Log.d(TAG, "message : ${result.message}")
//                Log.d(TAG, "status : ${result.status}")
//                Log.d(TAG, "data : ${result.data}")
//                launch(Dispatchers.Main) {
//
//                    commentsAdapter = result.data?.let {
//                        CommentsAdapter(
//                            commentsList = it.reversed(),
//                        ) { id ->
//                            val commentBottomSheet = CommentBottomSheet(id)
//                            commentBottomSheet.show(
//                                requireActivity().supportFragmentManager,
//                                commentBottomSheet.tag
//                            )
//
//                        }
//                    }!!
//                    commentsAdapter.notifyItemRemoved(0)
//                    with(binding) {
//                        commentRecyclerView.adapter = commentsAdapter
//                    }
//                }
//
//            }.onFailure { result ->
//                result.printStackTrace()
//                Log.d(TAG, "message : ${result.message}")
//
//            }
//        }
//    }


    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
    }

    override fun onStop() {
        super.onStop()
        viewModel.initPublicCommentList()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}