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
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentEditDiaryBinding
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.home.viewmodel.CommonViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.DAO.TokenDAO
import kr.pandadong2024.babya.server.remote.request.diary.EditDiaryRequest
import kr.pandadong2024.babya.server.remote.request.diary.PostDiaryRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.util.GregorianCalendar
import java.util.Locale

class EditDiaryFragment : Fragment() {
    private var _binding: FragmentEditDiaryBinding? = null
    private val binding get() = _binding!!
    private val TAG = "EditDiaryFragment"
    private lateinit var tokenDao: TokenDAO
    private var selectedImageUri: Uri? = null
    private lateinit var getImage: ActivityResultLauncher<String>
    private lateinit var diaryRequestBody: PostDiaryRequest
    private val gregorianCalendar = GregorianCalendar()
    private var year = gregorianCalendar.get(Calendar.YEAR)
    private var date = gregorianCalendar.get(Calendar.DATE)
    private var month = gregorianCalendar.get(Calendar.MONTH)
    private val commonViewModel by activityViewModels<CommonViewModel>()
    private val diaryViewModel by activityViewModels<DiaryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditDiaryBinding.inflate(inflater, container, false)
        tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!
        if (diaryViewModel.editDiaryData.value == null) {
            binding.editDateText.text = "${year}.${month + 1}.${date}"
            binding.nextDaySelectedText.text = "${year}.${month + 1}.${date}"
            binding.diaryEditSubmitButton.text = "등록"
        } else {
            //2024-07-14
            binding.diaryEditSubmitButton.text = "수정"
            val diaryDaya = diaryViewModel.editDiaryData.value
            if (diaryDaya != null) {
                year = diaryDaya.writtenDt?.substring(0, 4)?.toInt() ?: gregorianCalendar.get(
                    Calendar.YEAR
                )
                month = diaryDaya.writtenDt?.substring(5, 7)?.toInt() ?: gregorianCalendar.get(
                    Calendar.MONTH
                )
                date = diaryDaya.writtenDt?.substring(8, 9)?.toInt() ?: gregorianCalendar.get(
                    Calendar.DATE
                )
                if (!diaryDaya.files?.get(0)?.url.isNullOrBlank()) {
                    binding.selectedImage.load(diaryDaya.files?.get(0)?.url)
                }
                binding.selectedImage
                binding.SwitchCompat.isChecked = diaryDaya.isPublic.toBoolean()
                binding.editDiaryTitleEditText.setText(diaryDaya.title)
                binding.weightEditText.setText(diaryDaya.weight.toString())
                binding.pregnancyEditText.setText(diaryDaya.pregnancyWeeks.toString())
                binding.bloodPressureHeightInputText.setText(diaryDaya.systolicPressure.toString())
                binding.bloodPressureLowInputText.setText(diaryDaya.diastolicPressure.toString())
//                binding.editDiaryFetalFindingsEditText.setText(diaryDaya.fetusComment)
                binding.editDiaryMainContentEditText.setText(diaryDaya.content)
                binding.nextDaySelectedText.text =
                    diaryDaya.nextAppointment?.replace('-', '.')
                binding.editDiaryStatusRadioGroup.check(
                    when (diaryDaya.emojiCode) {
                        "좋음" -> binding.goodRadioButton.id
                        "보통" -> binding.normalRadioButton.id
                        "아픔" -> binding.painRadioButton.id
                        "피곤" -> binding.tiredRadioButton.id
                        "불안" -> binding.unrestRadioButton.id
                        else -> binding.goodRadioButton.id
                    }
                )
            }
        }


        binding.editDiaryAddImageCardView.setOnClickListener {
            getImage.launch("image/*")
        }
        binding.diaryBackButton.setOnClickListener {
            diaryViewModel.initEditDate()
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("정말로 나가시겠습니까?")
                .setMessage("진행된 수정기록은 남지 않습니다.")
                .setNegativeButton("취소") { dialog, which ->
                    // 취소 버튼을 누르면 다이얼로그를 닫음
                    dialog.dismiss()
                }
                .setPositiveButton("나가기") { dialog, which ->
                    findNavController().navigate(R.id.action_editDiaryFragment_to_diaryFragment)
                }
            dialog.show()
        }

        binding.nextDaySelectButton.setOnClickListener {
            val dlg =
                DatePickerDialog(
                    requireContext(),
                    { view, year, month, dayOfMonth -> //month는 +1 해야 함
                        binding.nextDaySelectedText.text = "$year.${month + 1}.$dayOfMonth"
                        binding.nextDaySelectedText.setTextColor(Color.BLACK)
                    }, year, month, date
                )
            dlg.show()
        }

