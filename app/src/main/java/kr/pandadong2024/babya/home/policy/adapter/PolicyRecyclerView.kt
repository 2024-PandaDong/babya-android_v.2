package kr.pandadong2024.babya.home.policy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.pandadong2024.babya.databinding.ItemPolicyListBinding
import kr.pandadong2024.babya.util.roundAll

class PolicyRecyclerView (val policyList : List<String>,val onClick : (position : Int) -> Unit) : RecyclerView.Adapter<PolicyRecyclerView.PolicyViewHolder>() {
    inner class PolicyViewHolder(val binding : ItemPolicyListBinding) :RecyclerView.ViewHolder(binding.root){
        fun policyBind(policyDate : String, position: Int){
            binding.policyImage.load("https://file.thisisgame.com/upload/nboard/news/2023/08/14/20230814121422_4417w.jpg")
            roundAll(binding.policyImage, 10f)
            binding.termText.text = policyDate
            binding.locationText.text =policyDate
            binding.localTagText.text = policyDate
            binding.titleText.text = policyDate
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