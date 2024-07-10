package kr.pandadong2024.babya.home.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemDiaryRecyclerviewBinding
import kr.pandadong2024.babya.server.remote.responses.profile.ProfileMyDiaryResponses

class ProfileDiaryAdapter(val item: ArrayList<ProfileMyDiaryResponses>):RecyclerView.Adapter<ProfileDiaryAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val biding = ItemDiaryRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(biding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.title.setText(item[position].title)
    }

    override fun getItemCount(): Int {
        return item.size
    }

    inner class Holder(val binding: ItemDiaryRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root){
        val title = binding.title
    }
}