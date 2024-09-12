package kr.pandadong2024.babya.home.diary.diaryviewmodle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.pandadong2024.babya.server.remote.responses.SubCommentResponses

class DiaryViewModel() : ViewModel() {
    var diaryId = MutableLiveData<Int>().apply { value = -1 }
    var isPublic = MutableLiveData<Boolean>().apply { value = false }
    val subCommentList = MutableLiveData<List<SubCommentResponses>>().apply { value = listOf<SubCommentResponses>()}

    fun initViewModel(){
        subCommentList.value = listOf()
    }
}