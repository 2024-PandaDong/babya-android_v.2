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
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentSignup7Binding
import kr.pandadong2024.babya.databinding.FragmentSignup8Binding
import kr.pandadong2024.babya.start.viewmodel.SignupViewModel
import java.util.Calendar
import java.util.GregorianCalendar

class Signup8 : Fragment() {

    private var _binding: FragmentSignup8Binding? = null
    private val binding get() = _binding!!
    private var childrenNameList = ArrayList<BirthName>()
    private val viewModel by activityViewModels<SignupViewModel>()
    private var birthNameList = ArrayList<BirthName>()

    private val gregorianCalendar = GregorianCalendar()
    private val year = gregorianCalendar.get(Calendar.YEAR)
    private val date = gregorianCalendar.get(Calendar.DATE)
    private val month = gregorianCalendar.get(Calendar.MONTH)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignup8Binding.inflate(inflater, container, false)

        binding.fetusNameButton.setOnClickListener {
            addFetus()
        }

        binding.babyNameButton.setOnClickListener {
            addChildren()
        }

        binding.fetusDayButton.setOnClickListener {
            val dlg = DatePickerDialog(requireContext(), object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int
                ) {
                    //month는 +1 해야 함
                    Log.d("MAIN", "${year}, ${month + 1}, ${dayOfMonth}")

                    val parsedDate = String.format("%d년 %02d월 %02d일", year, month + 1, dayOfMonth)
                    binding.fetusDayEditText.setText(parsedDate)
                }
            }, year, month, date)
            dlg.show()
        }

        binding.nextBtn.setOnClickListener {
            next()
        }

        binding.signUpBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_signup8_to_signup5)
        }

        binding.run {
            binding.fetusNameEditText?.doAfterTextChanged {
                if ( birthNameList.size == 0){
                    binding.fetusEditText.visibility = View.GONE
                    binding.nextBtn.isEnabled = false
                } else {
                    binding.fetusEditText.visibility = View.VISIBLE
                    binding.nextBtn.isEnabled = true
                }
            }
            binding.babyEditText?.doAfterTextChanged {
                if (childrenNameList.size == 0){
                    binding.babyEditText.visibility = View.GONE
                } else {
                    binding.babyEditText.visibility = View.VISIBLE
                }
            }
        }


        return binding.root
    }

    private fun addFetus() {
        val childrenName = binding.fetusNameEditText.text.toString()

        if (binding.fetusNameRv.adapter != null){
            birthNameList = (binding.fetusNameRv.adapter as BriNmAdapter).birthNameList
        }

        if (childrenName.isNotEmpty()){
            birthNameList.add(BirthName((childrenName), true))
        }

        if (binding.fetusNameRv.adapter == null){
            val adapter = BriNmAdapter(birthNameList)
            binding.fetusNameRv.adapter = adapter
            binding.fetusNameRv.layoutManager = LinearLayoutManager(requireContext())
        } else{

            (binding.fetusNameRv.adapter as BriNmAdapter).notifyDataSetChanged()
        }

        binding.fetusEditText.setText(birthNameList.size.toString()+"명")
        binding.fetusNameEditText.text?.clear()
        if (childrenNameList.size != 0 && birthNameList.size != 0 && binding.fetusDayEditText.text.toString().isNotEmpty()){
            binding.nextBtn.isEnabled = true
        }
    }


    private fun next() {
        viewModel.birthNameList.value = birthNameList
        viewModel.childrenNameList.value = childrenNameList
        viewModel.birthDt.value = binding.fetusDayEditText.text.toString() // 이것도 파싱
        findNavController().navigate(R.id.action_signup8_to_signup92)
    }


    private fun addChildren() {
        val childrenName = binding.babyNameEditText.text.toString()

        if (binding.babyNameRv.adapter != null){
            childrenNameList = (binding.babyNameRv.adapter as BriNmAdapter).birthNameList
        }

        if (childrenName.isNotEmpty()){
            childrenNameList.add(BirthName((childrenName), true))
        }

        if (binding.babyNameRv.adapter == null){
            val adapter = BriNmAdapter(childrenNameList)
            binding.babyNameRv.adapter = adapter
            binding.babyNameRv.layoutManager = LinearLayoutManager(requireContext())
        } else{

            (binding.babyNameRv.adapter as BriNmAdapter).notifyDataSetChanged()
        }

        binding.babyEditText.setText(childrenNameList.size.toString()+"명")
        binding.babyNameEditText.text?.clear()
        if (childrenNameList.size != 0 && birthNameList.size != 0 && binding.fetusDayEditText.text.toString().isNotEmpty()){
            binding.nextBtn.isEnabled = true
        }
    }

}