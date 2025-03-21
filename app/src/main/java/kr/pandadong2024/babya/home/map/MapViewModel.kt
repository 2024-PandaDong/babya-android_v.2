package kr.pandadong2024.babya.home.map

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MapViewModel(application: Application) : AndroidViewModel(application) {

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
