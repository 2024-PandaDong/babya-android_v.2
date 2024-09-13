package kr.pandadong2024.babya.home.quiz

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.pandadong2024.babya.server.remote.responses.quiz.QuizResponses

class QuizViewModel : ViewModel() {
    var quizData = MutableLiveData<QuizResponses>().apply { value = QuizResponses() }
    val answer = MutableLiveData<String> ().apply { value="N" }
}