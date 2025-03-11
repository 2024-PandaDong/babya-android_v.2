package kr.pandadong2024.babya.home.map

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.server.local.DAO.TokenDAO
import kr.pandadong2024.babya.server.local.entity.TokenEntity
import javax.inject.Inject

class MapViewModel @Inject constructor(
    application: Application,
    private val tokenDAO: TokenDAO
) : AndroidViewModel(application) {

    fun getToken(): TokenEntity?{
        return tokenDAO.getMembers()
    }

    fun insertToken(tokenEntity: TokenEntity){
        viewModelScope.launch(Dispatchers.IO){
            tokenDAO.insertMember(tokenEntity)
        }
    }

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    // 위치 정보를 저장할 LiveData
    val locationLiveData: MutableLiveData<Location?> = MutableLiveData()

    // 현재 위치 가져오기
    fun fetchCurrentLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    locationLiveData.value = location
                }
                .addOnFailureListener {
                    locationLiveData.value = null // 실패 시 null 설정
                }
        } catch (e: SecurityException) {
            // 권한 예외 처리
            locationLiveData.value = null
        }
    }
}
