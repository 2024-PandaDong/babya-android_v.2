package kr.pandadong2024.babya.home.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentMainBinding
import kr.pandadong2024.babya.home.find_company.find_company_viewModel.FindCompanyViewModel
import kr.pandadong2024.babya.home.policy.adapter.PolicyRecyclerView
import kr.pandadong2024.babya.home.policy.viewmdole.PolicyViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.remote.responses.BannerResponses
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.Policy.PolicyListResponse
import kr.pandadong2024.babya.server.remote.responses.UserDataResponses
import kr.pandadong2024.babya.server.remote.responses.company.CompanyDataResponses
import kr.pandadong2024.babya.util.BottomControllable
import java.time.Duration
import kotlin.math.ceil

class MainFragment : Fragment() {
    val TAG = "MainFragment"

    private lateinit var bannerList: List<BannerResponses>
    private lateinit var companyList: List<CompanyDataResponses>
    private lateinit var companyData: BaseResponse<List<CompanyDataResponses>>
    private lateinit var policyData: List<PolicyListResponse>
    private lateinit var bannerAdapter: MainBannerAdapter
    private lateinit var rankAdapter: CompanyRankAdapter
    private lateinit var policyAdapter: PolicyRecyclerView
    private val findCompanyViewModel by activityViewModels<FindCompanyViewModel>()

    private val policyViewModel by activityViewModels<PolicyViewModel>()

    private lateinit var tokenDao: TokenDAO
    private lateinit var statusAdapter: StatusAdapter
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var bannerPosition = 0
    private var form = 1 // 임산부인지 지역별인지
    private var userData: UserDataResponses = UserDataResponses()


