package kr.pandadong2024.babya.home.policy.viewmdole

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PolicyViewModel : ViewModel() {
    val tagsList = MutableLiveData<List<String>>().apply { value = listOf()
    }

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

    fun removeAll(){
        tagsList.value = listOf()
    }

}