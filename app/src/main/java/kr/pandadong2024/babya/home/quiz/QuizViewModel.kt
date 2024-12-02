package kr.pandadong2024.babya.home.quiz

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.remote.responses.quiz.QuizResponses
import retrofit2.HttpException

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private var _quizData = MutableLiveData<QuizResponses>().apply { value = QuizResponses() }
    val quizData: LiveData<QuizResponses> = _quizData

    private var _message = MutableLiveData<String>().apply { value = "" }
    val message: LiveData<String> = _message

    private var _accessToken = MutableLiveData<String>().apply { value = "" }
    val accessToken: LiveData<String> = _accessToken

    val answer = MutableLiveData<String>().apply { value = "N" }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            setAccessToken(
                BabyaDB.getInstance(application)?.tokenDao()
                    ?.getMembers()?.accessToken.toString()
            )
        }
    }

    fun setAccessToken(accessToken: String) = viewModelScope.launch(Dispatchers.Main) {
        _accessToken.value = accessToken
    }


    fun getQuiz() = viewModelScope.launch() {
        Log.d("Test", "getQuiz _accessToken:${_accessToken.value}")
        if (_accessToken.value.isNullOrEmpty()){
            return@launch
        }
        kotlin.runCatching {
            RetrofitBuilder.getQuizService().getQuiz(
                accessToken = "Bearer ${_accessToken.value}"
            )
        }.onSuccess { result ->
            _quizData.value = result.data ?: QuizResponses()
        }.onFailure { result ->
            result.printStackTrace()
            if (result is HttpException) {
                Log.d("Test", "it : ${result.response()}")
                if (result.code() == 500) {
                    _message.value = "인터넷이 연결되어있는지 확인해 주십시오"
                } else {
                    _message.value = "데이터를 불러오는 도중 문제가 발생했습니다. CODE : ${result.code()}"
                }
            }
        }
    }
}