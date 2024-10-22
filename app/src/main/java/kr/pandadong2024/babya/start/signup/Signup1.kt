package kr.pandadong2024.babya.start.signup

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentSignup1Binding
import kr.pandadong2024.babya.databinding.FragmentSignup2Binding

class Signup1 : Fragment() {

    private var _binding: FragmentSignup1Binding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignup1Binding.inflate(inflater, container, false)


        // 처음에는 버튼을 비활성화
        binding.nextBtn.setOnClickListener {
            Log.d(TAG, "onCreateView: dddd")
            findNavController().navigate(R.id.action_signup1_to_signup2)
         //   findNavController().navigate(R.id.action_signup1_to_signup5)
        }

        binding.signUpBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_signup1_to_loginFragment)
        }

        binding.agreementBtn1.setOnClickListener {
            dateService()
        }



        return binding.root
    }


    private fun dateService(){

        val bottomSheetDialog =
            PolicyTextBottomSheet()
        bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 전체동의 체크박스 클릭 이벤트
        binding.fullAgreementCheckBox.setOnCheckedChangeListener { _, isChecked ->
            setAllCheckBoxes(isChecked) // 전체동의 체크 여부에 따라 개별 체크박스 상태 변경
        }

        // 개별 체크박스 클릭 이벤트
        binding.agreementCheckBox1.setOnCheckedChangeListener { _, _ -> checkFullAgreement() }
        binding.agreementCheckBox2.setOnCheckedChangeListener { _, _ -> checkFullAgreement() }
        binding.agreementCheckBox3.setOnCheckedChangeListener { _, _ -> checkFullAgreement() }
        binding.agreementCheckBox4.setOnCheckedChangeListener { _, _ -> checkFullAgreement() }
        binding.agreementCheckBox5.setOnCheckedChangeListener { _, _ -> checkFullAgreement() }
        // next 버튼 활성화 여부 확인
        checkEnableNextButton()
    }

    // 전체동의 체크박스에 따라 개별 체크박스 상태 설정
    private fun setAllCheckBoxes(isChecked: Boolean) {
        binding.agreementCheckBox1.isChecked = isChecked
        binding.agreementCheckBox2.isChecked = isChecked
        binding.agreementCheckBox3.isChecked = isChecked
        binding.agreementCheckBox4.isChecked = isChecked
        binding.agreementCheckBox5.isChecked = isChecked
        checkEnableNextButton() // next 버튼 활성화 여부 재확인
    }

    // 개별 체크박스 상태에 따라 전체동의 체크박스 설정
    private fun checkFullAgreement() {
        val allChecked = binding.agreementCheckBox1.isChecked &&
                binding.agreementCheckBox2.isChecked &&
                binding.agreementCheckBox3.isChecked &&
                binding.agreementCheckBox4.isChecked &&
                binding.agreementCheckBox5.isChecked
        binding.fullAgreementCheckBox.isChecked = allChecked // 모두 체크되면 전체동의 체크
        checkEnableNextButton() // next 버튼 활성화 여부 재확인
    }

    // 필수 항목 체크 여부에 따라 next 버튼 활성화
    private fun checkEnableNextButton() {
        binding.nextBtn.isEnabled = binding.agreementCheckBox1.isChecked &&
                binding.agreementCheckBox2.isChecked &&
                binding.agreementCheckBox3.isChecked // 필수 체크박스 확인
    }
}