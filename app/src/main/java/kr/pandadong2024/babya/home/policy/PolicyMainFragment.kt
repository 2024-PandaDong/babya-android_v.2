package kr.pandadong2024.babya.home.policy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.remote.responses.Policy.PolicyListResponse
import kr.pandadong2024.babya.util.BottomControllable
import kr.pandadong2024.babya.util.shortToast


class PolicyMainFragment : Fragment() {
    val TAG = "PolicyMainFragment"
    private val viewModel by activityViewModels<PolicyViewModel>()
    var _binding: FragmentPolicyMainBinding? = null
    val binding get() = _binding!!

    var tokenDao: TokenDAO? = null

    private var isSearchActivated = false

    private var searchKeyWord: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPolicyMainBinding.inflate(inflater, container, false)
        tokenDao = BabyaDB.getInstance(requireContext())?.tokenDao()
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_policyMainFragment_to_mainFragment)
            viewModel.initKeyword()
        }

        binding.swipeRefreshLayout.setOnRefreshListener(
            SwipeRefreshLayout.OnRefreshListener {
                setCategory(categoryList = viewModel.tagsList.value!!)
            }
        )

        viewModel.policySearchKeyWord.observe(viewLifecycleOwner) {
            searchKeyWord = it
            if (viewModel.tagsList.value?.isNotEmpty() == true) {
                selectPolicy(
                    mainTag = viewModel.tagsList.value?.get(0) ?: "알 수 없음",
                    subTag = viewModel.tagsList.value?.get(1) ?: "알 수 없음",
                    keyWord = searchKeyWord
                )
            } else {
                selectPolicy(
                    mainTag = viewModel.userRegionList.value?.get(0) ?: "알 수 없음",
                    subTag = viewModel.userRegionList.value?.get(1) ?: "알 수 없음",
                    keyWord = searchKeyWord
                )
            }
            if (isSearchActivated && it != "") {
                binding.policyTitleLayout.visibility = View.GONE
            } else {
                binding.policyTitleLayout.visibility = View.VISIBLE
            }
        }

        viewModel.isOpenSearchView.observe(viewLifecycleOwner) {
            isSearchActivated = it
            if (it) {
                binding.searchEditText.visibility = View.VISIBLE
            } else {
                binding.searchEditText.visibility = View.GONE
            }
        }

        binding.searchButton.setOnClickListener {
            viewModel.setPolicySearchKeyWord(binding.searchEditText.text.toString())
            if (!(isSearchActivated && binding.searchEditText.text.isNotBlank())) {
                viewModel.changeOpenSearchView()
            }
        }



        getProfileData()

        viewModel.tagsList.observe(viewLifecycleOwner) {
            setCategory(categoryList = it)
        }

        //결과 나왔을 때 리사이 클러뷰 업데이트
        viewModel.policyList.observe(viewLifecycleOwner) {
            setRecyclerView(it, viewModel.tagsList.value!![1])
        }

//        selectPolicy(viewModel.tagsList.value?.get(0) ?: "대구광역시",viewModel.tagsList.value?.get(1) ?: "수성구", "")

        binding.tagEditText.setOnClickListener {
            Log.d("setOnClickListener", "click tagEditText")
            val bottomSheetDialog =
                PolicyBottomSheet() { tag ->
                    selectPolicy(
                        mainTag = viewModel.tagsList.value?.get(0) ?: "대구광역시",
                        subTag = tag,
                        keyWord = ""
                    )
                    viewModel.initKeyword()
                }

            bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
        }


        return binding.root
    }

    private fun getProfileData() {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getProfileService().getProfile(
                    accessToken = "Bearer ${tokenDao?.getMembers()?.accessToken.toString()}",
                    email = "my"
                )
            }.onSuccess { result ->
                launch(Dispatchers.Main) {
                    binding.titleText.text = "${result.data?.nickname}님을 위한 추천 정책"
                    binding.tagTitleText.text = "${result.data?.nickname}님의 지역"
                    binding.subTitleText.text = "지역에 따라 정책을 모았어요"
                }
            }.onFailure { result ->
                result.printStackTrace()
                requireContext().shortToast("인터넷 연결을 확인해 주세요")
            }
        }
    }


    private fun setRecyclerView(policyList: List<PolicyListResponse>, tag: String) {
        val recyclerAdapter = PolicyRecyclerView(policyList = policyList, tag) { position ->
            viewModel.setPolicyId(position)
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
                    Log.d("selectPolicy", "result : $result")
                        withContext(Dispatchers.Main) {
                            viewModel.setPolicyList(result.data ?: listOf())
                            if (!viewModel.tagsList.value.isNullOrEmpty()) {
                                setRecyclerView(
                                    policyList = result.data ?: listOf(),
                                    tag = "${viewModel.tagsList.value?.get(0)} ${
                                        viewModel.tagsList.value?.get(
                                            1
                                        )
                                    } 보건소"
                                )
                            }
                        }
                }.onFailure { result ->
                    result.printStackTrace()
                    requireContext().shortToast("인터넷 연결을 확인해 주세요")
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
                setCategory(viewModel.tagsList.value!!)
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
