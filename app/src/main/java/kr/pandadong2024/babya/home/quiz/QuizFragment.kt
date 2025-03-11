package kr.pandadong2024.babya.home.quiz

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.MyApplication.Companion.prefs
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentQuizBinding
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.home.find_company.find_company_viewModel.FindCompanyViewModel
import kr.pandadong2024.babya.home.main.MainViewModel
import kr.pandadong2024.babya.home.policy.viewmdole.PolicyViewModel
import kr.pandadong2024.babya.home.profile.profileviewmodle.ProfileViewModel
import kr.pandadong2024.babya.home.viewmodel.CommonViewModel
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.DAO.TokenDAO
import kr.pandadong2024.babya.server.local.DatabaseModule
import kr.pandadong2024.babya.server.local.entity.UserEntity
import kr.pandadong2024.babya.util.BottomControllable
import kr.pandadong2024.babya.util.shortToast

@AndroidEntryPoint
class QuizFragment : Fragment() {
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val quizViewModel: QuizViewModel by viewModels()
    private val findCompanyViewModel : FindCompanyViewModel by viewModels()
    private val mainViewModel : MainViewModel by viewModels()
    private val policyViewModel : PolicyViewModel by viewModels()
    private val commonViewModel : CommonViewModel by viewModels()
    private val profileViewModel : ProfileViewModel by viewModels()
    private val diaryViewModel : DiaryViewModel by viewModels()
    private lateinit var token: String
    private lateinit var tokenDao: TokenDAO
    private var userEntity: UserEntity = UserEntity(
        email = "",
        nickname = "",
        dDay = "",
        birthDt = "",
        marriedYears = "",
        children = "",
        localCode = "",
        profileImg = "",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 메인 스레드가 아닌 IO 스레드에서 데이터베이스에 접근하도록 수정
        runBlocking {
            lifecycleScope.launch(Dispatchers.IO) {
                launch {
                    token = quizViewModel.getToken()?.accessToken.toString()
                }
                launch(Dispatchers.IO) {
                    val userLocalCode =
                        DatabaseModule.provideDatabase(requireContext())?.userDao()?.getMembers()?.localCode
                            ?: "0000000000"

                    launch(Dispatchers.Main) {
                        policyViewModel.setTagList(userLocalCode.toInt())
                        policyViewModel.getPolicyList(userLocalCode)
                    }
                }
                withContext(Dispatchers.Main) {
                    launch {
                        findCompanyViewModel.setAccessToken(token)
                    }
                    launch {
                        mainViewModel.setAccessToken(token)
                    }
                    launch {
                        profileViewModel.setAccessToken(token)
                    }
                    launch {
                        diaryViewModel.setAccessToken(token)
                    }
                    launch {
                        commonViewModel.setAccessToken(token)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
//        profileViewModel.getUserLocalCode()
        quizViewModel.accessToken.observe(viewLifecycleOwner){
            quizViewModel.getQuiz()
        }
        profileViewModel.accessToken.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                Log.d("dbTest", "toekn data : $it")
//                profileViewModel.getUserLocalCode()
//                if (profileViewModel.getUserLocalCode()?.nickname?.isNotEmpty() == true){
//                    profileViewModel.getUserLocalCode()
//                }else{
//                    profileViewModel.getUserLocalCode()
//                }
            }
        }
        profileViewModel.userData.observe(viewLifecycleOwner) {
            Log.d("dbTest", "user data : $it")
            userEntity.email = userEntity.email
            userEntity.nickname = it.nickname
            userEntity.dDay = it.dDay
            userEntity.birthDt = it.birthDt
            userEntity.marriedYears = it.marriedYears
            userEntity.children = it.children.toString()
            userEntity.profileImg = it.profileImg
            saveUserData()
        }

        profileViewModel.userLocalCode.observe(viewLifecycleOwner) {
            Log.d("dbTest", "local data : $it")
            if (it.length >= 3) {
                userEntity.localCode = it
                policyViewModel.setTagList(it.toInt())
                policyViewModel.getPolicyList(it)
                profileViewModel.getUserData()
            }
        }
        quizViewModel.message.observe(viewLifecycleOwner) {
            if (it != "") {
                requireContext().shortToast(it)
            }
        }
        quizViewModel.quizData.observe(viewLifecycleOwner) {
            binding.quizText.text = "Q.${it.title}"
        }
        if (prefs.skipQuiz || prefs.completeQuiz) {
            moveOtherView(true)
        } else {
            tokenDao = DatabaseModule.provideDatabase(requireContext().applicationContext)?.tokenDao()!!
        }

        binding.positiveButton.setOnClickListener {
            moveOtherView(false)
            quizViewModel.answer.value = "Y"
        }
        binding.negativeButton.setOnClickListener {
            moveOtherView(false)
            quizViewModel.answer.value = "N"
        }
        binding.skipText.setOnClickListener {
            moveOtherView(true)
        }
        return binding.root
    }


    private fun saveUserData() {
        Log.d("dbTest", "save data : $userEntity")
        lifecycleScope.launch(Dispatchers.IO) {
            DatabaseModule.provideDatabase(requireContext())?.userDao()?.insertMember(userEntity)
        }
    }

    private fun moveOtherView(isSkip: Boolean) = lifecycleScope.launch {
        prefs.completeQuiz = true
        findNavController().navigate(
            if (isSkip) {
                R.id.action_quizFragment_to_mainFragment
            } else {
                R.id.action_quizFragment_to_quizResultFragment
            }
        )
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }
}