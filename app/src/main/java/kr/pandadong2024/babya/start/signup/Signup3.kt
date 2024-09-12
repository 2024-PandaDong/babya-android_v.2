package kr.pandadong2024.babya.start.signup

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import androidx.compose.ui.graphics.Color
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentSignup2Binding
import kr.pandadong2024.babya.databinding.FragmentSignup3Binding
import kr.pandadong2024.babya.start.viewmodel.SignupViewModel
import java.util.Calendar
import java.util.GregorianCalendar

class Signup3 : Fragment() {

    private var _binding: FragmentSignup3Binding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<SignupViewModel>()
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

        // 지역코드 해야함

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
        // 메인에 머지하고 ㄱㄱ
        val locationCode = "";
        viewModel.nickName.value = nickName
        viewModel.birthDt.value = birthDt //
        viewModel.marriedDt.value = marriedDt // yyyy-mm-dd로 바꿔서 줘야함
        viewModel.locationCode.value = locationCode
        findNavController().navigate(R.id.action_signup3_to_signup5)
    }
}