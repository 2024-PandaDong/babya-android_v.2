package kr.pandadong2024.babya.start.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.pandadong2024.babya.databinding.LoginBottomSheetBinding
import kr.pandadong2024.babya.util.Pattern
import kr.pandadong2024.babya.util.setOnSingleClickListener

class LoginBottomSheet(
    val login: (email: String, password: String) -> Unit,
    val moveSignUp: () -> Unit
) : BottomSheetDialogFragment() {
    private var _binding: LoginBottomSheetBinding? = null
    private val binding get() = _binding!!


    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val bottomSheet =
                dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = bottomSheet?.let { BottomSheetBehavior.from(it) }
            if (behavior != null) {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = LoginBottomSheetBinding.inflate(inflater, container, false)

        binding.registTextButton.setOnClickListener {
            moveSignUp()
            dismiss()
        }
        binding.loginBtn.setOnSingleClickListener {
            val emailText = binding.emailEditText.text?.toString() ?: ""
            val passwordText = binding.passwordEditText.text?.toString() ?: ""
            if (emailText.matches(Pattern.email)) {
                binding.emailLayout.error = null
            } else {
                binding.emailLayout.error = "등록되지 않은 이메일입니다."
            }
            if (passwordText.matches(Pattern.passwordRegex)) {
                binding.passwordLayout.error = null
            } else {
                binding.passwordLayout.error = "비밀번호를 잘못 입력하셨습니다."
            }
            if (
                binding.emailEditText.text?.toString()
                    ?.isNotEmpty() == true && binding.passwordEditText.text?.toString()
                    ?.isNotEmpty() == true
            ) {
                login(
                    binding.emailEditText.text?.toString() ?: "",
                    binding.passwordEditText.text?.toString() ?: ""
                )
            }
        }

        binding.passwordEditText.doAfterTextChanged { text ->
            val email = binding.emailEditText.text.toString()
            if (text != null) {
                binding.loginBtn.isEnabled =
                    text.isNotEmpty() && text.matches(Pattern.passwordRegex) && email.isNotEmpty() && email.matches(
                        Pattern.email
                    )
            }
        }

        binding.emailEditText.doAfterTextChanged { text ->
            val password = binding.passwordEditText.text.toString()
            if (text != null) {
                binding.loginBtn.isEnabled =
                    password.isNotEmpty() && password.matches(Pattern.passwordRegex) && text.isNotEmpty() && text.matches(
                        Pattern.email
                    )
            }
        }

        return binding.root
    }
}