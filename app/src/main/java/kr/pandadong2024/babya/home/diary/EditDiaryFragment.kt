package kr.pandadong2024.babya.home.diary

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentEditDiaryBinding
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.remote.request.diary.PostDiaryRequest
import kr.pandadong2024.babya.util.BottomControllable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class EditDiaryFragment : Fragment() {
    private var _binding: FragmentEditDiaryBinding? = null
    private val binding get() = _binding!!
    private val TAG = "EditDiaryFragment"
    private lateinit var tokenDao: TokenDAO
    private var selectedImageUri : Uri? = null
    private lateinit var getImage: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditDiaryBinding.inflate(inflater, container, false)
        tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)


        binding.editDiaryAddImageCardView.setOnClickListener {
            getImage.launch("image/*")
        }
        binding.diaryBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_editDiaryFragment_to_diaryFragment)
        }
        binding.diaryEditSubmitButton.setOnClickListener {

            val emoji = when(binding.editDiaryStatusRadioGroup.checkedRadioButtonId){
                binding.goodRadioButton.id -> "좋음"
                binding.normalRadioButton.id->"보통"
                binding.painRadioButton.id -> "아픔"
                binding.tiredRadioButton.id->"피곤"
                binding.unrestRadioButton.id -> "불안"
                else -> "없음"
            }

            val diaryRequestBody = PostDiaryRequest(
                title = binding.editDiaryTitleEditText.text.toString(),
                content = binding.editDiaryMainContentEditText.text.toString(),
                pregnancyWeeks = binding.pregnancyEditText.text.toString().toInt(),
                weight = binding.weightEditText.text.toString().toInt(),
                nextAppointment = binding.nextDaySelectedText.toString(),
                emoji = emoji,
                fetusComment = binding.editDiaryFetalFindingsEditText.text.toString(),
                isPublic = !binding.SwitchCompat.isChecked,
                url = uploadFile(listOf(selectedImageUri!!))
                )

            Log.i(TAG, diaryRequestBody.toString())

            lifecycleScope.launch(Dispatchers.IO) {
                RequestBody
                kotlin.runCatching {
                    RetrofitBuilder.getDiaryService().postDiary(
                        accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                        request = diaryRequestBody
                        )
                }.onSuccess {result ->
                    if (result.status == 200){
                        findNavController().navigate(R.id.action_editDiaryFragment_to_diaryFragment)
                    }else{
                        Log.i(TAG, "status : ${result.status}")
                        Log.i(TAG, "message : ${result.message}")
                    }
                }.onFailure {result ->
                    result.printStackTrace()
                    Log.e(TAG, "result = ${result.message}")
                }
            }

        }
        return binding.root
    }

    private fun uploadFile(selectedUri : List<Uri>):List<String>{
        val imageLinkList = mutableListOf<String>()
        for (uri in selectedUri) {
            lifecycleScope.launch(Dispatchers.IO) {
                 kotlin.runCatching {
                    RetrofitBuilder.getCommonService().fileUpload(
                        "Bearer ${tokenDao.getMembers().accessToken}",
                        file =  prepareFilePart(uri)!!
                    )
                }.onSuccess {
                    result ->
                     imageLinkList.add(result.data!!)
                 }.onFailure {
                     result ->
                     result.printStackTrace()
                     Log.e(TAG, "result = ${result.message}")
                 }

            }
            }

        return imageLinkList.toList()
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





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getImage = registerForActivityResult(
                ActivityResultContracts.GetContent(),
                ActivityResultCallback{
                        uri ->
                    if (uri == null){
                        binding.selectedImage.setImageURI(selectedImageUri)
                        if(selectedImageUri == null){
                            binding.iconImage.visibility = View.VISIBLE
                        }
                    }
                    else{
                        binding.iconImage.visibility = View.GONE
                        binding.selectedImage.setImageURI(uri)
                        selectedImageUri = uri
                    }

                }
            )

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)

    }

}