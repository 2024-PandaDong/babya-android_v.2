package kr.pandadong2024.babya.home.diary.bottomsheet

import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.SubCommentBottomSheetBinding
import kr.pandadong2024.babya.home.diary.diaryadapters.SubCommentAdapter
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.remote.request.SubCommentRequest
import kr.pandadong2024.babya.server.remote.responses.SubCommentResponses
import kr.pandadong2024.babya.util.setOnSingleClickListener
import kotlin.properties.Delegates

// TODO  : 서브 코멘트 보여주기
class CommentBottomSheet(
    private val parentCommentId: Int,
) : BottomSheetDialogFragment() {
    private var diaryId by Delegates.notNull<Int>()
    private lateinit var tokenDao: TokenDAO
    private var _binding: SubCommentBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<DiaryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.editTextDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SubCommentBottomSheetBinding.inflate(inflater, container, false)
        tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!
        viewModel.subCommentList.value =
            setSubComment(size = 100, page = 1, commentId = parentCommentId)
        diaryId = viewModel.diaryId.value ?: -1

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.Transparent.toArgb()))

        binding.sendButton.setOnSingleClickListener {
            if (binding.editCommentEditText.text?.isNotEmpty() == true) {
                val comment = binding.editCommentEditText.text.toString()
                postSubComment(parentCommentId = parentCommentId, comment = comment)
                val inputManager: InputMethodManager =
                    requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(
                    requireActivity().currentFocus?.windowToken,
                    0
                )
            }
        }
        viewModel.subCommentList.observe(viewLifecycleOwner) {
            val subCommentAdapter = SubCommentAdapter(it, requireContext())
            subCommentAdapter.notifyItemRemoved(0)
            binding.subCommentRecyclerView.adapter = subCommentAdapter
        }
        binding.iconCloseButton.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    private fun postSubComment(parentCommentId: Int, comment: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getDiaryService().postComment(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                    body = SubCommentRequest(
                        comment = comment,
                        diaryId = diaryId,
                        parentCommentId = parentCommentId
                    )
                )
            }.onSuccess { _ ->
                withContext(Dispatchers.Main) {
                    binding.editCommentEditText.setText("")
                    delay(500)
                    viewModel.subCommentList.value =
                        setSubComment(page = 1, size = 100, commentId = parentCommentId)
                }
            }.onFailure { result ->
                result.printStackTrace()
            }
        }
    }


    private fun setSubComment(commentId: Int, page: Int, size: Int): List<SubCommentResponses> {
        var commentResult = listOf<SubCommentResponses>()

        val subCommentList = runBlocking(Dispatchers.IO) {
            launch {
                kotlin.runCatching {
                    RetrofitBuilder.getDiaryService().getSubComment(
                        accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                        parentId = commentId,
                        page = page,
                        size = size
                    )
                }.onSuccess { result ->
                    commentResult = result.data ?: listOf()
                }.onFailure { result ->
                    result.printStackTrace()
                }
            }
        }
        return commentResult
    }
}