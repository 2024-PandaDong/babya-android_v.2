package kr.pandadong2024.babya.home.policy.viewmdole

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.pandadong2024.babya.server.remote.responses.Policy.PolicyResponse

class PolicyViewModel : ViewModel() {
    val tagsList = MutableLiveData<List<String>>().apply { value = listOf("대구광역시", "수성구")
    }

    val policyList = MutableLiveData<List<PolicyResponse>>().apply { value = listOf()
    }

    val policyId = MutableLiveData<Int>().apply { value = -1 }



    fun inputLocal(tagName : String){
        val list =  tagsList.value?.toMutableList()
        list?.add(tagName)
        tagsList.value = list
    }

    fun popLocal(tagName : String){
        val list =  tagsList.value?.toMutableList()
        list?.remove(tagName)
        tagsList.value = list
    }

    fun setPolicyId(inputId :  Int){
        policyId.value = inputId
    }

    fun removeAll(){
        tagsList.value = listOf()
    }

    fun removeSubTags(){
        val list = listOf(tagsList.value!![0])
        tagsList.value = list
    }

    fun initViewModel(){
        tagsList.value = listOf("대구광역시", "수성구")
    }

}