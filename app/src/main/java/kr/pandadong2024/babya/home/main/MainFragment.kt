package kr.pandadong2024.babya.home.main

import android.os.Bundle
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
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentMainBinding
import kr.pandadong2024.babya.home.find_company.find_company_viewModel.FindCompanyViewModel
import kr.pandadong2024.babya.home.policy.adapter.PolicyRecyclerView
import kr.pandadong2024.babya.home.policy.getLocalByCode
import kr.pandadong2024.babya.home.policy.getMemberLocalCode
import kr.pandadong2024.babya.home.policy.getRegionByCode
import kr.pandadong2024.babya.home.policy.viewmdole.PolicyViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
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
    private val findCompanyViewModel by activityViewModels<FindCompanyViewModel>()

    private val policyViewModel by activityViewModels<PolicyViewModel>()

    private lateinit var tokenDao: TokenDAO
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var bannerPosition = 0
    private var form = 1 // 임산부인지 지역별인지

    private suspend fun getBanner(): List<BannerResponses> {
        val response = RetrofitBuilder.getHttpMainService().getBanner(
            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
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


        binding.policyMoreText.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_policyMainFragment)
        }
        binding.companyMoreText.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_findCompanyFragment2)
        }

        initCompanyList()
        initPolicyList()
        initBannerData()

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
                    break
                } else {
                    list.add(policyData[i])
                }
            }
        }

        policyViewModel.setPolicyList(list)
        policyAdapter = PolicyRecyclerView(list.toList(), "") { position ->

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
                RetrofitBuilder.getCompanyService().getCompanyList(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                    page = 1,
                    size = 10
                )
            }.onSuccess {
                companyData = it
                companyList = it.data ?: listOf()
                launch(Dispatchers.Main) {
                    setCompanyRecyclerView()
                }
            }.onFailure {
                it.stackTrace
                companyList =
                    listOf(CompanyListResponses(), CompanyListResponses(), CompanyListResponses())
                launch(Dispatchers.Main) {
                    setCompanyRecyclerView()
                }
            }
        }
    }

    private fun initPolicyList() {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getProfileService().getLocalCode(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}"
                )
            }.onSuccess { result ->
                val response = result.data

                if (response?.length == 2) {
                    withContext(Dispatchers.Main) {
                        policyViewModel.setTagList(getMemberLocalCode(response))
                        policyViewModel.setUserRegionList(
                            listOf(
                                getLocalByCode(
                                    getMemberLocalCode(
                                        response
                                    ).toString()
                                ), getRegionByCode(getMemberLocalCode(response))
                            )
                        )
                        getPolicyList()
                    }
                } else {
                    policyViewModel.setTagList(response?.toInt() ?: 0)
                    getPolicyList()
                }
            }.onFailure {
                it.stackTrace
                companyList =
                    listOf(CompanyListResponses(), CompanyListResponses(), CompanyListResponses())
                launch(Dispatchers.Main) {
                    setCompanyRecyclerView()
                }
            }
        }
    }

    private fun getPolicyList() {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getPolicyService().getPolicyList(
                    type = policyViewModel.policyId.value.toString(),
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