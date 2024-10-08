package kr.pandadong2024.babya.start.signup

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentSignup2Binding
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.util.Pattern
import kr.pandadong2024.babya.start.viewmodel.SignupViewModel

class Signup2 : Fragment() {

    private var _binding: FragmentSignup2Binding? = null
    private val binding get() = _binding!!

    private var passwordCheck = false
    private var emailCheck = false

    private val TAG = "Signup2"
    private val viewModel by activityViewModels<SignupViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignup2Binding.inflate(inflater, container, false)

        binding.emailCheckButton.setOnClickListener{
            emailCheck()
        }

        binding.verificationCodeSendButton.setOnClickListener {
            verificationCodeSend()
        }

        binding.nextBtn.setOnClickListener {
            next()
        }

        binding.signUpBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_signup2_to_signup1)
        }



        binding.run {
            binding.emailEditText?.doAfterTextChanged { text ->
                Log.d(TAG, "onCreateView: 12345")
                val emailText = text.toString()
                if (emailText.isNotEmpty() && emailText.matches(Pattern.email)) {
                    Log.d(TAG, "onCreateView: 성공!!")
                    binding.emailCheckButton.isEnabled = true
                } else {
                    binding.emailCheckButton.isEnabled = false
                }
            }
            binding.verificationCodeEditText?.doAfterTextChanged { text ->
                Log.d(TAG, "onCreateView: 12345")
                val reemailText = text.toString()
                if (reemailText.length == 6){
                    Log.d(TAG, "onCreateView: 성공!!")
                    binding.verificationCodeSendButton.isEnabled = true
                } else{
                    binding.verificationCodeSendButton.isEnabled = false
                    Log.d(TAG, "onCreateView: 실패!!")
                }
            }

            binding.passwordEditText?.doAfterTextChanged { text ->
                val passwordText = text.toString()
                if (passwordText.isNotEmpty() && passwordText.matches(Pattern.passwordRegex)) {
                    binding.passwordText.visibility = View.GONE
                } else {
                    binding.passwordText.visibility = View.VISIBLE
                }
            }

            binding.passwordCheckEditText?.doAfterTextChanged { text ->
                val pw = binding.passwordEditText.text.toString()
                val pwCheck = text.toString()
                if (pw == pwCheck){
                    binding.passwordCheckText.visibility = View.GONE
                    passwordCheck = true
                    if (passwordCheck && emailCheck == true){
                        binding.nextBtn.isEnabled = true
                    }
                }
                else {
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
                Log.d(TAG, "emailCheck: 성공")
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
                Log.d(TAG, "verificationCodeSend: 성공")
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
            if (passwordCheck && emailCheck == true){
                binding.nextBtn.isEnabled = true
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}