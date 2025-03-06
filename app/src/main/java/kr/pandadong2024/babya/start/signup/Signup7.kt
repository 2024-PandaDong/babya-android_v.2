package kr.pandadong2024.babya.start.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentSignup7Binding
import kr.pandadong2024.babya.start.viewmodel.SignupViewModel

@AndroidEntryPoint
class Signup7 : Fragment() {

    private var _binding: FragmentSignup7Binding? = null
    private val binding get() = _binding!!
    private var childrenNameList = ArrayList<BirthName>()
    private val viewModel: SignupViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            binding.babyEditText.doAfterTextChanged { text ->
                val size = text.toString()
                if (size == "0명") {
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

        if (binding.babyNameRv.adapter != null) {
            childrenNameList = (binding.babyNameRv.adapter as BriNmAdapter).birthNameList
        }

        if (childrenName.isNotEmpty()) {
            childrenNameList.add(BirthName((childrenName), true))
        }

        if (binding.babyNameRv.adapter == null) {
            val adapter = BriNmAdapter(childrenNameList)
            binding.babyNameRv.adapter = adapter
            binding.babyNameRv.layoutManager = LinearLayoutManager(requireContext())
        } else {

            (binding.babyNameRv.adapter as BriNmAdapter).notifyDataSetChanged()
        }

        val numberOfChildren =
            childrenNameList.size.toString() + "명" // 안드로이드 개발 권장으로 변경하기 위해 로컬 변수 추가 (내용 : setText로 표시된 텍스트를 연결하지 마십시오. 자리 표시자와 함께 리소스 문자열을 사용합니다.)

        binding.babyEditText.setText(numberOfChildren)
        binding.babyNameEditText.text?.clear()
        binding.nextBtn.isEnabled = true
    }

}