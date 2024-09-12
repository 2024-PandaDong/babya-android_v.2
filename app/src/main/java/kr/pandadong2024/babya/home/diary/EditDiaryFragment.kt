package kr.pandadong2024.babya.home.diary

import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
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
import kr.pandadong2024.babya.server.remote.request.diary.PostDiaryRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.util.GregorianCalendar

class EditDiaryFragment : Fragment() {
    private var _binding: FragmentEditDiaryBinding? = null
    private val binding get() = _binding!!
    private val TAG = "EditDiaryFragment"
    private lateinit var tokenDao: TokenDAO
    private var selectedImageUri : Uri? = null
    private lateinit var getImage: ActivityResultLauncher<String>
    private lateinit var diaryRequestBody : PostDiaryRequest
    private val gregorianCalendar = GregorianCalendar()
    private val year = gregorianCalendar.get(Calendar.YEAR)
    private val date = gregorianCalendar.get(Calendar.DATE)
    private val month = gregorianCalendar.get(Calendar.MONTH)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditDiaryBinding.inflate(inflater, container, false)
        tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!
        binding.editDateText.text = "${year}.${month+1}.${date}"
        binding.nextDaySelectedText.text = "${year}.${month+1}.${date}"



        binding.editDiaryAddImageCardView.setOnClickListener {
            getImage.launch("image/*")
        }
        binding.diaryBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_editDiaryFragment_to_diaryFragment)
        }

        binding.nextDaySelectButton.setOnClickListener {

            val dlg = DatePickerDialog(requireContext(), object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int
                ) {
                    //month는 +1 해야 함
                    Log.d("MAIN", "${year}, ${month+1}, ${dayOfMonth}")

                    binding.nextDaySelectedText.text = "$year.${month+1}.$dayOfMonth"
                    binding.nextDaySelectedText.setTextColor(Color.BLACK)
                }

            }, year, month, date)
            dlg.show()
        }

        binding.diaryEditSubmitButton.setOnClickListener {
            Log.d(TAG,  (!binding.editDiaryTitleEditText.text.isNullOrBlank()&&!binding.editDiaryMainContentEditText.text.isNullOrBlank()&&!binding.weightEditText.text.isNullOrBlank()&&!binding.editDiaryFetalFindingsEditText.text.isNullOrBlank()&&binding.editDiaryStatusRadioGroup.checkedRadioButtonId != -1).toString())
            if (
                (binding.editDiaryTitleEditText.text.isNullOrBlank()
                        &&binding.editDiaryMainContentEditText.text.isNullOrBlank()
                        &&binding.weightEditText.text.isNullOrBlank()
                        &&binding.editDiaryFetalFindingsEditText.text.isNullOrBlank()
                        &&binding.editDiaryStatusRadioGroup.checkedRadioButtonId == -1)
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
                val test = lifecycleScope.async(Dispatchers.IO) {
                    kotlin.runCatching {
                        RetrofitBuilder.getCommonService().fileUpload(
                            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
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

    private fun submit(){

        val emoji = when(binding.editDiaryStatusRadioGroup.checkedRadioButtonId){
            binding.goodRadioButton.id -> "좋음"
            binding.normalRadioButton.id->"보통"
            binding.painRadioButton.id -> "아픔"
            binding.tiredRadioButton.id->"피곤"
            binding.unrestRadioButton.id -> "불안"
            else -> "없음"
        }
        Log.d(TAG, "title = ${binding.editDiaryTitleEditText.text.toString()::class.simpleName},\n" +
                "content = ${binding.editDiaryMainContentEditText.text.toString()::class.simpleName},\n" +
                "pregnancyWeeks = ${binding.pregnancyEditText.text.toString().toInt()::class.simpleName},\n" +
                "weight = ${binding.weightEditText.text.toString().toInt()::class.simpleName},\n" +
                "nextAppointment = $date,\n" +
                "emoji = $emoji,\n" +
                "fetusComment = ${binding.editDiaryFetalFindingsEditText.text.toString()::class.simpleName},\n" +
                "isPublic = ${(!binding.SwitchCompat.isChecked)::class.simpleName},")

        lifecycleScope.launch(Dispatchers.IO) {


            launch {
                var date = binding.nextDaySelectedText.text.toString().replace('.', '-')
                val y = date.slice(0..<date.indexOf('-'))
                val m = date.slice(date.indexOf('-')+1..<date.indexOf('-', date.indexOf('-')+1))
                val d =date.slice(date.indexOf('-', date.indexOf('-')+1)+1..<date.length)
                date = String.format("%4d-%02d-%02d", y.toInt(),m.toInt(),d.toInt())
                val regex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
                Log.d(TAG, "regex test : ${regex.matches(date)}, $y, $m, $d")

                diaryRequestBody = PostDiaryRequest(
                    title = binding.editDiaryTitleEditText.text.toString(),
                    content = binding.editDiaryMainContentEditText.text.toString(),
                    pregnancyWeeks = binding.pregnancyEditText.text.toString().toInt(),
                    weight = binding.weightEditText.text.toString().toInt(),
                    nextAppointment = date, //와 이거 형식을 안 맞추면 400 뜸 그래서 고생함.
                    emoji = emoji,
                    systolicPressure = binding.bloodPressureHeightInputText.text.toString().toInt(),
                    diastolicPressure = binding.bloodPressureLowInputText.text.toString().toInt(),
                    fetusComment = binding.editDiaryFetalFindingsEditText.text.toString(),
                    isPublic = !binding.SwitchCompat.isChecked,
                    url = uploadFile(listOf(selectedImageUri!!))
                )
//                diaryRequestBody = PostDiaryRequest()
                Log.d(TAG, "title = ${diaryRequestBody.title!!::class.simpleName}, ${diaryRequestBody.title}\n" +
                        "content = ${diaryRequestBody.content!!::class.simpleName}, ${diaryRequestBody.content}\n" +
                        "pregnancyWeeks = ${diaryRequestBody.pregnancyWeeks!!.toInt()::class.simpleName}, ${diaryRequestBody.pregnancyWeeks!!.toInt()}\n" +
                        "weight = ${diaryRequestBody.weight!!::class.simpleName}, ${diaryRequestBody.weight}\n" +
                        "systolicPressure = ${diaryRequestBody.systolicPressure!!::class.simpleName}, ${diaryRequestBody.systolicPressure}\n"+
                        "diastolicPressure = ${diaryRequestBody.diastolicPressure!!::class.simpleName}, ${diaryRequestBody.diastolicPressure}\n"+
                        "nextAppointment = ${diaryRequestBody.nextAppointment!!::class.simpleName}, ${diaryRequestBody.nextAppointment}\n" +
                        "emoji = ${diaryRequestBody.emoji!!::class.simpleName}, ${diaryRequestBody.emoji}\n" +
                        "fetusComment = ${diaryRequestBody.fetusComment!!::class.simpleName}, ${diaryRequestBody.fetusComment}\n" +
                        "isPublic = ${diaryRequestBody.isPublic!!::class.simpleName}, ${diaryRequestBody.isPublic}\n"+
                        "url = ${diaryRequestBody.url!!::class.simpleName}, ${diaryRequestBody.url}")
                Log.i(TAG, diaryRequestBody.toString())
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
                    if (result is HttpException) {
                        val errorBody = result.response()?.errorBody()?.string()
                        Log.e(TAG, "Error body: $errorBody")
                    }
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