package kr.pandadong2024.babya.home.map

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
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
import kr.pandadong2024.babya.server.kakao.remote.responses.Document
import kr.pandadong2024.babya.start.signup.SignupBottomSheet
import kr.pandadong2024.babya.util.BottomControllable

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val TAG = "map"

    private val viewModel by activityViewModels<MapViewModel>()

    private var latitude: Double = 35.907691243826584
    private var longitude : Double  = 128.61267453961887

    lateinit var documents : List<Document>

    private lateinit var kakaoMap: KakaoMap
    private lateinit var mapView: MapView

    private val labelDataMap = mutableMapOf<Label, MapData>()

    private var rabel : Boolean = false



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)



        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_mapFragment_to_mainFragment)
        }

        // 응급실 버튼 눌렀을 떄 가까운 응급실 위치 보여주기
        binding.emergencyBtn.setOnClickListener {
            serverMarker("응급실")
        }

        // 산부인과 버튼 눌렀을 떄 가까운 산부인과 위치 보여주기
        binding.gynecologyBtn.setOnClickListener {
            serverMarker("산부인과")
        }

        // 현 위치로 돌아가기
        binding.selectMy.setOnClickListener {
            val center = CameraUpdateFactory.newCenterPosition(LatLng.from(latitude, longitude))
            val zoom = CameraUpdateFactory.zoomTo(14)
            kakaoMap.moveCamera(center)
            kakaoMap.moveCamera(zoom)
        }

        binding.deleteMarker.setOnClickListener {
            clearAllMarkers()
        }



        binding.mapView.start(object : MapLifeCycleCallback() {

            // 지도 API 가 정상적으로 종료될 때 호출됨
            override fun onMapDestroy() {
                Log.d(TAG, "onMapDestroy: 종료")
            }
            // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
            override fun onMapError(p0: Exception?) {
                Log.d(TAG, "onMapError: 오류")
            }

        },
            object : KakaoMapReadyCallback(){
                // 지도 시작 시 확대/축소 줌 레벨 설정
                override fun getZoomLevel(): Int {
                    return 14
                }

                override fun onMapReady(map: KakaoMap){
                    kakaoMap = map
                    var cameraUpdate =
                        CameraUpdateFactory.newCenterPosition(LatLng.from(latitude, longitude))
                    kakaoMap.moveCamera(cameraUpdate)

                    Log.d(TAG, "onMapReady: ${kakaoMap.getZoomLevel()}")
                    setupMap()


                }
            }
        )


        return binding.root
    }


    fun addMarkersToMap(id: String, latitude: Double, longitude: Double, text: String? = null, placeData: MapData): Label {
        val styles = LabelStyles.from(
            id,
            LabelStyle.from(R.drawable.ic_hospital).setZoomLevel(8).setTextStyles(32, Color.BLACK)
        )
        val options: LabelOptions = LabelOptions.from(LatLng.from(latitude, longitude))
            .setStyles(styles)
            .setClickable(true)
        val label = kakaoMap.labelManager!!.layer!!.addLabel(options)
        // string을 LabelTextBuilder로 변경
        label.changeText(LabelTextBuilder().setTexts(text));

        labelDataMap[label] = placeData

        return label
    }

    fun serverMarker(id : String) {
        lifecycleScope.launch(Dispatchers.IO){
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
                Log.d(TAG, "성공: ${result.body()?.documents}")
                val documents = result.body()?.documents ?: emptyList()
                documents.forEach{
                    // it.category_name에 병원 이라는 키워드가 있으면
                    if (it.category_name.contains("병원")){
                        Log.d("url", "serverMarker: ${it.place_url}")
                        // x, y를 바꿔서 씀(오류)
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

            }.onFailure { result->
                result.printStackTrace()
                Log.d(TAG, "onCreateView: ${result.message}")
            }
        }
    }

    fun setupMap() {

        kakaoMap.setOnLabelClickListener { _, _, label ->
            val placeData = labelDataMap[label]
            if (placeData != null) {
                Log.d("LabelClick", "Clicked Place: ${placeData.placeName}")
                showBottomSheet(placeData)
            } else {
                Log.w("LabelClick", "No data found for this label!")
            }
            true
        }
    }

    private fun showBottomSheet(placeData: MapData){
        Log.d("마커", "showBottomSheet: ${placeData}")
        val bottomSheetDialog =
            MapBottomSheet(placeData){

            }
        bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
    }



    private fun clearAllMarkers() {
        // 마커 레이어 가져오기
        val layer = kakaoMap.labelManager!!.layer!!

        // 레이어에서 모든 레이블 제거
        layer.removeAll()
        Log.d(TAG, "clearAllMarkers: 삭제")
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}