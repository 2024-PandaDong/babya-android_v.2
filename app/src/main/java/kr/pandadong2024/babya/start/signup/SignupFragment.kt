package kr.pandadong2024.babya.start.signup;

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignupBinding.inflate(inflater, container, false)


        binding.birthDayBtn.setOnClickListener {
            dateService(binding.birthDayEdit)
        }

        binding.weddingDayBtn.setOnClickListener {
            dateService(binding.weddingDayEdit)
        }

        binding.pregnancyDayBtn.setOnClickListener {
            dateService(binding.pregnancyDayEdit)
        }

        binding.cityBtn.setOnClickListener {
            locationSave(binding.cityEdit)
        }

        binding.signUpBackButton.setOnClickListener{
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }

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
            binding.reemailEdit?.doAfterTextChanged { text ->
                Log.d(TAG, "onCreateView: 12345")
                val reemailText = text.toString()
                if (reemailText.length == 6){
                    Log.d(TAG, "onCreateView: 성공!!")
                    binding.checkEmail.isEnabled = true
                } else{
                    binding.checkEmail.isEnabled = false
                    Log.d(TAG, "onCreateView: 실패!!")
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

    // 날짜 지정
    private fun dateService(edit : EditText) {
        val dialog = AlertDialog.Builder(context).create()
        val edialog : LayoutInflater = LayoutInflater.from(context)
        val mView : View = edialog.inflate(R.layout.calendar_dialog_view, null)

        val year : NumberPicker = mView.findViewById(R.id.yearDatePicker)
        val month : NumberPicker = mView.findViewById(R.id.monthDatePicker)
        val day : NumberPicker = mView.findViewById(R.id.dayDatePicker)
        val cancel : Button = mView.findViewById(R.id.cancelBtn)
        val save : Button = mView.findViewById(R.id.saveBtn)


        year.wrapSelectorWheel = false
        month.wrapSelectorWheel = false
        day.wrapSelectorWheel = false

        year.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        month.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        day.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS


        month.maxValue = 12
        month.minValue = 1
        day.maxValue = 31
        day.minValue = 1

        // 연도를 역순으로 표시할 배열 생성
        val years = (2024 downTo 1900).map { it.toString() }.toTypedArray()

        // 최소값과 최대값 설정
        year.minValue = 0
        year.maxValue = years.size - 1

        // displayedValues 설정
        year.displayedValues = years

        // 기본값 설정 (2024)
        year.value = 0


        cancel.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }

        save.setOnClickListener {
            val selectedYear = years[year.value]
            val selectedDate = "$selectedYear-${String.format("%02d", month.value)}-${String.format("%02d", day.value)}"
            edit.setText(selectedDate)
            dialog.dismiss()
            dialog.cancel()
        }


        dialog.setView(mView)
        dialog.create()
        dialog.show()
    }

    private fun locationSave(edit : EditText){
        val dialog = AlertDialog.Builder(context).create()
        val edialog : LayoutInflater = LayoutInflater.from(context)
        val mView : View = edialog.inflate(R.layout.zone_dialog_view, null)

        val city : NumberPicker = mView.findViewById(R.id.cityPicker)
        val cancel : Button = mView.findViewById(R.id.cancelBtn)
        val save : Button = mView.findViewById(R.id.saveBtn)

        val cities = arrayOf("서울특별시", "부산광역시", "대구광역시", "인천광역시", "광주광역시", "대전광역시", "울산광역시", "세종특별시", "경기도", "강원도", "충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주특별자치도")
        city.minValue = 0
        city.maxValue = cities.size - 1
        city.displayedValues = cities

        cancel.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }

        save.setOnClickListener {
            val selectedCity = cities[city.value]
            edit.setText(selectedCity)
            dialog.dismiss()
            dialog.cancel()
        }

        dialog.setView(mView)
        dialog.create()
        dialog.show()
    }


//    private fun callBack(dtae : String, mode : Int){
//        if (mode == 1){
//            binding.birthDayEdit.setText(dtae)
//        } else if (mode == 2){
//            binding.weddingDayEdit.setText(dtae)
//        } else if (mode == 3){
//            binding.pregnancyDayEdit.setText(dtae)
//        }
//    }


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

    // 이메일 인증 보내기
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
//                it.printStackTrace()
            }
        }
    }

    // 이메일 인증 확인
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
            }.onFailure {
//                throwable ->
//                handleFailure(throwable)
            }
        }
    }

    // 이메일 인증 성공 시
    private fun handleSuccess() {
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(requireContext(), "이메일이 인증되었습니다", Toast.LENGTH_SHORT).show()
            binding.reemailBtn.setText("인증완료")
            binding.reemailBtn.setBackgroundColor(Color.GREEN)
            binding.reemailLayout.visibility = View.GONE
            emailCheck = true
        }
    }

    // 이메일 인증 실패 시
    private fun handleFailure(throwable: Throwable) {
        throwable.printStackTrace()
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(requireContext(), "이메일이 인증에 실패했습니다", Toast.LENGTH_SHORT).show()
            binding.reemailBtn.isEnabled = true
            binding.reemailBtn.setText("다시 인증하기")
        }
    }


    // 태아여부
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


    // 아이여부
    private fun childrenCheck(checkedId: Int) {
        when (checkedId) {
            R.id.childrenRadioYesButton -> binding.childrenLayout.visibility = View.VISIBLE
            R.id.childrenRadioNoButton -> binding.childrenLayout.visibility = View.GONE
        }
    }

    // 약관동의
    private fun fullAgreementCheck() {
        val isChecked = binding.fullAgreementCheckBox.isChecked
        binding.agreementCheckBox1.isChecked = isChecked
        binding.agreementCheckBox2.isChecked = isChecked
        binding.agreementCheckBox3.isChecked = isChecked
        binding.agreementCheckBox4.isChecked = isChecked
    }


    // 지역코드로 변환
    private fun cityNumber(city: String): String {
        return when (city) {
            "서울특별시" -> "11"
            "부산광역시" -> "21"
            "대구광역시" -> "22"
            "인천광역시" -> "23"
            "광주광역시" -> "24"
            "대전광역시" -> "25"
            "울산광역시" -> "26"
            "세종특별자치시" -> "29"
            "경기도" -> "31"
            "강원도" -> "32"
            "충청북도" -> "33"
            "충청남도" -> "34"
            "전라북도" -> "35"
            "전라남도" -> "36"
            "경상북도" -> "37"
            "경상남도" -> "38"
            "제주특별자치도" -> "39"
            else -> "00"
        }
    }


    // 회원가입
    private fun Signup(){
        // 필수 항목
        val email = binding.emailEdit.text.toString()
        val password = binding.passWordEdit.text.toString()
        val name = binding.nameEdit.text.toString()
        val birthDay = binding.birthDayEdit.text.toString()
        val city = binding.cityEdit.text.toString()
        val locationCode = cityNumber(city)
        val checkBox1 = binding.agreementCheckBox1.isChecked
        val checkBox2 = binding.agreementCheckBox2.isChecked
        val checkBox3 = binding.agreementCheckBox3.isChecked


        // 선택 항목
        val marriedDay :String = binding.weddingDayEdit.text.toString()




        if (name.isNotEmpty() && birthDay.isNotEmpty() && locationCode.isNotEmpty() && passwordCheck == true && checkBox1 == true && checkBox2 == true && checkBox3 == true && emailCheck == true){
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
                            locationCode = locationCode,
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