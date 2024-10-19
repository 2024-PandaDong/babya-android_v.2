package kr.pandadong2024.babya.home.diary.diaryviewmodle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.remote.responses.SubCommentResponses
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryDataResponses
import retrofit2.HttpException

class DiaryViewModel() : ViewModel() {
    var diaryId = MutableLiveData<Int>().apply { value = -1 }
    var isPublic = MutableLiveData<Boolean>().apply { value = false }
    val subCommentList = MutableLiveData<List<SubCommentResponses>>().apply { value = listOf<SubCommentResponses>()}
    val isOpenSearchView = MutableLiveData<Boolean>().apply { value = false }
    val diarySearchKeyWord = MutableLiveData<String>().apply { value = "" }
    val editDiaryData = MutableLiveData<DiaryDataResponses?>().apply { value = null }
    private var _accessToken = MutableLiveData<String>().apply { value = "" }
    var accessToken: LiveData<String> = _accessToken
    private val _toastMessage = MutableLiveData<String>().apply { value = "" }
    val toastMessage: LiveData<String> = _toastMessage

    fun setToastMessage(message: String) {
        _toastMessage.value = message
    }



    fun setDiarySearchKeyWord(inputKeyWord: String) {
        diarySearchKeyWord.value = inputKeyWord
    }

    fun changeOpenSearchView() {
        isOpenSearchView.value = isOpenSearchView.value?.not() ?: false
    }

    fun initKeyword() {
        diarySearchKeyWord.value = ""
        isOpenSearchView.value = false
    }

    fun  initEditDate(){
        editDiaryData.value = null
    }

    fun reportDiary(diaryId : Int, action : ()->Unit) = viewModelScope.launch(Dispatchers.IO){
        runCatching {
            RetrofitBuilder.getDiaryService().reportDiary(
                accessToken = "Bearer ${_accessToken.value}",
                id =  diaryId
            )
        }.onSuccess {
            _toastMessage.value = "정상적으로 신고가 접수되었습니다."
            action()
        }.onFailure { result ->
            if (result is HttpException){
                if(result.code() == 500){
                    _toastMessage.value = "서버에서 문제가 발생했습니다."
                }else{
                    _toastMessage.value = "신고를 하는도중 문제가 발생했습니다. CODE : ${result.code()}"
                }
            }

        }
    }

}
