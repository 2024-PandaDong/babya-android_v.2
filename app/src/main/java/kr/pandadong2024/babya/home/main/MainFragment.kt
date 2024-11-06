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
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
        // TODO : 영마이스터 끝나고 코드 115번 위치 코드 지우기
        prefs.remove()
        mainViewModel.getBannerData()
        findCompanyViewModel.initCompanyList()
        profileViewModel.getUserLocalCode()
        profileViewModel.getUserData()

        profileViewModel.userLocalCode.observe(viewLifecycleOwner) {
            if (it.isEmpty()) return@observe
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
                policyViewModel.getPolicyList(it)

            } else {
                policyViewModel.setTagList(it.toInt())
                policyViewModel.getPolicyList(it)
            }
        }

        findCompanyViewModel.companyList.observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
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
            override fun onPageSelected(position: Int) {  //사용자가 스크롤 했을때 position 수정
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


    private fun setCompanyRecyclerView( companyList: List<CompanyListResponses>) {
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
                findCompanyViewModel.id.value = position
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
                    Log.d("setPolicyRecyclerView", "list break")
                    break
                } else {
                    Log.d("setPolicyRecyclerView", "list add")
                    list.add(policyList[i])
                }
            }
        }

        policyViewModel.setPolicyList(list)

        policyAdapter = PolicyRecyclerView(
            list.toList(),
            tag = "${policyViewModel.tagsList.value?.get(0)} ${policyViewModel.tagsList.value?.get(1)}"
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
}