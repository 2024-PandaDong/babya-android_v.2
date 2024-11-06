package kr.pandadong2024.babya.home.policy.viewmdole

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.home.policy.getLocalByCode
import kr.pandadong2024.babya.home.policy.getRegionByCode
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.remote.responses.Policy.PolicyListResponse
import retrofit2.HttpException

class PolicyViewModel : ViewModel() {
    // 항상 0번째가 기초자치단체( 시, 군, 구 ) 1번째가 행정구 or 행정 군
    val tagsList = MutableLiveData<List<String>>(listOf())

    val policyList = MutableLiveData<List<PolicyListResponse>>(listOf())

    val _policyListData = MutableLiveData<List<PolicyListResponse>>(emptyList())
    val policyListData: LiveData<List<PolicyListResponse>> = _policyListData

    val _message = MutableLiveData<String>("")
    val message: LiveData<String> = _message

    val policyId = MutableLiveData(-1)

    val policySearchKeyWord = MutableLiveData("")

    val isOpenSearchView = MutableLiveData(false)

    val isSearch = MutableLiveData(false)

    val userRegionList = MutableLiveData<List<String>>(listOf())


    fun inputLocal(tagName: String) {
        val list = tagsList.value?.toMutableList() ?: return
        list.add(tagName)
        tagsList.value = list
    }

    fun setUserRegionList(localList: List<String>) {
        val list = userRegionList.value ?: return
        userRegionList.value = list + localList
    }

    fun changeOpenSearchView() {
        isOpenSearchView.value = isOpenSearchView.value?.not() ?: false
    }


    fun setTagList(code: Int) {
        tagsList.value = listOf(getLocalByCode(code.toString()), getRegionByCode(code))
    }

    fun popLocal(tagName: String) {
        val list = tagsList.value?.toMutableList() ?: return
        list.remove(tagName)

        tagsList.value = list
    }

    fun setPolicyId(inputId: Int) {
        policyId.value = inputId
    }

    fun setPolicySearchKeyWord(inputKeyWord: String) {
        policySearchKeyWord.value = inputKeyWord
    }

    fun setPolicyList(inputList: List<PolicyListResponse>) {
        policyList.value = inputList
    }

    fun removeAll() {
        tagsList.value = listOf()
    }

    fun removeSubTags() {
        val list = listOf(tagsList.value?.get(0) ?: "알 수 없음")
        tagsList.value = list
    }

    fun initViewModel() {
        tagsList.value = listOf()
    }

    fun initKeyword() {
        policySearchKeyWord.value = ""
        isOpenSearchView.value = false
    }


    fun getPolicyList(code: String) = viewModelScope.launch(Dispatchers.IO) {
        kotlin.runCatching {
            RetrofitBuilder.getPolicyService().getPolicyList(
                type = code,
                keyword = ""
            )
        }.onSuccess {
            withContext(Dispatchers.Main) {
                Log.d("getPolicyList", "policy list ${it.data}")
                _policyListData.value = it.data ?: emptyList()
            }
        }.onFailure { result ->
            if (result is HttpException) {
                launch(Dispatchers.Main) {
                    if (result.code() == 500) {
                        _message.value = "인터넷이 연결되어있는지 확인해 주십시오"
                    } else {
                        _message.value = "데이터를 불러오는 도중 문제가 발생했습니다. CODE : ${result.code()}"
                    }
                }
            }
        }
    }

}