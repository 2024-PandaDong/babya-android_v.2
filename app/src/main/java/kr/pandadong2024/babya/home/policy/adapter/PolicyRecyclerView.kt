package kr.pandadong2024.babya.home.policy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemPolicyListBinding
import kr.pandadong2024.babya.server.remote.responses.Policy.PolicyListResponse

class PolicyRecyclerView (val policyList : List<PolicyListResponse>, val tag : String, val onClick : (position : Int) -> Unit) : RecyclerView.Adapter<PolicyRecyclerView.PolicyViewHolder>() {
    inner class PolicyViewHolder(val binding : ItemPolicyListBinding) :RecyclerView.ViewHolder(binding.root){
        fun policyBind(policyDate : PolicyListResponse, position: Int){

            binding.localTagText.text = "${tag}보건소"
            if(policyDate.editDate == null){
                binding.termText.visibility = View.GONE
            }
            else{
                binding.termText.text = "최종수정일: ${policyDate.editDate.substring(startIndex = 5, endIndex = 7).toInt()}월 ${policyDate.editDate.substring(startIndex = 8, endIndex = 10).toInt()}일"
            }

//            binding.locationText.text =tag
            binding.localTagText.text = tag
            binding.titleText.text = policyDate.title
            binding.policyBackground.setOnClickListener {
                onClick(position)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PolicyRecyclerView.PolicyViewHolder {
        val binding =
            ItemPolicyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PolicyViewHolder(binding)
    }

    override fun getItemCount(): Int = policyList.size

    override fun onBindViewHolder(holder: PolicyRecyclerView.PolicyViewHolder, position: Int) {
        holder.policyBind(policyList[position], position)
    }
}