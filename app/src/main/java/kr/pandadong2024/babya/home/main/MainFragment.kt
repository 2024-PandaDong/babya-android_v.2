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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.delay
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.MyApplication.Companion.prefs
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentMainBinding
import kr.pandadong2024.babya.home.find_company.find_company_viewModel.FindCompanyViewModel
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
import kotlin.math.ceil

class MainFragment : Fragment() {

    private lateinit var bannerList: List<BannerResponses>
    private lateinit var companyList: List<CompanyListResponses>
    private lateinit var companyData: BaseResponse<List<CompanyListResponses>>
    private lateinit var policyData: List<PolicyListResponse>
    private lateinit var bannerAdapter: MainBannerAdapter
    private lateinit var rankAdapter: CompanyRankAdapter
    private lateinit var policyAdapter: PolicyRecyclerView
    private lateinit var infiniteViewPager: ViewPager2
    private val findCompanyViewModel by activityViewModels<FindCompanyViewModel>()

    private val policyViewModel by activityViewModels<PolicyViewModel>()
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private val commonViewModel by activityViewModels<CommonViewModel>()

    private lateinit var accessToken: String
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var bannerPosition = 0
    private var form = 1 // 임산부인지 지역별인지

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
                accessToken = BabyaDB.getInstance(requireContext())?.tokenDao()
                    ?.getMembers()?.accessToken.toString()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        policyViewModel.initViewModel()
        prefs.completeQuiz = false
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
        profileViewModel.setAccessToken(accessToken)
        commonViewModel.setAccessToken(accessToken)
        profileViewModel.setAccessToken(accessToken)
        profileViewModel.accessToken.observe(viewLifecycleOwner) {
            if (it != "") {
                profileViewModel.getUserLocalCode()
                profileViewModel.getUserData()
            }
        }

        profileViewModel.userLocalCode.observe(viewLifecycleOwner) {
            if (it != "") {
                Log.d("userLocalCode", "code : $it")
                if (it.length == 2) {
                    policyViewModel.setTagList(getMemberLocalCode(it))
                    policyViewModel.setUserRegionList(
                        listOf(
                            getLocalByCode(
                                getMemberLocalCode(
                                    it
                                ).toString()
                            ), getRegionByCode(getMemberLocalCode(it))
                        )
                    )
                    getPolicyList(it)
                } else {
                    policyViewModel.setTagList(it.toInt())
                    getPolicyList(it)
                }
            }
        }

        binding.policyMoreText.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_policyMainFragment)
        }
        binding.companyMoreText.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_findCompanyFragment2)
        }

        initCompanyList()
        initBannerData()

        binding.bannerViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {  //사용자가 스크롤 했을때 position 수정
                super.onPageSelected(position)
                bannerPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    when (infiniteViewPager.currentItem) {
                        (bannerList.size+1) -> {
                            binding.bannerIndicator.visibility = View.GONE
                            infiniteViewPager.setCurrentItem(1, false)
                        }
                        0 -> {
                            binding.bannerIndicator.visibility = View.GONE
                            infiniteViewPager.setCurrentItem(bannerList.size, false)
                        }
                    }
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

    private fun setBannerViewPager() {
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


    private fun setCompanyRecyclerView() {
        val list = mutableListOf<CompanyListResponses>()
        if (companyList.isNotEmpty()) {
            for (i in 0..2) {
                if (companyList.size == i + 1) {
                    break
                } else {
                    list.add(companyList[i])
                }
            }

            rankAdapter = CompanyRankAdapter() { position ->
                kotlin.runCatching {
                    findCompanyViewModel.id.value = position
                    findNavController().navigate(R.id.action_mainFragment_to_companyDetailsFragment)
                }
            }

        }

        rankAdapter.setCompanyList(list.toMutableList())
        rankAdapter.notifyItemRemoved(0)
        with(binding) {
            companyRecyclerView.adapter = rankAdapter
        }
    }

    private fun setPolicyRecyclerView() {
        val list = mutableListOf<PolicyListResponse>()
        if (policyData.isNotEmpty()) {
            for (i in 0..2) {
                if (policyData.size == i + 1) {
                    Log.d("setPolicyRecyclerView", "list break")
                    break
                } else {
                    Log.d("setPolicyRecyclerView", "list add")
                    list.add(policyData[i])
                }
            }
        }

        policyViewModel.setPolicyList(list)

        policyAdapter = PolicyRecyclerView(list.toList(),
            tag = policyViewModel.tagsList.value?.get(1) ?: ""
        ) { position ->
            policyViewModel.setPolicyId(position)
            findNavController().navigate(R.id.action_mainFragment_to_policyContentFragment)
        }
        policyAdapter.notifyItemRemoved(0)
        Log.d("setPolicyRecyclerView", "list : $list")
        with(binding) {
            policyRecyclerView.adapter = policyAdapter
        }
    }

    private fun initBannerData() {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                bannerList = getBanner()

            }.onSuccess {
                launch(Dispatchers.Main) {
                    bannerPosition =
                        Int.MAX_VALUE / 2 - ceil(bannerList.size.toDouble() / 2).toInt()
                    setBannerViewPager()
                }
            }.onFailure { e ->
                e.stackTrace
                launch(Dispatchers.Main) {
                    bannerList = listOf()
                    bannerPosition = 0
                    setBannerViewPager()
                }
            }
        }
    }

    private fun initCompanyList() {
        Log.d("initCompanyList", "it : in")
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getCompanyService().getCompanyList(
                    accessToken = "Bearer $accessToken",
                    page = 1,
                    size = 10
                )
            }.onSuccess {
                launch(Dispatchers.Main) {
                    if (it.status == 200) {
                        companyData = it
                        companyList = it.data ?: listOf()
                        setCompanyRecyclerView()
                    } else {

                        Log.d("initCompanyList", "it : ${it.message}, st : ${it.status}")
                    }
                }
            }.onFailure {
                Log.d("initCompanyList", "it : in false")
                it.printStackTrace()
                if (it is retrofit2.HttpException) {
                    val errorBody = it.response()?.raw()?.request
                    Log.d("initCompanyList", "it : $errorBody")
                    Log.d("initCompanyList", "it : ${it.response()?.body()}")
                }
                it.stackTrace
                companyList =
                    listOf()
                launch(Dispatchers.Main) {
                    setCompanyRecyclerView()
                }
            }
        }
    }


    private fun getPolicyList(code : String  =policyViewModel.policyId.value.toString()) {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getPolicyService().getPolicyList(
                    type = code,
                    keyword = ""
                )
            }.onSuccess {
                policyData = it.data ?: listOf()
                launch(Dispatchers.Main) {
                    setPolicyRecyclerView()
                }
            }.onFailure {
                it.stackTrace
                policyData =
                    listOf(PolicyListResponse(), PolicyListResponse(), PolicyListResponse())
                launch(Dispatchers.Main) {
                    setPolicyRecyclerView()
                }
            }
        }
    }

}