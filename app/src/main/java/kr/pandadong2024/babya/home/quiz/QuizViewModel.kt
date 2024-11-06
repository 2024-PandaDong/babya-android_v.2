package kr.pandadong2024.babya.home.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.remote.responses.quiz.QuizResponses
import retrofit2.HttpException

class QuizViewModel : ViewModel() {
    private var _quizData = MutableLiveData<QuizResponses>().apply { value = QuizResponses() }
    val quizData : LiveData<QuizResponses> = _quizData

    private var _message = MutableLiveData<String>().apply { value = "" }
    val message : LiveData<String> = _message

    val answer = MutableLiveData<String> ().apply { value="N" }





    fun getQuiz(token : String) = viewModelScope.launch(Dispatchers.IO){
        kotlin.runCatching {
            RetrofitBuilder.getQuizService().getQuiz(
                accessToken = "Bearer $token"
            )
        }.onSuccess { result ->
            launch(Dispatchers.Main) {
                _quizData.value = result.data ?: QuizResponses()
            }
        }.onFailure { result ->
            result.printStackTrace()
            if (result is HttpException) {
                launch(Dispatchers.Main) {
                    if (result.code() == 500) {
                        _message.value = "인터넷이 연결되어있는지 확인해 주십시오"
                    } else {
                        _message.value = "데이터를 불러오는 도중 문제가 발생했습니다. CODE : ${result.code()}"
                    }
                }
            }
        }
    }
}