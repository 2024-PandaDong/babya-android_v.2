package kr.pandadong2024.babya.home.dash_board.dash_boardViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.size.Dimension
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.server.local.DAO.TokenDAO
import kr.pandadong2024.babya.server.local.entity.TokenEntity
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(
    private val tokenDao: TokenDAO
) : ViewModel() {

    fun getToken(): TokenEntity? {
        return tokenDao.getMembers()
    }

    fun insertToken(tokenEntity: TokenEntity){
        viewModelScope.launch(Dispatchers.IO){
            tokenDao.insertMember(tokenEntity)
        }
    }

    val id = MutableLiveData<Int>().apply { value = -1 }
}