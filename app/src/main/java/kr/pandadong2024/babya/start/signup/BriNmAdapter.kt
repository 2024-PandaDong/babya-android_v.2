package kr.pandadong2024.babya.start.signup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemRecyclerviewBinding
import kr.pandadong2024.babya.databinding.ItemSignupRecyclerviewBinding


class BriNmAdapter(val birthNameList:ArrayList<BirthName>) : RecyclerView.Adapter<BriNmAdapter.Holder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemSignupRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.birthDayRvName.setText(birthNameList[position].name)
//        holder.binding.deleteBtn.setOnClickListener {
//            removeItem(position)
//        }
    }

    override fun getItemCount(): Int {
        return birthNameList.size
    }

    inner class Holder(val binding: ItemSignupRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root){
        val birthDayRvName = binding.fetusEditText
    }

    fun removeItem(position: Int) {
        birthNameList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }
}