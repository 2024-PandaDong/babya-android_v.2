package kr.pandadong2024.babya.home.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.MainActivity
import kr.pandadong2024.babya.MyApplication
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentProfileBinding
import kr.pandadong2024.babya.home.viewmodel.CommonViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.util.BottomControllable
import kr.pandadong2024.babya.util.setOnSingleClickListener

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val TAG = "ProfileFragment"
    private lateinit var token: String
    private val commonViewModel by activityViewModels<CommonViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 메인 스레드가 아닌 IO 스레드에서 데이터베이스에 접근하도록 수정
        runBlocking {
            lifecycleScope.launch(Dispatchers.IO) {
                token = BabyaDB.getInstance(requireContext())?.tokenDao()
                    ?.getMembers()?.accessToken.toString()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.isSkipQuizSwitch.isChecked = !MyApplication.prefs.skipQuiz
        binding.isSkipQuizSwitch.setOnClickListener { view ->
            commonViewModel.setToastMessage("변경된 설정은 다음날부터 적용됩니다")
            if (view is SwitchCompat) {
                MyApplication.prefs.skipQuiz = !view.isChecked
            }
        }
        binding.logoutView.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("정말로 로그아웃하시겠습니까?")
                .setNegativeButton("취소") { dialog, which ->
                    // 취소 버튼을 누르면 다이얼로그를 닫음
                    dialog.dismiss()
                }
                .setPositiveButton("로그아웃") { dialog, which ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        BabyaDB.getInstance(requireContext())?.tokenDao()?.getMembers()
                            ?.let { tokenEntity ->
                                BabyaDB.getInstance(requireContext())?.tokenDao()
                                    ?.deleteMember(tokenEntity)
                            }
                        MyApplication.prefs.remove()
                    }
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }.show()
        }

        binding.withdrawView.setOnSingleClickListener {
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
                            if (it.status == 200) {
                                BabyaDB.getInstance(requireContext())?.tokenDao()?.getMembers()
                                    ?.let { tokenEntity ->
                                        BabyaDB.getInstance(requireContext())?.tokenDao()
                                            ?.deleteMember(tokenEntity)
                                    }
                                // UI 스레드에서 프레그먼트 종료
                                withContext(Dispatchers.Main) {
                                    MyApplication.prefs.remove()
                                    parentFragmentManager.popBackStack()
                                }
                            } else {
                                commonViewModel.setToastMessage("회원탈퇴 도중 문제가 발생했습니다. CDOE : ${it.status}")
                            }
                        }.onFailure {
                            Log.d(TAG, "onViewCreated: 실패")
                            it.printStackTrace()
                            // 실패 시 UI 스레드에서 에러 메시지 표시
                            withContext(Dispatchers.Main) {
                                commonViewModel.setToastMessage("인터넷이 연결되어있는지 확인해 주십시오")
                            }
                        }
                    }
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                }
                .show()
        }
        val packageInfo = context?.packageManager?.getPackageInfo(requireContext().packageName, 0)
        val versionName = packageInfo?.versionName // 버전 이름 (예: "1.0")


        binding.appVersionText.text = "v$versionName"



        binding.profileModifyView.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileModifyFragment)
        }
        binding.profileModifyView.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileModifyFragment)
        }


//        toolbar.setOnMenuItemClickListener{item ->
//            when(item.itemId){
//                R.id.logout -> {
//                    Log.d(TAG, "test")
//                    MaterialAlertDialogBuilder(requireContext())
//                        .setMessage("정말로 로그아웃하시겠습니까?")
//                        .setNegativeButton("취소") { dialog, which ->
//                            // 취소 버튼을 누르면 다이얼로그를 닫음
//                            dialog.dismiss()
//                        }
//                        .setPositiveButton("로그아웃") { dialog, which ->
//                            lifecycleScope.launch(Dispatchers.IO) {
//                                BabyaDB.getInstance(requireContext())?.tokenDao()?.getMembers()?.let { tokenEntity ->
//                                    BabyaDB.getInstance(requireContext())?.tokenDao()?.deleteMember(tokenEntity)
//                                }
//                            }
//                            val intent = Intent(requireContext(), MainActivity::class.java)
//                            startActivity(intent)
//                            requireActivity().finish()
//                        }.show()
//
//                    true
//                }
//                R.id.delete -> {
//                    MaterialAlertDialogBuilder(requireContext())
//                        .setMessage("정말로 탈퇴하시겠습니까?")
//                        .setNegativeButton("취소") { dialog, which ->
//                            // 취소 버튼을 누르면 다이얼로그를 닫음
//                            dialog.dismiss()
//                        }
//                        .setPositiveButton("탈퇴") { dialog, which ->
//                            lifecycleScope.launch(Dispatchers.IO) {
//                                kotlin.runCatching {
//                                    // 서버에 탈퇴 요청
//                                    RetrofitBuilder.getProfileService().deleteMember(
//                                        accessToken = "Bearer $token"
//                                    )
//                                }.onSuccess {
//                                    Log.d(TAG, "onViewCreated: 성공")
//                                    BabyaDB.getInstance(requireContext())?.tokenDao()?.getMembers()?.let { tokenEntity ->
//                                        BabyaDB.getInstance(requireContext())?.tokenDao()?.deleteMember(tokenEntity)
//                                    }
//                                    // UI 스레드에서 프레그먼트 종료
//                                    withContext(Dispatchers.Main) {
//                                        parentFragmentManager.popBackStack()
//                                    }
//                                }.onFailure {
//                                    Log.d(TAG, "onViewCreated: 실패")
//                                    it.printStackTrace()
//                                    // 실패 시 UI 스레드에서 에러 메시지 표시
//                                    withContext(Dispatchers.Main) {
//                                        Toast.makeText(requireContext(), "탈퇴에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                            }
//                            val intent = Intent(requireContext(), MainActivity::class.java)
//                            startActivity(intent)
//                        }
//                        .show()
//                    true
//                }
//                else -> false
//            }
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.profileBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_mainFragment)
        }
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
                if (result.status == 200) {

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
                } else {
                    commonViewModel.setToastMessage("프로필 정보를 불러오는 도중 문제가 발생했습니다. CODE : ${result.status}")
                }
            }.onFailure { result ->
                result.printStackTrace()
                commonViewModel.setToastMessage("인터넷이 연결되어있는지 확인해 주십시오")
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}