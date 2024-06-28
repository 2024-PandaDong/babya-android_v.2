package kr.pandadong2024.babya.home.todo_list.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemTodoListContentBinding
import kr.pandadong2024.babya.server.remote.responses.todo.TodoResponses

class TodoItemAdapter(
    val todoList : List<TodoResponses>,
    val work : (type : Int,  todoId : Int) -> Unit)
    : RecyclerView.Adapter<TodoItemAdapter.TodoItemViewHolder>() {
    inner class TodoItemViewHolder(val binding : ItemTodoListContentBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(itemDate : TodoResponses){
            binding.todoCheckRadio.text = itemDate.content
            binding.todoCheckRadio.isChecked = itemDate.isChecked!!

            binding.deleteButton.setOnClickListener {
                Log.d("tageg", "deleteButton")
                work(1, itemDate.todoId!!)
            }
            binding.modifyButton.setOnClickListener {
                Log.d("tageg", "modifyButton")
                work(2, itemDate.todoId!!)
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