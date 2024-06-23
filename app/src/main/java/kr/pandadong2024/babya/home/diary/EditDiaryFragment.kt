package kr.pandadong2024.babya.home.diary

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
import java.time.LocalDate
import java.util.GregorianCalendar

class EditDiaryFragment : Fragment() {
    private var _binding: FragmentEditDiaryBinding? = null
    private val binding get() = _binding!!
    private val TAG = "EditDiaryFragment"
    private lateinit var tokenDao: TokenDAO
    private var selectedImageUri : Uri? = null
    private lateinit var getImage: ActivityResultLauncher<String>
    private lateinit var diaryRequestBody : PostDiaryRequest

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

        binding.nextDaySelectButton.setOnClickListener {
            val today = GregorianCalendar()
            val year: Int = today.get(Calendar.YEAR)
            val month: Int = today.get(Calendar.MONTH)
            val date: Int = today.get(Calendar.DATE)

            val dlg = DatePickerDialog(requireContext(), object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int
                ) {
                    //month는 +1 해야 함
                    Log.d("MAIN", "${year}, ${month+1}, ${dayOfMonth}")

                    binding.nextDaySelectedText.text = "$year.${month+1}.$dayOfMonth"
                }

            }, year, month, date)
            dlg.show()
        }

        binding.diaryEditSubmitButton.setOnClickListener {
            if (
                (binding.editDiaryTitleEditText.text.isNullOrBlank()
                        && binding.editDiaryMainContentEditText.text.isNullOrBlank()
                        &&binding.weightEditText?.text.isNullOrBlank()
                        &&binding.editDiaryFetalFindingsEditText.text.isNullOrBlank()
                        &&binding.editDiaryStatusRadioGroup.checkedRadioButtonId == -1).not()
            ){
                submit()
            }
            else{
                Toast.makeText(requireContext(), "전부 입력했는지 다시 확인해 주십시오.", Toast.LENGTH_SHORT).show()
            }

        }
        return binding.root
    }

    private suspend fun uploadFile(selectedUri : List<Uri>?):List<String>{
        val imageLinkList = mutableListOf<String>()
            if (selectedUri != null) {
                for (uri in selectedUri) {
                    val test = lifecycleScope.async((Dispatchers.IO)) {
                        kotlin.runCatching {
                            RetrofitBuilder.getCommonService().fileUpload(
                                "Bearer ${tokenDao.getMembers().accessToken}",
                                file = prepareFilePart(uri)!!
                            )
                        }.onSuccess { result ->
                            Log.d(TAG, "result : ${result.data}")
                        }.onFailure { result ->
                            Log.e(TAG, "result = ${result.message}")
                            result.printStackTrace()
                        }
                    }.await()
                    Log.d(TAG, "test :  ${test.onSuccess { it.data }}")
                    test.onSuccess { result ->
                        imageLinkList.add(result.data!!)
                    }
                }
            }
        return imageLinkList
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

    private fun submit(){

        val emoji = when(binding.editDiaryStatusRadioGroup.checkedRadioButtonId){
            binding.goodRadioButton.id -> "좋음"
            binding.normalRadioButton.id->"보통"
            binding.painRadioButton.id -> "아픔"
            binding.tiredRadioButton.id->"피곤"
            binding.unrestRadioButton.id -> "불안"
            else -> "없음"
        }
        val date = binding.nextDaySelectedText.text.toString().replace('.', '-')
//            Log.i(TAG, LocalDate.now().toString())
//            Log.i(TAG, date.toString())

        lifecycleScope.launch(Dispatchers.IO) {
            launch {
                diaryRequestBody = PostDiaryRequest(
                    title = binding.editDiaryTitleEditText.text.toString(),
                    content = binding.editDiaryMainContentEditText.text.toString(),
                    pregnancyWeeks = binding.pregnancyEditText?.text.toString().toInt(),
                    weight = binding.weightEditText?.text.toString().toInt(),
                    nextAppointment = date,
                    emoji = emoji,
                    fetusComment = binding.editDiaryFetalFindingsEditText.text.toString(),
                    isPublic = !binding.SwitchCompat.isChecked,
                    url = uploadFile(listOf(selectedImageUri!!))
                )
                Log.i(TAG, diaryRequestBody.toString())
            }.join()

            launch {
                kotlin.runCatching {
                    val result = RetrofitBuilder.getDiaryService().postDiary(
                        accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                        request = diaryRequestBody
                    )
                    result
                }.onSuccess {result ->
                    Log.d(TAG, "message : ${result.message}")
                    Log.d(TAG, "status : ${result.status}")
                    if (result.status in 200 .. 299){
                        lifecycleScope.launch (Dispatchers.Main){
                            findNavController().navigate(R.id.action_editDiaryFragment_to_diaryFragment)
                        }
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
    }

}