package kr.pandadong2024.babya.home.profile.profileviewmodle

import android.util.Log
import android.util.TypedValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.home.policy.getMemberLocalCode
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.remote.request.UserEditRequest
import kr.pandadong2024.babya.server.remote.responses.ProfileData
import retrofit2.HttpException

class ProfileViewModel() : ViewModel() {
    val id = MutableLiveData<Int>().apply { value = -1 }

    private var _accessToken = MutableLiveData("")
    var accessToken: LiveData<String> = _accessToken

    private var _userData = MutableLiveData(ProfileData())
    val userData: LiveData<ProfileData> = _userData

    private var _userLocalCode = MutableLiveData("")
    val userLocalCode: LiveData<String> = _userLocalCode

    private var _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> = _toastMessage

    private var _editUserImageResult = MutableLiveData(false)
    val editUserImageResult: LiveData<Boolean> = _editUserImageResult

    private var _editUserResult = MutableLiveData(false)
    val editUserResult: LiveData<Boolean> = _editUserResult

    fun setAccessToken(token: String) {
        _accessToken.value = token
    }

    fun setEditUserImageResult(value: Boolean) {
        _editUserImageResult.value = value
    }

    fun setToastMessage(message: String) {
        _toastMessage.value = message
    }

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
            withContext(Dispatchers.Main) {
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
        }.onFailure { result ->
            withContext(Dispatchers.Main) {
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
    }

    fun deleteUser(onSuccess: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        kotlin.runCatching {
            // 서버에 탈퇴 요청
            RetrofitBuilder.getProfileService().deleteMember(
                accessToken = "Bearer ${_accessToken.value}"
            )
        }.onSuccess {
            withContext(Dispatchers.Main) {
                _toastMessage.value = "정상적으로 회원탈퇴가 완료되었습니다."
                onSuccess()
            }
        }.onFailure { result ->
            result.printStackTrace()
            // 실패 시 UI 스레드에서 에러 메시지 표시
            withContext(Dispatchers.Main) {
                _toastMessage.value = "서버에서 문제가 발생했습니다."
            }
        }
    }

    fun editUser(userData: UserEditRequest) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            RetrofitBuilder.getProfileService().editUserInfo(
                accessToken = "Bearer ${_accessToken.value}",
                body = userData
            )
        }.onSuccess {
            withContext(Dispatchers.Main) {
//                _toastMessage.value = "성공적으로 프로필 수정이 완료되었습니다."
                _editUserResult.value = true
            }
        }.onFailure { result ->
            if (result is HttpException) {
                Log.d("editUser", "msg : ${result.response()}")
                if (result.code() == 500) {
                    withContext(Dispatchers.Main) {
                        _toastMessage.value = "서버에서 문제가 발생했습니다."
                    }
                }
            }
        }
    }

    fun editUserImage(imageLink: String) =
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getProfileService().editUserImage(
                    accessToken = "Bearer ${_accessToken.value}",
                    imgUrl = imageLink
                )
            }.onSuccess {
                withContext(Dispatchers.Main) {
                    _editUserImageResult.value = true
                }
            }.onFailure { result ->
                if (result is HttpException) {
                    Log.d("editUser", "msg : ${result.response()}")
                    if (result.code() == 500) {
                        withContext(Dispatchers.Main) {
                            _toastMessage.value = "서버에서 문제가 발생했습니다."
                        }
                    }
                }
            }
        }

    fun initResult() {
        _editUserImageResult.value = false
        _editUserResult.value = false
    }
}
