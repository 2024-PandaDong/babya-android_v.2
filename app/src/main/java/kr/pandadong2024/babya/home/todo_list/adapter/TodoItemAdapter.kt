package kr.pandadong2024.babya.home.todo_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemTodoListContentBinding
import kr.pandadong2024.babya.server.remote.responses.todo.TodoResponses

class TodoItemAdapter(val todoList : List<TodoResponses>) : RecyclerView.Adapter<TodoItemAdapter.TodoItemViewHolder>() {
    inner class TodoItemViewHolder(val binding : ItemTodoListContentBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(itemDate : TodoResponses){
            binding.todoCheckRadio.text = itemDate.content
            binding.todoCheckRadio.isChecked = itemDate.isChecked!!
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