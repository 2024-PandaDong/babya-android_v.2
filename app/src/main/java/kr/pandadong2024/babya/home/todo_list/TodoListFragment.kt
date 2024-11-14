package kr.pandadong2024.babya.home.todo_list

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentTodoListBinding
import kr.pandadong2024.babya.home.todo_list.adapter.TodoCategoryAdapter
import kr.pandadong2024.babya.home.todo_list.adapter.TodoDayAdapter
import kr.pandadong2024.babya.home.todo_list.decoration.CategoryItemDecoration
import kr.pandadong2024.babya.home.todo_list.decoration.TodoIDayItemDecoration
import kr.pandadong2024.babya.home.viewmodel.CommonViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.DAO.TokenDAO
import kr.pandadong2024.babya.server.remote.request.todo.TodoModifyRequest
import kr.pandadong2024.babya.server.remote.request.todo.TodoRequestBody
import kr.pandadong2024.babya.server.remote.responses.todo.TodoResponses
import kr.pandadong2024.babya.util.BottomControllable
import kr.pandadong2024.babya.util.shortToast
import retrofit2.HttpException
import java.util.GregorianCalendar


class TodoListFragment : Fragment() {
    private val todoViewModel by activityViewModels<TodoViewModel>()
    private val commonViewModel by activityViewModels<CommonViewModel>()
    private var _binding: FragmentTodoListBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenDao: TokenDAO

    private val gregorianCalendar = GregorianCalendar()
    private val year = gregorianCalendar.get(Calendar.YEAR)
    private val date = gregorianCalendar.get(Calendar.DATE)
    private val month = gregorianCalendar.get(Calendar.MONTH) + 1
    private var getToday = String.format("%4d-%02d-%02d", year.toInt(), month.toInt(), date.toInt())
    private val TAG = "TodoListFragment"
    private var allCategoryList = mutableListOf<String>("")
    private lateinit var bottomSheetDialog: TodoBottomSheet


