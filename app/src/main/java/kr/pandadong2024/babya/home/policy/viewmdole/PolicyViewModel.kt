package kr.pandadong2024.babya.home.policy.viewmdole

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.pandadong2024.babya.home.policy.getLocalByCode
import kr.pandadong2024.babya.home.policy.getRegionByCode
import kr.pandadong2024.babya.server.remote.responses.Policy.PolicyListResponse

class PolicyViewModel : ViewModel() {
    // 항상 0번째가 기초자치단체( 시, 군, 구 ) 1번째가 행정구 or 행정 군
    val tagsList = MutableLiveData<List<String>>().apply { value = listOf()
    }

    val policyList = MutableLiveData<List<PolicyListResponse>>().apply { value = listOf()
    }

    val policyId = MutableLiveData<Int>().apply { value = -1 }

    val policySearchKeyWord = MutableLiveData<String>().apply { value = "" }

    val isOpenSearchView = MutableLiveData<Boolean>().apply { value = false  }

    val userRegionList = MutableLiveData<List<String>>().apply { value = listOf() }




    fun inputLocal(tagName : String){
        val list =  tagsList.value?.toMutableList()
        list?.add(tagName)
        tagsList.value = list
    }

    fun setUserRegionList(localList :List<String>) {
        val list = userRegionList.value?.toMutableList()
        list?.addAll(localList)
        userRegionList.value = list
    }

    fun changeOpenSearchView(){
        isOpenSearchView.value = isOpenSearchView.value?.not() ?: false
    }


    fun setTagList(code : Int){
        tagsList.value = listOf(getLocalByCode(code.toString()), getRegionByCode(code), )
    }

    fun popLocal(tagName : String){
        val list =  tagsList.value?.toMutableList()
        list?.remove(tagName)
        tagsList.value = list
    }

    fun setPolicyId(inputId :  Int){
        policyId.value = inputId
    }

    fun setPolicySearchKeyWord(inputKeyWord : String){
        policySearchKeyWord.value = inputKeyWord
    }

    fun setPolicyList(inputList :  List<PolicyListResponse>){
        policyList.value = inputList
    }

    fun removeAll(){
        tagsList.value = listOf()
    }

    fun removeSubTags(){
        val list = listOf(tagsList.value?.get(0) ?: "알 수 없음")
        tagsList.value = list
    }

    fun initViewModel(){
        tagsList.value = listOf()
    }

    fun initKeyword(){
        policySearchKeyWord.value = ""
        isOpenSearchView.value = false
    }

}