package kr.pandadong2024.babya.home.dash_board.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemDashBoardRecyclerviewBinding
import kr.pandadong2024.babya.home.dash_board.data.DashBoardData

class DashBoardAdapter(val item: ArrayList<DashBoardData>): RecyclerView.Adapter<DashBoardAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val biding = ItemDashBoardRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(biding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.title.setText(item[position].title)
        holder.name.setText(item[position].name)
        holder.body.setText(item[position].body)
        holder.timed.setText(item[position].timed)
        holder.views.setText(item[position].views)
        holder.comment.setText(item[position].comment)
    }

    override fun getItemCount(): Int {
        return item.size
    }

    inner class Holder(val binding: ItemDashBoardRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root){
        val title = binding.title
        val name = binding.name
        val body = binding.body
        val timed = binding.ago
        val views = binding.views
        val comment = binding.comment
    }
}