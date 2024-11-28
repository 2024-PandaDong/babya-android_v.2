package kr.pandadong2024.babya.home.policy.viewmdole

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.pandadong2024.babya.home.policy.getLocalByCode
import kr.pandadong2024.babya.home.policy.getRegionByCode
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.entity.UserEntity
import kr.pandadong2024.babya.server.remote.responses.Policy.PolicyListResponse
import retrofit2.HttpException

class PolicyViewModel(private val application: Application) : AndroidViewModel(application) {
    // 항상 0번째가 기초자치단체( 시, 군, 구 ) 1번째가 행정구 or 행정 군
    val _tagsList = MutableLiveData<List<String>>(listOf())
    val tagsList: LiveData<List<String>> = _tagsList

    private val _saveList = MutableLiveData<MutableList<String>>(mutableListOf())
    val saveList: LiveData<MutableList<String>> = _saveList

    val policyList = MutableLiveData<List<PolicyListResponse>>(listOf())

    val _policyListData = MutableLiveData<List<PolicyListResponse>>(emptyList())
    val policyListData: LiveData<List<PolicyListResponse>> = _policyListData

    private val _localUserData = MutableLiveData<UserEntity>()
    val localUserData: LiveData<UserEntity> = _localUserData

    val pagingSize = 4

    private val _startPolicyPage = MutableLiveData<Int>(0)
    val startPolicyPage: LiveData<Int> = _startPolicyPage

    val _message = MutableLiveData<String>("")
    val message: LiveData<String> = _message

    val policyId = MutableLiveData(-1)

    val policySearchKeyWord = MutableLiveData("")

    val isOpenSearchView = MutableLiveData(false)

    val isSearch = MutableLiveData(false)

    val userRegionList = MutableLiveData<List<String>>(listOf())


    fun inputLocal(tagName: String) {
        val list = saveList.value ?: return
        list.add(tagName)
        _saveList.value = list
        Log.d("test", "_saveList ${_saveList.value}")
        Log.d("test", "saveList ${saveList.value}")
    }

    fun setLocalTagList() {
        _tagsList.value = saveList.value
    }

    fun setUserRegionList(localList: List<String>) {
        val list = userRegionList.value ?: return
        userRegionList.value = list + localList
    }

    fun changeOpenSearchView() {
        isOpenSearchView.value = isOpenSearchView.value?.not() ?: false
    }


    fun setTagList(code: Int) {
        _tagsList.value = listOf(getLocalByCode(code.toString()), getRegionByCode(code))
        _saveList.value = mutableListOf(getLocalByCode(code.toString()), getRegionByCode(code))
    }

    fun getLocalUserData() : UserEntity? {
        val userData = runBlocking {
            val user = viewModelScope.async(Dispatchers.IO) {
                BabyaDB.getInstance(application)?.userDao()
                    ?.getMembers()
            }.await()

            return@runBlocking user
        }
        return userData
    }

    fun popLocal(tagName: String) {
//        val list = tagsList.value?.toMutableList() ?: return
//        list.remove(tagName)
//
//        _tagsList.value = list
        Log.d("test", "in list")
        val list = saveList.value ?: return
        list.remove(tagName)
        _saveList.value = list
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
//        _tagsList.value = listOf()
        _saveList.value = mutableListOf()
    }

    fun removeSubTags() {
//        val list = listOf(tagsList.value?.get(0) ?: "알 수 없음")
//        _tagsList.value = list
        _saveList.value = mutableListOf(saveList.value?.get(0) ?: "알 수 없음")
    }


    fun initViewModel() {
//        _tagsList.value = listOf()
        _saveList.value = mutableListOf()
    }

    fun initKeyword() {
        policySearchKeyWord.value = ""
        isOpenSearchView.value = false
    }


    fun getPolicyList(code: String, keyword: String = "") = viewModelScope.launch() {
        kotlin.runCatching {
            RetrofitBuilder.getPolicyService().getPolicyList(
                type = code,
                keyword = keyword
            )
        }.onSuccess {
            _policyListData.value = it.data ?: emptyList()
        }.onFailure { result ->
            if (result is HttpException) {
                if (result.code() == 500) {
                    _message.value = "인터넷이 연결되어있는지 확인해 주십시오"
                } else {
                    _message.value = "데이터를 불러오는 도중 문제가 발생했습니다. CODE : ${result.code()}"
                }
            }
        }
    }

    fun setSaveTagList(value: List<String>?) {
        _saveList.value = value?.toMutableList()
    }

}