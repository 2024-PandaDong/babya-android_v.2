package kr.pandadong2024.babya.home.todo_list.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemTodoListContentBinding
import kr.pandadong2024.babya.server.remote.responses.todo.TodoResponses

class PolicyItemAdapter(
    val todoList : List<TodoResponses>,
    val work : (type : Int, todoData : TodoResponses) -> Unit,
    )
    : RecyclerView.Adapter<PolicyItemAdapter.TodoItemViewHolder>() {

    inner class TodoItemViewHolder(val binding : ItemTodoListContentBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(itemDate : TodoResponses){
            Log.d("itme adapte","item data $itemDate")
            binding.todoCheckRadio.isChecked = itemDate.isChecked!!
            binding.todoCheckRadio.text = itemDate.content
            binding.todoCheckRadio.setOnClickListener {
                Log.d("teat", "check : ${itemDate.isChecked}")
                itemDate.isChecked = binding.todoCheckRadio.isChecked
                Log.d("teat", "check : ${itemDate.isChecked}")
                work(3, itemDate)
            }
            binding.swipeView.setOnClickListener {
                Log.d("tageg", "swipeView")
            }




            binding.deleteButton.setOnClickListener {
                Log.d("tageg", "deleteButton")
                work(1, itemDate)
            }
            binding.modifyButton.setOnClickListener {
                Log.d("tageg", "modifyButton")
                work(2, itemDate)
            }
        }



    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TodoItemViewHolder {
        val binding = ItemTodoListContentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TodoItemViewHolder(binding)
    }

    override fun getItemCount() : Int = todoList.size

    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
        holder.bind(todoList[position])
    }

}