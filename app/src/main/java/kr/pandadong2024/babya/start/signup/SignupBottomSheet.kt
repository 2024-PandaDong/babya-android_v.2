package kr.pandadong2024.babya.start.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.pandadong2024.babya.databinding.FragmentSignupBottomSheetBinding
import kr.pandadong2024.babya.databinding.LoginBottomSheetBinding
import kr.pandadong2024.babya.start.login.Pattern
import kr.pandadong2024.babya.util.setOnSingleClickListener


class SignupBottomSheet(
    var check : (date : String) -> Unit
) : BottomSheetDialogFragment() {
    private var _binding: FragmentSignupBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignupBottomSheetBinding.inflate(inflater, container, false)


        val year = binding.yearDatePicker
        val month = binding.monthDatePicker
        val day = binding.dayDatePicker
        val save = binding.saveBtn

        year.wrapSelectorWheel = false
        month.wrapSelectorWheel = false
        day.wrapSelectorWheel = false

        year.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        month.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        day.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        month.maxValue = 12
        month.minValue = 1

        day.maxValue = 31
        day.minValue = 1

        val years = (2024 downTo 1900).map { it.toString() }.toTypedArray()

        year.maxValue = years.size-1
        year.minValue = 1
        year.displayedValues = years
        year.value = 0

        save.setOnClickListener {
            val selectedYear = years[year.value]
            val selectedDate = "${selectedYear}년 ${String.format("%02d", month.value)}월 ${String.format("%02d", day.value)}일"
            check(selectedDate)
            this.dismiss()
        }



        return binding.root
    }

}