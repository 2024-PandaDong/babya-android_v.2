package kr.pandadong2024.babya.home.policy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.pandadong2024.babya.databinding.ItemPolicyListBinding
import kr.pandadong2024.babya.server.remote.responses.Policy.PolicyListResponse
import kr.pandadong2024.babya.util.roundAll

class PolicyRecyclerView (val policyList : List<PolicyListResponse>, val tag : String, val onClick : (position : Int) -> Unit) : RecyclerView.Adapter<PolicyRecyclerView.PolicyViewHolder>() {
    inner class PolicyViewHolder(val binding : ItemPolicyListBinding) :RecyclerView.ViewHolder(binding.root){
        fun policyBind(policyDate : PolicyListResponse, position: Int){
            binding.termText.text = policyDate.editDate
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