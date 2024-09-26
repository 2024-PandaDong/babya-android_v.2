package kr.pandadong2024.babya.home.policy

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentPolicyMainBinding
import kr.pandadong2024.babya.home.policy.adapter.PolicyRecyclerView
import kr.pandadong2024.babya.home.policy.bottom_sheet.PolicyBottomSheet
import kr.pandadong2024.babya.home.policy.decoration.PolicyItemDecoration
import kr.pandadong2024.babya.home.policy.viewmdole.PolicyViewModel
import kr.pandadong2024.babya.home.todo_list.adapter.PolicyCategoryAdapter
import kr.pandadong2024.babya.home.todo_list.decoration.PolicyCategoryItemDecoration
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.remote.responses.Policy.PolicyListResponse
import kr.pandadong2024.babya.util.BottomControllable
import retrofit2.HttpException

class PolicyMainFragment : Fragment() {
    val TAG = "PolicyMainFragment"
    private val viewModel by activityViewModels<PolicyViewModel>()
    var _binding: FragmentPolicyMainBinding? = null
    val binding get() = _binding!!

    var tokenDao: TokenDAO? = null

    var isSearchActivated = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPolicyMainBinding.inflate(inflater, container, false)
        tokenDao = BabyaDB.getInstance(requireContext())?.tokenDao()
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_policyMainFragment_to_mainFragment)
        }

        binding.swipeRefreshLayout.setOnRefreshListener(
            SwipeRefreshLayout.OnRefreshListener {
                Log.d("", "atest")
                setCategory(categoryList = viewModel.tagsList.value!!)
            }
        )

        binding.searchButton.setOnClickListener {
            Log.d(TAG, "searchButton")
            if (isSearchActivated && binding.searchEditText.text.isNotBlank()) {
                //TODO : 검색하기
                Log.d(TAG, "searchButton1")
            } else {
                if (isSearchActivated) {
                    Log.d(TAG, "searchButton2")
                    binding.searchEditText.visibility = View.GONE
                } else {
                    Log.d(TAG, "searchButton3")
                    binding.searchEditText.visibility = View.VISIBLE
                }
                Log.d(TAG, "searchButton4")
                isSearchActivated = isSearchActivated.not()
            }
        }



        getProfileData()

        viewModel.tagsList.observe(viewLifecycleOwner) {
            Log.d(TAG, "changed")
            setCategory(categoryList = it)
        }

        viewModel.policyList.observe(viewLifecycleOwner) {
            setRecyclerView(it, viewModel.tagsList.value!![1])
        }
        selectPolicy(viewModel.tagsList.value!![1] ?: "수성구")
        binding.tagEditText.setOnClickListener {
            val bottomSheetDialog =
                PolicyBottomSheet() { tag ->
                    selectPolicy(tag)
                    Log.d(TAG,"tag : ${tag}")
                }

            bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
            Log.d(TAG, "show aaa")
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
                Log.d(TAG, "getProfileData: ${result.data}")

                launch(Dispatchers.Main) {
                    binding.titleText.text = "${result.data?.nickname}님을 위한 추천 정책"
                    binding.tagTitleText.text = "${result.data?.nickname}님의 지역"
//                    binding.argText.text = "나이: ${result.data?.age}살"
//                    binding.dayText.text = "D-Day: ${result.data?.dDay}일"


//                    binding.weddingYearText.text = if (result.data?.marriedYears == 0) {
//                        "결혼: 미혼"
//                    } else {
//                        "결혼: ${result.data?.marriedYears}년차"
//                    }
                }
            }.onFailure { result ->
                Log.d(TAG, "onCreateView: ${result.message}")
                result.printStackTrace()
                Log.d(TAG, "onCreateView: 서버연결 실패")
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
            Log.d("TAG", "itemDecorationCount : ${policyListRecyclerView.itemDecorationCount}")
            if (policyListRecyclerView.itemDecorationCount == 0) policyListRecyclerView.addItemDecoration(
                PolicyItemDecoration(policyList.size)
            )
        }


    }

    private fun selectPolicy(tag: String) {
        val tagNumber = encodingLocateNumber(tag)
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getPolicyService().getPolicyList(tagNumber)
            }.onSuccess { result ->
                Log.d(TAG, "data : ${result.data}")
                if (result.status == 200) {
                    withContext(Dispatchers.Main) {
                        Log.d(TAG, "200,\nstatus : ${result.data}")
                        viewModel.setPolicyList(result.data!!)
                        setRecyclerView(
                            policyList = result.data,
                            tag = "${viewModel.tagsList.value!![0]} ${viewModel.tagsList.value!![1]} 보건소"
                        )

                    }
                } else {
                    Log.d(TAG, "200이 아닌 다른 상태,\nstatus : ${result.status}")
                }
            }.onFailure { result ->
                result.printStackTrace()
                Log.e(TAG, "result = ${result.message}")
                if (result is HttpException) {
                    val errorBody = result.response()?.errorBody()?.string()
                    Log.e(TAG, "Error body: $errorBody")
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
            Log.d("TAG", "itemDecorationCount : ${categoryRecyclerView.itemDecorationCount}")
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


    private fun encodingLocateNumber(locationList: String): String {

        return when (locationList) {
            "남구" -> "104010"
            "달서구" -> "104020"
            "달성군" -> "104030"
            "동구" -> "104040"
            "북구" -> "104050"
            "서구" -> "104060"
            "수성구" -> "104070"
            "중구" -> "104080"
            "군위군" -> "104090"
            else -> {
                "104030"
            }
        }
    }
}
