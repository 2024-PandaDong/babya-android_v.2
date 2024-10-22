package kr.pandadong2024.babya.home.quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentQuizResultBinding
import kr.pandadong2024.babya.server.remote.responses.quiz.QuizResponses
import kr.pandadong2024.babya.util.BottomControllable

class QuizResultFragment : Fragment() {

    private var _binding: FragmentQuizResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel : QuizViewModel by activityViewModels<QuizViewModel>()
    private var quiz : QuizResponses  = QuizResponses()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentQuizResultBinding.inflate(inflater, container, false)
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        quiz = viewModel.quizData.value ?: QuizResponses()
        isRight()
        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_quizResultFragment_to_mainFragment)
        }
        return binding.root
    }

    private fun isRight(){
        if (viewModel.answer.value == quiz.answer){
            binding.answerText.text = "정답입니다!"
            binding.answerText.setTextColor(requireContext().getColor(R.color.statusPositive))
        }
        else{
            binding.answerText.text = "오답입니다!"
            binding.answerText.setTextColor(requireContext().getColor(R.color.statusDestructive))
        }
        binding.quizText.text = "Q.${quiz.title}"
        binding.descriptionButton.text = quiz.quizCn
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }
}