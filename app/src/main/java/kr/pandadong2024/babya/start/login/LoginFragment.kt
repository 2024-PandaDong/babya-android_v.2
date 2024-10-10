package kr.pandadong2024.babya.start.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.babya_android.datastore.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.pandadong2024.babya.HomeActivity
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentLoginBinding
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.local.TokenEntity
import kr.pandadong2024.babya.server.local.proto.UserRepository
import kr.pandadong2024.babya.server.local.proto.UserSerializer
import kr.pandadong2024.babya.server.remote.request.LoginRequest
import kr.pandadong2024.babya.util.Pattern

private const val DATA_STORE_FILE_NAME = "user.pb"

private val Context.test: DataStore<User> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = UserSerializer
)

private lateinit var viewModel: LoginViewModel
private var bottomSheetDialog: LoginBottomSheet? = null

class LoginFragment : Fragment() {
    private lateinit var token: String
    private lateinit var tokenDao: TokenDAO

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        viewModel = ViewModelProvider(
            requireActivity(),
            ProtoViewModelFactory(UserRepository(requireContext().test))
        )[LoginViewModel::class.java]

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
//        binding.passwordLayout?.setEndIconOnClickListener {
//            changeVisiblePassword()
//        }
//        binding.singeUpTextButton?.setOnClickListener{
//            singeUp()
//        }
//        binding.loginButton?.setOnClickListener{
//            login()
//        }


        binding.registBtn?.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signup1)
        }

        binding.isLoginText?.setOnClickListener {
            bottomSheetDialog =
                LoginBottomSheet(login = { emailText, passwordText ->
                    login(
                        emailText = emailText,
                        passwordText = passwordText
                    )
                }, moveSignUp = {
                    findNavController().navigate(R.id.action_loginFragment_to_signup1)
                })
            bottomSheetDialog?.show(
                requireActivity().supportFragmentManager,
                bottomSheetDialog?.tag
            )
        }

        return binding.root
    }

    private fun oAuthLogin() {
        Toast.makeText(requireContext(), "제작중...", Toast.LENGTH_SHORT).show()
        //TODO oAuth로그인 하기
        runBlocking(Dispatchers.IO) {
            val dao = BabyaDB.getInstance(requireContext())
            if (dao != null) {
                tokenDao = dao.tokenDao()
            }
            token = tokenDao.getMembers().accessToken
        }
    }


    private fun login(emailText: String, passwordText: String) {
        var accessToken = ""
        var refreshToken = ""

        if (emailText.matches(Pattern.email) && passwordText.matches(Pattern.passwordRegex)) {
            lifecycleScope.launch(Dispatchers.IO) {
                kotlin.runCatching {
                    RetrofitBuilder.getLoginService()
                        .postLogin(LoginRequest(emailText, passwordText))

                }.onSuccess { result ->
                    accessToken = result.data?.accessToken ?: ""
                    refreshToken = result.data?.refreshToken ?: ""
                    lifecycleScope.launch(Dispatchers.Main) {
                        if (result.status == 200) {
                            saveToken(
                                accessToken = accessToken,
                                refreshToken = refreshToken,
                                emailText = emailText
                            )
                            bottomSheetDialog?.dismiss()
                            moveScreen()
                        } else {
                            Toast.makeText(requireContext(), "유저정보가 일치하지 않습니다.", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }
                }.onFailure { throwable ->
                    throwable.message
                    launch(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "유저정보가 일치하지 않습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun moveScreen() {
        startActivity(Intent(requireContext(), HomeActivity::class.java))
        requireActivity().finish()
    }

    private fun saveToken(accessToken: String, refreshToken: String, emailText: String) {
        CoroutineScope(Dispatchers.IO).launch {
            BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()?.insertMember(
                TokenEntity(
                    id = 1,
                    accessToken = accessToken,
                    email = emailText,
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