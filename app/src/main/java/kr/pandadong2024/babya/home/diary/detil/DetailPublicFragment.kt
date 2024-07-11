package kr.pandadong2024.babya.home.diary.detil

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
import coil.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentDetailPublicBinding
import kr.pandadong2024.babya.home.diary.diaryadapters.CommentsAdapter
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.remote.request.SubCommentRequest
import kr.pandadong2024.babya.server.remote.responses.SubCommentResponses
import kr.pandadong2024.babya.util.BottomControllable
import kotlin.properties.Delegates


class DetailPublicFragment : Fragment() {
    private var _binding: FragmentDetailPublicBinding? = null
    private val binding get() = _binding!!

    private var selectedCommentId : Int? = null

    private val viewModel by activityViewModels<DiaryViewModel>()
    private lateinit var  commentsAdapter : CommentsAdapter
    private lateinit var tokenDao: TokenDAO
    private val TAG = "DetailPublicFragment"
    private var diaryId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        diaryId = viewModel.id.value!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailPublicBinding.inflate(inflater, container, false)
        tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        initView()
        initCommentRecyclerView(1, 100, viewModel.id.value!!)

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_detailPublicFragment_to_diaryFragment)
        }

        binding.constraintLayout.setOnClickListener {
            selectedCommentId = null
        }

        binding.sendButton.setOnClickListener {
            if(selectedCommentId == null){
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
            }
            else{
                if (binding.editCommentEditText.text.toString().isNotBlank()){
                    postSubComment(selectedCommentId!!)
                    val inputManager: InputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
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

                    binding.publicTitleText.text = diaryData?.title
                    if (diaryData?.files?.get(0)?.url.isNullOrBlank()){
                        binding.mainImage.visibility = View.GONE
                    }else{
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
                        }, getSubComment = { commentId, page, size ->
                            getSubComment(
                                commentId = commentId,
                                page = page,
                                size = size
                            )
                        })  }!!
                    commentsAdapter.notifyItemRemoved(0)
                    with(binding){
                        commentRecyclerView.adapter = commentsAdapter
                    }
                }

            }.onFailure { result ->
                result.printStackTrace()
                Log.d(TAG, "message : ${result.message}")

            }
        }
    }
    private fun getSubComment(commentId: Int, page: Int, size: Int) : List<SubCommentResponses> {
        var subCommentList = listOf<SubCommentResponses>()
        runBlocking {
            lifecycleScope.launch(Dispatchers.IO) {
                val subCommentResult = lifecycleScope.async(Dispatchers.IO) {
                    kotlin.runCatching {
                        RetrofitBuilder.getDiaryService().getSubComment(
                            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                            parentId = commentId,
                            page = page,
                            size = size
                        ) }.onSuccess {result ->

                    }.onFailure { result ->
                        result.printStackTrace()
                    }
                }.await().onSuccess {  subCommentList = it.data!! }.onFailure {
                    it.printStackTrace()
                }
            }
        }


        return subCommentList

    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}