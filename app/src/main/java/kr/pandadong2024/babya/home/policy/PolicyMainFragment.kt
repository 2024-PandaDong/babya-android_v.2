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
import kr.pandadong2024.babya.server.remote.responses.Policy.PolicyResponse
import kr.pandadong2024.babya.util.BottomControllable
import retrofit2.HttpException

class PolicyMainFragment : Fragment() {
    val TAG = "PolicyMainFragment"
    private val viewModel by activityViewModels<PolicyViewModel>()
    var _binding: FragmentPolicyMainBinding? = null
    val binding get() = _binding!!

    private var _policy: MutableList<String> = mutableListOf<String>(
        "지역",
        "지역",
        "지역",
        "지역",
        "지역",
        "지역",
        "지역",
        "지역",
        "지역",
        "지역",
        "지역",
        "지역",
        "지역",
        "지역",
        "지역",
        "지역",
        "지역",
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPolicyMainBinding.inflate(inflater, container, false)
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_policyMainFragment_to_mainFragment)
        }
        viewModel.tagsList.observe(viewLifecycleOwner) {
            Log.d(TAG, "changed")
            setCategory(categoryList = it)
        }

        viewModel.policyList.observe(viewLifecycleOwner) {
            setRecyclerView(it, viewModel.tagsList.value!![1])
        }
        selectPolicy(viewModel.tagsList.value!![1])
        binding.tagEditText.setOnClickListener {
            val bottomSheetDialog =
                PolicyBottomSheet() { tag ->
                    selectPolicy(tag)
                }

            bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
            Log.d(TAG, "show aaa")
        }


        return binding.root
    }

    private fun setRecyclerView(policyList: List<PolicyResponse>, tag: String) {
        val recyclerAdapter = PolicyRecyclerView(policyList = policyList, tag) {position ->
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
                RetrofitBuilder.getPolicyService().getPolicy(tagNumber)
            }.onSuccess { result ->
                if (result.status == 200) {
                    withContext(Dispatchers.Main){
                        Log.d(TAG, "200,\nstatus : ${result.data}")
                        setRecyclerView(
                            policyList = result.data!!,
                            tag = tag
                        )
                    }
                }else{
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

        }
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
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
