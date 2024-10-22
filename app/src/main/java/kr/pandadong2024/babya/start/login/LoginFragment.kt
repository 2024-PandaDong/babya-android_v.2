package kr.pandadong2024.babya.start.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.HomeActivity
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentLoginBinding
import kr.pandadong2024.babya.home.viewmodel.CommonViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.local.TokenEntity
import kr.pandadong2024.babya.server.remote.request.LoginRequest
import kr.pandadong2024.babya.util.Pattern
import retrofit2.HttpException

private const val DATA_STORE_FILE_NAME = "user.pb"


class LoginFragment : Fragment() {
    private val commonViewModel: CommonViewModel by activityViewModels<CommonViewModel>()
    private var bottomSheetDialog: LoginBottomSheet? = null
    private lateinit var token: String
    private lateinit var tokenDao: TokenDAO

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

//        loginViewModel = ViewModelProvider(
//            requireActivity(),
//            ProtoViewModelFactory(UserRepository(requireContext().test))
//        )[LoginViewModel::class.java]

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

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
                        saveToken(
                            accessToken = accessToken,
                            refreshToken = refreshToken,
                            emailText = emailText
                        )
                        bottomSheetDialog?.dismiss()
                        moveScreen()
                    }
                }.onFailure { throwable ->
                    throwable.message
                    withContext(Dispatchers.Main) {
                        if (throwable is HttpException) {
                            Log.d("in", " code : ${throwable.code()}")
                            if (throwable.code() == 500) {
                                commonViewModel.setToastMessage("인터넷이 연결되어있는지 확인해 주십시오")
                            } else if (throwable.code() == 401) {
                                Log.d("in", " in 401")
                                commonViewModel.setToastMessage("유저정보가 일치하지 않습니다")
                            } else if (throwable.code() == 404) {
                                Log.d("in", " in 404")
                                commonViewModel.setToastMessage("유저정보가 존재하지 않습니다")
                            }
                        }
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
        }
    }

    @Override
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}