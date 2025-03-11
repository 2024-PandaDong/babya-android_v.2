package kr.pandadong2024.babya.home.dash_board

import android.app.WallpaperManager.getInstance
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentDetailDashBoardBinding
import kr.pandadong2024.babya.home.dash_board.adapter.DashBoardCommentsAdapter
import kr.pandadong2024.babya.home.dash_board.dash_boardViewModel.DashBoardViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.DAO.TokenDAO
import kr.pandadong2024.babya.server.remote.request.dash_board.DashBoardCommentRequest
import kr.pandadong2024.babya.server.remote.responses.SubCommentResponses
import kr.pandadong2024.babya.util.BottomControllable
import kotlin.properties.Delegates

@AndroidEntryPoint
class DetailDashBoardFragment : Fragment() {
    private var _binding : FragmentDetailDashBoardBinding? = null
    private val binding get() = _binding!!
    private val viewModel : DashBoardViewModel by viewModels()
    private lateinit var  commentsAdapter : DashBoardCommentsAdapter
    private val TAG = "DetailDashBoardFragment"

    private lateinit var token : String
    private lateinit var email : String

    private var postId by Delegates.notNull<Int>()
    private var selectedCommentId : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = viewModel.id.value!!
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        _binding = FragmentDetailDashBoardBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            token = viewModel.getToken()?.accessToken.toString()
            email = viewModel.getToken()?.email.toString()
        }

        initView()
        initCommentRecyclerView(1, 100, viewModel.id.value!!)

        binding.backButton.setOnClickListener {
//            findNavController().navigate(R.id.action_detailDashBoardFragment_to_dashBoardFragment)
        }

        // 댓글
        binding.constraintLayout.setOnClickListener {
            selectedCommentId = null
        }

        binding.sendButton.setOnClickListener {
            if (selectedCommentId == null){
                if (binding.editCommentEditText.text.toString().isNotEmpty()){
                    postComment()
                    val inputManager : InputMethodManager =
                        requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(
                        requireActivity().currentFocus?.windowToken,0
                    )
                    binding.editCommentEditText.setText("")
                }
            }
            else {
                if (binding.editCommentEditText.text.toString().isNotEmpty()){
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

    private fun postSubComment(selectedCommentId: Int) {
        lifecycleScope.launch(Dispatchers.IO){
            kotlin.runCatching {
                RetrofitBuilder.getDashBoardService().postComment(
                    accessToken = "Bearer ${token}",
                    body = DashBoardCommentRequest(
                        comment = binding.editCommentEditText.text.toString(),
                        postId = postId,
                        parentCommentId = selectedCommentId
                    )
                )
            }.onSuccess {result ->
                delay(500)
                initCommentRecyclerView(1, 100, viewModel.id.value!!)

            }.onFailure {result ->
                result.printStackTrace()
            }
        }
    }

    private fun postComment() {
        lifecycleScope.launch(Dispatchers.IO){
            kotlin.runCatching {
                RetrofitBuilder.getDashBoardService().postComment(
                    accessToken = "Bearer ${token}",
                    body = DashBoardCommentRequest(
                        comment = binding.editCommentEditText.text.toString(),
                        postId = postId,
                        parentCommentId = 0
                    )
                )
            }.onSuccess {result ->
                delay(500)
                initCommentRecyclerView(1, 100, viewModel.id.value!!)

            }.onFailure {result ->
                result.printStackTrace()
            }

        }
    }

    private fun initCommentRecyclerView(page: Int, size: Int, postId: Int) {
        lifecycleScope.launch(Dispatchers.IO){
            kotlin.runCatching {
                RetrofitBuilder.getDashBoardService().getComment(
                    accessToken = "Bearer ${token}",
                    page = page,
                    size = size,
                    postId = postId
                )
            }.onSuccess {result ->
                launch(Dispatchers.Main) {
                    commentsAdapter = result.data?.let { DashBoardCommentsAdapter(
                        commentsList = it.reversed(),
                        replayComment = { id ->
                            binding.editCommentEditText.setHint("답글쓰기")
                            selectedCommentId = id
                        },
                        getSubComment = { commentId, page, size ->
                            getSubComment(
                                commentId = commentId,
                                page = page,
                                size = size
                            )
                        }
                        ) }!!
                    commentsAdapter.notifyItemRemoved(0)
                    with(binding){
                        commentRecyclerView.adapter = commentsAdapter
                    }
                }
            }.onFailure { result ->
                result.printStackTrace()
            }
        }
    }

    private fun getSubComment(commentId: Int, page: Int, size: Int) : List<SubCommentResponses> {
        var commentResult = listOf<SubCommentResponses>()
        val subCommentList  = runBlocking(Dispatchers.IO) {
            launch {
                kotlin.runCatching {
                    RetrofitBuilder.getDiaryService().getSubComment(
                        accessToken = "Bearer ${token}",
                        parentId = commentId,
                        page = page,
                        size = size
                    ) }.onSuccess {result ->
                    commentResult = result.data!!
                }.onFailure { result ->
                    result.printStackTrace()
                }
            }
        }

        return commentResult

    }

    private fun categoryClass(category: String?) {
        when(category){
            "1" -> {
                binding.dashBoardCategory.load(R.drawable.ic_comment)
                binding.dashBoardText.setText("질문")
            }
            "2" -> {
                binding.dashBoardCategory.load(R.drawable.ic_community)
                binding.dashBoardText.setText("공유")
            }
            "3" -> {
                binding.dashBoardCategory.load(R.drawable.ic_daily)
                binding.dashBoardText.setText("일상")
            }
        }
    }

    private fun initView() {
        lifecycleScope.launch(Dispatchers.IO){
            launch(Dispatchers.IO){
                kotlin.runCatching {
                    RetrofitBuilder.getCommonService().getProfile(
                        accessToken = "Bearer ${token}",
                        email = email
                    )
                }.onSuccess { result ->
                    launch(Dispatchers.Main) {
                        binding.profileImage.load(result.data?.profileImg)
                    }
                }.onFailure {result ->
                    result.printStackTrace()
                }
                kotlin.runCatching {
                    RetrofitBuilder.getDashBoardService().getDashBoard(
                        accessToken = "Bearer ${token}",
                        id = viewModel.id.value!!
                    )
                }.onSuccess {result ->
                    val dashBoardData = result.data
                    lifecycleScope.launch(Dispatchers.Main){

                        if (dashBoardData?.files?.get(0)?.url.isNullOrBlank()){
                            binding.mainImage.visibility = View.GONE
                        }else{
                            binding.mainImage.load(dashBoardData?.files?.get(0)?.url)
                        }

                        categoryClass(dashBoardData?.category)

                        binding.mainText.text = dashBoardData?.content
                        binding.name.text = dashBoardData?.nickname
                        binding.title.text = dashBoardData?.title
                        binding.views.text = dashBoardData?.view.toString()
                        binding.comment.text = dashBoardData?.commentCnt.toString()
                        // 슬라이싱 해야함
                        binding.dateText.text = dashBoardData?.createdAt.toString().substring(5 until 10)
                        if(dashBoardData?.profileImg == null){
                            binding.dashBoardProfileImage.load(R.drawable.ic_basic_profile)
                        } else{
                            binding.dashBoardProfileImage.load(dashBoardData.profileImg)
                        }


                    }
                }.onFailure {result ->
                    result.printStackTrace()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

//    override fun onPause() {
//        super.onPause()
//        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
//    }
}