package kr.pandadong2024.babya.home.profile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentEditProfileBinding
import kr.pandadong2024.babya.home.policy.bottom_sheet.PolicyBottomSheet
import kr.pandadong2024.babya.home.policy.getCodeByRegion
import kr.pandadong2024.babya.home.policy.getLocalByCode
import kr.pandadong2024.babya.home.policy.viewmdole.PolicyViewModel
import kr.pandadong2024.babya.home.profile.profileviewmodle.ProfileViewModel
import kr.pandadong2024.babya.home.viewmodel.CommonViewModel
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.DatabaseModule
import kr.pandadong2024.babya.server.remote.request.UserEditRequest
import kr.pandadong2024.babya.start.signup.SignupBottomSheet
import kr.pandadong2024.babya.util.setOnSingleClickListener
import kr.pandadong2024.babya.util.shortToast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel : ProfileViewModel by viewModels()
    private val policyViewModel : PolicyViewModel by viewModels()
    private val commonViewModel : CommonViewModel by viewModels()
    private var accessToken: String = ""
    private var selectedImageUri: Uri? = null
    private val userData: UserEditRequest = UserEditRequest()
    private lateinit var getImage: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 메인 스레드가 아닌 IO 스레드에서 데이터베이스에 접근하도록 수정
        lifecycleScope.launch {
            accessToken = policyViewModel.getToken()?.accessToken.toString()
        }

        getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                if (uri == null) {
                    binding.userProfileImage.setImageURI(selectedImageUri)
                } else {
                    binding.userProfileImage.setImageURI(uri)
                    selectedImageUri = uri
                }

            }
        )
    }

    override fun onPause() {
        super.onPause()
        userViewModel.initResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        userViewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            if (message != "") {
                requireContext().shortToast(message)
                userViewModel.setToastMessage("")
            }
        }

        userViewModel.userLocalCode.observe(viewLifecycleOwner) {
            if (it.length >= 4) {
                binding.locationEditText.text = getLocalByCode(it)
                userData.lc = it
            }
        }

        binding.selectProfileImageLayout.setOnSingleClickListener {
            getImage.launch("image/*")
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
            binding.birthDayEditText.text = birthDt
            val marriedDt = "${userData.marriedYears?.substring(0, 4) ?: 0}년 ${
                userData.marriedYears?.substring(5, 7) ?: 0
            }월 ${userData.marriedYears?.substring(8, 10) ?: 0}일"
            binding.marriedDayEditText.text = marriedDt
            val fetusDt = if (userData.dDay != null) {
                val date = userData.dDay
                "${date.substring(0, 4)}년 ${date.substring(5, 7)}월 ${date.substring(8, 10)}일"
            } else {
                "0000년 00월 00일"
            }
            binding.fetusDayEditText.text = fetusDt
        }

        userViewModel.editUserResult.observe(viewLifecycleOwner) {
            if (it && (userViewModel.editUserImageResult.value == true)) {
                userViewModel.setToastMessage("성공적으로 프로필 수정이 완료되었습니다")
                userViewModel.getUserData()
                findNavController().navigate(R.id.action_editProfileFragment_to_profileModifyFragment)
            }
        }

        userViewModel.editUserImageResult.observe(viewLifecycleOwner) {
            if (it && (userViewModel.editUserResult.value == true)) {
                userViewModel.setToastMessage("성공적으로 프로필 수정이 완료되었습니다")
                userViewModel.getUserData()
                findNavController().navigate(R.id.action_editProfileFragment_to_profileModifyFragment)
            }
        }

        binding.backButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("정말로 나가시겠습니까?")
                .setMessage("진행된 수정기록은 남지 않습니다.")
                .setNegativeButton("취소") { dialog, which ->
                    // 취소 버튼을 누르면 다이얼로그를 닫음
                    dialog.dismiss()
                }
                .setPositiveButton("나가기") { dialog, which ->
                    findNavController().navigate(R.id.action_editProfileFragment_to_profileModifyFragment)
                }
                .show()
        }

        policyViewModel.tagsList.observe(viewLifecycleOwner) {
            if (it.size >= 2) {
                val location = getCodeByRegion("${it[0]}_${it[1]}")
                userData.lc = location
                Log.d("tagsList", "it : $it")
                binding.locationEditText.text = it[0]
            }
        }

        binding.selectFetusDayLayout.setOnSingleClickListener {
            dateService(binding.fetusDayEditText)
        }

        binding.selectMarriedDayLayout.setOnSingleClickListener {
            dateService(binding.marriedDayEditText)
        }

        binding.selectBirthDayLayout.setOnSingleClickListener {
            dateService(binding.birthDayEditText)
        }

        binding.selectLocationLayout.setOnSingleClickListener {
            val bottomSheetDialog =
                PolicyBottomSheet() { a ->
                }
            bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
        }

        commonViewModel.imageLink.observe(viewLifecycleOwner) {
            if (it != "") {
                userViewModel.editUserImage(it)
                commonViewModel.setImageLink()
                commonViewModel.setToastMessage("")
            }
        }

        binding.submitButton.setOnSingleClickListener {
            if (binding.fetusDayEditText.text.substring(0, 4)
                    .toInt() + binding.fetusDayEditText.text.substring(6, 8)
                    .toInt() + binding.fetusDayEditText.text.substring(10, 12).toInt() != 0
            ) {
                userData.pregnancyDt = "${
                    binding.fetusDayEditText.text.substring(
                        0,
                        4
                    )
                }-${
                    binding.fetusDayEditText.text.substring(
                        6,
                        8
                    )
                }-${binding.fetusDayEditText.text.substring(10, 12)}"
            }
            userData.birthDt = "${
                binding.birthDayEditText.text.substring(
                    0,
                    4
                )
            }-${
                binding.birthDayEditText.text.substring(
                    6,
                    8
                )
            }-${binding.birthDayEditText.text.substring(10, 12)}"
            userData.marriedDt = "${
                binding.marriedDayEditText.text.substring(
                    0,
                    4
                )
            }-${
                binding.marriedDayEditText.text.substring(
                    6,
                    8
                )
            }-${binding.marriedDayEditText.text.substring(10, 12)}"
            userData.nickName = binding.nickNameEditText.text.toString()
            userViewModel.editUser(
                userData
            )
            val multipartData = selectedImageUri?.let { uri -> prepareFilePart(uri) }
            if (multipartData != null) {
                commonViewModel.uploadImage(multipartData)
            } else {
                userViewModel.setEditUserImageResult(true)
            }
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

        if (
            nickName.isNotEmpty()
            && birthDt.isNotEmpty()
            && marriedDt.isNotEmpty()
            && locationCode.isNotEmpty()
        ) {
            binding.submitButton.isEnabled = true
        }
    }

    private fun dateService(edit: TextView) {
        val bottomSheetDialog = SignupBottomSheet { d ->
            edit.text = d
        }
        bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().cacheDir, "temp_file")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun prepareFilePart(uri: Uri): MultipartBody.Part? {
        val file = getFileFromUri(uri = uri)
        return file?.let {
            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("file", it.name, requestFile)
        }
    }
}