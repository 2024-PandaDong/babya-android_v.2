package kr.pandadong2024.babya.start.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.server.local.DAO.TokenDAO
import kr.pandadong2024.babya.server.local.entity.TokenEntity
import kr.pandadong2024.babya.start.signup.BirthName
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val tokenDAO: TokenDAO
): ViewModel() {

    fun getToken(): TokenEntity? {
        return tokenDAO.getMembers()
    }

    fun insertToken(tokenEntity: TokenEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            tokenDAO.insertMember(tokenEntity)
        }
    }

    val email = MutableLiveData<String>().apply { value = "" }
    val pw = MutableLiveData<String>().apply { value = "" }
    val nickName = MutableLiveData<String>().apply { value = "" }
    val marriedDt = MutableLiveData<String>().apply { value = "" }
    val pregnancyDt = MutableLiveData<String>().apply { value = "" }
    val birthDt = MutableLiveData<String>().apply { value = "" }
    val locationCode = MutableLiveData<String>().apply { value = "" }
    val pushToken = MutableLiveData<String>().apply { value = "" }
    var birthNameList =
        MutableLiveData<MutableList<BirthName>>().apply { value = mutableListOf<BirthName>() }
    var childrenNameList =
        MutableLiveData<MutableList<BirthName>>().apply { value = mutableListOf<BirthName>() }
}