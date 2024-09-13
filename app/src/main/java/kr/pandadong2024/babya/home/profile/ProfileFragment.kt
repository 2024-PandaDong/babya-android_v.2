package kr.pandadong2024.babya.home.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.MainActivity
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentProfileBinding
import kr.pandadong2024.babya.home.dash_board.dash_boardViewModel.DashBoardViewModel
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.home.profile.adapter.ProfileBoardAdapter
import kr.pandadong2024.babya.home.profile.adapter.ProfileDiaryAdapter
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.profile.ProfileMyDashBoardResponses
import kr.pandadong2024.babya.server.remote.responses.profile.ProfileMyDiaryResponses
import kr.pandadong2024.babya.util.BottomControllable

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // 툴바를 초기화하고 설정
        val toolbar: androidx.appcompat.widget.Toolbar = view.findViewById(R.id.profileToolbar)
        // 툴바에 메뉴를 인플레이트
        toolbar.inflateMenu(R.menu.profile_menu)
        val packageInfo = context?.packageManager?.getPackageInfo(requireContext().packageName, 0)
        val versionName = packageInfo?.versionName // 버전 이름 (예: "1.0")

        binding.appVersionText.text = "v$versionName"



        binding.profileModifyView.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileModifyFragment)
        }


        toolbar.setOnMenuItemClickListener{item ->
            when(item.itemId){
                R.id.logout -> {
                    Log.d(TAG, "test")
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("정말로 로그아웃하시겠습니까?")
                        .setNegativeButton("취소") { dialog, which ->
                            // 취소 버튼을 누르면 다이얼로그를 닫음
                            dialog.dismiss()
                        }
                        .setPositiveButton("로그아웃") { dialog, which ->
                            lifecycleScope.launch(Dispatchers.IO) {
                                BabyaDB.getInstance(requireContext())?.tokenDao()?.getMembers()?.let { tokenEntity ->
                                    BabyaDB.getInstance(requireContext())?.tokenDao()?.deleteMember(tokenEntity)
                                }
                            }
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }.show()

                    true
                }
                R.id.delete -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("정말로 탈퇴하시겠습니까?")
                        .setNegativeButton("취소") { dialog, which ->
                            // 취소 버튼을 누르면 다이얼로그를 닫음
                            dialog.dismiss()
                        }
                        .setPositiveButton("탈퇴") { dialog, which ->
                            lifecycleScope.launch(Dispatchers.IO) {
                                kotlin.runCatching {
                                    // 서버에 탈퇴 요청
                                    RetrofitBuilder.getProfileService().deleteMember(
                                        accessToken = "Bearer $token"
                                    )
                                }.onSuccess {
                                    Log.d(TAG, "onViewCreated: 성공")
                                    BabyaDB.getInstance(requireContext())?.tokenDao()?.getMembers()?.let { tokenEntity ->
                                        BabyaDB.getInstance(requireContext())?.tokenDao()?.deleteMember(tokenEntity)
                                    }
                                    // UI 스레드에서 프레그먼트 종료
                                    withContext(Dispatchers.Main) {
                                        parentFragmentManager.popBackStack()
                                    }
                                }.onFailure {
                                    Log.d(TAG, "onViewCreated: 실패")
                                    it.printStackTrace()
                                    // 실패 시 UI 스레드에서 에러 메시지 표시
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(requireContext(), "탈퇴에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                        }
                        .show()
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)


        // 정보 받기
        lifecycleScope.launch {
            getProfileData()
        }


        return binding.root
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

                    if (result.data?.profileImg == null) {
                        binding.profileImage.load(R.drawable.ic_basic_profile)
                    } else {
                        binding.profileImage.load(result.data.profileImg)
                    }

                }
            }.onFailure { result ->
                Log.d(TAG, "onCreateView: ${result.message}")
                result.printStackTrace()
                Log.d(TAG, "onCreateView: 서버연결 실패")
            }
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}