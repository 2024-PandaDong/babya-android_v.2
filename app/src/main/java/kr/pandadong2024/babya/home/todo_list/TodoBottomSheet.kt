package kr.pandadong2024.babya.home.todo_list

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.pandadong2024.babya.databinding.BottomSheetBinding
import kr.pandadong2024.babya.server.remote.responses.todo.TodoResponses
import java.time.LocalDate

// 1 : 수정, 0 : 포스트
class TodoBottomSheet(
    private val type: Int,
    private val todoData: TodoResponses?,
    private val function: (todoData: TodoResponses) -> Unit,
) : BottomSheetDialogFragment() {
    private var _binding: BottomSheetBinding? = null
    private val binding get() = _binding!!
    private var mainDate: Int = todoData?.planedDt?.slice(
        todoData.planedDt.indexOf(
            '-',
            todoData.planedDt.indexOf('-') + 1
        ) + 1..<todoData.planedDt.length
    )?.toInt() ?: LocalDate.now().dayOfMonth
    private var mainMonth: Int = todoData?.planedDt?.slice(
        todoData.planedDt.indexOf('-') + 1..<todoData.planedDt.indexOf(
            '-',
            todoData.planedDt.indexOf('-') + 1
        )
    )?.toInt() ?: LocalDate.now().monthValue
    private var mainYear: Int =
        todoData?.planedDt?.slice(0..<todoData.planedDt.indexOf('-'))?.toInt()
            ?: LocalDate.now().year

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetBinding.inflate(inflater, container, false)
        binding.todoSelectedTimeText.text = "${mainMonth}/$mainDate"
        if (todoData != null) {
            binding.categoryEditText.setText(todoData.category)
            binding.todoEditText.setText(todoData.content)
            binding.categoryEditText.isEnabled = false
        } else {
            binding.categoryEditText.isEnabled = true
        }

        binding.bottomSheetSubmitButton.setOnClickListener {
            if (!binding.categoryEditText.text.isNullOrBlank() && !binding.todoEditText.text.isNullOrBlank()) {
                function(
                    TodoResponses(
                        todoId = todoData?.todoId,
                        category = binding.categoryEditText.text.toString(),
                        content = binding.todoEditText.text.toString(),
                        planedDt = String.format("%4d-%02d-%02d", mainYear, mainMonth, mainDate)
                    )
                )
            }
        }

        binding.bottomSheetTimeLayout.setOnClickListener {
            val dlg = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    Log.d("MAIN", "${year}, ${month}, ${dayOfMonth}")
                    mainDate = dayOfMonth
                    mainMonth = month
                    mainYear = year

                    binding.todoSelectedTimeText.text = "$mainMonth/$mainDate"
                    binding.todoSelectedTimeText.setTextColor(Color.BLACK)
                }, mainYear, mainMonth, mainDate
            )
            dlg.show()
        }
        return binding.root
    }
}