package kr.pandadong2024.babya.home.policy.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kr.pandadong2024.babya.databinding.DeclarationDialogBinding
import kr.pandadong2024.babya.util.shortToast

class DeclarationDialog : DialogFragment() {
    private var _binding: DeclarationDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DeclarationDialogBinding.inflate(inflater, container, false)

        binding.positiveTextButton.setOnClickListener{
            if(binding.inputEditText.text.isNotEmpty()){
                //TODO : 신고 기능
                dismiss()
            }
            else{
                requireContext().shortToast("신고사항을 입력해 주세요.")
            }

        }
        binding.negativeTextButton.setOnClickListener{
            dismiss()
        }
        return binding.root
    }
}