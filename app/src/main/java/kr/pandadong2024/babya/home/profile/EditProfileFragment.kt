package kr.pandadong2024.babya.home.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentEditProfileBinding
import kr.pandadong2024.babya.home.policy.getLocalByCode
import kr.pandadong2024.babya.home.profile.profileviewmodle.ProfileViewModel
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.util.shortToast
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel by activityViewModels<ProfileViewModel>()
    private var accessToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 메인 스레드가 아닌 IO 스레드에서 데이터베이스에 접근하도록 수정
        runBlocking {
            lifecycleScope.launch(Dispatchers.IO) {
                accessToken = BabyaDB.getInstance(requireContext())?.tokenDao()
                    ?.getMembers()?.accessToken.toString()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        userViewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            if (message != "") {
                if (message == "a") {
                    findNavController().navigate(R.id.action_editProfileFragment_to_profileModifyFragment)
                }
                requireContext().shortToast(message)
            }
        }

        userViewModel.userLocalCode.observe(viewLifecycleOwner) {
            binding.locationEditText.setText(getLocalByCode(it))
        }

        userViewModel.userData.observe(viewLifecycleOwner) { userData ->

            if (userData.profileImg == null) {
                binding.userProfileImage.load(R.drawable.ic_basic_profile)
            } else {
                binding.userProfileImage.load(userData.profileImg)
            }

            binding.nickNameEditText.setText(userData.nickname)
            val birthDt = "${userData.birthDt?.substring(0, 4) ?: 0}년 ${
                userData.birthDt?.substring(
                    5,
                    7
                ) ?: 0
            }월 ${userData.birthDt?.substring(8, 10) ?: 0}일"
            binding.birthDayEditText.setText(birthDt)
            //2007년  4월 10일
            val marriedDt = "${userData.marriedYears?.substring(0, 4) ?: 0}년 ${
                userData.marriedYears?.substring(
                    5,
                    7
                ) ?: 0
            }월 ${userData.marriedYears?.substring(8, 10) ?: 0}일"
            binding.marriedDayEditText.setText(marriedDt)
            val fetusDt = if (userData.dDay != null) {
                val now = LocalDateTime.now()
                val date = now.plusDays((userData.dDay.toLong() *-1)).format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                "${date.substring(0, 4)}년 ${date.substring(4, 6)}월 ${date.substring(6, 8)}일"
            } else {
                "0000년 00월 00일"
            }
            binding.fetusDayEditText.setText(fetusDt)
        }

        binding.backButton.setOnClickListener {
            //dialog띄우고 이동하기!!
        }

        binding.submitButton.setOnClickListener {
            userViewModel.editUser()
        }



        kotlin.run {
            binding.nickNameEditText.doAfterTextChanged {
                check()
            }
            binding.birthDayEditText.doAfterTextChanged {
                check()
            }
            binding.marriedDayEditText.doAfterTextChanged {
                check()
            }
            binding.locationEditText.doAfterTextChanged {
                check()
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun check() {
        val nickName = binding.nickNameEditText.text.toString()
        val birthDt = binding.birthDayEditText.text.toString()
        val marriedDt = binding.marriedDayEditText.text.toString()
        val locationCode = binding.locationEditText.text.toString()

        if (nickName.isNotEmpty() && birthDt.isNotEmpty() && marriedDt.isNotEmpty() && locationCode.isNotEmpty()) {
            binding.submitButton.isEnabled = true
        }
    }
}