package kr.pandadong2024.babya.home.profile.profileviewmodle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel() : ViewModel() {
    val id = MutableLiveData<Int>().apply { value = -1 }
}