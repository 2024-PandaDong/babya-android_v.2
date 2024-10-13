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

        val bottomSheetDialog =
            SignupBottomSheet(){d->
                edit.setText(d)
            }
        bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
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
        viewModel.pregnancyDt.value = ""
        findNavController().navigate(R.id.action_signup3_to_signup5)
    }



    fun main(value: String) {
        // 입력 형식: "2024년 09월 13일" 또는 "2024년 9월 1일" 등
        val year = value.substring(0, 4) // 연도 부분
        val monthStart = value.indexOf("년") + 2
        val monthEnd = value.indexOf("월")
        val month = value.substring(monthStart, monthEnd).padStart(2, '0') // 월 부분, 두 자리로 보장

        val dayStart = value.indexOf("월") + 2
        val dayEnd = value.indexOf("일")
        val day = value.substring(dayStart, dayEnd).padStart(2, '0') // 일 부분, 두 자리로 보장

        // 새로운 형식으로 조합하여 반환
        viewModel.birthDt.value = "$year-$month-$day"
    }

    fun main2(value: String) {
        // 입력 형식: "2024년 09월 13일" 또는 "2024년 9월 1일" 등
        val year = value.substring(0, 4) // 연도 부분
        val monthStart = value.indexOf("년") + 2
        val monthEnd = value.indexOf("월")
        val month = value.substring(monthStart, monthEnd).padStart(2, '0') // 월 부분, 두 자리로 보장

        val dayStart = value.indexOf("월") + 2
        val dayEnd = value.indexOf("일")
        val day = value.substring(dayStart, dayEnd).padStart(2, '0') // 일 부분, 두 자리로 보장

        // 새로운 형식으로 조합하여 반환
        viewModel.marriedDt.value = "$year-$month-$day"
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