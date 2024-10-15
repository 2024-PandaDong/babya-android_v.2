package kr.pandadong2024.babya.start.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentSignup6Binding
import kr.pandadong2024.babya.start.viewmodel.SignupViewModel

class Signup6 : Fragment() {

    private var _binding: FragmentSignup6Binding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<SignupViewModel>()
    private var birthNameList = ArrayList<BirthName>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignup6Binding.inflate(inflater, container, false)

        binding.fetusNameButton.setOnClickListener {
            addFetus()
        }

        binding.fetusDayButton.setOnClickListener {
            dateService(binding.fetusDayEditText)
        }

        binding.nextBtn.setOnClickListener {
            next()
        }

        binding.signUpBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_signup6_to_signup5)
        }

        binding.run {
            binding.fetusNameEditText.doAfterTextChanged {
                if (birthNameList.size == 0) {
                    binding.fetusEditText.visibility = View.GONE
                    binding.nextBtn.isEnabled = false
                } else {
                    binding.fetusEditText.visibility = View.VISIBLE
                    binding.nextBtn.isEnabled = true
                }
            }
        }

        return binding.root
    }

    private fun addFetus() {
        val childrenName = binding.fetusNameEditText.text.toString()

        if (binding.fetusNameRv.adapter != null) {
            birthNameList = (binding.fetusNameRv.adapter as BriNmAdapter).birthNameList
        }

        if (childrenName.isNotEmpty()) {
            birthNameList.add(BirthName((childrenName), false))
        }

        if (binding.fetusNameRv.adapter == null) {
            val adapter = BriNmAdapter(birthNameList)
            binding.fetusNameRv.adapter = adapter
            binding.fetusNameRv.layoutManager = LinearLayoutManager(requireContext())
        } else {

            (binding.fetusNameRv.adapter as BriNmAdapter).notifyDataSetChanged()
        }

        val numberOfFetus =
            "${birthNameList.size}명" // 안드로이드 개발 권장으로 변경하기 위해 로컬 변수 추가 (내용 : setText로 표시된 텍스트를 연결하지 마십시오. 자리 표시자와 함께 리소스 문자열을 사용합니다.)
        binding.fetusEditText.setText(numberOfFetus)
        binding.fetusNameEditText.text?.clear()
        if (binding.fetusDayEditText.text.toString().isNotEmpty()) {
            binding.nextBtn.isEnabled = true
        }
    }

    private fun dateService(edit : EditText){

        val bottomSheetDialog =
            SignupBottomSheet(){d->
                edit.setText(d)
            }
        bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
    }

    private fun next() {
        viewModel.pregnancyDt.value = binding.fetusDayEditText.text.toString() // 이것도 파싱
        viewModel.birthNameList.value = birthNameList
        findNavController().navigate(R.id.action_signup6_to_signup9)
    }

}