package kr.pandadong2024.babya.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.pandadong2024.babya.server.RetrofitBuilder
import okhttp3.MultipartBody
import okhttp3.internal.wait
import retrofit2.HttpException

class CommonViewModel : ViewModel() {
    private val _toastMessage = MutableLiveData<String>().apply { value = "" }
    val toastMessage: LiveData<String> = _toastMessage

    private var _accessToken = MutableLiveData<String>().apply { value = "" }
    var accessToken: LiveData<String> = _accessToken

    private val _imageLink = MutableLiveData<String>().apply { value = "" }
    val imageLink: LiveData<String> = _imageLink

    private val _imageLinkList = MutableLiveData<List<String>>().apply { value = listOf() }
    val imageLinkList : LiveData<List<String>> = _imageLinkList

    fun setToastMessage(message: String) {
        _toastMessage.value = message
    }

    fun setAccessToken(token: String) {
        _accessToken.value = token
    }

    fun uploadImage(image: MultipartBody.Part) = viewModelScope.launch(Dispatchers.IO) {
        kotlin.runCatching {
            RetrofitBuilder.getCommonService().fileUpload(
                accessToken = "Bearer ${_accessToken.value}",
                file = image
            )
        }.onSuccess { result ->
            _imageLink.value = result.data
        }.onFailure { result ->
            result.printStackTrace()
        }
    }

    fun uploadImageList(imageList: List<MultipartBody.Part>) =
        viewModelScope.launch(Dispatchers.IO) {
            val imageResult = mutableListOf<String>()
            runBlocking {
                imageList.forEach { image ->
                    val imageConvert = viewModelScope.async(Dispatchers.IO) {
                        kotlin.runCatching {
                            RetrofitBuilder.getCommonService().fileUpload(
                                accessToken = "Bearer ${_accessToken.value}",
                                file = image
                            )
                        }
                    }

                    imageConvert.await().onSuccess {
                        imageResult.add(it.data ?: "")
                    }.onFailure { result ->
                        if (result is HttpException) {
                            if (result.code() == 500) {
                                _toastMessage.value = "서버에서 문제가 발생헀습니다."
                            } else {
                                _toastMessage.value =
                                    "이미지를 업로드 하는데 문제가 발생했습니다. CODE : ${result.code()}"
                            }
                        }
                    }
                }
            }
            _imageLinkList.value = imageResult
        }
}