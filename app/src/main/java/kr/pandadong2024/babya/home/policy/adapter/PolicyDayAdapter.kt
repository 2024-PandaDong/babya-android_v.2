package kr.pandadong2024.babya.home.todo_list.adapter

import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.ItemTodoDayListBinding
import kr.pandadong2024.babya.home.todo_list.TodoItemTouchHelper
import kr.pandadong2024.babya.home.policy.decoration.PolicyTodoItemDecoration
import kr.pandadong2024.babya.server.remote.responses.todo.TodoResponses
import java.util.GregorianCalendar

class PolicyDayAdapter(
    val todoList: Map<String, List<TodoResponses>>,
    val context: Context,
    val work : (type : Int, todoData : TodoResponses) -> Unit
) : RecyclerView.Adapter<PolicyDayAdapter.TodoDayViewHolder>() {
    private val keyList = todoList.keys.toList()
    inner class TodoDayViewHolder(
        private val binding: ItemTodoDayListBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val gregorianCalendar = GregorianCalendar()
        private val date = gregorianCalendar.get(Calendar.DATE)
        private var itemData : List<TodoResponses>? = null
        private var isExpand = false
        private val isHavingDecoList = mutableMapOf<String, Boolean>()
        fun bindItem(key: String, getData: List<TodoResponses>) {
            keyList.forEach {
                isHavingDecoList[it] = false
            }


            itemData = getData
            if(date == key.toInt()){
                isExpand = true
                binding.todoDayText.text = "오늘"
                openItem(key)
            }
            else{
                binding.todoDayText.text = key
            }
            binding.root.setOnClickListener {
                openItem(key)
            }

        }
        private fun openItem(key: String){
            val adapter = PolicyItemAdapter(itemData!!){ type, todoId ->
                work(type, todoId)
                //TODO : 지워지고 수정되는거 이쪽에서 맡아서 하기
            }


            val swipeHelperCallback = TodoItemTouchHelper()

            val itemTouchHelper: ItemTouchHelper = ItemTouchHelper(swipeHelperCallback)
            itemTouchHelper.attachToRecyclerView(binding.todoDayItemRecyclerView)

            if (!isExpand) {
                isExpand = true
                adapter.notifyItemRemoved(0)
                binding.todoDayItemRecyclerView.visibility = View.GONE
                binding.todoDayIcon.setImageResource(R.drawable.ic_up_arrow)

            } else {

                isExpand = false


                binding.todoDayItemRecyclerView.visibility = View.VISIBLE
                adapter.notifyItemRemoved(0)

                binding.todoDayItemRecyclerView.adapter = adapter
                Log.d("itemDecorationCount", "itemDecorationCount : ${binding.todoDayItemRecyclerView.itemDecorationCount}")
                if (isHavingDecoList[key]!!.not()) {
                    binding.todoDayItemRecyclerView.addItemDecoration(
                        PolicyTodoItemDecoration(
                            horizontalPadding = 35,
                            lastPos = itemData!!.size
                        )
                    )
                    isHavingDecoList[key] = isHavingDecoList[key]!!.not()
                }
                binding.todoDayItemRecyclerView.apply {
                    setOnTouchListener { _, _ ->
                        performClick()
                        swipeHelperCallback.removePreviousClamp(this)
                        false
                    }
                }
                binding.todoDayIcon.setImageResource(R.drawable.ic_down_arrow)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoDayViewHolder {

        val binding =
            ItemTodoDayListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoDayViewHolder(binding)
    }

    override fun getItemCount(): Int = todoList.size

    override fun onBindViewHolder(holder: TodoDayViewHolder, position: Int) {
        holder.bindItem(
            key = todoList.keys.toList()[position],
            getData = todoList[todoList.keys.toList()[position]]!!
        )
    }
}