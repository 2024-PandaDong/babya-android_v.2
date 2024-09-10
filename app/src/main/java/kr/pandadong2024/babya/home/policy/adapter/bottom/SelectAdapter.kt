package kr.pandadong2024.babya.home.policy.adapter.bottom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.ItemPolicyListTagBinding

class SelectAdapter(
    private val textDataList: List<String>,
    private val selectList: List<Int>,
    val context: Context,
    val changeSelectState: (position: Int) -> Unit
) : RecyclerView.Adapter<SelectAdapter.SelectViewHolder>() {
    inner class SelectViewHolder(private val binding: ItemPolicyListTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tagText: String, position: Int) {
            binding.itemLocalParent.visibility = View.GONE
            binding.policyTagText.text = tagText
            if(position+1 in selectList){
                binding.itemPolicyParent.setBackgroundResource(R.drawable.sp_item_category_select_background)
                binding.policyTagText.setTextColor(ContextCompat.getColor(context, R.color.primaryLight) )

            }else{
                binding.itemPolicyParent.setBackgroundResource(R.drawable.sp_item_policy_select_background)
                binding.policyTagText.setTextColor(ContextCompat.getColor(context, R.color.labelDisable) )
            }

            binding.itemPolicyParent.setOnClickListener {
                changeSelectState(position+1)
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
        holder.bind(textDataList[position],  position)
    }
}