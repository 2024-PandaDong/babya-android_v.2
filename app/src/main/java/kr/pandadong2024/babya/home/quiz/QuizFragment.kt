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
import kr.pandadong2024.babya.server.local.entity.UserEntity
import kr.pandadong2024.babya.util.BottomControllable
import kr.pandadong2024.babya.util.shortToast

class QuizFragment : Fragment() {
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by activityViewModels<QuizViewModel>()
    private val findCompanyViewModel by activityViewModels<FindCompanyViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val policyViewModel by activityViewModels<PolicyViewModel>()
    private val commonViewModel by activityViewModels<CommonViewModel>()
    private val profileViewModel by viewModels<ProfileViewModel>()
    private val diaryViewModel by activityViewModels<DiaryViewModel>()
    private lateinit var tokenDao: TokenDAO
    private lateinit var accessToken: String
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
                    accessToken = BabyaDB.getInstance(requireContext())?.tokenDao()
                        ?.getMembers()?.accessToken.toString()
                }
                launch(Dispatchers.IO) {
                    val userLocalCode =
                        BabyaDB.getInstance(requireContext())?.userDao()?.getMembers()?.localCode
                            ?: "0000000000"

                    launch(Dispatchers.Main) {
                        policyViewModel.setTagList(userLocalCode.toInt())
                        policyViewModel.getPolicyList(userLocalCode)
                    }
                }
                withContext(Dispatchers.Main) {
                    launch {
                        findCompanyViewModel.setAccessToken(accessToken)
                    }
                    launch {
                        mainViewModel.setAccessToken(accessToken)
                    }
                    launch {
                        profileViewModel.setAccessToken(accessToken)
                    }
                    launch {
                        diaryViewModel.setAccessToken(accessToken)
                    }
                    launch {
                        commonViewModel.setAccessToken(accessToken)
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
        viewModel.accessToken.observe(viewLifecycleOwner){
            viewModel.getQuiz()
        }

        profileViewModel.userData.observe(viewLifecycleOwner) {
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
            if (it.length >= 3) {
                userEntity.localCode = it
                policyViewModel.setTagList(it.toInt())
                policyViewModel.getPolicyList(it)
                profileViewModel.getUserData()
            }
        }
        viewModel.message.observe(viewLifecycleOwner) {
            if (it != "") {
                requireContext().shortToast(it)
            }
        }
        viewModel.quizData.observe(viewLifecycleOwner) {
            binding.quizText.text = "Q.${it.title}"
        }
        if (prefs.skipQuiz || prefs.completeQuiz) {
            moveOtherView(true)
        } else {
            tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!
        }

        binding.positiveButton.setOnClickListener {
            moveOtherView(false)
            viewModel.answer.value = "Y"
        }
        binding.negativeButton.setOnClickListener {
            moveOtherView(false)
            viewModel.answer.value = "N"
        }
        binding.skipText.setOnClickListener {
            moveOtherView(true)
        }
        return binding.root
    }


    private fun saveUserData() {
        lifecycleScope.launch(Dispatchers.IO) {
            BabyaDB.getInstance(requireContext())?.userDao()?.insertMember(userEntity)
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