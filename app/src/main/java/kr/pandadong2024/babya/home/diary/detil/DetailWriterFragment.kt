package kr.pandadong2024.babya.home.diary.detil

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentDetailWriterBinding
import kr.pandadong2024.babya.home.diary.bottomsheet.CommentBottomSheet
import kr.pandadong2024.babya.home.diary.diaryadapters.CommentsAdapter
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.home.viewmodel.CommonViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.remote.request.SubCommentRequest
import kr.pandadong2024.babya.server.remote.responses.SubCommentResponses
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryDataResponses
import kr.pandadong2024.babya.util.BottomControllable
import retrofit2.HttpException
import kotlin.properties.Delegates

class DetailWriterFragment : Fragment() {
    private var _binding: FragmentDetailWriterBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenDao: TokenDAO
    private val viewModel by activityViewModels<DiaryViewModel>()
    private val commonViewModel by activityViewModels<CommonViewModel>()
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var diaryData: DiaryDataResponses

    private var diaryId by Delegates.notNull<Int>()
    private var selectedCommentId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        diaryId = viewModel.diaryId.value!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        _binding = FragmentDetailWriterBinding.inflate(inflater, container, false)
        tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!
        initView()
        initCommentRecyclerView(1, 100, viewModel.diaryId.value!!)
        binding.writerBackButton.setOnClickListener {
            backScreen()
        }

        binding.constraintLayout.setOnClickListener {
            selectedCommentId = null
        }

        binding.writerBloodPressureSplitText.text = "/"
        binding.writerWeightUnitText.text = "KG"

        binding.sendButton.setOnClickListener {
            if (selectedCommentId == null) {
                if (binding.editCommentEditText.text.toString().isNotBlank()) {
                    postComment()
                    val inputManager: InputMethodManager =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(
                        requireActivity().currentFocus?.windowToken,
                        0
                    )
                    binding.editCommentEditText.setText("")
                }
            } else {
                if (binding.editCommentEditText.text.toString().isNotBlank()) {
                    postSubComment(selectedCommentId!!)
                    val inputManager: InputMethodManager =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(
                        requireActivity().currentFocus?.windowToken,
                        0
                    )
                    binding.editCommentEditText.setText("")
                    selectedCommentId = null
                }
            }

        }

