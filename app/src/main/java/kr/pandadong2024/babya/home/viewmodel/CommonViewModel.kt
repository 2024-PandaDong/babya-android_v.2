package kr.pandadong2024.babya.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.DAO.TokenDAO
import kr.pandadong2024.babya.server.local.entity.TokenEntity
import okhttp3.MultipartBody
import okhttp3.internal.wait
import retrofit2.HttpException
import retrofit2.http.HTTP
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    private val tokenDao: TokenDAO
): ViewModel() {

    fun getToken() : TokenEntity?{
        return tokenDao.getMembers()
    }

    fun insertToken(tokenEntity: TokenEntity){
        viewModelScope.launch(Dispatchers.IO){
            tokenDao.insertMember(tokenEntity)
        }
    }


    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> = _toastMessage

    private var _accessToken = MutableLiveData("")
    var accessToken: LiveData<String> = _accessToken

    private val _imageLink = MutableLiveData("")
    val imageLink: LiveData<String> = _imageLink

    private val _imageLinkList = MutableLiveData<List<String>>(listOf())
    val imageLinkList: LiveData<List<String>> = _imageLinkList

    fun setToastMessage(message: String) {
        _toastMessage.value = message
    }

    fun setAccessToken(token: String) {
        _accessToken.value = token
    }

    fun setImageLink(link: String = "") {
        _imageLink.value = link
    }

    fun setImageLinkList(linkList: List<String> = listOf()) {
        _imageLinkList.value = linkList
    }

    fun uploadImage(image: MultipartBody.Part) = viewModelScope.launch() {
        if (_accessToken.value?.isNotEmpty() == true) {
            kotlin.runCatching {
                RetrofitBuilder.getCommonService().fileUpload(
                    accessToken = "Bearer ${_accessToken.value}",
                    file = image
                )
            }.onSuccess { result ->
                withContext(Dispatchers.Main) {
                    _imageLink.value = result.data
                }
            }.onFailure { result ->
                result.printStackTrace()
                if (result is HttpException) {
                    Log.e("upload image", "log : ${result.response()}")
                    Log.e("upload image", "log : ${result.response()?.body()}")
                }
            }
        }else{
            _toastMessage.value = "인증되지 않은 사용자 입니다."
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
                            val code = result.code()
                            when(code){
                                500 ->{
                                    _toastMessage.value = "서버에서 문제가 발생헀습니다."
                                }
                                413 ->{
                                    _toastMessage.value =
                                        "이미지를 업로드 하는데 문제가 발생했습니다. CODE : ${result.code()}"
                                }
                                else ->{
                                    _toastMessage.value =
                                        "이미지를 업로드 하는데 문제가 발생했습니다. CODE : ${result.code()}"
                                }
                            }
                        }
                    }
                }
            }
            _imageLinkList.value = imageResult
        }
}