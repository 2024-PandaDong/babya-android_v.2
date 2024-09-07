package kr.pandadong2024.babya.home.policy.adapter.bottom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.ItemPolicyListTagBinding

class SelectAdapter(
    private val textDataList: List<String>,
    private val selectList: List<Boolean>,
    val changeSelectState: (position: Int) -> Unit
) : RecyclerView.Adapter<SelectAdapter.SelectViewHolder>() {
    inner class SelectViewHolder(private val binding: ItemPolicyListTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tagText: String, isSelected: Boolean, position: Int) {
            binding.itemLocalParent.visibility = View.GONE
            binding.policyTagText.text = tagText
            if(isSelected){
                binding.itemPolicyParent.setBackgroundResource(R.drawable.sp_item_category_select_background)
            }else{
                binding.itemPolicyParent.setBackgroundResource(R.drawable.sp_item_category_select_background)
            }

            binding.itemPolicyParent.setOnClickListener {
                changeSelectState(position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectViewHolder {
        val binding =
            ItemPolicyListTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectViewHolder(binding)
    }

    override fun getItemCount(): Int = textDataList.size

    override fun onBindViewHolder(holder: SelectViewHolder, position: Int) {
        holder.bind(textDataList[position], selectList[position], position)
    }
}