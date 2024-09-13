package kr.pandadong2024.babya.start.signup

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentSignup3Binding
import kr.pandadong2024.babya.home.policy.bottom_sheet.PolicyBottomSheet
import kr.pandadong2024.babya.home.policy.viewmdole.PolicyViewModel
import kr.pandadong2024.babya.start.viewmodel.SignupViewModel
import java.util.Calendar
import java.util.GregorianCalendar

class Signup3 : Fragment() {

    private var _binding: FragmentSignup3Binding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<SignupViewModel>()
    private val policyViewModel by activityViewModels<PolicyViewModel>()
    private val gregorianCalendar = GregorianCalendar()
    private val year = gregorianCalendar.get(Calendar.YEAR)
    private val date = gregorianCalendar.get(Calendar.DATE)
    private val month = gregorianCalendar.get(Calendar.MONTH)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignup3Binding.inflate(inflater, container, false)

        binding.birthDayButton.setOnClickListener {
            dateService(binding.birthDayEditText)
        }

        binding.birthDayButton2.setOnClickListener {
            dateService(binding.marriedDayEditText)
        }

        binding.signUpBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_signup3_to_signup2)
        }

        binding.nextBtn.setOnClickListener {
            next()
        }

        policyViewModel.tagsList.observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                val location = encodingLocateNumber(it[0])
                Log.d(TAG, "location: ${location}")
                binding.locationEditText.setText(it[0])
                viewModel.locationCode.value = location.toString()
            }
        }

        // 지역코드 해야함
        binding.locationButton.setOnClickListener {
            val bottomSheetDialog =
                PolicyBottomSheet() { tag ->

                }

            bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
            Log.d(TAG, "show aaa")
        }

        kotlin.run {
            binding.nickNameEditText?.doAfterTextChanged {
                check()
            }
            binding.birthDayEditText?.doAfterTextChanged {
                check()
            }
            binding.marriedDayEditText?.doAfterTextChanged {
                check()
            }
            binding.locationEditText?.doAfterTextChanged {
                check()
            }
        }

        return binding.root
    }





    private fun dateService(edit : EditText){
        val dlg = DatePickerDialog(requireContext(), object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int
            ) {
                //month는 +1 해야 함
                Log.d("MAIN", "${year}, ${month+1}, ${dayOfMonth}")

                val parsedDate = String.format("%d년 %02d월 %02d일", year, month + 1, dayOfMonth)
                edit.setText(parsedDate)
            }

        }, year, month, date)
        dlg.show()
    }

    private fun check(){
        val nickName = binding.nickNameEditText.text.toString()
        val birthDt = binding.birthDayEditText.text.toString()
        val marriedDt = binding.marriedDayEditText.text.toString()
        // 메인에 머지하고 ㄱㄱ
        val locationCode = binding.locationEditText.text.toString();

        if (nickName.isNotEmpty() && birthDt.isNotEmpty() && marriedDt.isNotEmpty() && locationCode.isNotEmpty()) {
            binding.nextBtn.isEnabled = true
        }
    }

    private fun next() {
        val nickName = binding.nickNameEditText.text.toString()
        val birthDt = binding.birthDayEditText.text.toString()
        val marriedDt = binding.marriedDayEditText.text.toString()
        main(birthDt)
        main2(marriedDt)

        // 메인에 머지하고 ㄱㄱ
        viewModel.nickName.value = nickName
        findNavController().navigate(R.id.action_signup3_to_signup5)
    }




    fun main(birthDt: String) {
        val input = birthDt
        // 입력 문자열의 포맷 정의
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
        // 출력 문자열의 포맷 정의
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // 문자열을 LocalDate로 변환한 후 원하는 형식으로 다시 변환
        val date = LocalDate.parse(input, inputFormatter)
        val formattedDate = date.format(outputFormatter)

        viewModel.birthDt.value = birthDt
    }

    fun main2(marriedDt: String) {
        val input = marriedDt
        // 입력 문자열의 포맷 정의
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
        // 출력 문자열의 포맷 정의
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // 문자열을 LocalDate로 변환한 후 원하는 형식으로 다시 변환
        val date = LocalDate.parse(input, inputFormatter)
        val formattedDate = date.format(outputFormatter)

        viewModel.marriedDt.value = marriedDt
    }



    private fun encodingLocateNumber(location: String): String {
        return when (location) {
            "서울특별시" -> "11"
            "부산광역시" -> "21"
            "대구광역시" -> "22"
            "인천광역시" -> "23"
            "광주광역시" -> "24"
            "대전광역시" -> "25"
            "울산광역시" -> "26"
            "경기도" -> "31"
            "세종특별자치시" -> "29"
            "강원도" -> "32"
            "충청북도" -> "33"
            "충청남도" -> "34"
            "전라북도" -> "35"
            "전라남도" -> "36"
            "경상북도" -> "37"
            "경상남도" -> "38"
            "제주특별자치도" -> "39"
            else -> "-1"
        }
    }
}