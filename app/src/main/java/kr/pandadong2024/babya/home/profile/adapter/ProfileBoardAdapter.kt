package kr.pandadong2024.babya.home.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemBoardRecyclerviewBinding
import kr.pandadong2024.babya.home.profile.data.ProfileBoardData

class ProfileBoardAdapter (val item: ArrayList<ProfileBoardData>): RecyclerView.Adapter<ProfileBoardAdapter.Holder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val biding = ItemBoardRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(biding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.day.setText(item[position].day)
        holder.title.setText(item[position].title)
    }

    override fun getItemCount(): Int {
        return item.size
    }

    inner class Holder(val binding: ItemBoardRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root){
        val day = binding.day
        val title = binding.title
    }


}