        binding.diaryEditSubmitButton.setOnClickListener {
            // 조건을 반대로 수정: 모든 필드가 채워졌을 때만 submit() 호출
            if (
                (!binding.editDiaryTitleEditText.text.isNullOrBlank()
                        && !binding.editDiaryMainContentEditText.text.isNullOrBlank()
                        && !binding.pregnancyEditText.text.isNullOrBlank()
                        && !binding.bloodPressureHeightInputText.text.isNullOrBlank()
                        && !binding.bloodPressureLowInputText.text.isNullOrBlank()
                        && !binding.weightEditText.text.isNullOrBlank()
                        && binding.editDiaryStatusRadioGroup.checkedRadioButtonId != -1)
            ) {
                submit()
            } else {
                Toast.makeText(requireContext(), "전부 입력했는지 다시 확인해 주십시오.", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private suspend fun uploadFile(selectedUri: List<Uri?>): List<String> {
        val imageLinkList = mutableListOf<String>()
        for (uri in selectedUri) {
            val test = lifecycleScope.async(Dispatchers.IO) {
                kotlin.runCatching {
                    prepareFilePart(uri!!)?.let {
                        RetrofitBuilder.getCommonService().fileUpload(
                            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                            file = it
                        )
                    }
                }.onSuccess { result ->
                }.onFailure { result ->
                    result.printStackTrace()
                }
            }.await()
            test.onSuccess { result ->
                imageLinkList.add(result?.data ?: "")
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

    private fun submit() {
        val emoji = when (binding.editDiaryStatusRadioGroup.checkedRadioButtonId) {
            binding.goodRadioButton.id -> "좋음"
            binding.normalRadioButton.id -> "보통"
            binding.painRadioButton.id -> "아픔"
            binding.tiredRadioButton.id -> "피곤"
            binding.unrestRadioButton.id -> "불안"
            else -> "없음"
        }
        var date = binding.nextDaySelectedText.text.toString().replace('.', '-')
        val y = date.slice(0..<date.indexOf('-'))
        val m = date.slice(date.indexOf('-') + 1..<date.indexOf('-', date.indexOf('-') + 1))
        val d = date.slice(date.indexOf('-', date.indexOf('-') + 1) + 1..<date.length)
        date = String.format(Locale.KOREA, "%4d-%02d-%02d", y.toInt(), m.toInt(), d.toInt())
        if (diaryViewModel.editDiaryData.value != null) {
            modifyDiary(
                diaryId = diaryViewModel.editDiaryData.value?.diaryId ?: -1,
                emoji = emoji,
                date = date
            )
        } else if (diaryViewModel.editDiaryData.value == null) {
            postDiary(emoji, date)
        }
    }

    private fun postDiary(emoji: String, date: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            launch {
                diaryRequestBody = PostDiaryRequest(
                    title = binding.editDiaryTitleEditText.text.toString(),
                    content = binding.editDiaryMainContentEditText.text.toString(),
                    pregnancyWeeks = binding.pregnancyEditText.text.toString().toInt(),
                    weight = binding.weightEditText.text.toString().toInt(),
                    nextAppointment = date, //와 이거 형식을 안 맞추면 400 뜸 그래서 고생함.
                    emoji = emoji,
                    systolicPressure = binding.bloodPressureHeightInputText.text.toString().toInt(),
                    diastolicPressure = binding.bloodPressureLowInputText.text.toString().toInt(),
//                    fetusComment = binding.editDiaryFetalFindingsEditText.text.toString(),
                    isPublic = !binding.SwitchCompat.isChecked,
                    url = uploadFile(
                        if (selectedImageUri == null) {
                            listOf<Uri>()
                        } else {
                            listOf(selectedImageUri ?: "".toUri())
                        }
                    )
                )
                Log.i(TAG, diaryRequestBody.toString())
                kotlin.runCatching {

                    val result = RetrofitBuilder.getDiaryService().postDiary(
                        accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                        request = diaryRequestBody
                    )
                    result
                }.onSuccess { result ->
                    if (result.status in 200..299) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    } else {
                        Log.i(TAG, "status : ${result.status}")
                        Log.i(TAG, "message : ${result.message}")
                        commonViewModel.setToastMessage("데이터를 저장하는 도중 문제가 발생했습니다. CODE : ${result.status}")
                    }
                }.onFailure { result ->
                    result.printStackTrace()
                    Log.e(TAG, "result = ${result.message}")
                    commonViewModel.setToastMessage("인터넷이 연결되어있는지 확인해 주십시오")
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
            ActivityResultCallback { uri ->
                if (uri == null) {
                    binding.selectedImage.setImageURI(selectedImageUri)
                    if (selectedImageUri == null) {
                        binding.iconImage.visibility = View.VISIBLE
                    }
                } else {
                    binding.iconImage.visibility = View.GONE
                    binding.selectedImage.setImageURI(uri)
                    selectedImageUri = uri
                }
            }
        )
    }

    private fun modifyDiary(diaryId: Int, emoji: String, date: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val urlList: List<String>
            runBlocking {
                urlList = if (selectedImageUri != null) {
                    uploadFile(listOf(selectedImageUri!!))
                } else {
                    if (diaryViewModel.editDiaryData.value?.files?.size == 0) {
                        listOf(diaryViewModel.editDiaryData.value?.files!![0].url)
                    } else {
                        listOf()
                    }
                }
            }

            val editDiaryRequestBody = EditDiaryRequest(
                title = binding.editDiaryTitleEditText.text.toString(),
                content = binding.editDiaryMainContentEditText.text.toString(),
                pregnancyWeeks = binding.pregnancyEditText.text.toString().toInt(),
                weight = binding.weightEditText.text.toString().toInt(),
                nextAppointment = date, //와 이거 형식을 안 맞추면 400 뜸 그래서 고생함.
                emoji = emoji,
                systolicPressure = binding.bloodPressureHeightInputText.text.toString().toInt(),
                diastolicPressure = binding.bloodPressureLowInputText.text.toString().toInt(),
                isPublic = !binding.SwitchCompat.isChecked,
                url = urlList
            )

            kotlin.runCatching {
                RetrofitBuilder.getDiaryService().modifyDiary(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                    id = diaryId,
                    body = editDiaryRequestBody
                )
            }.onSuccess {
                withContext(Dispatchers.Main) {
                    requireActivity().supportFragmentManager.popBackStack()
                    diaryViewModel.initEditDate()
                    commonViewModel.setToastMessage("성공적으로 일기가 수정되었습니다.")
                }
            }.onFailure {
                withContext(Dispatchers.Main) {
                    if (it is HttpException) {
                        if (it.code() == 500) {
                            commonViewModel.setToastMessage("서버에 문제가 발생했습니다.")
                        } else {
                            commonViewModel.setToastMessage("일기를 수정하는도중 문제가 발생했습니다 CODE : ${it.code()}")
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}