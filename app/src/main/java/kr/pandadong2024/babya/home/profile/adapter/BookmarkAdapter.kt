package kr.pandadong2024.babya.home.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemBookmarkRecyclerviewBinding
import kr.pandadong2024.babya.home.profile.data.BookmarkData

class BookmarkAdapter (val item:ArrayList<BookmarkData>) : RecyclerView.Adapter<BookmarkAdapter.Holder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val biding = ItemBookmarkRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(biding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.title.setText(item[position].title)
    }

    override fun getItemCount(): Int {
        return item.size
    }

    inner class Holder(val binding: ItemBookmarkRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root){
        val title = binding.title
    }
}