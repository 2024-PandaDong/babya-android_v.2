package kr.pandadong2024.babya.home.dash_board.dash_boardViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashBoardViewModel() : ViewModel() {
    val id = MutableLiveData<Int>().apply { value = -1 }
}