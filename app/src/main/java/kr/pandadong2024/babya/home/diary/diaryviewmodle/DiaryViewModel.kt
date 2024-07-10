package kr.pandadong2024.babya.home.diary.diaryviewmodle

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData

class DiaryViewModel() : ViewModel() {
    var id = MutableLiveData<Int>().apply { value = -1 }
    var isPublic = MutableLiveData<Boolean>().apply { value = false }

}