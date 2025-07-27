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
import kr.pandadong2024.babya.home.profile.profileviewmodle.ProfileViewModel
import kr.pandadong2024.babya.home.viewmodel.CommonViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.start.signup.Policy
import kr.pandadong2024.babya.start.signup.PolicyTextBottomSheet
import kr.pandadong2024.babya.util.BottomControllable
import kr.pandadong2024.babya.util.setOnSingleClickListener
import kr.pandadong2024.babya.util.shortToast

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val TAG = "ProfileFragment"
    private lateinit var token: String
    private val commonViewModel by activityViewModels<CommonViewModel>()
    private val userViewModel by activityViewModels<ProfileViewModel>()


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

        userViewModel.toastMessage.observe(viewLifecycleOwner){ message ->
            if (message != ""){
                requireContext().shortToast(message)
            }
        }

        binding.withdrawView.setOnSingleClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("정말로 탈퇴하시겠습니까?")
                .setNegativeButton("취소") { dialog, which ->
                    // 취소 버튼을 누르면 다이얼로그를 닫음
                    dialog.dismiss()
                }
                .setPositiveButton("탈퇴") { dialog, which ->
                    userViewModel.deleteUser {
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
                    }
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        userViewModel.getUserLocalCode()
        binding.profileBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_mainFragment)
        }



        userViewModel.userData.observe(viewLifecycleOwner){ userData ->
            if (userData.nickname.isNullOrEmpty()){
                userViewModel.getUserData()
            }
                else {
                binding.welcomeText.text = "${userData.nickname}님 반가워요!"
                if (userData.profileImg == null) {
                    binding.profileImage.load(R.drawable.ic_basic_profile)
                } else {
                    binding.profileImage.load(userData.profileImg)
                }
            }
        }


        binding.agreementBtn1.setOnClickListener {
            dateService(Policy.PRIVACY)
        }

        binding.agreementBtn2.setOnClickListener{
            dateService(Policy.SERVICE)
        }


        return binding.root
    }


    private fun dateService(privacy: Policy) {

        val bottomSheetDialog =
            PolicyTextBottomSheet(privacy)
        bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
    }


    // 프로필 정보 받기
    private fun getProfileData() {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getProfileService().getProfile(
                    accessToken = "Bearer $token",
                    email = "my"
                )
            }.onSuccess { result ->
                launch(Dispatchers.Main) {
                    if (result.status == 200) {

                        binding.welcomeText.text = "${result.data?.nickname}님 반가워요!"

                        if (result.data?.profileImg == null) {
                            binding.profileImage.load(R.drawable.ic_basic_profile)
                        } else {
                            binding.profileImage.load(result.data.profileImg)
                        }
                    } else {

                        commonViewModel.setToastMessage("프로필 정보를 불러오는 도중 문제가 발생했습니다. CODE : ${result.status}")
                    }
                }
            }.onFailure { result ->
                result.printStackTrace()
                withContext(Dispatchers.Main) {
                    commonViewModel.setToastMessage("인터넷이 연결되어있는지 확인해 주십시오")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}