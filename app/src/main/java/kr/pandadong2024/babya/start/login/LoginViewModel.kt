package kr.pandadong2024.babya.start.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.pandadong2024.babya.server.local.proto.UserRepository
import kotlinx.coroutines.launch


class LoginViewModel(private val repository: UserRepository): ViewModel() {
    fun setUserData(accessToken : String, refreshToken : String){
        viewModelScope.launch { repository.updateUserData(accessToken, refreshToken) }
    }

    fun clearUserData(){
        viewModelScope.launch { repository.clearUserData() }
    }
}