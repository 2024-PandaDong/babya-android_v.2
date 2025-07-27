package kr.pandadong2024.babya.home.main

import android.Manifest
import android.content.ContentValues.TAG
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
import androidx.viewpager2.widget.ViewPager2
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.delay
import kr.pandadong2024.babya.MyApplication.Companion.prefs
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentMainBinding
import kr.pandadong2024.babya.home.find_company.find_company_viewModel.FindCompanyViewModel
import kr.pandadong2024.babya.home.map.MapViewModel
import kr.pandadong2024.babya.home.policy.adapter.PolicyRecyclerView
import kr.pandadong2024.babya.home.policy.getLocalByCode
import kr.pandadong2024.babya.home.policy.getMemberLocalCode
import kr.pandadong2024.babya.home.policy.getRegionByCode
import kr.pandadong2024.babya.home.policy.viewmdole.PolicyViewModel
import kr.pandadong2024.babya.home.profile.profileviewmodle.ProfileViewModel
import kr.pandadong2024.babya.home.viewmodel.CommonViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.remote.responses.BannerResponses
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.Policy.PolicyListResponse
import kr.pandadong2024.babya.server.remote.responses.company.CompanyListResponses
import kr.pandadong2024.babya.util.BottomControllable
import java.time.Duration
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTextBuilder
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.server.Url
import kotlin.math.ceil


class MainFragment : Fragment() {
    private lateinit var bannerList: List<BannerResponses>

    //    private lateinit var companyList: List<CompanyListResponses>
    private lateinit var companyData: BaseResponse<List<CompanyListResponses>>
    private lateinit var bannerAdapter: MainBannerAdapter
    private lateinit var rankAdapter: CompanyRankAdapter
    private lateinit var policyAdapter: PolicyRecyclerView
    private lateinit var infiniteViewPager: ViewPager2
    private val findCompanyViewModel by activityViewModels<FindCompanyViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val policyViewModel by activityViewModels<PolicyViewModel>()
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private val commonViewModel by activityViewModels<CommonViewModel>()
    private val mapViewModel by activityViewModels<MapViewModel>()

    private lateinit var accessToken: String
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var bannerPosition = 0
    private var form = 1 // 임산부인지 지역별인지

