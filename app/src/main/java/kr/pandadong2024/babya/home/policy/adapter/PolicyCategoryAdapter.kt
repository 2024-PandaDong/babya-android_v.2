package kr.pandadong2024.babya.home.todo_list.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.ItemPolicyListTagBinding
import kr.pandadong2024.babya.databinding.ItemTodoListTagBinding

class PolicyCategoryAdapter(val flash :(position:Int, localCategoryList : MutableList<String>)->Unit, private val localCategoryList : List<String>) : RecyclerView.Adapter<PolicyCategoryAdapter.CategoryViewHolder>() {
    inner class CategoryViewHolder(private val binding: ItemPolicyListTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(localText: String, position: Int) {
            if (position == 0) {
                binding.localTagText.text = localText
                binding.itemPolicyParent.visibility = View.GONE
                binding.itemLocalParent.setOnClickListener {
                    // TODO : show bottom Sheet
                    flash(position, localCategoryList.toMutableList())
                }
            } else {
                binding.policyTagText.text = localText
                binding.itemLocalParent.visibility = View.GONE
                binding.itemPolicyParent.setOnClickListener {
                    //TODO : delete category
                    flash(position, localCategoryList.toMutableList())
                }
                Log.d("isSelect", "test : $localText")
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        Log.d("test","test3")
        val binding =
            ItemPolicyListTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
            Log.d("test","test")
            holder.bind(localCategoryList[position], position)
    }

    override fun getItemCount(): Int {
        Log.d("test","test1 : ${localCategoryList}")
      return  localCategoryList.size
    }



}