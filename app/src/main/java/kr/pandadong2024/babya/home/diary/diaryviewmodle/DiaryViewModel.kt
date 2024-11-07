package kr.pandadong2024.babya.home.diary.diaryviewmodle

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
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.SubCommentResponses
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryDataResponses
import retrofit2.HttpException

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    var diaryId = MutableLiveData<Int>().apply { value = -1 }
    var isPublic = MutableLiveData<Boolean>().apply { value = true }  // true : 공개 | 비공개 : false
    val subCommentList =
        MutableLiveData<List<SubCommentResponses>>().apply { value = listOf<SubCommentResponses>() }
    val isOpenSearchView = MutableLiveData<Boolean>().apply { value = false }
    val diarySearchKeyWord = MutableLiveData<String>().apply { value = "" }
    val editDiaryData = MutableLiveData<DiaryDataResponses?>().apply { value = null }
    private var _accessToken = MutableLiveData<String>().apply { value = "" }
    var accessToken: LiveData<String> = _accessToken
    private val _toastMessage = MutableLiveData<String>().apply { value = "" }
    val toastMessage: LiveData<String> = _toastMessage

    private var _diaryList = MutableLiveData<List<DiaryDataResponses>>(emptyList())
    var diaryList: LiveData<List<DiaryDataResponses>> = _diaryList

    fun setToastMessage(message: String) {
        _toastMessage.value = message
    }

    val tokenDao = BabyaDB.getInstance(application.applicationContext)?.tokenDao()!!

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

    fun initEditDate() {
        editDiaryData.value = null
    }

    fun getDiaryData(
        page: Int,
        size: Int,
        keyword: String = "",
    ) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("test",  "init")
        kotlin.runCatching {
            var diaryData: BaseResponse<List<DiaryDataResponses>>? = null
            val myEmail = tokenDao.getMembers().email
            runCatching {
                when (isPublic.value) {
                    true -> {
                        RetrofitBuilder.getDiaryService().getDiaryList(
                            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                            page = page,
                            size = size,
                            keyword = keyword
                        )

                    }
                    false -> {
                        RetrofitBuilder.getDiaryService().getMyDiaryList(
                            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                            page = page,
                            size = size,
                            keyword = keyword
                        )
                    }
                    null -> {
                        RetrofitBuilder.getDiaryService().getDiaryList(
                            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                            page = page,
                            size = size,
                            keyword = keyword
                        )
                    }
                }
            }.onSuccess {
                viewModelScope.launch(Dispatchers.Main) {
                    _diaryList.value = it.data ?: listOf()
                }
            }.onFailure {
                viewModelScope.launch(Dispatchers.Main) {
                    _diaryList.value = listOf()
                    if (it is HttpException) {
                        _toastMessage.value = when(it.code()){
                            500 ->  "서버 에러"
                            else -> ""
                        }
                    }
                }
            }
        }
    }

    fun reportDiary(diaryId: Int, action: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            RetrofitBuilder.getDiaryService().reportDiary(
                accessToken = "Bearer ${_accessToken.value}",
                id = diaryId
            )
        }.onSuccess {
            _toastMessage.value = "정상적으로 신고가 접수되었습니다."
            action()
        }.onFailure { result ->
            if (result is HttpException) {
                if (result.code() == 500) {
                    _toastMessage.value = "서버에서 문제가 발생했습니다."
                } else {
                    _toastMessage.value = "신고를 하는도중 문제가 발생했습니다. CODE : ${result.code()}"
                }
            }

        }
    }

}
