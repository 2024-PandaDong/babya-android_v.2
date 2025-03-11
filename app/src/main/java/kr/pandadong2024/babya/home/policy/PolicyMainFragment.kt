package kr.pandadong2024.babya.home.policy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentPolicyMainBinding
import kr.pandadong2024.babya.home.policy.adapter.PolicyCategoryAdapter
import kr.pandadong2024.babya.home.policy.adapter.PolicyRecyclerView
import kr.pandadong2024.babya.home.policy.bottom_sheet.PolicyBottomSheet
import kr.pandadong2024.babya.home.policy.decoration.PolicyCategoryItemDecoration
import kr.pandadong2024.babya.home.policy.decoration.PolicyItemDecoration
import kr.pandadong2024.babya.home.policy.viewmdole.PolicyViewModel
import kr.pandadong2024.babya.home.profile.profileviewmodle.ProfileViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.DAO.TokenDAO
import kr.pandadong2024.babya.server.remote.responses.Policy.PolicyListResponse
import kr.pandadong2024.babya.util.BottomControllable
import kr.pandadong2024.babya.util.shortToast

@AndroidEntryPoint
class PolicyMainFragment : Fragment() {
    val TAG = "PolicyMainFragment"
    private val policyViewModel : PolicyViewModel by viewModels()
    private val profileViewModel : ProfileViewModel by viewModels()
    var _binding: FragmentPolicyMainBinding? = null
    val binding get() = _binding!!

    private lateinit var token : String

    private var isSearchActivated = false

    private var searchKeyWord: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPolicyMainBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            token = policyViewModel.getToken()?.accessToken.toString()
        }
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_policyMainFragment_to_mainFragment)
            policyViewModel.initKeyword()
        }
        profileViewModel.getUserData()

        binding.swipeRefreshLayout.setOnRefreshListener(
            SwipeRefreshLayout.OnRefreshListener {
                setCategory(categoryList = policyViewModel.tagsList.value ?: listOf())
            }
        )

        policyViewModel.policySearchKeyWord.observe(viewLifecycleOwner) {
            searchKeyWord = it
            if (policyViewModel.tagsList.value?.isNotEmpty() == true) {
                selectPolicy(
                    mainTag = policyViewModel.tagsList.value?.get(0) ?: "알 수 없음",
                    subTag = policyViewModel.tagsList.value?.get(1) ?: "알 수 없음",
                    keyWord = searchKeyWord
                )
            } else {
                if (policyViewModel.userRegionList.value?.isNotEmpty() == true) {
                    selectPolicy(
                        mainTag = policyViewModel.userRegionList.value?.get(0) ?: "알 수 없음",
                        subTag = policyViewModel.userRegionList.value?.get(1) ?: "알 수 없음",
                        keyWord = searchKeyWord
                    )
                }
            }
            if (isSearchActivated && it != "") {
                binding.policyTitleLayout.visibility = View.GONE
            } else {
                binding.policyTitleLayout.visibility = View.VISIBLE
            }
        }

        policyViewModel.isOpenSearchView.observe(viewLifecycleOwner) {
            isSearchActivated = it
            if (it) {
                binding.searchEditText.visibility = View.VISIBLE
            } else {
                binding.searchEditText.visibility = View.GONE
            }
        }

        binding.searchButton.setOnClickListener {
            policyViewModel.setPolicySearchKeyWord(binding.searchEditText.text.toString())
            if (!(isSearchActivated && binding.searchEditText.text.isNotBlank())) {
                policyViewModel.changeOpenSearchView()
            }
        }

        profileViewModel.userData.observe(viewLifecycleOwner){ userData ->
            Log.d("dbTest", "name : ${userData}")
            binding.titleText.text = "${userData.nickname}님을 위한 추천 정책"
            binding.tagTitleText.text = "${userData.nickname}님의 지역"
            binding.subTitleText.text = "지역에 따라 정책을 모았어요"
        }

        policyViewModel.tagsList.observe(viewLifecycleOwner) {
            setCategory(categoryList = it)
        }

        //결과 나왔을 때 리사이 클러뷰 업데이트
        policyViewModel.policyList.observe(viewLifecycleOwner) {
            if (it.size > 1) {
                setRecyclerView(
                    it,
                    "${policyViewModel.tagsList.value?.get(0) ?: ""} ${policyViewModel.tagsList.value?.get(1) ?: ""}"
                )
            }
        }


        binding.tagEditText.setOnClickListener {
            policyViewModel.setSaveTagList(policyViewModel.tagsList.value)
            val bottomSheetDialog =
                PolicyBottomSheet() { tag ->
                    val tagNumber = getCodeByRegion("${policyViewModel.tagsList.value?.get(0)}_${tag}")
                    policyViewModel.getPolicyList(tagNumber)
                    policyViewModel.initKeyword()
                }
            bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
        }


        return binding.root
    }


    private fun setRecyclerView(policyList: List<PolicyListResponse>, tag: String) {
        val recyclerAdapter = PolicyRecyclerView(policyList = policyList, tag) { position ->
            policyViewModel.setPolicyId(position)
            findNavController().navigate(R.id.action_policyMainFragment_to_policyContentFragment)
        }
        with(binding) {
            recyclerAdapter.notifyItemRemoved(0)
            policyListRecyclerView.adapter = recyclerAdapter
            if (policyListRecyclerView.itemDecorationCount == 0) policyListRecyclerView.addItemDecoration(
                PolicyItemDecoration(policyList.size)
            )
        }
    }

    private fun selectPolicy(mainTag: String, subTag: String, keyWord: String) {
        val tagNumber = getCodeByRegion("${mainTag}_${subTag}")
        if (tagNumber != "-1") {
            lifecycleScope.launch(Dispatchers.IO) {
                kotlin.runCatching {
                    RetrofitBuilder.getPolicyService().getPolicyList(tagNumber, keyWord)
                }.onSuccess { result ->
                    withContext(Dispatchers.Main) {
                        policyViewModel.setPolicyList(result.data ?: listOf())
                        if (!policyViewModel.tagsList.value.isNullOrEmpty()) {
                            setRecyclerView(
                                policyList = result.data ?: listOf(),
                                tag = "${policyViewModel.tagsList.value?.get(0)} ${
                                    policyViewModel.tagsList.value?.get(
                                        1
                                    )
                                }"
                            )
                        }
                    }
                }.onFailure { result ->
                    result.printStackTrace()
                    if(isAdded && activity != null) {
                        requireContext().shortToast("인터넷 연결을 확인해 주세요")
                    }
                }
            }
        }
    }

    private fun setCategory(
        categoryList: List<String>,
    ) {
        val todoCategoryAdapter = PolicyCategoryAdapter(
            localCategoryList = categoryList,
            flash = { position, localCategoryList ->
                setCategory(policyViewModel.tagsList.value!!)
            }
        )
        todoCategoryAdapter.notifyItemRemoved(0)
        with(binding) {
            categoryRecyclerView.adapter = todoCategoryAdapter
            if (categoryRecyclerView.itemDecorationCount == 0) categoryRecyclerView.addItemDecoration(
                PolicyCategoryItemDecoration(10, 0, categoryList.size)
            )

            if (binding.swipeRefreshLayout.isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = false
            }

        }
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }
}
