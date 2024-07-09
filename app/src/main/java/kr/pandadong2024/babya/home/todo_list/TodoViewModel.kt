package kr.pandadong2024.babya.home.todo_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TodoViewModel : ViewModel() {
    var isSubmit = MutableLiveData<Boolean>().apply { value = false }
}