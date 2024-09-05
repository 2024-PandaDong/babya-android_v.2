package kr.pandadong2024.babya.home.todo_list

import android.icu.util.Calendar
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
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentTodoListBinding
import kr.pandadong2024.babya.home.todo_list.adapter.PolicyCategoryAdapter
import kr.pandadong2024.babya.home.todo_list.adapter.PolicyDayAdapter
import kr.pandadong2024.babya.home.todo_list.adapter.TodoCategoryAdapter
import kr.pandadong2024.babya.home.todo_list.decoration.PolicyCategoryItemDecoration
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.remote.request.todo.TodoModifyRequest
import kr.pandadong2024.babya.server.remote.request.todo.TodoRequestBody
import kr.pandadong2024.babya.server.remote.responses.todo.TodoResponses
import kr.pandadong2024.babya.util.BottomControllable
import retrofit2.HttpException
import java.util.GregorianCalendar


class TodoListFragment : Fragment() {
    private val viewModel by activityViewModels<TodoViewModel>()
    private var _binding: FragmentTodoListBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenDao: TokenDAO

    private val gregorianCalendar = GregorianCalendar()
    private val year = gregorianCalendar.get(Calendar.YEAR)
    private val date = gregorianCalendar.get(Calendar.DATE)
    private val month = gregorianCalendar.get(Calendar.MONTH)+1
    private var getToday = String.format("%4d-%02d-%02d", year.toInt(),month.toInt(),date.toInt())
    private val TAG = "TodoListFragment"
    private var allCategoryList = mutableListOf<String>("")


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

