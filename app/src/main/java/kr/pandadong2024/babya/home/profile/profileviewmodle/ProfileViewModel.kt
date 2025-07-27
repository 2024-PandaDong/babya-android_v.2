package kr.pandadong2024.babya.home.profile.profileviewmodle

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.home.policy.getMemberLocalCode
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.entity.UserEntity
import kr.pandadong2024.babya.server.remote.request.UserEditRequest
import kr.pandadong2024.babya.server.remote.responses.ProfileData
import retrofit2.HttpException

class ProfileViewModel(private val application: Application) : AndroidViewModel(application) {
    val id = MutableLiveData<Int>().apply { value = -1 }
    init {
        viewModelScope.launch(Dispatchers.IO) {
            setAccessToken(BabyaDB.getInstance(application)?.tokenDao()
                ?.getMembers()?.accessToken.toString())
        }
    }
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

    fun setAccessToken(token: String) = viewModelScope.launch(Dispatchers.Main) {
        _accessToken.value = token
    }

    fun setEditUserImageResult(value: Boolean) {
        _editUserImageResult.value = value
    }

    fun setToastMessage(message: String) {
        _toastMessage.value = message
    }

    fun getUserData() = viewModelScope.launch() {
        kotlin.runCatching {
            RetrofitBuilder.getProfileService().getProfile(
                accessToken = "Bearer ${_accessToken.value}",
                email = "my"
            )
        }.onSuccess { result ->
            val data = result.data
            launch(Dispatchers.IO) {
                val userEntity = UserEntity(
                    email = "",
                    nickname = data?.nickname,
                    dDay = data?.dDay,
                    birthDt = data?.birthDt,
                    marriedYears = data?.marriedYears,
                    children = data?.children.toString(),
                    profileImg = data?.profileImg,
                    localCode = "",
                )

                BabyaDB.getInstance(application)?.userDao()?.updateMember(userEntity)
            }
            launch(Dispatchers.Main) {
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

    fun getUserLocalData(): UserEntity? {
        val userData = runBlocking {
            val user = viewModelScope.async(Dispatchers.IO) {
                BabyaDB.getInstance(application)?.userDao()
                    ?.getMembers()
            }.await()

            return@runBlocking user
        }
        return userData
    }


    fun getUserLocalCode() = viewModelScope.launch{
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

    fun editUser(userData: UserEditRequest) = viewModelScope.launch {
        runCatching {
            RetrofitBuilder.getProfileService().editUserInfo(
                accessToken = "Bearer ${_accessToken.value}",
                body = userData
            )
        }.onSuccess {
            _editUserResult.value = true
        }.onFailure { result ->
            if (result is HttpException) {
                if (result.code() == 500) {
                    _toastMessage.value = "서버에서 문제가 발생했습니다."
                }
            }
        }
    }

    fun editUserImage(imageLink: String) =
        viewModelScope.launch {
            kotlin.runCatching {
                RetrofitBuilder.getProfileService().editUserImage(
                    accessToken = "Bearer ${_accessToken.value}",
                    imgUrl = imageLink
                )
            }.onSuccess {
                _editUserImageResult.value = true
            }.onFailure { result ->
                if (result is HttpException) {
                    if (result.code() == 500) {
                        _toastMessage.value = "서버에서 문제가 발생했습니다."
                    }
                }
            }
        }

    fun initResult() {
        _editUserImageResult.value = false
        _editUserResult.value = false
    }
}
