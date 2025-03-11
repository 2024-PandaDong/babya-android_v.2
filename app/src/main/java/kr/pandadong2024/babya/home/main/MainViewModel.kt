package kr.pandadong2024.babya.home.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.DAO.TokenDAO
import kr.pandadong2024.babya.server.local.entity.TokenEntity
import kr.pandadong2024.babya.server.remote.responses.BannerResponses
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenDao: TokenDAO
): ViewModel() {

    fun getToken(): TokenEntity? {
        return tokenDao.getMembers()
    }

    fun insertToken(tokenEntity: TokenEntity){
        viewModelScope.launch(Dispatchers.IO){
            tokenDao.insertMember(tokenEntity)
        }
    }

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