package kr.pandadong2024.babya.home.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemBoardRecyclerviewBinding
import kr.pandadong2024.babya.server.remote.responses.dash_board.DashBoardResponses
import kr.pandadong2024.babya.server.remote.responses.profile.ProfileMyDashBoardResponses

class ProfileBoardAdapter(
    val item: List<ProfileMyDashBoardResponses>,
    private val onItemClick: (Id : Int) -> Unit
): RecyclerView.Adapter<ProfileBoardAdapter.Holder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val biding = ItemBoardRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(biding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(item[position])
    }

    override fun getItemCount(): Int {
        return item.size
    }

    inner class Holder(val binding: ItemBoardRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(profileMyDashBoardResponses: ProfileMyDashBoardResponses) {
            val data = item[position]
            binding.apply {
                title.text = profileMyDashBoardResponses.title
                day.text = profileMyDashBoardResponses.createdAt

                root.setOnClickListener {
                    if (data.id != null){
                        onItemClick(data.id)
                    }
                }
            }
        }
    }
}