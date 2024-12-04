package kr.pandadong2024.babya.home.map

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTextBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentMapBinding
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.Url
import kr.pandadong2024.babya.util.BottomControllable
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val TAG = "map"

    private val mapViewModel by activityViewModels<MapViewModel>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var kakaoMap: KakaoMap
    private val labelDataMap = mutableMapOf<Label, MapData>()

    private var latitude: Double = 37.511880
    private var longitude: Double = 127.059296

    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        checkLocationPermissions()

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_mapFragment_to_mainFragment)
        }

        binding.emergencyBtn.setOnClickListener { serverMarker("응급실") }
        binding.gynecologyBtn.setOnClickListener { serverMarker("산부인과") }
        binding.selectMy.setOnClickListener { moveToCurrentLocation() }
        binding.deleteMarker.setOnClickListener { clearAllMarkers() }

        binding.mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.d(TAG, "onMapDestroy: 종료")
            }

            override fun onMapError(p0: Exception?) {
                Log.d(TAG, "onMapError: 오류")
            }
        }, object : KakaoMapReadyCallback() {
            override fun getZoomLevel() = 14

            override fun onMapReady(map: KakaoMap) {
                kakaoMap = map
                moveToCurrentLocation()
                setupMap()
                Log.d(ContentValues.TAG, "getCurrentLocation: ${latitude} ${longitude}")
                addLocationLabel(latitude, longitude)
            }
        })

        return binding.root
    }

    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한 요청
            ActivityCompat.requestPermissions(
                requireActivity(), locationPermissions, 100
            )
        } else {
            // 권한이 이미 있으면 위치를 가져옴
            getCurrentLocation()
        }
    }


    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                latitude = it.latitude
                longitude = it.longitude
                // kakaoMap이 초기화되었는지 확인 후 마커 추가
                Log.d(ContentValues.TAG, "getCurrentLocation: ${latitude} ${longitude}")
                if (::kakaoMap.isInitialized) {
                    addLocationLabel(latitude, longitude)
                } else {
                    Log.d(TAG, "kakaoMap이 초기화되지 않았습니다.")
                }
            } ?: run {
                Log.d(TAG, "위치를 가져올 수 없습니다.")
            }
        }
    }



    private fun addLocationLabel(latitude: Double, longitude: Double) {
        val labelOptions = LabelOptions.from(LatLng.from(latitude, longitude))
            .setStyles(LabelStyles.from("user-location", LabelStyle.from(R.drawable.ic_my_map)))
        val label = kakaoMap.labelManager!!.layer!!.addLabel(labelOptions)
        label.changeText(LabelTextBuilder().setTexts("현재 위치"))
    }

    private fun moveToCurrentLocation() {
        val center = CameraUpdateFactory.newCenterPosition(LatLng.from(latitude, longitude))
        val zoom = CameraUpdateFactory.zoomTo(14)
        kakaoMap.moveCamera(center)
        kakaoMap.moveCamera(zoom)
    }

    private fun serverMarker(id: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getKakaoService().searchKeyword(
                    authorization = "KakaoAK ${Url.restApiKey}",
                    latitude = latitude,
                    longitude = longitude,
                    radius = 5000,
                    query = id
                )
            }.onSuccess { result ->
                clearAllMarkers()
                result.body()?.documents?.forEach {
                    if (it.category_name.contains("병원")) {
                        val placeData = MapData(
                            placeName = it.place_name,
                            addressName = it.address_name,
                            roadAddressName = it.road_address_name,
                            placeUrl = it.place_url,
                            tel = it.phone
                        )
                        addMarkersToMap(it.place_name, it.y.toDouble(), it.x.toDouble(), it.place_name, placeData)
                    }
                }
            }.onFailure { error ->
                Log.d(TAG, "Error fetching markers: ${error.message}")
            }
        }
    }

    private fun addMarkersToMap(id: String, latitude: Double, longitude: Double, text: String?, placeData: MapData): Label {
        val styles = LabelStyles.from(id, LabelStyle.from(R.drawable.ic_hospital).setZoomLevel(8).setTextStyles(32, Color.BLACK))
        val options = LabelOptions.from(LatLng.from(latitude, longitude)).setStyles(styles).setClickable(true)
        val label = kakaoMap.labelManager!!.layer!!.addLabel(options)
        label.changeText(LabelTextBuilder().setTexts(text))
        labelDataMap[label] = placeData
        return label
    }

    private fun setupMap() {
        kakaoMap.setOnLabelClickListener { _, _, label ->
            val placeData = labelDataMap[label]
            placeData?.let {
                showBottomSheet(it)
            }
            true
        }
    }

    private fun showBottomSheet(placeData: MapData) {
        val bottomSheetDialog = MapBottomSheet(placeData) {}
        bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
    }

    private fun clearAllMarkers() {
        kakaoMap.labelManager!!.layer!!.removeAll()
        Log.d(TAG, "clearAllMarkers: 삭제")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
