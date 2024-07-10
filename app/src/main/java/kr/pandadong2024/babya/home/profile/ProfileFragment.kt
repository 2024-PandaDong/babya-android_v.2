package kr.pandadong2024.babya.home.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentProfileBinding
import kr.pandadong2024.babya.home.dash_board.dash_boardViewModel.DashBoardViewModel
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.home.profile.adapter.ProfileBoardAdapter
import kr.pandadong2024.babya.home.profile.adapter.ProfileDiaryAdapter
import kr.pandadong2024.babya.home.profile.profileviewmodle.ProfileViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.profile.ProfileMyDashBoardResponses
import kr.pandadong2024.babya.server.remote.responses.profile.ProfileMyDiaryResponses

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val TAG = "ProfileFragment"
    private lateinit var token: String
    private lateinit var boardAdapter: ProfileBoardAdapter
    private var boardList: List<ProfileMyDashBoardResponses>? = null
    private lateinit var diaryAdapter: ProfileDiaryAdapter
    private var diaryList: List<ProfileMyDiaryResponses>? = null

    private val dashBoardViewModel by activityViewModels<DashBoardViewModel>()
    private val diaryViewModel by activityViewModels<DiaryViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 메인 스레드가 아닌 IO 스레드에서 데이터베이스에 접근하도록 수정
        lifecycleScope.launch(Dispatchers.IO) {
            token = BabyaDB.getInstance(requireContext())?.tokenDao()?.getMembers()?.accessToken.toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // 정보 받기
        lifecycleScope.launch {
            getProfileData()
            profileDiary()
            profileBoard()
        }

        binding.boardMoveBtn.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_dashBoardFragment)
        }

        binding.diaryMoveBtn.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_diaryFragment)
        }

//        // 즐겨찾기 화면으로 이동
//        binding.bookmarkBtn.setOnClickListener {
//            goBookmark()
//        }

        return binding.root
    }

    private fun dashBoardRecyclerView() {
        Log.d(TAG, "dashBoardRecyclerView: $boardList")
        boardAdapter = ProfileBoardAdapter(boardList!!) { id ->
            lifecycleScope.launch(Dispatchers.Main) {
                Log.d(TAG, "dashBoardRecyclerView: $id")
                kotlin.runCatching {
                    dashBoardViewModel.id.value = id
                    findNavController().navigate(R.id.action_profileFragment_to_detailDashBoardFragment)
                }
            }
        }
        boardAdapter.notifyDataSetChanged()
        binding.boardRv.adapter = boardAdapter
    }

    private fun diaryRecyclerView() {
        Log.d(TAG, "diaryRecyclerView: $diaryList")
        diaryAdapter = ProfileDiaryAdapter(diaryList!!) { id ->
            lifecycleScope.launch(Dispatchers.Main) {
                Log.d(TAG, "diaryRecyclerView: ${id}")
                kotlin.runCatching {
                    diaryViewModel.id.value = id
                    findNavController().navigate(R.id.action_profileFragment_to_detailWriterFragment)
                }
            }
        }
        diaryAdapter.notifyDataSetChanged()
        binding.diaryRv.adapter = diaryAdapter
    }

    // 게시판 정보 받기
    private fun profileBoard() {
        Log.d(TAG, "profileBoard: 들어옴")
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                val dashBoardData: BaseResponse<List<ProfileMyDashBoardResponses>> =
                    RetrofitBuilder.getProfileService().getMyDashBoard(
                        accessToken = "Bearer $token",
                        page = 1,
                        size = 100
                    )
                boardList = dashBoardData.data
            }.onSuccess {
                Log.d(TAG, "boardList: $boardList")
                launch(Dispatchers.Main) {
                    dashBoardRecyclerView()
                }
            }.onFailure { result ->
                result.printStackTrace()
            }
        }
    }

    // 산모일기 정보 받기
    private fun profileDiary() {
        lifecycleScope.launch(Dispatchers.IO){
            kotlin.runCatching {
                val diaryData: BaseResponse<List<ProfileMyDiaryResponses>> =
                    RetrofitBuilder.getProfileService().getMyDiary(
                        accessToken = "Bearer $token",
                        page = 1,
                        size = 100
                    )
                diaryList = diaryData.data
            }.onSuccess {
                Log.d(TAG, "diaryList: ${diaryList}")
                launch(Dispatchers.Main){
                    diaryRecyclerView()
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    // 프로필 정보 받기
    private fun getProfileData() {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                Log.d(TAG, "token : $token")
                RetrofitBuilder.getProfileService().getProfile(
                    accessToken = "Bearer $token",
                    email = "my"
                )
            }.onSuccess { result ->
                Log.d(TAG, "status : ${result.status}")
                Log.d(TAG, "message : ${result.message}")
                Log.d(TAG, "getProfileData: ${result.data}")

                launch(Dispatchers.Main) {
                    binding.welcomeText.text = "${result.data?.nickname}님 반가워요!"
                    binding.argText.text = "나이: ${result.data?.age}살"
                    binding.dayText.text = "D-Day: ${result.data?.dDay}일"

                    if (result.data?.profileImg == null) {
                        binding.profileImage.load(R.drawable.ic_basic_profile)
                    } else {
                        binding.profileImage.load(result.data.profileImg)
                    }

                    binding.weddingYearText.text = if (result.data?.marriedYears == 0) {
                        "결혼: 미혼"
                    } else {
                        "결혼: ${result.data?.marriedYears}년차"
                    }
                }
            }.onFailure { result ->
                Log.d(TAG, "onCreateView: ${result.message}")
                result.printStackTrace()
                Log.d(TAG, "onCreateView: 서버연결 실패")
            }
        }
    }

    // 즐겨찾기 화면으로 이동
//    private fun goBookmark() {
//        findNavController().navigate(R.id.action_profileFragment_to_bookmarkFragment)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}