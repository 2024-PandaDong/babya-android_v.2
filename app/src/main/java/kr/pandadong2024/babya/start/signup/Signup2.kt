package kr.pandadong2024.babya.start.signup

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentSignup2Binding
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.start.viewmodel.SignupViewModel
import kr.pandadong2024.babya.util.Pattern
import kr.pandadong2024.babya.util.setOnSingleClickListener

class Signup2 : Fragment() {

    private var _binding: FragmentSignup2Binding? = null
    private val binding get() = _binding!!

    private var passwordCheck = false
    private var emailCheck = false
    private var visible = false

    private val TAG = "Signup2"
    private val viewModel by activityViewModels<SignupViewModel>()
    private var isVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignup2Binding.inflate(inflater, container, false)

        binding.emailCheckButton.setOnSingleClickListener {
            emailCheck()
        }

        binding.verificationCodeSendButton.setOnSingleClickListener {
            verificationCodeSend()
        }

        binding.nextBtn.setOnClickListener {
            next()
        }

        binding.signUpBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_signup2_to_signup1)
        }


        // 나중에 둘이 합치기
        binding.visiblePassword.setOnClickListener {
            changeShowBtn()
        }

        binding.visiblePassword2.setOnClickListener {
            changeShowBtn2()
        }



        binding.run {
            binding.emailEditText.doAfterTextChanged { text ->
                val emailText = text.toString()
                binding.emailCheckButton.isEnabled = emailText.isNotEmpty() && emailText.matches(Pattern.email)
            }
            binding.verificationCodeEditText.doAfterTextChanged { text ->
                val reemailText = text.toString()
                binding.verificationCodeSendButton.isEnabled = reemailText.length == 6
            }

            binding.passwordEditText.doAfterTextChanged { text ->
                val passwordText = text.toString()
                if (passwordText.isNotEmpty() && passwordText.matches(Pattern.passwordRegex)) {
                    binding.passwordText.visibility = View.GONE
                } else {
                    binding.passwordText.visibility = View.VISIBLE
                }
            }

            binding.passwordCheckEditText.doAfterTextChanged { text ->
                val pw = binding.passwordEditText.text.toString()
                val pwCheck = text.toString()
                if (pw == pwCheck){
                    binding.passwordCheckText.visibility = View.GONE
                    passwordCheck = true
                    if (passwordCheck && emailCheck){
                        binding.nextBtn.isEnabled = true
                    }
                } else {
                    binding.passwordCheckText.visibility = View.VISIBLE
                }
            }
        }

        return binding.root
    }

    private fun next() {
        val email = binding.emailEditText.text.toString()
        val pw = binding.passwordEditText.text.toString()
        viewModel.email.value = email
        viewModel.pw.value = pw
        viewModel.pushToken.value = ""
        findNavController().navigate(R.id.action_signup2_to_signup3)
    }


    private fun emailCheck(){
        val email = binding.emailEditText.text.toString()
        binding.verificationCodeLayout.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO){
            kotlin.runCatching {
                RetrofitBuilder.getSignupService().postEmailSend(
                    email = email
                )
            }.onSuccess {
                binding.emailCheckButton.text = "재인증"
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private fun verificationCodeSend(){
        val verificationCode = binding.verificationCodeEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        lifecycleScope.launch(Dispatchers.IO){
            kotlin.runCatching {
                RetrofitBuilder.getSignupService().postEmailVerify(
                    email = email,
                    code = verificationCode
                )
            }.onSuccess {
                emailCheckSuccess()
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private fun emailCheckSuccess() {
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(requireContext(), "이메일이 인증되었습니다", Toast.LENGTH_SHORT).show()
            binding.verificationCodeLayout.visibility = View.GONE
            binding.emailEditText.isEnabled = false
            binding.emailEditText.setTextColor(Color.parseColor("#FD7D7C"))
            emailCheck = true
            if (passwordCheck && emailCheck){
                binding.nextBtn.isEnabled = true
            }
        }
    }

    private fun changeShowBtn(){
        var lastIndex : Int = 0
        if (binding.passwordEditText.text?.lastIndex != null){
            lastIndex = binding.passwordEditText.text?.lastIndex!!+1
        }

        isVisible = if(isVisible){
            binding.visiblePassword.setBackgroundResource(R.drawable.ic_visibility)
            binding.passwordEditText.inputType = 0x00000081
            binding.passwordEditText.setSelection(lastIndex)
            false
        } else{
            binding.visiblePassword.setBackgroundResource(R.drawable.ic_visible)
            binding.passwordEditText.inputType = 0x00000091
            binding.passwordEditText.setSelection(lastIndex)
            true
        }
    }

    private fun changeShowBtn2(){
        var lastIndex : Int = 0
        if (binding.passwordCheckEditText.text?.lastIndex != null){
            lastIndex = binding.passwordCheckEditText.text?.lastIndex!!+1
        }

        isVisible = if(isVisible){
            binding.visiblePassword2.setBackgroundResource(R.drawable.ic_visibility)
            binding.passwordCheckEditText.inputType = 0x00000081
            binding.passwordCheckEditText.setSelection(lastIndex)
            false
        } else{
            binding.visiblePassword2.setBackgroundResource(R.drawable.ic_visible)
            binding.passwordCheckEditText.inputType = 0x00000091
            binding.passwordCheckEditText.setSelection(lastIndex)
            true
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}