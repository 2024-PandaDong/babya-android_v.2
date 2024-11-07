package kr.pandadong2024.babya.home.find_company.find_company_viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.remote.responses.company.CompanyListResponses

class FindCompanyViewModel : ViewModel() {
    val id = MutableLiveData<Int>().apply { value = -1 }

    private var _accessToken = MutableLiveData("")
    var accessToken: LiveData<String> = _accessToken

    private var _companyList = MutableLiveData<List<CompanyListResponses>>(emptyList())
    var companyList: LiveData<List<CompanyListResponses>> = _companyList

    private var _message = MutableLiveData<String>("")
    var message: LiveData<String> = _message

    fun setAccessToken(token: String) {
        _accessToken.value = token
    }


    fun initCompanyList() = viewModelScope.launch(Dispatchers.IO) {
        kotlin.runCatching {
            RetrofitBuilder.getCompanyService().getCompanyList(
                accessToken = "Bearer ${_accessToken.value}",
                page = 1,
                size = 3
            )
        }.onSuccess {
            launch(Dispatchers.Main) {
                _companyList.value = it.data ?: listOf()
            }
        }.onFailure {
            Log.d("initCompanyList", "it : in false")
            it.printStackTrace()
            if (it is retrofit2.HttpException) {
                val errorBody = it.response()?.raw()?.request
                Log.d("initCompanyList", "it : $errorBody")
                Log.d("initCompanyList", "it : ${it.response()?.body()}")
            }
            launch(Dispatchers.Main) {
            }
        }
    }

}