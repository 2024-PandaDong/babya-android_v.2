package kr.pandadong2024.babya.home.diary.diaryviewmodle

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData

class DiaryViewModel() : ViewModel() {
    val id : LiveData<Int> = MutableLiveData<Int>(0)

}