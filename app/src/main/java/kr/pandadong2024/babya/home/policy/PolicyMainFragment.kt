package kr.pandadong2024.babya.home.policy

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.pandadong2024.babya.databinding.FragmentPolicyMainBinding
import kr.pandadong2024.babya.home.todo_list.adapter.PolicyCategoryAdapter
import kr.pandadong2024.babya.home.todo_list.decoration.PolicyCategoryItemDecoration

class PolicyMainFragment : Fragment() {
    val TAG = "PolicyMainFragment"

    var _binding: FragmentPolicyMainBinding? = null
    val binding get() = _binding!!

    private var _category : MutableList<String> = mutableListOf<String>("지역")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPolicyMainBinding.inflate(inflater, container, false)


        setCategory(categoryList = _category)

        return binding.root
    }

    private fun setCategory(
        categoryList: List<String>,
    ) {
        val todoCategoryAdapter = PolicyCategoryAdapter(
            localCategoryList = categoryList,
            flash = { position, localCategoryList ->

                if (position != 0){
                    localCategoryList.removeAt(position)
                    _category = localCategoryList
                }
                else{
                    Log.d(TAG, "show aaa")
                    // TODO : show BottomSheet
                }

                setCategory(localCategoryList)
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