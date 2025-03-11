package kr.pandadong2024.babya.home.todo_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.server.local.DAO.TokenDAO
import kr.pandadong2024.babya.server.local.entity.TokenEntity
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val tokenDao : TokenDAO
): ViewModel() {

    fun getToken() : TokenEntity?{
        return tokenDao.getMembers()
    }

    fun insertToken(tokenEntity: TokenEntity){
        viewModelScope.launch(Dispatchers.IO){
            tokenDao.insertMember(tokenEntity)
        }
    }

    var isSubmit = MutableLiveData<Boolean>().apply { value = false }
}