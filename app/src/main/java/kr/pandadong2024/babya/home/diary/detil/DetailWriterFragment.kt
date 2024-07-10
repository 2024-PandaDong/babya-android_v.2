package kr.pandadong2024.babya.home.diary.detil

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentDetailWriterBinding
import kr.pandadong2024.babya.home.diary.diaryadapters.CommentsAdapter
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.remote.request.SubCommentRequest
import kr.pandadong2024.babya.util.BottomControllable
import kotlin.properties.Delegates

class DetailWriterFragment : Fragment() {
    private var _binding : FragmentDetailWriterBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenDao: TokenDAO
    private val viewModel by activityViewModels<DiaryViewModel>()
    private lateinit var  commentsAdapter : CommentsAdapter
    private val TAG = "DetailWriterFragment"

    private var diaryId by Delegates.notNull<Int>()
    private var selectedCommentId : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        diaryId = viewModel.id.value!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentDetailWriterBinding.inflate(inflater, container, false)
        tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        initView()
        initCommentRecyclerView(1, 100, viewModel.id.value!!)
        binding.writerBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_detailWriterFragment_to_diaryFragment)
        }

        binding.constraintLayout.setOnClickListener {
            selectedCommentId = null
        }

        binding.sendButton.setOnClickListener {
            if(selectedCommentId == null){
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
            }
            else{
                if (binding.editCommentEditText.text.toString().isNotBlank()){
                    postSubComment(selectedCommentId!!)
                    val inputManager: InputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(
                        requireActivity().currentFocus?.windowToken,
                        0
                    )
                    binding.editCommentEditText.setText("")
                    selectedCommentId = null
                }
            }

        }

        return binding.root
    }

    private fun postSubComment(parentCommentId : Int){
        lifecycleScope.launch (Dispatchers.IO){
            kotlin.runCatching {
                RetrofitBuilder.getDiaryService().postComment(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                    body = SubCommentRequest(
                        comment = binding.editCommentEditText.text.toString(),
                        diaryId = diaryId,
                        parentCommentId = parentCommentId
                    )
                )
            }.onSuccess {result ->
                Log.d(TAG, "message : ${result.message}")
                Log.d(TAG, "status : ${result.status}")
                Log.d(TAG, "data : ${result.data}")
                delay(500)
                initCommentRecyclerView(1, 100, viewModel.id.value!!)
            }.onFailure { result ->
                Log.e(TAG, "result : ${result.message}")
                result.printStackTrace()
            }

        }
    }

    private fun postComment(){

        lifecycleScope.launch (Dispatchers.IO){
            kotlin.runCatching {
                RetrofitBuilder.getDiaryService().postComment(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                    body = SubCommentRequest(
                        comment = binding.editCommentEditText.text.toString(),
                        diaryId = diaryId,
                        parentCommentId = 0
                    )
                )
            }.onSuccess {result ->
                Log.d(TAG, "message : ${result.message}")
                Log.d(TAG, "status : ${result.status}")
                Log.d(TAG, "data : ${result.data}")
                delay(500)
                initCommentRecyclerView(1, 100, viewModel.id.value!!)
            }.onFailure { result ->
                Log.e(TAG, "result : ${result.message}")
                result.printStackTrace()
            }

        }
    }

    private fun initCommentRecyclerView(page : Int, size : Int, parentId : Int){
        lifecycleScope.launch (Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getDiaryService().getComment(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                    page = page,
                    size = size,
                    diaryId = parentId
                )
            }.onSuccess { result ->
                Log.d(TAG, "message : ${result.message}")
                Log.d(TAG, "status : ${result.status}")
                Log.d(TAG, "data : ${result.data}")
                launch(Dispatchers.Main) {

                    commentsAdapter = result.data?.let { CommentsAdapter(
                        commentsList = it.reversed(),
                        replayComment = {id ->
                            binding.editCommentEditText.setHint("답글쓰기")
                            selectedCommentId = id
                            Log.d(TAG, "test in comment")
                        }) }!!
                    commentsAdapter.notifyItemRemoved(0)
                    with(binding){
                        detailWitterRecyclerView.adapter = commentsAdapter
                    }
                }

            }.onFailure { result ->
                result.printStackTrace()
                Log.d(TAG, "message : ${result.message}")

            }
        }
    }

    private fun initView(){
        lifecycleScope.launch (Dispatchers.IO){
            launch (Dispatchers.IO) {
                kotlin.runCatching {
                    RetrofitBuilder.getCommonService().getProfile(
                        accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                        email = "my"
                    )
                }.onSuccess { result ->
                    Log.e(TAG, "profile result : ${result.message}")
                    Log.e(TAG, "profile result : ${result.data}")
                    Log.e(TAG, "profile result : ${result.status}")
                    launch (Dispatchers.Main){
                        binding.profileImage.load(result.data?.profileImg)
                    }

                }.onFailure { result ->
                    Log.e(TAG, "result : ${result.message}")
                    result.printStackTrace()
                }

            }
            kotlin.runCatching {
                RetrofitBuilder.getDiaryService().getDiaryData(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                    id = viewModel.id.value!!
                )
            }.onSuccess { result ->
                Log.e(TAG, "result : ${result.message}")
                val diaryData = result.data
                lifecycleScope.launch (Dispatchers.Main) {

                    binding.writerTitleText.text = diaryData?.title
                    if (diaryData?.files?.get(0)?.url.isNullOrBlank()){
                        Log.d(TAG, "in if")
                        binding.selectedImage.visibility = View.GONE
                        binding.writerDiaryAddImageCardView.visibility = View.GONE
                    }else{
                        binding.selectedImage.load(diaryData?.files?.get(0)?.url)
                    }
                    binding.writerWeightInputText.text = diaryData?.weight.toString()
                    binding.writerFetalFindingsContentText.text = diaryData?.fetusComment
                    binding.writerBloodPressureHeightInputText.text = diaryData?.systolicPressure.toString()
                    binding.writerBloodPressureLowInputText.text = diaryData?.diastolicPressure.toString()
                    binding.writerNextDaySelectedText.text = (diaryData?.nextAppointment?.substring(5))?.replace('-', '/')
                    binding.writerDateText.text = diaryData?.writtenDt?.replace('-', '/')
                    binding.writerContentContentText.text = diaryData?.content
                    binding.writerEmojiCode.text = diaryData?.emojiCode

                    binding.writerEmojiImage.load(when(diaryData?.emojiCode){
                        "좋음"->R.drawable.img_good
                        "평범"->R.drawable.img_normal
                        "아픔"->R.drawable.img_pain
                        "피곤"->R.drawable.img_tired
                        "불안"->R.drawable.img_unrest
                        else ->R.drawable.img_normal
                    })
                }

            }.onFailure { result ->
                Log.e(TAG, "result : ${result.message}")
                result.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }
}