    private lateinit var kakaoMap: KakaoMap
    private var latitude: Double = 35.907691243826584
    private var longitude : Double  = 128.61267453961887

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )



    private var closest = 0


    private suspend fun getBanner(): List<BannerResponses> {
        val response = RetrofitBuilder.getHttpMainService().getBanner(
            accessToken = "Bearer $accessToken",
            lc = "my",
            type = "$form"
        )
        return response.data ?: listOf()
    }

    lateinit var job: Job

    fun scrollJobCreate() {
        job = lifecycleScope.launchWhenResumed {
            val duration = Duration.ofMillis(2000)
            delay(duration)
            binding.bannerViewPager.setCurrentItem(++bannerPosition, true)
        }
    }


    override fun onResume() {
        closest = 0
        super.onResume()
        scrollJobCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (job.isActive) {
            job.cancel()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 메인 스레드가 아닌 IO 스레드에서 데이터베이스에 접근하도록 수정
        runBlocking {
            lifecycleScope.launch(Dispatchers.IO) {
                if(isAdded && activity != null) {
                    accessToken = BabyaDB.getInstance(requireContext())?.tokenDao()
                        ?.getMembers()?.accessToken.toString()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        policyViewModel.initViewModel()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
        // TODO : 소프트 웨이브 끝나고 코드 115번 위치 코드 지우기
        prefs.remove()

        if(isAdded && activity != null) { fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext()) }
        checkLocationPermissions()

        kotlin.run {
            findName()
        }


        binding.mapViewLayout.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_mapFragment)
        }


        binding.mapView.start(object : MapLifeCycleCallback() {

            override fun onMapDestroy() {
                // 지도 API 가 정상적으로 종료
            }
            override fun onMapError(p0: Exception?) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출
                Log.e(TAG, "onMapError: 오류")
            }

        },
            object : KakaoMapReadyCallback(){
                override fun getZoomLevel(): Int {
                    return 14
                }

                override fun onMapReady(map: KakaoMap){

                    kakaoMap = map
                    val cameraUpdate =
                        CameraUpdateFactory.newCenterPosition(LatLng.from(latitude, longitude))
                    kakaoMap.moveCamera(cameraUpdate)
                    serverMarker()

                }
            }
        )

        runBlocking {
            launch {
                mainViewModel.getBannerData()
            }
            launch {
                findCompanyViewModel.addCompany()
            }
        }

        findCompanyViewModel.companyList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                setCompanyRecyclerView(it)
            }
        }

        policyViewModel.policyListData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                setPolicyRecyclerView(it)
            }
        }

        mainViewModel.bannerData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                bannerList = it
                setBannerViewPager()
            }
        }

        binding.policyMoreText.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_policyMainFragment)
        }
        binding.companyMoreText.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_findCompanyFragment2)
        }

        binding.bannerViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                //사용자가 스크롤 했을때 position 수정
                super.onPageSelected(position)
                bannerPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    when (infiniteViewPager.currentItem) {
                        (bannerList.size + 1) -> {
                            binding.bannerIndicator.visibility = View.GONE
                            infiniteViewPager.setCurrentItem(1, false)
                        }

                        0 -> {
                            binding.bannerIndicator.visibility = View.GONE
                            infiniteViewPager.setCurrentItem(bannerList.size, false)
                        }
                    }
                    binding.bannerIndicator.visibility = View.VISIBLE
                }

                when (state) {
                    ViewPager2.SCROLL_STATE_IDLE -> {
                        if (!job.isActive) scrollJobCreate()
                    }

                    ViewPager2.SCROLL_STATE_DRAGGING -> job.cancel()

                    ViewPager2.SCROLL_STATE_SETTLING -> {}
                }
            }
        })

        return binding.root
    }

    private fun findName() {
        lifecycleScope.launch(Dispatchers.IO){
            kotlin.runCatching {
                val token = BabyaDB.getInstance(requireContext())?.tokenDao()?.getMembers()?.accessToken
                RetrofitBuilder.getProfileService().getProfile(
                    accessToken = "Bearer $token",
                    email = "my"
                )
            }.onSuccess {  result ->
                withContext(Dispatchers.Main) {
                    binding.hospitalText.text = result.data?.nickname+"님 가장 가까운 산부인과는 여기에 있어요"
                }
            }
        }
    }


    private fun setBannerViewPager() {
        bannerPosition =
            Int.MAX_VALUE / 2 - ceil(bannerList.size.toDouble() / 2).toInt()
        bannerAdapter = MainBannerAdapter(
            context = requireContext(),
            bannerList = bannerList
        )
        bannerAdapter.notifyItemRemoved(0)
        with(binding) {
            infiniteViewPager = bannerViewPager
            bannerViewPager.adapter = bannerAdapter
            bannerViewPager.currentItem = 1
            bannerViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            bannerIndicator.setViewPager(bannerViewPager)
        }
    }


    private fun setCompanyRecyclerView(companyList: List<CompanyListResponses>) {
        val list = mutableListOf<CompanyListResponses>()
        if (companyList.isNotEmpty()) {
            for (i in 0..2) {
                if (companyList.size == i) {
                    break
                } else {
                    list.add(companyList[i])
                }
            }
        }
        rankAdapter = CompanyRankAdapter() { position ->
            kotlin.runCatching {
                findCompanyViewModel.id.value = list[position].companyId
                findNavController().navigate(R.id.action_mainFragment_to_companyDetailsFragment)
            }
        }

        rankAdapter.setCompanyList(list.toMutableList())
        rankAdapter.notifyItemRemoved(0)
        with(binding) {
            companyRecyclerView.adapter = rankAdapter
        }
    }

    private fun setPolicyRecyclerView(policyList: List<PolicyListResponse>) {
        val list = mutableListOf<PolicyListResponse>()
        if (policyList.isNotEmpty()) {
            for (i in 0..2) {
                if (policyList.size == i + 1) {
                    break
                } else {
                    list.add(policyList[i])
                }
            }
        }

        policyViewModel.setPolicyList(list)

        policyAdapter = PolicyRecyclerView(
            list.toList(),
            tag = "${policyViewModel.tagsList.value?.get(0) ?: 0} ${policyViewModel.tagsList.value?.get(1) ?: 0}"
        ) { position ->
            policyViewModel.setPolicyId(position)
            findNavController().navigate(R.id.action_mainFragment_to_policyContentFragment)
        }
        policyAdapter.notifyItemRemoved(0)
        with(binding) {
            policyRecyclerView.adapter = policyAdapter
        }
    }


    private fun serverMarker() {
        lifecycleScope.launch(Dispatchers.IO){
            kotlin.runCatching {
                RetrofitBuilder.getKakaoService().searchKeyword(
                    authorization = "KakaoAK ${Url.restApiKey}",
                    latitude = latitude,
                    longitude = longitude,
                    radius = 5000,
                    query = "산부인과"
                )
            }.onSuccess { result ->
                val documents = result.body()?.documents ?: emptyList()
                documents.forEach{
                    // it.category_name에 병원 이라는 키워드가 있으면 검색
                    if (it.category_name.contains("병원")){
                        addMarkersToMap(it.place_name)
                    }
                }

            }.onFailure { result->
                result.printStackTrace()
                Log.e(TAG, "onCreateView: ${result.message}")
            }
        }
    }

    fun addMarkersToMap(id: String) {
        lifecycleScope.launch(Dispatchers.Main){
            if (closest == 0){
                binding.locationLabelText.text = id
                closest += 1
            }
        }
    }

    private fun checkLocationPermissions() {
        if(isAdded && activity != null) {
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
                if (::kakaoMap.isInitialized) {
                    addLocationLabel(latitude, longitude)
                } else {
                    Log.e(TAG, "kakaoMap이 초기화되지 않았습니다.")
                }
            } ?: run {
                Log.e(TAG, "위치를 가져올 수 없습니다.")
            }
        }
    }

    private fun addLocationLabel(latitude: Double, longitude: Double) {
        val labelOptions = LabelOptions.from(LatLng.from(latitude, longitude))
            // labelstyle 변경 필요
            .setStyles(LabelStyles.from("user-location", LabelStyle.from(R.drawable.ic_my_map)))
        val label = kakaoMap.labelManager!!.layer!!.addLabel(labelOptions)
        label.changeText(LabelTextBuilder().setTexts("현재 위치"))
    }
}