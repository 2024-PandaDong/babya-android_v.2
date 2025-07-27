package kr.pandadong2024.babya.start.signup

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentSignup1Binding

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
            findNavController().navigate(R.id.action_signup1_to_signup2)
        }

        binding.signUpBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_signup1_to_loginFragment)
        }

        binding.agreementBtn1.setOnClickListener {
            dateService(Policy.PRIVACY)
        }

        binding.agreementBtn2.setOnClickListener {
            dateService(Policy.SERVICE)
        }

        binding.agreementBtn3.setOnClickListener {
            dateService(Policy.INFORMATION)
        }




        return binding.root
    }


    private fun dateService(privacy: Policy) {

        val bottomSheetDialog =
            PolicyTextBottomSheet(privacy)
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
        // next 버튼 활성화 여부 확인
        checkEnableNextButton()
    }

    // 전체동의 체크박스에 따라 개별 체크박스 상태 설정
    private fun setAllCheckBoxes(isChecked: Boolean) {
        binding.agreementCheckBox1.isChecked = isChecked
        binding.agreementCheckBox2.isChecked = isChecked
        binding.agreementCheckBox3.isChecked = isChecked
        checkEnableNextButton() // next 버튼 활성화 여부 재확인
    }

    // 개별 체크박스 상태에 따라 전체동의 체크박스 설정
    private fun checkFullAgreement() {
        // 전체가 체크된 상태에서 개별 체크박스 상태 변경에 따라 전체동의 체크박스 상태 변경
        val allChecked = binding.agreementCheckBox1.isChecked &&
                binding.agreementCheckBox2.isChecked &&
                binding.agreementCheckBox3.isChecked
        binding.fullAgreementCheckBox.setOnCheckedChangeListener(null) // 리스너 일시 해제
        binding.fullAgreementCheckBox.isChecked = allChecked // 모든 개별 체크박스가 체크되면 전체 체크박스 체크
        binding.fullAgreementCheckBox.setOnCheckedChangeListener { _, isChecked ->
            setAllCheckBoxes(isChecked) // 다시 리스너 설정
        }
        checkEnableNextButton() // next 버튼 활성화 여부 재확인
    }

    // 필수 항목 체크 여부에 따라 next 버튼 활성화
    private fun checkEnableNextButton() {
        binding.nextBtn.isEnabled = binding.agreementCheckBox1.isChecked &&
                binding.agreementCheckBox2.isChecked &&
                binding.agreementCheckBox3.isChecked // 필수 체크박스 확인
    }
}