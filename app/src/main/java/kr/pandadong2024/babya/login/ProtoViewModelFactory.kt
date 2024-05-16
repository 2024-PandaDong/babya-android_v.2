package kr.pandadong2024.babya.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.pandadong2024.babya.proto.UserRepository

class ProtoViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}