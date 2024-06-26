package kr.pandadong2024.babya.home.todo_list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemTodoDayListBinding

class TodoDayAdapter : RecyclerView.Adapter<TodoDayAdapter.TodoDayViewHolder>() {
    inner class TodoDayViewHolder(private val binding : ItemTodoDayListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindItem(itemData : String){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoDayViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: TodoDayViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}