package kr.pandadong2024.babya.start.signup

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentSignup9Binding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.remote.request.SignUpRequest
import kr.pandadong2024.babya.start.viewmodel.SignupViewModel

class Signup9 : Fragment() {

    private var _binding: FragmentSignup9Binding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<SignupViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignup9Binding.inflate(inflater, container, false)

        kotlin.run {
            Log.d(TAG, "email: ${viewModel.email.value}")
            Log.d(TAG, "pw: ${viewModel.pw.value}")
            Log.d(TAG, "nickName: ${viewModel.nickName.value}")
            Log.d(TAG, "marriedDt: ${viewModel.marriedDt.value}")
            Log.d(TAG, "pregnancyDt: ${viewModel.pregnancyDt.value}")
            Log.d(TAG, "birthDt: ${viewModel.birthDt.value}")
            Log.d(TAG, "locationCode: ${viewModel.locationCode.value}")
            Log.d(TAG, "pushToken: ${viewModel.pushToken.value}")
            Log.d(TAG, "birthNameList: ${viewModel.birthNameList.value}")
            Log.d(TAG, "childrenNameList: ${viewModel.childrenNameList.value}")
            main(viewModel.pregnancyDt.value)
        }

        binding.nextBtn.setOnClickListener {
            Signup()
        }

        binding.signUpBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_signup9_to_signup5)
        }


        return binding.root
    }

    fun main(value: String?) {
        val input = value
        // 입력 문자열의 포맷 정의
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
        // 출력 문자열의 포맷 정의
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // 문자열을 LocalDate로 변환한 후 원하는 형식으로 다시 변환
        val date = LocalDate.parse(input, inputFormatter)
        val formattedDate = date.format(outputFormatter)

        viewModel.pregnancyDt.value = formattedDate

    }


    private fun Signup(){
        lifecycleScope.launch(Dispatchers.IO){
            kotlin.runCatching {
                RetrofitBuilder.getSignupService().postSignup(

                    body = SignUpRequest(
                        email = viewModel.email.value.toString(),
                        pw = viewModel.pw.value.toString(),
                        nickName = viewModel.nickName.value.toString(),
                        marriedDt = viewModel.marriedDt.value.toString(),
                        pregnancyDt = viewModel.pregnancyDt.value.toString(),
                        birthDt = viewModel.birthDt.value.toString(),
                        locationCode = "22",
                        pushToken = "", // fcm
                        childList = (viewModel.birthNameList.value ?: emptyList()) + (viewModel.childrenNameList.value ?: emptyList())
                    )
                )

            }.onSuccess {

                Log.d(TAG, "Signup: 성공")
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "회원가입에 성공했습니다", Toast.LENGTH_SHORT).show()

                    // 회원가입 -> 홈(quiz)
                    // val intent = Intent(requireContext(), QuizFragment::class.java)
                    // startActivity(intent)

                    // 회원가입 -> 로그인
                    findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
                }
            }.onFailure {
                Log.d(TAG, "Signup: ${it.message}")
                it.printStackTrace()
                Log.d(TAG, "Signup: 실패")
                Log.d(TAG, "Signup: ${it.stackTrace}")
            }

        }

    }

}