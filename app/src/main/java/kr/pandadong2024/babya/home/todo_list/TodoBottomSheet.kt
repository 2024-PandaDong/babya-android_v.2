package kr.pandadong2024.babya.home.todo_list

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.pandadong2024.babya.databinding.BottomSheetBinding
import kr.pandadong2024.babya.server.remote.request.todo.TodoRequestBody
import java.time.LocalDate
import java.util.GregorianCalendar

class TodoBottomSheet( context: Context, private val postTodo : (todoData :TodoRequestBody) -> Unit) : BottomSheetDialogFragment() {
    private var _binding : BottomSheetBinding? = null
    private val binding get() = _binding!!
    private val TAG = "BottomSheetDialogFragment"
    private val gregorianCalendar = GregorianCalendar()
    private var mainDate = LocalDate.now().dayOfMonth.toInt()
    private var mainMonth = LocalDate.now().monthValue
    private var mainYear = LocalDate.now().year.toInt()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetBinding.inflate(inflater, container, false)
        binding.todoSelectedTimeText.text = "${mainMonth}/$mainDate"
        binding.bottomSheetSubmitButton.setOnClickListener {
            if(!binding.categoryEditText.text.isNullOrBlank() && !binding.todoEditText.text.isNullOrBlank() ){
                postTodo(
                    TodoRequestBody(
                        category = binding.categoryEditText.text.toString(),
                        content = binding.todoEditText.text.toString(),
                        planedDt = String.format("%4d-%02d-%02d", mainYear,mainMonth,mainDate)
                        )
                )
            }

        }
        binding.bottomSheetTimeLayout.setOnClickListener {
            Log.d("MAIN", "${mainYear}, ${mainMonth}, ${mainDate}")
            val dlg = DatePickerDialog(requireContext(),
                { _, year, month, dayOfMonth ->
                    Log.d("MAIN", "${year}, ${month}, ${dayOfMonth}")
                    mainDate = dayOfMonth
                    mainMonth = month
                    mainYear = year

                    binding.todoSelectedTimeText.text = "$mainMonth/$mainDate"
                    binding.todoSelectedTimeText.setTextColor(Color.BLACK)
                }, mainYear, mainMonth, mainDate)
            Log.d("MAIN", "${mainYear}, ${mainMonth}, ${mainDate}")
            dlg.show()
        }

        return binding.root
    }
}