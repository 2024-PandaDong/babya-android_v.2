package kr.pandadong2024.babya.start.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kr.pandadong2024.babya.HomeActivity
import com.example.babya_android.datastore.User
import kr.pandadong2024.babya.server.local.proto.UserRepository
import kr.pandadong2024.babya.server.local.proto.UserSerializer
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.remote.request.LoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentLoginBinding
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.local.TokenEntity

private const val DATA_STORE_FILE_NAME = "user.pb"

private val Context.test : DataStore<User> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = UserSerializer
)

private lateinit var viewModel: LoginViewModel

class LoginFragment : Fragment() {
    private lateinit var token: String
    private lateinit var tokenDao: TokenDAO

    private val TAG = "LoginFragment"
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var isVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        viewModel = ViewModelProvider(requireActivity(), ProtoViewModelFactory(UserRepository(requireContext().test)))[LoginViewModel::class.java]

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.passwordLayout?.setEndIconOnClickListener {
            changeVisiblePassword()
        }
        binding.singeUpTextButton?.setOnClickListener{
            singeUp()
        }
        binding.loginButton?.setOnClickListener{
            login()
        }
        binding.googleAuthButton?.setOnClickListener {
            oAuthLogin()
        }

        return binding.root
    }

    private fun oAuthLogin() {
        Toast.makeText(requireContext(), "제작중...", Toast.LENGTH_SHORT).show()
        //TODO oAuth로그인 하기
        runBlocking(Dispatchers.IO) {
            val dao = BabyaDB.getInstance(requireContext())!!
            tokenDao = dao.tokenDao()
            token = tokenDao.getMembers().accessToken
            Log.d(TAG, "${token}")
        }
//        lifecycleScope.launch {
//        viewModel.flow.collectLatest {
//                userProto ->
//            Log.d(TAG, "${userProto.accessToken}, ${userProto.refreshToken}")
//        }
//    }



    }


    private fun login() {
        val emailText = binding.emailEditText?.text.toString()
        val passwordText = binding.passwordEditText?.text.toString()
        var accessToken = ""
        var refreshToken = ""
        Log.d(TAG, "${passwordText.matches(Pattern.passwordRegex)}")
        Log.d(TAG, "${emailText.matches(Pattern.email)}")
        if(emailText.matches(Pattern.email)){
            binding.emailLayout?.error = null
        }
        else{
            binding.emailLayout?.error = "등록되지 않은 이메일입니다."
        }
        if(passwordText.matches(Pattern.passwordRegex)){
            binding.passwordLayout?.error = null
        }else{
            binding.passwordLayout?.error = "비밀번호를 잘못 입력하셨습니다."
        }

//        emailText.matches(Pattern.email) && passwordText.matches(Pattern.passwordRegex)
        if(emailText.matches(Pattern.email) && passwordText.matches(Pattern.passwordRegex)) {
//            moveScreen()
            lifecycleScope.launch(Dispatchers.IO) {
                kotlin.runCatching {
                    RetrofitBuilder.getLoginService().postLogin(LoginRequest(emailText, passwordText))
                        .let { result ->
                            Log.d(TAG, "${result.data}")
                            accessToken = result.data!!.accessToken
                            refreshToken = result.data.refreshToken
                        }
                }.onSuccess {
                    lifecycleScope.launch(Dispatchers.Main) {
                        Log.d(TAG, "성공 $it")
                        Toast.makeText(requireContext(), "성공.", Toast.LENGTH_SHORT).show()
                        saveToken(accessToken,refreshToken)
                        moveScreen()
                    }
                }.onFailure { throwable ->
                    Log.e(TAG, "$throwable")
                    lifecycleScope.launch(Dispatchers.Main) {
                        throwable.message
                        Toast.makeText(requireContext(), "유저정보가 일치하지 않습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

//    사용했던 코드
//    lifecycleScope.launch {
//        viewModel.flow.collectLatest {
//                userProto ->
//            Log.d(TAG, "${userProto.accessToken}, ${userProto.refreshToken}")
//        }
//    }

    private fun singeUp() {
        findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
    }

    private fun changeVisiblePassword(){
        var lastIndex : Int = 0
        if (binding.passwordEditText?.text?.lastIndex != null){
            lastIndex = binding.passwordEditText?.text?.lastIndex!!+1
        }

        isVisible = if(isVisible){
            binding.passwordLayout?.setEndIconDrawable(R.drawable.ic_visibility)
            binding.passwordEditText?.inputType = 0x00000081
            binding.passwordEditText?.setSelection(lastIndex)
            false
        } else{

            binding.passwordLayout?.setEndIconDrawable(R.drawable.ic_visibility_off)
            binding.passwordEditText?.inputType = 0x00000091
            binding.passwordEditText?.setSelection(lastIndex)
            true
        }
    }
    private fun moveScreen(){
        startActivity(Intent(requireContext(), HomeActivity::class.java))
        requireActivity().finish()
    }

    private fun saveToken(accessToken : String, refreshToken:String){
        CoroutineScope(Dispatchers.IO).launch {
            BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()?.insertMember(
                TokenEntity(
                    id = 1,
                    accessToken = accessToken,
                    email = binding.emailEditText?.text.toString(),
                    refreshToken = refreshToken
                )
            )
            viewModel.clearUserData()
            viewModel.setUserData(accessToken = accessToken, refreshToken = refreshToken)
        }
    }

    @Override
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}