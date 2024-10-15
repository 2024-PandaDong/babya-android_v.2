package kr.pandadong2024.babya.home.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.MyApplication.Companion.prefs
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentQuizBinding
import kr.pandadong2024.babya.home.viewmodel.CommonViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.remote.responses.quiz.QuizResponses
import kr.pandadong2024.babya.util.BottomControllable
import retrofit2.HttpException

class QuizFragment : Fragment() {
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private var quiz: QuizResponses = QuizResponses()
    private val commonViewModel by activityViewModels<CommonViewModel>()
    private val viewModel: QuizViewModel by activityViewModels<QuizViewModel>()
    private lateinit var tokenDao: TokenDAO


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        if(prefs.skipQuiz || prefs.completeQuiz){
            moveOtherView(true)
        }else{
            tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!
            getQuiz()
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

    private fun moveOtherView(isSkip: Boolean) {
        prefs.completeQuiz = true
        if (isSkip) {
            findNavController().navigate(R.id.action_quizFragment_to_mainFragment)
        } else {
            viewModel.quizData.value = quiz
            findNavController().navigate(R.id.action_quizFragment_to_quizResultFragment)
        }

    }

    private fun getQuiz() {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getQuizService().getQuiz(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}"
                )
            }.onSuccess { result ->
                if (result.status == 200) {
                    quiz = result.data ?: QuizResponses()

                    launch(Dispatchers.Main) {
                        binding.quizText.text = quiz.title
                    }
                } else {
                    commonViewModel.setToastMessage("데이터를 불러오는 도중 문제가 발생했습니다. CODE : ${result.status}")
                }
            }.onFailure { result ->
                result.printStackTrace()
                if (result is HttpException) {
                    val errorBody = result.response()?.raw()?.request
                }

                commonViewModel.setToastMessage("인터넷이 연결되어있는지 확인해 주십시오")

                launch(Dispatchers.Main) {
                    binding.quizText.text = quiz.title
                }

            }
        }

    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }
}