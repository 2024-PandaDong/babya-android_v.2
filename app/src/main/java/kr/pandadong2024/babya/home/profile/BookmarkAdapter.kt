package kr.pandadong2024.babya.home.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemBookmarkRecyclerviewBinding

class BookmarkAdapter (val item:ArrayList<BookmarkData>) : RecyclerView.Adapter<BookmarkAdapter.Holder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkAdapter.Holder {
        val biding = ItemBookmarkRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(biding)
    }

    override fun onBindViewHolder(holder: BookmarkAdapter.Holder, position: Int) {
        holder.title.text = item[position].title
    }

    override fun getItemCount(): Int {
        return item.size
    }

    inner class Holder(val binding: ItemBookmarkRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root){
        val title = binding.title
    }
}