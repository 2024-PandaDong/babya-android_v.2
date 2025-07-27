package kr.pandadong2024.babya.home.policy.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemPolicyListBinding
import kr.pandadong2024.babya.server.remote.responses.Policy.PolicyListResponse

class PolicyRecyclerView(
    val policyList: List<PolicyListResponse>,
    val tag: String,
    val onClick: (position: Int) -> Unit
) : RecyclerView.Adapter<PolicyRecyclerView.PolicyViewHolder>() {
    inner class PolicyViewHolder(val binding: ItemPolicyListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun policyBind(policyDate: PolicyListResponse, position: Int) {
            binding.apply {
                root.setOnTouchListener { view, motionEvent ->
                    when (motionEvent.action) {
                        MotionEvent.ACTION_MOVE -> root.setBackgroundColor(Color.parseColor("#F7F7F7"))
                        MotionEvent.ACTION_UP -> root.setBackgroundColor(Color.parseColor("#ffffff"))
                        MotionEvent.ACTION_DOWN -> root.setBackgroundColor(Color.parseColor("#F7F7F7"))
                        MotionEvent.ACTION_CANCEL -> root.setBackgroundColor(Color.parseColor("#ffffff"))
                    }
                    false
                }

                localTagText.text = "${tag} 보건소"
                if (policyDate.editDate == null) {
                    termText.visibility = View.GONE
                } else {
                    termText.text = "최종수정일: ${
                        policyDate.editDate.substring(startIndex = 5, endIndex = 7).toInt()
                    }월 ${policyDate.editDate.substring(startIndex = 8, endIndex = 10).toInt()}일"
                }

                if (policyDate.editDate == null) {
                    termText.visibility = View.GONE
                } else {
                    termText.text = "최종수정일: ${
                        policyDate.editDate.substring(startIndex = 5, endIndex = 7).toInt()
                    }월 ${policyDate.editDate.substring(startIndex = 8, endIndex = 10).toInt()}일"
                }


                titleText.text = policyDate.title
                root.setOnClickListener {
                    onClick(position)
                }
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