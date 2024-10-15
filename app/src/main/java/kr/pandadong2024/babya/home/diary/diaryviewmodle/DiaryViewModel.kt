package kr.pandadong2024.babya.home.diary.diaryviewmodle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.pandadong2024.babya.server.remote.responses.SubCommentResponses
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryDataResponses

class DiaryViewModel() : ViewModel() {
    var diaryId = MutableLiveData<Int>().apply { value = -1 }
    var isPublic = MutableLiveData<Boolean>().apply { value = false }
    val subCommentList = MutableLiveData<List<SubCommentResponses>>().apply { value = listOf<SubCommentResponses>()}
    val isOpenSearchView = MutableLiveData<Boolean>().apply { value = false }
    val diarySearchKeyWord = MutableLiveData<String>().apply { value = "" }
    val editDiaryData = MutableLiveData<DiaryDataResponses?>().apply { value = null }

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
}
