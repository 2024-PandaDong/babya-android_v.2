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
import kr.pandadong2024.babya.server.remote.responses.todo.TodoResponses
import java.util.GregorianCalendar

class TodoDayAdapter(
    val todoList: Map<String, List<TodoResponses>>,
    val context: Context,
    val work : (type : Int, todoId : Int) -> Unit
) : RecyclerView.Adapter<TodoDayAdapter.TodoDayViewHolder>() {
    inner class TodoDayViewHolder(
        private val binding: ItemTodoDayListBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val gregorianCalendar = GregorianCalendar()
        private val date = gregorianCalendar.get(Calendar.DATE)
        private var itemData : List<TodoResponses>? = null
        private var isExpand = false
        fun bindItem(key: String, getData: List<TodoResponses>) {
            itemData = getData
            if(date == key.toInt()){
                Log.d("tood adayer", "Test in if")
                isExpand = true
                openItem()
            }
            binding.todoDayText.text = key
            binding.root.setOnClickListener {
                Log.d("tood adayer", "Test in click")
                openItem()
            }

        }
        private fun openItem(){
            val adapter = TodoItemAdapter(itemData!!){ type, todoId ->
                work(type, todoId)
            }

            val swipeHelperCallback = TodoItemTouchHelper()
            swipeHelperCallback.setClamp(1000f)
            val itemTouchHelper: ItemTouchHelper = ItemTouchHelper(swipeHelperCallback)
            itemTouchHelper.attachToRecyclerView(binding.todoDayItemRecyclerView)

            binding.todoDayItemRecyclerView.apply {
                setOnTouchListener { _, _ ->
                    swipeHelperCallback.removePreviousClamp(this)
                    false
                }
            }

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