    private var selectedPosition: Int = 0


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
            bottomSheetDialog = TodoBottomSheet(
                type = 0,
                todoData = null,
                function = { request ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        kotlin.runCatching {
                            RetrofitBuilder.getTodoListService().createTodo(
                                accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                                requestBody = TodoRequestBody(
                                    category = request.category,
                                    content = request.content,
                                    planedDt = request.planedDt
                                )
                            )
                        }.onSuccess { _ ->
                            launch(Dispatchers.Main) {
                                todoViewModel.isSubmit.value = true
                                getCategory()
                            }
                        }.onFailure { result ->
                            commonViewModel.setToastMessage("인터넷이 연결되어있는지 확인해 주십시오")
                            result.printStackTrace()
                            if (result is HttpException) {
                                val errorBody = result.response()?.errorBody()
                            }
                        }
                    }
                }
            )
            bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
            todoViewModel.isSubmit.observe(viewLifecycleOwner) {
                if (it) {
                    bottomSheetDialog.dismiss()
                    todoViewModel.isSubmit.value = false
                }
            }
        }

        return binding.root
    }


    private fun setCategory(categoryList: List<String>) {
        val todoCategoryAdapter = TodoCategoryAdapter(
            categoryList = categoryList,
            selectedItemPosition = selectedPosition,
            context = requireContext(),
            flash = { position ->
                selectedPosition = position
                setCategory(categoryList)
                getTodoList(
                    category = allCategoryList[position],
                    date = getToday
                )
            }
        )
        todoCategoryAdapter.notifyItemRemoved(0)
        with(binding) {
            categoryRecyclerView.adapter = todoCategoryAdapter
            if (categoryRecyclerView.itemDecorationCount == 0) categoryRecyclerView.addItemDecoration(
                CategoryItemDecoration(10, categoryList.size)
            )
            categoryRecyclerView.scrollToPosition(selectedPosition)
        }
    }

    private fun checkTodo(todoId: Int, isChecked: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getTodoListService().checkTodo(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                    isChecked = isChecked,
                    id = todoId
                )
            }.onSuccess {

            }.onFailure { result ->
                commonViewModel.setToastMessage("인터넷이 연결되어있는지 확인해 주십시오")
                result.printStackTrace()
                if (result is HttpException) {
                    val errorBody = result.response()?.raw()?.request
                }
            }
        }
    }

    private fun initDayRecyclerView(todoList: Map<String, List<TodoResponses>>) {
        val todoAdapter = TodoDayAdapter(
            todoList = todoList,
            context = requireContext()
        ) { type, todoData ->
            // | 1 : 삭제 | 2 : 수정 | 3 : 체크 |
            if (todoData.todoId != null)
                when (type) {
                    1 -> {
                        Log.d("initDayRecyclerView", "in 1")
                        deleteTodo(todoData.todoId)
                    }

                    2 -> {
                        modifyTodo(todoData)
                    }

                    3 -> {
                        todoData.isChecked?.let {
                            checkTodo(
                                todoId = todoData.todoId,
                                isChecked = it
                            )
                        }
                    }
                }

        }
        todoAdapter.notifyItemRemoved(0)
        with(binding) {
            todoListRecyclerView.adapter = todoAdapter
            if (todoListRecyclerView.itemDecorationCount == 0) todoListRecyclerView.addItemDecoration(
                TodoIDayItemDecoration(todoList.size)
            )
            todoListRecyclerView.scrollToPosition(selectedPosition)
        }
    }

    private fun modifyTodo(todoData: TodoResponses) {
        val bottomSheetDialog = TodoBottomSheet(
            type = 1,
            todoData = todoData,
            function = { request ->
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
                        if (result.status == 200) {
                            launch(Dispatchers.Main) {
                                todoViewModel.isSubmit.value = true
                                getCategory()
                            }
                        } else {
                            commonViewModel.setToastMessage("데이터를 변경하는 도중 문제가 발생했습니다. CODE : ${result.status}")
                        }
                    }.onFailure { result ->
                        commonViewModel.setToastMessage("인터넷이 연결되어있는지 확인해 주십시오")
                        result.printStackTrace()
                        if (result is HttpException) {
                            val errorBody = result.response()?.raw()?.request
                        }
                    }
                }
            }
        )
        bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
        todoViewModel.isSubmit.observe(viewLifecycleOwner) {
            if (it) {
                bottomSheetDialog.dismiss()
                todoViewModel.isSubmit.value = false
            }
        }

    }

    private fun deleteTodo(todoId: Int) {
        Log.d("deleteTodo", "in fun")
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getTodoListService().deleteTodo(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                    id = todoId
                )
            }.onSuccess { result ->
                if (result.status == 200) {
                    launch(Dispatchers.Main) {
                        getCategory()
                    }
                } else {
                    commonViewModel.setToastMessage( "데이터를 불러오는 도중 문제가 발생했습니다. CODE : ${result.status}")
                }
            }.onFailure { result ->
                result.printStackTrace()
//                commonViewModel.setToastMessage("인터넷이 연결되어있는지 확인해 주십시오")
                if (result is HttpException) {
                    val errorBody = result.response()
                    Log.e(TAG, "Error body: ${errorBody?.raw()?.request}")
                }
            }
        }
    }


    private fun getTodoList(category: String, date: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getTodoListService().getTodoList(
                    category = category,
                    date = date,
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                )
            }.onSuccess { result ->
                if (result.status == 200) {
                    val todoList: MutableMap<String, MutableList<TodoResponses>> = mutableMapOf()
                    result.data?.forEach {
                        val day = "${it.planedDt}"
                        if (todoList.keys.contains(day)) { //0000-00-00
                            todoList[day]?.add(it)
                        } else {
                            todoList[day] = mutableListOf(it)
                        }
                    }

                    launch(Dispatchers.Main) {
                        initDayRecyclerView(todoList)
                    }
                } else {
                    commonViewModel.setToastMessage( "데이터를 불러오는 도중 문제가 발생했습니다. CODE : ${result.status}")
                }

            }.onFailure { result ->
                val todoList: MutableMap<String, MutableList<TodoResponses>> = mutableMapOf()
                result.printStackTrace()
                launch(Dispatchers.Main) {
                    initDayRecyclerView(todoList)
                }
                requireContext().shortToast("인터넷이 연결되어있는지 확인해 주십시오")
            }
        }
    }

    private fun getCategory() {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getTodoListService().getCategory(
                    accessToken = "Bearer ${tokenDao.getMembers().accessToken}"
                )
            }.onSuccess { result ->
                withContext(Dispatchers.Main)
                {
                    if (result.status == 200) {
                        val categoryList = result.data?.category?.toMutableList()
                        allCategoryList.addAll(categoryList?.toList() ?: listOf())
                        categoryList?.add(0, "전체")
                        setCategory(categoryList?.toList() ?: listOf())
                        getTodoList(
                            category = allCategoryList[selectedPosition],
                            date = getToday
                        )
                    } else {
                        commonViewModel.setToastMessage("데이터를 불러오는 도중 문제가 발생했습니다. CODE : ${result.status}")
                    }
                }

            }.onFailure { result ->
                result.printStackTrace()
                if (result is HttpException) {
                    val errorBody = result.response()?.errorBody()
                    Log.e(TAG, "Error body: $errorBody")
                }
                withContext(Dispatchers.Main){
                    requireContext().shortToast("인터넷이 연결되어있는지 확인해 주십시오")
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