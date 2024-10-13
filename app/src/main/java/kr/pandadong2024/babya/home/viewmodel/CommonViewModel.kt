package kr.pandadong2024.babya.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CommonViewModel : ViewModel() {
    private val _toastMessage = MutableLiveData<String>().apply { value = "" }
    val toastMessage : LiveData<String> = _toastMessage

    fun setToastMessage(message : String){
        _toastMessage.value = message
    }
}