    private suspend fun getBanner(): List<BannerResponses> {
        Log.d(TAG, "mainBanner : $form")
        val response = RetrofitBuilder.getHttpMainService().getBanner(
            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
            lc = "my",
            type = "$form"
        )
        Log.d(TAG, "mainBanner : ${response.data}")
        Log.d(TAG, "mainBanner : ${response.status}")
        Log.d(TAG, "mainBanner : ${response.message}")
        return response.data!!
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
        super.onResume()
        scrollJobCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!
        policyViewModel.initViewModel()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
//        binding.maternityInfoRadioButton.setTextColor(requireContext().getColor(R.color.black))
//        binding.LocaleInfoRadioButton.setTextColor(requireContext().getColor(R.color.gray))
//        binding.radioGroup.setOnCheckedChangeListener { radioGroup, checkId ->
//            Log.d(TAG, "in setOnCheckedChangeListener")
//            when (checkId) {
//                binding.LocaleInfoRadioButton.id -> {
//                    form = 2
//                    binding.maternityInfoRadioButton.setTextColor(requireContext().getColor(R.color.gray))
//                    binding.LocaleInfoRadioButton.setTextColor(requireContext().getColor(R.color.black))
//                    initBannerData()
//                }
//
//                binding.maternityInfoRadioButton.id -> {
//                    form = 1
//                    binding.maternityInfoRadioButton.setTextColor(requireContext().getColor(R.color.black))
//                    binding.LocaleInfoRadioButton.setTextColor(requireContext().getColor(R.color.gray))
//                    initBannerData()
//                }
//            }
//        }
        initCompanyList()
        initPolicyList()
        initBannerData()

//        binding.radioGroup.check(binding.maternityInfoRadioButton.id)
        binding.bannerViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {  //사용자가 스크롤 했을때 position 수정
                super.onPageSelected(position)
                bannerPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
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

//    private fun initStatusViewPager( userWeekStatus : UserWeekStatus, userData : UserDataResponses) {
//        statusAdapter = StatusAdapter(userWeekStatus, userData)
//        statusAdapter.notifyItemRemoved(0)
//        with(binding) {
//            statusViewPager.adapter = statusAdapter
//            statusViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
//            ciIndicator.setViewPager(statusViewPager)
//        }
//    }

//    private fun setUserWeekStatus(){
//        lifecycleScope.launch(Dispatchers.IO){
//
//            val userStatus : Deferred<UserWeekStatus> = async {
//                var data : UserWeekStatus = UserWeekStatus()
//                kotlin.runCatching {
//                    RetrofitBuilder.getHttpMainService().getUserWeekStatus(
//                        accessToken = "Bearer ${tokenDao.getMembers().accessToken}"
//                    )
//                }.onSuccess { result ->
//                    Log.d(TAG, "setUserWeekStatusResult : ${result}")
//                    data = result.data!!
//                }.onFailure { result ->
//                    result.printStackTrace()
//                    if (result is HttpException) {
//                        val errorBody = result.response()?.errorBody()?.string()
//                        result.response()?.code()
//
//                        Log.e(TAG, "Error body: $errorBody")
//                    }
//                }
//                data
//            }

//            val userData : Deferred<UserDataResponses> = async {
//                var data : UserDataResponses = UserDataResponses()
//                kotlin.runCatching {
//                    RetrofitBuilder.getCommonService().getProfile(
//                        accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
//                        email = tokenDao.getMembers().email
//                    )
//                }.onSuccess { result ->
//                    Log.d(TAG, "setUserWeekStatusResult : ${result}")
//                    data = result.data!!
//                }.onFailure { result ->
//                    result.printStackTrace()
//                    if (result is HttpException) {
//                        val errorBody = result.response()?.errorBody()?.string()
//                        Log.e(TAG, "Error body: $errorBody")
//                    }
//                }
//                data
//            }
//            launch(Dispatchers.Main) {
//                initStatusViewPager(
//                    userData = userData.await(),
//                    userWeekStatus = userStatus.await()
//                )
//            }


//        }
//    }

    private fun setBannerViewPager() {
        bannerAdapter = MainBannerAdapter(
            context = requireContext(),
            bannerList = bannerList
        )
        bannerAdapter.notifyItemRemoved(0)
        with(binding) {
            bannerViewPager.adapter = bannerAdapter
            bannerViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            bannerIndicator
        }
    }


    private fun setCompanyRecyclerView() {
        val list = mutableListOf<CompanyDataResponses>()
        if (companyList.isNotEmpty()) {
            for (i in 0..2) {
                if (companyList.size == i + 1) {
                    break
                } else {
                    list.add(companyList[i])
                }
            }
            rankAdapter = CompanyRankAdapter()
                { position ->
                    kotlin.runCatching {
                        findCompanyViewModel.id.value = position
                        findNavController().navigate(R.id.action_mainFragment_to_companyDetailsFragment)
                    }
                }

        }

        rankAdapter.setCompanyList(list)
        rankAdapter.notifyItemRemoved(0)
        with(binding) {
//            companyRecyclerView.addItemDecoration(CompanyOffsetItemDecoration(25))
            companyRecyclerView.adapter = rankAdapter
        }

    }

    private fun setPolicyRecyclerView() {
        val list = mutableListOf<PolicyListResponse>()
        if (policyData.isNotEmpty()) {
            for (i in 0..2) {
                if (policyData.size == i + 1) {
                    break
                } else {
                    list.add(policyData[i])
                }
            }
        }
        policyViewModel.setPolicyList(list)
        policyAdapter = PolicyRecyclerView(list.toList(), "") {position->

            kotlin.runCatching {
                policyViewModel.setPolicyId(position)
                findNavController().navigate(R.id.action_mainFragment_to_policyContentFragment)
            }
        }
        policyAdapter.notifyItemRemoved(0)
        with(binding) {
            policyRecyclerView.adapter = policyAdapter
        }

    }

    private fun initBannerData() {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                Log.d(TAG, "token : ${tokenDao.getMembers().accessToken}")
                bannerList = getBanner()

            }.onSuccess {
                Log.d(TAG, "succeed")
                launch(Dispatchers.Main) {
                    bannerPosition =
                        Int.MAX_VALUE / 2 - ceil(bannerList.size.toDouble() / 2).toInt()
                    setBannerViewPager()
                }
            }.onFailure { e ->
                Log.e(TAG, "$e")
                e.stackTrace
                Log.e(TAG, e.message!!)
                launch(Dispatchers.Main) {
                    bannerList = listOf(BannerResponses())
                    bannerPosition = 0
                    setBannerViewPager()
                }
            }
        }
    }

    private fun initCompanyList() {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                 RetrofitBuilder.getMainService().getCompanyData(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                    page = 1,
                    size = 10
                )
            }.onSuccess {
                companyData = it
                companyList = it.data!!
                Log.d(TAG, "initCompanyRecyclerView: ${it.data}")
                launch(Dispatchers.Main) {
                    setCompanyRecyclerView()
                }
            }.onFailure {
                it.stackTrace
                companyList =
                    listOf(CompanyDataResponses(), CompanyDataResponses(), CompanyDataResponses())
                Log.e(TAG, "initCompanyRecyclerView: ${it.message.toString()}")
                launch(Dispatchers.Main) {
                    setCompanyRecyclerView()
                }
            }
        }
    }

    private fun initPolicyList() {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getPolicyService().getPolicyList(
                    type = "104030"
                )
            }.onSuccess {
                policyData = it.data!!
                launch(Dispatchers.Main) {
                    setPolicyRecyclerView()

                }
            }.onFailure {
                it.stackTrace
                policyData =
                    listOf(PolicyListResponse(), PolicyListResponse(), PolicyListResponse())
                Log.e(TAG, "initCompanyRecyclerView: ${it.message.toString()}")
                launch(Dispatchers.Main) {
                    setPolicyRecyclerView()
                }
            }
        }
    }

}