        binding.todoListBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_todoListFragment_to_mainFragment)
        }


        binding.todoListAddTodoButton.setOnClickListener {
            val bottomSheetDialog = TodoBottomSheet(
                type = 0,
                todoData = null,
                function = { request ->
                    Log.d(TAG, "data : $request")
                    lifecycleScope.launch(Dispatchers.IO){
                        kotlin.runCatching {
                            RetrofitBuilder.getTodoListService().createTodo(
                                accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                                requestBody = TodoRequestBody(
                                    category = request.category,
                                    content = request.content,
                                    planedDt = request.planedDt
                                )
                            )
                        }.onSuccess {result ->
                            Log.d(TAG, "data : $request")
                            Log.d(TAG, "createTodo message : ${result.message}")
                            Log.d(TAG, "createTodo status : ${result.status}")
                            launch(Dispatchers.Main) {
                                viewModel.isSubmit.value = true
                                getCategory()
                            }
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
            viewModel.isSubmit.observe(viewLifecycleOwner){
                if(viewModel.isSubmit.value!!){
                    bottomSheetDialog.dismiss()
                    Log.d(TAG, "test")
                    viewModel.isSubmit.value = false
                }
            }
        }

        return binding.root
    }


    private fun setCategory(categoryList : List<String>){
        val todoCategoryAdapter = TodoCategoryAdapter(
            categoryList = categoryList,
            selectedItemPosition = selectedPosition,
            flash = {position ->
                selectedPosition = position
                setCategory(categoryList)
                getTodoList(
                    category =  allCategoryList[position],
                    date = getToday
                )
            }
        )
        todoCategoryAdapter.notifyItemRemoved(0)
        with(binding){
            categoryRecyclerView.adapter = todoCategoryAdapter
            Log.d(TAG, "itemDecorationCount : ${categoryRecyclerView.itemDecorationCount}")
            if (categoryRecyclerView.itemDecorationCount == 0)categoryRecyclerView.addItemDecoration(PolicyCategoryItemDecoration(10, categoryList.size))
            categoryRecyclerView.scrollToPosition(selectedPosition)
        }
    }

    private fun checkTodo(todoId : Int, isChecked : Boolean){
        lifecycleScope.launch (Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getTodoListService().checkTodo(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                    isChecked = isChecked,
                    id = todoId
                )
            }.onSuccess {result->
                Log.d(TAG, "check result : $result")

            }.onFailure { result->
                result.printStackTrace()
                if (result is HttpException) {
                    val errorBody = result.response()?.raw()?.request
                    Log.e(TAG, "Error body: $errorBody")
                }
            }
        }
    }

    private fun initDayRecyclerView(todoList : Map<String, List<TodoResponses>>){
        Log.d(TAG, "$todoList")
        val todoAdapter = PolicyDayAdapter(
            todoList = todoList,
            context =  requireContext()
        ){type, todoData ->
            // | 1 : 삭제 | 2 : 수정 | 3 : 체크 |
            when(type){
                1 ->{
                    Log.d(TAG, "Delete")
                    deleteTodo(todoData.todoId!!)
                }
                2 ->{
                    Log.d(TAG, "Modify")
                    modifyTodo(todoData)
                }
                3 ->{
                    Log.d(TAG, "check")
                    checkTodo(
                        todoId = todoData.todoId!!,
                        isChecked = todoData.isChecked!!)
                }
            }

        }
        todoAdapter.notifyItemRemoved(0)
        with(binding){
            todoListRecyclerView.adapter = todoAdapter
        }
    }

    private fun modifyTodo(todoData : TodoResponses){
        val bottomSheetDialog = TodoBottomSheet(
            type = 1,
            todoData = todoData,
            function = { request ->
                Log.d(TAG, "data : $request")
                lifecycleScope.launch(Dispatchers.IO) {
                    kotlin.runCatching {
                        RetrofitBuilder.getTodoListService().modifyTodo(
                            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                            requestBody = TodoModifyRequest(
                                id = request.todoId,
                                category = request.category,
                                content = request.content,
                                planedDt = request.planedDt
                            )
                        )
                    }.onSuccess { result ->
                        Log.d(TAG, "log : $result")
                        launch (Dispatchers.Main){
                            viewModel.isSubmit.value = true
                            getCategory()
                        }
                    }.onFailure { result ->
                        result.printStackTrace()
                        if (result is HttpException) {
                            val errorBody = result.response()?.raw()?.request
                            Log.e(TAG, "Error body: $errorBody")
                        }
                    }
                }
            }
        )
        bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
        viewModel.isSubmit.observe(viewLifecycleOwner){
            if(viewModel.isSubmit.value!!){
                bottomSheetDialog.dismiss()
                Log.d(TAG, "test")
                viewModel.isSubmit.value = false
            }
        }

    }
    private fun deleteTodo(todoId : Int){
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getTodoListService().deleteTodo(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                    id = todoId
                )
            }.onSuccess { result ->
                launch (Dispatchers.Main){
                    getCategory()
                }
            }.onFailure { result ->
                result.printStackTrace()
                if (result is HttpException) {
                    val errorBody = result.response()
                    Log.e(TAG, "Error body: ${errorBody?.raw()?.request}")
                }
            }
        }
    }


    private fun getTodoList(category : String, date: String){
        lifecycleScope.launch (Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getTodoListService().getTodoList(
                    category= category,
                    date = date,
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                )
            }.onSuccess { result ->
                Log.d(TAG, "message : ${result.message}")
                Log.d(TAG, "message : $date")
                val todoList : MutableMap<String, MutableList<TodoResponses>> = mutableMapOf()
                result.data?.forEach{
                    if(todoList.keys.contains(it.planedDt?.substring(8,10)!!)){ //0000-00-00
                        todoList[it.planedDt.substring(8,10)]?.add(it)
                    }
                    else{
                        todoList[it.planedDt.substring(8,10)] = mutableListOf(it)
                    }
                }

                launch (Dispatchers.Main) {
                    initDayRecyclerView(todoList)
                }

            }.onFailure { result ->
                val todoList : MutableMap<String, MutableList<TodoResponses>> = mutableMapOf()
                todoList.put("1", mutableListOf<TodoResponses>())
                todoList.put("2", mutableListOf<TodoResponses>())
                todoList.put("3", mutableListOf<TodoResponses>())
                todoList["1"]?.add(TodoResponses())
                todoList["1"]?.add(TodoResponses())
                todoList["2"]?.add(TodoResponses())
                todoList["3"]?.add(TodoResponses())
                Log.d(TAG, "$todoList")
                result.printStackTrace()
                if (result is HttpException){
                    Log.d(TAG, "getTodoList Error body : ${result.response()?.raw()?.request}")
                }
                launch (Dispatchers.Main) {
                    initDayRecyclerView(todoList)
                }
            }
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
                allCategoryList.addAll(categoryList!!.toList())
                categoryList.add(0, "전체")
                Log.d(TAG, "getCategory message : ${result.message}")
                Log.d(TAG, "getCategory message : ${result.data}")
                Log.d(TAG, "getCategory status : ${result.status}")
                launch (Dispatchers.Main) {
                    setCategory(categoryList.toList())
                }
                getTodoList(
                    category = allCategoryList[selectedPosition],
                    date = getToday
                )

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
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }
}