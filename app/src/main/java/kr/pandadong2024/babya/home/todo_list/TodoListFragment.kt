package kr.pandadong2024.babya.home.todo_list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.databinding.FragmentTodoListBinding
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.util.BottomControllable
import retrofit2.HttpException


class TodoListFragment : Fragment() {

    private var _binding: FragmentTodoListBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenDao: TokenDAO
    private val TAG = "TodoListFragment"

    private var selectedPosition : Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTodoListBinding.inflate(inflater, container, false)
        tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        getCategory()

        binding.todoListAddTodoButton.setOnClickListener {
            val bottomSheetDialog = TodoBottomSheet(
                context = requireContext(),
                postTodo = { request ->
                    Log.d(TAG, "data : $it")
                    lifecycleScope.launch(Dispatchers.IO){
                        kotlin.runCatching {
                            RetrofitBuilder.getTodoListService().createTodo(
                                accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                                requestBody = request
                            )
                        }.onSuccess {result ->
                            Log.d(TAG, "createTodo message : ${result.message}")
                            Log.d(TAG, "createTodo status : ${result.status}")
                        }.onFailure {result ->
                            result.printStackTrace()
                            if (result is HttpException) {
                                val errorBody = result.response()?.errorBody()
                                Log.e(TAG, "Error body: $errorBody")
                            }
                        }
                    }
                }
            )
            bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
        }

        return binding.root
    }

    private fun setCategory(categoryList : List<String>){
        val todoCategoryAdapter = TodoCategoryAdapter(
            categoryList = categoryList,
            selectedItemPosition = selectedPosition,
            flash = {position ->
                selectedPosition = position

            }
        )
        todoCategoryAdapter.notifyItemRemoved(0)
        with(binding){
            categoryRecyclerView.adapter = todoCategoryAdapter
        }

    }

    private fun getCategory(){
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getTodoListService().getCategory(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}"
                )
            }.onSuccess { result ->
                val categoryList = result.data?.category?.toMutableList()
                categoryList?.add(0, "전체")
                Log.d(TAG, "getCategory message : ${result.message}")
                Log.d(TAG, "getCategory message : ${result.data}")
                Log.d(TAG, "getCategory status : ${result.status}")
                launch (Dispatchers.Main) {
                    setCategory(categoryList!!.toList())
                }

            }.onFailure { result ->
                result.printStackTrace()
                if (result is HttpException) {
                    val errorBody = result.response()?.errorBody()
                    Log.e(TAG, "Error body: $errorBody")
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }


}