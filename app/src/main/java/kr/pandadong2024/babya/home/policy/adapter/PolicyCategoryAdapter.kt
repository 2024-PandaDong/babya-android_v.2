package kr.pandadong2024.babya.home.policy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemPolicyListTagBinding

class PolicyCategoryAdapter(val flash :(position:Int, localCategoryList : MutableList<String>)->Unit, private val localCategoryList : List<String>) :
    RecyclerView.Adapter<PolicyCategoryAdapter.CategoryViewHolder>() {
    inner class CategoryViewHolder(private val binding: ItemPolicyListTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(localText: String, position: Int) {

                binding.policyTagText.text = localText
                binding.itemPolicyParent.setOnClickListener {
                    flash(position, localCategoryList.toMutableList())
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemPolicyListTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
            holder.bind(localCategoryList[position], position)
    }

    override fun getItemCount(): Int {
      return  localCategoryList.size
    }



}