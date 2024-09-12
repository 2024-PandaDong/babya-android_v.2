package kr.pandadong2024.babya.start.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.pandadong2024.babya.start.signup.BirthName

class SignupViewModel : ViewModel() {
     val email = MutableLiveData<String>().apply{ value = ""}
     val pw = MutableLiveData<String>().apply{ value = ""}
     val nickName = MutableLiveData<String>().apply{ value = ""}
     val marriedDt = MutableLiveData<String>().apply{ value = ""}
     val pregnancyDt = MutableLiveData<String>().apply{ value = ""}
     val birthDt = MutableLiveData<String>().apply{ value = ""}
     val locationCode = MutableLiveData<String>().apply{ value = ""}
     val pushToken = MutableLiveData<String>().apply{ value = ""}
     var birthNameList = MutableLiveData<MutableList<BirthName>>().apply { value = mutableListOf<BirthName>() }
     var childrenNameList = MutableLiveData<MutableList<BirthName>>().apply { value = mutableListOf<BirthName>() }
}