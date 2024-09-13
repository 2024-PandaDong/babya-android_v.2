package kr.pandadong2024.babya.home.quiz

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentMainBinding
import kr.pandadong2024.babya.databinding.FragmentQuizBinding
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.remote.responses.quiz.QuizResponses
import kr.pandadong2024.babya.util.BottomControllable
import retrofit2.HttpException

class QuizFragment : Fragment() {
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private var quiz : QuizResponses = QuizResponses()
    private val viewModel : QuizViewModel by activityViewModels<QuizViewModel>()
    val TAG = "QuizFragment"
    private lateinit var tokenDao: TokenDAO


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!
        getQuiz()
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
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

    private  fun moveOtherView(isSkip : Boolean){
        if (isSkip){
            findNavController().navigate(R.id.action_quizFragment_to_mainFragment)
        }else{
            viewModel.quizData.value = quiz
            findNavController().navigate(R.id.action_quizFragment_to_quizResultFragment)
        }

    }

    private fun getQuiz(){
        lifecycleScope.launch (Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getQuizService().getQuiz(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}"
                )
            }.onSuccess { result ->
                if (result.data != null){
                    quiz = result.data
                }
                else{
                    quiz = QuizResponses()
                }

                launch (Dispatchers.Main){
                    binding.quizText.text = quiz.title
                }
            }.onFailure {result ->
                result.printStackTrace()
                Log.e(TAG, "resutl : ${result.message}")
                if (result is HttpException) {
                    val errorBody = result.response()?.raw()?.request
                    Log.e(TAG, "Error body: $errorBody")
                }
                launch (Dispatchers.Main){
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