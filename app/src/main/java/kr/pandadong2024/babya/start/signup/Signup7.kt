package kr.pandadong2024.babya.start.signup

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentSignup5Binding
import kr.pandadong2024.babya.databinding.FragmentSignup7Binding
import kr.pandadong2024.babya.start.viewmodel.SignupViewModel

class Signup7 : Fragment() {

    private var _binding: FragmentSignup7Binding? = null
    private val binding get() = _binding!!
    private var childrenNameList = ArrayList<BirthName>()
    private val viewModel by activityViewModels<SignupViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignup7Binding.inflate(inflater, container, false)

        binding.babyNameButton.setOnClickListener {
            addChildren()
        }

        binding.nextBtn.setOnClickListener {
            next()
        }

        binding.signUpBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_signup7_to_signup5)
        }

        binding.run {
            binding.babyEditText?.doAfterTextChanged { text ->
                val size = text.toString()
                if (size == "0명"){
                    binding.babyEditText.visibility = View.GONE
                } else {
                    binding.babyEditText.visibility = View.VISIBLE
                }
            }
        }


        return binding.root
    }

    private fun next() {
        viewModel.birthNameList.value = childrenNameList
        findNavController().navigate(R.id.action_signup7_to_signup9)
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
        binding.nextBtn.isEnabled = true
    }

}