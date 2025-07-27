package kr.pandadong2024.babya.home.todo_list.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemTodoListContentBinding
import kr.pandadong2024.babya.server.remote.responses.todo.TodoResponses

class TodoItemAdapter(
    private val todoList: List<TodoResponses>,
    val work: (type: Int, todoData: TodoResponses) -> Unit,
) : RecyclerView.Adapter<TodoItemAdapter.TodoItemViewHolder>() {
    inner class TodoItemViewHolder(val binding: ItemTodoListContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemDate: TodoResponses) {
            binding.todoCheckRadio.isChecked = itemDate.isChecked!!
            binding.todoContentText.text = itemDate.content

            binding.todoContentText.setOnClickListener {
                binding.todoCheckRadio.isChecked = !binding.todoCheckRadio.isChecked
                itemDate.isChecked = binding.todoCheckRadio.isChecked
                work(3, itemDate)
            }
            binding.todoCheckRadio.setOnClickListener {
                itemDate.isChecked = binding.todoCheckRadio.isChecked
                work(3, itemDate)
            }

            binding.deleteButton.setOnClickListener {
                work(1, itemDate)
            }

            binding.modifyButton.setOnClickListener {
                work(2, itemDate)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TodoItemViewHolder {
        val binding =
            ItemTodoListContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoItemViewHolder(binding)
    }

    override fun getItemCount(): Int = todoList.size

    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
        holder.bind(todoList[position])
    }
}