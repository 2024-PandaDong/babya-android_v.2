package kr.pandadong2024.babya.home.profile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentEditProfileBinding
import kr.pandadong2024.babya.home.policy.bottom_sheet.PolicyBottomSheet
import kr.pandadong2024.babya.home.policy.encodingLocateNumber
import kr.pandadong2024.babya.home.policy.getLocalByCode
import kr.pandadong2024.babya.home.policy.viewmdole.PolicyViewModel
import kr.pandadong2024.babya.home.profile.profileviewmodle.ProfileViewModel
import kr.pandadong2024.babya.home.viewmodel.CommonViewModel
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.start.signup.SignupBottomSheet
import kr.pandadong2024.babya.util.setOnSingleClickListener
import kr.pandadong2024.babya.util.shortToast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel by activityViewModels<ProfileViewModel>()
    private val policyViewModel by activityViewModels<PolicyViewModel>()
    private val commonViewModel by activityViewModels<CommonViewModel>()
    private var accessToken: String = ""
    private var selectedImageUri: Uri? = null
    private lateinit var getImage: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 메인 스레드가 아닌 IO 스레드에서 데이터베이스에 접근하도록 수정
        runBlocking {
            lifecycleScope.launch(Dispatchers.IO) {
                accessToken = BabyaDB.getInstance(requireContext())?.tokenDao()
                    ?.getMembers()?.accessToken.toString()
            }
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
            binding.locationEditText.text = getLocalByCode(it)
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
                val now = LocalDateTime.now()
                val date = now.plusDays((userData.dDay.toLong() * -1))
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                "${date.substring(0, 4)}년 ${date.substring(4, 6)}월 ${date.substring(6, 8)}일"
            } else {
                "0000년 00월 00일"
            }
            binding.fetusDayEditText.text = fetusDt
        }

        binding.backButton.setOnClickListener {
            //dialog띄우고 이동하기!!
        }


        policyViewModel.tagsList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val location = encodingLocateNumber(it[0])
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
                PolicyBottomSheet() { _ ->

                }

            bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
        }

        commonViewModel.imageLink.observe(viewLifecycleOwner){
            userViewModel.editUser()
        }

        binding.submitButton.setOnSingleClickListener {
            val multipartData = selectedImageUri?.let { uri -> prepareFilePart(uri) }
            if (multipartData != null) {
                commonViewModel.uploadImage(multipartData)
            }else{
                userViewModel.editUser()
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
        val bottomSheetDialog =
            SignupBottomSheet() { d ->
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