        binding.writerMoreButton.setOnClickListener { view ->
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.inflate(R.menu.diary_popup)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.delete -> {
                        deleteDiary(diaryId)
                    }

                    R.id.modify -> {
                        viewModel.editDiaryData.value = diaryData
                        findNavController().navigate(R.id.action_detailWriterFragment_to_editDiaryFragment)
                    }
                }
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }

        return binding.root
    }

    private fun postSubComment(parentCommentId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getDiaryService().postComment(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                    body = SubCommentRequest(
                        comment = binding.editCommentEditText.text.toString(),
                        diaryId = diaryId,
                        parentCommentId = parentCommentId
                    )
                )
            }.onSuccess { result ->
                delay(500)
                initCommentRecyclerView(1, 100, viewModel.diaryId.value!!)
            }.onFailure { result ->
                result.printStackTrace()
            }

        }
    }

    private fun postComment() {

        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getDiaryService().postComment(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                    body = SubCommentRequest(
                        comment = binding.editCommentEditText.text.toString(),
                        diaryId = diaryId,
                        parentCommentId = 0
                    )
                )
            }.onSuccess { result ->
                delay(500)
                initCommentRecyclerView(1, 100, viewModel.diaryId.value!!)
            }.onFailure { result ->
                result.printStackTrace()
            }

        }
    }

    private fun initCommentRecyclerView(page: Int, size: Int, parentId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getDiaryService().getComment(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                    page = page,
                    size = size,
                    diaryId = parentId
                )
            }.onSuccess { result ->
                launch(Dispatchers.Main) {

                    commentsAdapter = result.data?.let {
                        CommentsAdapter(
                            commentsList = it.reversed(),
                            showReplayComment = { id ->
                                selectedCommentId = id
                                val commentBottomSheet = CommentBottomSheet(id)
                                commentBottomSheet.show(
                                    requireActivity().supportFragmentManager,
                                    commentBottomSheet.tag
                                )
                            },
                        )
                    }!!
                    commentsAdapter.notifyItemRemoved(0)
                    with(binding) {
                        detailWitterRecyclerView.adapter = commentsAdapter
                    }
                }

            }.onFailure { result ->
                result.printStackTrace()

            }
        }
    }

    private fun getSubComment(commentId: Int, page: Int, size: Int): List<SubCommentResponses> {
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
                    commentResult = result.data!!
                }.onFailure { result ->
                    result.printStackTrace()
                }
            }
        }

        return commentResult
    }

    private fun initView() {
        lifecycleScope.launch(Dispatchers.IO) {
            launch(Dispatchers.IO) {
                kotlin.runCatching {
                    RetrofitBuilder.getCommonService().getProfile(
                        accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                        email = "my"
                    )
                }.onSuccess { result ->


                }.onFailure { result ->
                    result.printStackTrace()
                }
            }
            kotlin.runCatching {

                RetrofitBuilder.getDiaryService().getDiaryData(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                    id = viewModel.diaryId.value!!
                )
            }.onSuccess { result ->

                val diaryResult = result.data
                lifecycleScope.launch(Dispatchers.Main) {
                    if (diaryResult != null) {
                        diaryData = diaryResult
                    }
                    binding.writerTitleText.text = diaryResult?.title
                    if (diaryResult?.files?.get(0)?.url.isNullOrBlank()) {
                        binding.selectedImage.visibility = View.GONE
                        binding.writerDiaryAddImageCardView.visibility = View.GONE
                    } else {
                        binding.selectedImage.load(diaryResult?.files?.get(0)?.url)
                    }
                    binding.writerPregnancyText.text = diaryResult?.pregnancyWeeks.toString()
                    binding.writerWeightInputText.text = diaryResult?.weight.toString()
                    binding.writerFetalFindingsContentText.text = diaryResult?.fetusComment
                    binding.writerBloodPressureHeightInputText.text =
                        diaryResult?.systolicPressure.toString()
                    binding.writerBloodPressureLowInputText.text =
                        diaryResult?.diastolicPressure.toString()
                    binding.writerNextDaySelectedText.text =
                        (diaryResult?.nextAppointment?.substring(5))?.replace('-', '/')
                    binding.writerDateText.text = diaryResult?.writtenDt?.replace('-', '/')
                    binding.writerContentContentText.text = diaryResult?.content
                    Log.d("test", "${diaryResult?.emojiCode}")
                    binding.writerEmojiCode.text = diaryResult?.emojiCode
                    binding.writerText.text = diaryResult?.nickname

                    binding.writerEmojiImage.load(
                        when (diaryResult?.emojiCode) {
                            "좋음" -> R.drawable.img_good
                            "평범" -> R.drawable.img_normal
                            "아픔" -> R.drawable.img_pain
                            "피곤" -> R.drawable.img_tired
                            "불안" -> R.drawable.img_unrest
                            else -> R.drawable.img_normal
                        }
                    )
                }

            }.onFailure { result ->
                result.printStackTrace()
            }
        }
    }



    private fun deleteDiary(diaryId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getDiaryService().deleteDiary(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                    id = diaryId
                )
            }.onSuccess {
                withContext(Dispatchers.Main) {
                    commonViewModel.setToastMessage("성공적으로 일기가 제거되었습니다.")
                    backScreen()
                }
            }.onFailure {
                withContext(Dispatchers.Main) {
                    if (it is HttpException) {
                        if (it.code() == 500) {
                            commonViewModel.setToastMessage("서버에 문제가 발생했습니다.")
                        } else {
                            commonViewModel.setToastMessage("일기가 정상적으로 제거되지 못했습니다. CODE : ${it.code()}")
                        }
                    }

                }
            }
        }
    }

    private fun backScreen(){
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}