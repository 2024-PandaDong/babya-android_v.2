package kr.pandadong2024.babya.home.policy

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentPolicyMainBinding
import kr.pandadong2024.babya.home.policy.adapter.PolicyRecyclerView
import kr.pandadong2024.babya.home.policy.bottom_sheet.PolicyBottomSheet
import kr.pandadong2024.babya.home.policy.decoration.PolicyItemDecoration
import kr.pandadong2024.babya.home.policy.viewmdole.PolicyViewModel
import kr.pandadong2024.babya.home.todo_list.adapter.PolicyCategoryAdapter
import kr.pandadong2024.babya.home.todo_list.decoration.PolicyCategoryItemDecoration

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

        viewModel.tagsList.observe(viewLifecycleOwner) {
            Log.d(TAG, "changed")
            setCategory(viewModel.tagsList.value!!)
        }
        binding.tagEditText.setOnClickListener {
            val bottomSheetDialog =
                PolicyBottomSheet() { tagList ->
                    setCategory(tagList)
                }

            bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
            Log.d(TAG, "show aaa")
        }

        setCategory(categoryList = viewModel.tagsList.value!!)
        setRecyclerView(_policy)

        return binding.root
    }

    private fun setRecyclerView(policyList: List<String>) {
        val recyclerAdapter = PolicyRecyclerView(policyList = policyList) {
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

    private fun setCategory(
        categoryList: List<String>,
    ) {
        val todoCategoryAdapter = PolicyCategoryAdapter(
            localCategoryList = categoryList,
            flash = { position, localCategoryList ->

//                if (position != 0){
//                    localCategoryList.removeAt(position)
//                    _category = localCategoryList
                setCategory(viewModel.tagsList.value!!)
//                }
//                else{
//                    val bottomSheetDialog = PolicyBottomSheet(localCategoryList){
//                            tagList ->
//                        _category = tagList.toMutableList()
//                        setCategory(tagList)
//                    }
//
//                    bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
//                    Log.d(TAG, "show aaa")
//
//                    // TODO : show BottomSheet
//                }


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
}