package kr.pandadong2024.babya.home.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.remote.responses.BannerResponses

class MainViewModel : ViewModel() {
    private var _accessToken = MutableLiveData("")
    var accessToken: LiveData<String> = _accessToken

    private var _bannerData = MutableLiveData<List<BannerResponses>>(emptyList())
    var bannerData: LiveData<List<BannerResponses>> = _bannerData

    fun setAccessToken(token: String) {
        _accessToken.value = token
    }

    fun getBannerData() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getHttpMainService().getBanner(
                    accessToken = "Bearer ${_accessToken.value}",
                    lc = "my",
                    type = "1"
                )

            }.onSuccess {
                launch(Dispatchers.Main) {
                    _bannerData.value = it.data ?: emptyList()
                }
            }.onFailure { e ->
                launch(Dispatchers.Main) {
                    _bannerData.value = emptyList()
                }
            }
        }
    }
}