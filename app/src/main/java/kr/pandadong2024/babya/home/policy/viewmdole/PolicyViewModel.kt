package kr.pandadong2024.babya.home.policy.viewmdole

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.pandadong2024.babya.home.policy.getLocalByCode
import kr.pandadong2024.babya.home.policy.getRegionByCode
import kr.pandadong2024.babya.server.remote.responses.Policy.PolicyListResponse

class PolicyViewModel : ViewModel() {
    // 항상 0번째가 기초자치단체( 시, 군, 구 ) 1번째가 행정구 or 행정 군
    val tagsList = MutableLiveData<List<String>>(listOf())

    val policyList = MutableLiveData<List<PolicyListResponse>>(listOf())

    val policyId = MutableLiveData(-1)

    val policySearchKeyWord = MutableLiveData("")

    val isOpenSearchView = MutableLiveData(false)

    val isSearch = MutableLiveData(false)

    val userRegionList = MutableLiveData<List<String>>(listOf())


    fun inputLocal(tagName: String) {
        val list = tagsList.value?.toMutableList()?: return
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

}