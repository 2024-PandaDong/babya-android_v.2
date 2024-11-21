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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.SubCommentBottomSheetBinding
import kr.pandadong2024.babya.home.diary.diaryadapters.SubCommentAdapter
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.DAO.TokenDAO
import kr.pandadong2024.babya.util.setOnSingleClickListener
import kotlin.properties.Delegates

class CommentBottomSheet(
    private val parentCommentId: Int,
) : BottomSheetDialogFragment() {
    private var diaryId by Delegates.notNull<Int>()
    private lateinit var tokenDao: TokenDAO
    private var _binding: SubCommentBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<DiaryViewModel>()

    override fun onStart() {
        super.onStart()

        if (dialog != null) {
            val behavior = BottomSheetBehavior.from(binding.subCommentBottomSheetChild)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
            behavior.isHideable = false
            behavior.skipCollapsed = false
            behavior.isFitToContents = false
        }
    }

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

        lifecycleScope.launch {
            viewModel.getSubComment(commentId = parentCommentId)
        }

        diaryId = viewModel.diaryId.value ?: -1

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.Transparent.toArgb()))

        binding.sendButton.setOnSingleClickListener {
            if (binding.editCommentEditText.text?.isNotEmpty() == true) {
                val comment = binding.editCommentEditText.text.toString()
                lifecycleScope.launch {
                    viewModel.postSubComment(
                        parentCommentId = parentCommentId,
                        comment = comment,
                        diaryId = diaryId
                    ) {
                        binding.editCommentEditText.setText("")
                    }
                }

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
            if (viewModel.startSubCommentPage.value != 1) {
                binding.subCommentRecyclerView.scrollToPosition(
                    it.size  - viewModel.pagingSize
                )
            }
        }
        binding.iconCloseButton.setOnClickListener {
            dismiss()
        }

        binding.subCommentRecyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!binding.subCommentRecyclerView.canScrollVertically(1)
                        && newState == RecyclerView.SCROLL_STATE_IDLE
                    ) {
                        viewModel.addSubCommentPage(commentId = parentCommentId)
                    }
                }
            }
        )
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        viewModel.initBottomSubComment()
    }
}