package kr.pandadong2024.babya.start.signup;

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kr.pandadong2024.babya.QuizFragment
import kr.pandadong2024.babya.start.login.Pattern
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.remote.request.SignUpRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentSignupBinding


class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private var passwordCheck = false
    private var emailCheck = false
    private var birthNameList = ArrayList<BirthName>()
    private var childrenNameList = ArrayList<BirthName>()

    private val TAG = "SignupFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        // emailDelete 버튼 누르면 텍스트 삭제
        binding.emailDeleteBtn.setOnClickListener {
            binding.emailEdit.setText("")
        }

        binding.checkEmail.setOnClickListener {
            verifyEmail()
        }

        // 인증하러 감
        binding.reemailBtn.setOnClickListener {
            reemail()
        }

        // 태명 추가
        binding.birthDayCheckBth.setOnClickListener {
            addBirthBaby()
            binding.birthNameEdit.setText("")
        }

        // 아이 추가
        binding.childrenCheckBth.setOnClickListener{
            addChildren()
            binding.childrenEdit.setText("")
        }

        // 임신여부 체크
        binding.pregnancyRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            pregnancyCheck(checkedId)
        }

        // 아이여부 체크
        binding.childrenRadioGroup.setOnCheckedChangeListener {vgroup, checkedId ->
            childrenCheck(checkedId)
        }

        // 전체동의
        binding.fullAgreementCheckBox.setOnClickListener {
            fullAgreementCheck()
        }

        // 회원가입
        binding.signUpBtn.setOnClickListener {
            Signup()
        }

        // email, password
        binding.run {
            binding.emailEdit?.doAfterTextChanged { text ->
                val emailText = text.toString()
                if (emailText.isNotEmpty() && emailText.matches(Pattern.email)) {
                    binding.reemailBtn.isEnabled = true
                } else {
                    binding.reemailBtn.isEnabled = false
                }
            }
            binding.passWordEdit?.doAfterTextChanged { text ->
                val passwordText = text.toString()
                if (passwordText.isNotEmpty() && passwordText.matches(Pattern.passwordRegex)) {
                    passwordCheck = true
                    binding.passWordCheck.setText("사용가능")
                    binding.passWordCheck.setTextColor(Color.GREEN)
                } else {
                    binding.passWordCheck.setText("사용불가능")
                    binding.passWordCheck.setTextColor(Color.RED)
                }
            }
        }
        return binding.root
    }

    private fun addChildren() {
        val childrenName = binding.childrenEdit.text.toString()


        if (binding.childrenRv.adapter != null){
            childrenNameList = (binding.childrenRv.adapter as BriNmAdapter).birthNameList
        }

        if (childrenName.isNotEmpty()){
            childrenNameList.add(BirthName((childrenName), true))
        }

        if (binding.childrenRv.adapter == null){
            val adapter = BriNmAdapter(childrenNameList)
            binding.childrenRv.adapter = adapter
            binding.childrenRv.layoutManager = LinearLayoutManager(requireContext())
        } else{
            (binding.childrenRv.adapter as BriNmAdapter).notifyDataSetChanged()
        }
    }

    private fun addBirthBaby() {
        val birthName = binding.birthNameEdit.text.toString()


        if (binding.birthDayRv.adapter != null) {
            birthNameList = (binding.birthDayRv.adapter as BriNmAdapter).birthNameList
        }

        if (birthName.isNotEmpty()){
            birthNameList.add(BirthName((birthName), false))
        }

        if (binding.birthDayRv.adapter == null) {
            val adapter = BriNmAdapter(birthNameList)
            binding.birthDayRv.adapter = adapter
            binding.birthDayRv.layoutManager = LinearLayoutManager(requireContext())
        } else {
            (binding.birthDayRv.adapter as BriNmAdapter).notifyDataSetChanged()
        }
    }

    private fun reemail() {
        val email = binding.emailEdit.text.toString()
        binding.reemailLayout.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO){
            kotlin.runCatching {
                RetrofitBuilder.getSignupService().postEmailSend(
                    email = email
                )
            }.onSuccess {
                Log.d(TAG, "reemail: 성공")
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private fun verifyEmail() {
        binding.reemailBtn.isEnabled = false
        val email = binding.emailEdit.text.toString()
        val emailVerify = binding.reemailEdit.text.toString()

        lifecycleScope.launch(Dispatchers.IO) {
            runCatching {
                RetrofitBuilder.getSignupService().postEmailVerify(
                    email = email,
                    code = emailVerify
                )
            }.onSuccess {
                handleSuccess()
            }.onFailure { throwable ->
                handleFailure(throwable)
            }
        }
    }

    private fun handleSuccess() {
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(requireContext(), "이메일이 인증되었습니다", Toast.LENGTH_SHORT).show()
            binding.reemailBtn.setText("인증완료")
            binding.reemailBtn.setBackgroundColor(Color.GREEN)
            binding.reemailLayout.visibility = View.GONE
            emailCheck = true
        }
    }

    private fun handleFailure(throwable: Throwable) {
        throwable.printStackTrace()
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(requireContext(), "이메일이 인증에 실패했습니다", Toast.LENGTH_SHORT).show()
            binding.reemailBtn.isEnabled = true
            binding.reemailBtn.setText("다시 인증하기")
        }
    }


    private fun pregnancyCheck(checkedId: Int) {
        when (checkedId) {
            R.id.pregnancyRadioYesButton -> {
                binding.pregnancyLayout.visibility = View.VISIBLE
            }
            R.id.pregnancyRadioNoButton -> {
                binding.pregnancyLayout.visibility = View.GONE
            }
        }
    }


    private fun childrenCheck(checkedId: Int) {
        when (checkedId) {
            R.id.childrenRadioYesButton -> binding.childrenLayout.visibility = View.VISIBLE
            R.id.childrenRadioNoButton -> binding.childrenLayout.visibility = View.GONE
        }
    }

    private fun fullAgreementCheck() {
        val isChecked = binding.fullAgreementCheckBox.isChecked
        binding.agreementCheckBox1.isChecked = isChecked
        binding.agreementCheckBox2.isChecked = isChecked
        binding.agreementCheckBox3.isChecked = isChecked
        binding.agreementCheckBox4.isChecked = isChecked
    }


    private fun Signup(){
        // 필수 항목
        val email = binding.emailEdit.text.toString()
        val password = binding.passWordEdit.text.toString()
        val name = binding.nameEdit.text.toString()
        val birthDay = binding.birthDayEdit.text.toString()
        val city = binding.cityEdit.text.toString()
        val checkBox1 = binding.agreementCheckBox1.isChecked
        val checkBox2 = binding.agreementCheckBox2.isChecked
        val checkBox3 = binding.agreementCheckBox3.isChecked

        // 선택 항목
        val marriedDay :String = binding.weddingDayEdit.text.toString()



        if (name.isNotEmpty() && birthDay.isNotEmpty() && city.isNotEmpty() && passwordCheck == true && checkBox1 == true && checkBox2 == true && checkBox3 == true && emailCheck == true){
            lifecycleScope.launch(Dispatchers.IO){
                kotlin.runCatching {
                    RetrofitBuilder.getSignupService().postSignup(
                        body = SignUpRequest(
                            email = email,
                            pw = password,
                            nickName = name,
                            marriedDt = marriedDay,
                            pregnancyDt = marriedDay,
                            birthDt = birthDay,
                            locationCode = city,
                            pushToken = "", // fcm
                            childList = birthNameList + childrenNameList
                        )
                    )

                }.onSuccess {
                    Log.d(TAG, "Signup: 성공")
                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "회원가입에 성공했습니다", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), QuizFragment::class.java)
                        startActivity(intent)
                    }
                }.onFailure {
                    it.printStackTrace()
                    Log.d(TAG, "Signup: 실패")
                    Log.d(TAG, "Signup: ${it.stackTrace}")
                }
            }
        }
        else{
            Toast.makeText(requireContext(), "회원정보를 전부 입력해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}