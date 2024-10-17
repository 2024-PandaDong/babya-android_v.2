package kr.pandadong2024.babya.home.profile.profileviewmodle

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.home.policy.getMemberLocalCode
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.remote.responses.ProfileData
import retrofit2.HttpException

class ProfileViewModel() : ViewModel() {
    val id = MutableLiveData<Int>().apply { value = -1 }

    private var _accessToken = MutableLiveData<String>().apply { value = "" }
    var accessToken: LiveData<String> = _accessToken

    private var _userData = MutableLiveData<ProfileData>().apply { value = ProfileData() }
    val userData: LiveData<ProfileData> = _userData

    private var _userLocalCode = MutableLiveData<String>().apply { value = "" }
    val userLocalCode: LiveData<String> = _userLocalCode

    private var _toastMessage = MutableLiveData<String>().apply { value = "" }
    val toastMessage: LiveData<String> = _toastMessage


    fun getUserData() = viewModelScope.launch(Dispatchers.IO) {
        kotlin.runCatching {
            RetrofitBuilder.getProfileService().getProfile(
                accessToken = "Bearer ${_accessToken.value}",
                email = "my"
            )
        }.onSuccess { result ->
            kotlin.runCatching {
                RetrofitBuilder.getProfileService().getProfile(
                    accessToken = "Bearer ${_accessToken.value}",
                    email = "my"
                )
            }.onSuccess {
                _userData.postValue(result.data)
            }
        }.onFailure { result ->
            if (result is HttpException) {
                if (result.code() == 404) {
                    _toastMessage.value = "데이터를 불러오지 못했어요 CODE : ${result.code()}"
                }
            } else {
                _toastMessage.value = "서버에서 문제가 발생했어요"
            }
            result.printStackTrace()
        }
    }

    fun getUserLocalCode() = viewModelScope.launch(Dispatchers.IO) {
        Log.d("getUserLocalCode", "code : Bearer $_accessToken")
        kotlin.runCatching {
            RetrofitBuilder.getProfileService().getLocalCode("Bearer ${_accessToken.value}")
        }.onSuccess { result ->
            val response = result.data
            _userLocalCode.postValue(
                if (response?.length == 2) {
                    getMemberLocalCode(response).toString()
                } else {
                    response.toString()
                }
            )
            Log.d("getUserLocalCode", "code : ${_userLocalCode.value}")
        }.onFailure { result ->
            Log.d("getUserLocalCode", "end40000409399399403949")
            if (result is HttpException) {
                if (result.code() == 404) {
                    _toastMessage.value = "데이터를 불러오지 못했어요 CODE : ${result.code()}"
                }
            } else {
                _toastMessage.value = "서버에서 문제가 발생했어요"
            }
            result.printStackTrace()
        }
    }

    fun setAccessToken(token: String) {
        _accessToken.value = token
    }

    fun deleteUser(onSuccess: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        kotlin.runCatching {
            // 서버에 탈퇴 요청
            RetrofitBuilder.getProfileService().deleteMember(
                accessToken = "Bearer ${_accessToken.value}"
            )
        }.onSuccess {
            _toastMessage.value = "정상적으로 회원탈퇴가 완료되었습니다."
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }.onFailure { result ->
            result.printStackTrace()
            // 실패 시 UI 스레드에서 에러 메시지 표시
            _toastMessage.value = "서버에서 문제가 발생했어요"
        }
    }
}
