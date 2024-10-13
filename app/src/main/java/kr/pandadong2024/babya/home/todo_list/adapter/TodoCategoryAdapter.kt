package kr.pandadong2024.babya.home.todo_list.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.ItemTodoListTagBinding

class TodoCategoryAdapter(val flash :(position:Int)->Unit, val categoryList : List<String>, val context: Context, val selectedItemPosition : Int) : RecyclerView.Adapter<TodoCategoryAdapter.CategoryViewHolder>() {
    inner class CategoryViewHolder(private val binding : ItemTodoListTagBinding) :  RecyclerView.ViewHolder(binding.root){
        fun bind(themeText : String, position: Int){
            binding.itemTodoParent.setOnClickListener {
                flash(position)
            }
            binding.tagText.text = themeText
            if(position != selectedItemPosition){
                Log.d("isSelect", "test : $themeText")
                binding.itemTodoParent.setBackgroundResource(R.drawable.sp_item_category_unselect_background)
                binding.tagText.setTextColor(context.resources.getColor(R.color.labelDisable))
            }
            else{
                binding.itemTodoParent.setBackgroundResource(R.drawable.sp_item_category_select_background)
                binding.tagText.setTextColor(context.resources.getColor(R.color.primaryLight))
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemTodoListTagBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int  = categoryList.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(
            themeText =  categoryList[position],
            position = position)
    }
}