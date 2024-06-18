package kr.pandadong2024.babya.home.diary

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentEditDiaryBinding
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.util.BottomControllable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
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
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        _binding = FragmentEditDiaryBinding.inflate(inflater, container, false)
        tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!

        binding.editDiaryAddImageCardView.setOnClickListener {
            getImage.launch("image/*")
        }
        binding.diaryBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_editDiaryFragment_to_diaryFragment)
        }
        binding.diaryEditSubmitButton.setOnClickListener {
//            uploadFile( partName = "image", selectedUri =  listOf(selectedImageUri!!))
            val emoji = when(binding.editDiaryStatusRadioGroup.checkedRadioButtonId){
                binding.goodRadioButton.id -> "좋음"
                binding.normalRadioButton.id->"보통"
                binding.painRadioButton.id -> "아픔"
                binding.tiredRadioButton.id->"피곤"
                binding.unrestRadioButton.id -> "불안"
                else -> "없음"
            }

//            val diaryRequest = PostDiaryRequest(
//                title = binding.editDiaryTitleEditText.text.toString(),
//                content = binding.editDiaryMainContentEditText.text.toString(),
//                pregnancyWeeks = binding.pregnancyEditText.text.toString().toInt(),
//                weight = binding.weightEditText.toString().toInt(),
//                nextAppointment = binding.nextDaySelectedText.toString(),
//                emoji = emoji,
//                fetusComment = binding.editDiaryFetalFindingsEditText.text.toString(),
//                isPublic = !binding.SwitchCompat.isChecked,
//                url = listOf<String>(uploadFile())
//                )
//            RetrofitBuilder.getDiaryService().postDiary(
//                accessToken = "",
//                postDiaryRequest = diaryRequest,
//
//            )
        }
        return binding.root
    }

//    private fun uploadFile(selectedUri : List<Uri>, partName: String ):List<String>{
//        val imageLinkList = mutableListOf<String>()
//
//        var uriFile : File
//        for (uri in selectedUri) {
//            uriFile =  getFileFromUri(uri)!!
//            val requestFile = uriFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
//            val requestBody = MultipartBody.Part.createFormData(partName, uriFile.name, requestFile)
//            lifecycleScope.launch(Dispatchers.IO) {
//                val result = async {
//                    RetrofitBuilder.getCommonService().fileUpload(
//                        "Bearer ${tokenDao.getMembers().accessToken}",
//                        file =  requestBody
//                    ).data!!
//                }
//                Log.d(TAG, result.await())
//                imageLinkList.add(
//                    result.await()
//                )
//            }
//            }
//
//        return imageLinkList.toList()
//    }

//    private fun getFileFromUri(uri: Uri): File? {
//        return try {
//            val inputStream = requireContext().contentResolver.openInputStream(uri)
//            val outputStream = FileOutputStream(file)
//            inputStream?.copyTo(outputStream)
//            inputStream?.close()
//            outputStream.close()
//            file
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }



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

    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }
}