package kr.pandadong2024.babya.start.start

import android.content.Intent
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.HomeActivity
import kr.pandadong2024.babya.MyApplication.Companion.prefs
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentStartBinding
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.home.find_company.find_company_viewModel.FindCompanyViewModel
import kr.pandadong2024.babya.home.main.MainViewModel
import kr.pandadong2024.babya.home.policy.viewmdole.PolicyViewModel
import kr.pandadong2024.babya.home.profile.profileviewmodle.ProfileViewModel
import kr.pandadong2024.babya.home.quiz.QuizViewModel
import kr.pandadong2024.babya.server.local.BabyaDB
import java.text.SimpleDateFormat
import java.util.Locale

class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    private val quizViewModel: QuizViewModel by activityViewModels<QuizViewModel>()
    private val findCompanyViewModel by activityViewModels<FindCompanyViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val policyViewModel by activityViewModels<PolicyViewModel>()
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private val diaryViewModel by activityViewModels<DiaryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            val now =  System.currentTimeMillis()
            val today = SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN).format(now)
            if (today != prefs.lastEditTime){
                prefs.lastEditTime = today
                prefs.completeQuiz = false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStartBinding.inflate(inflater, container, false)


        lifecycleScope.launch(Dispatchers.IO) {
            val accessToken =
                BabyaDB.getInstance(requireContext())?.tokenDao()?.getMembers()?.accessToken

            delay(1500)
            withContext(Dispatchers.Main) {
                launch {
                    Log.d("StartFragment", "accessToken : $accessToken")
                    if (!accessToken.isNullOrEmpty()) {
                        profileViewModel.setAccessToken(token = accessToken)
                        runBlocking {
                            profileViewModel.getUserData()
                        }

                        startActivity(Intent(requireContext(), HomeActivity::class.java))
                        requireActivity().finish()
                    } else {
                        findNavController().navigate(R.id.action_startFragment_to_loginFragment)
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}