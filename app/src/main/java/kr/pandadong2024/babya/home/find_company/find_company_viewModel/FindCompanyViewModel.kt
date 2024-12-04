package kr.pandadong2024.babya.home.find_company.find_company_viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.remote.responses.company.CompanyListResponses

class FindCompanyViewModel : ViewModel() {
    val id = MutableLiveData<Int>().apply { value = -1 }

    private var _accessToken = MutableLiveData("")
    var accessToken: LiveData<String> = _accessToken

    private var _companyList = MutableLiveData<List<CompanyListResponses>>(emptyList())
    var companyList: LiveData<List<CompanyListResponses>> = _companyList

    val pagingSize = 8

    private val _lastCompanyPosition = MutableLiveData<Int>(0)
    val lastCompanyPosition: LiveData<Int> = _lastCompanyPosition

    private val _startCompanyPage = MutableLiveData<Int>(0)
    val startCompanyPage: LiveData<Int> = _startCompanyPage

    private var _message = MutableLiveData<String>("")
    var message: LiveData<String> = _message

    fun setAccessToken(token: String) {
        _accessToken.value = token
    }

    fun setLastPosition(position : Int){
        _lastCompanyPosition.value = position
    }

    fun addCompany() {
        _startCompanyPage.value = _startCompanyPage.value?.plus(1)
        viewModelScope.launch {
            getCompanyList(
                page = (_startCompanyPage.value ?: 1), size = pagingSize
            )
        }
    }

    fun initCompany() {
        _startCompanyPage.value = 0
        _companyList.value = emptyList()
        addCompany()
    }


    fun getCompanyList(size: Int? = null, page: Int? = null) = viewModelScope.launch {
        kotlin.runCatching {
            RetrofitBuilder.getCompanyService().getCompanyList(
                accessToken = "Bearer ${_accessToken.value}",
                page = page ?: (_startCompanyPage.value ?: 1),
                size = size ?: pagingSize
            )
        }.onSuccess {
            if (it.data?.isEmpty() == true) {
                _startCompanyPage.value = startCompanyPage.value?.minus(1)
            } else {
                if ((page ?: (_startCompanyPage.value ?: 1)) != 1) {
                    val list = _companyList.value?.toMutableList()
                    list?.addAll(it.data ?: listOf())
                    _companyList.value = list?.toList()
                    setLastPosition((list?.size?: pagingSize).minus(pagingSize) )
                } else {
                    _companyList.value = it.data ?: listOf()
                    setLastPosition(0)
                }
            }
        }.onFailure {
            it.printStackTrace()
            if (it is retrofit2.HttpException) {
                val errorBody = it.response()?.raw()?.request
            }
        }
    }
}