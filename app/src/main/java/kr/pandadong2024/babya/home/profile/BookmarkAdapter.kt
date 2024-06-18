package kr.pandadong2024.babya.home.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.FragmentBookmarkBinding
import kr.pandadong2024.babya.databinding.ItemBoardRecyclerviewBinding
import kr.pandadong2024.babya.databinding.ItemBookmarkRecyclerviewBinding
import kr.pandadong2024.babya.databinding.ItemRecyclerviewBinding
import kr.pandadong2024.babya.signup.BirthName
import kr.pandadong2024.babya.signup.BriNmAdapter

class BookmarkAdapter (val item:ArrayList<BookmarkData>) : RecyclerView.Adapter<BookmarkAdapter.Holder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkAdapter.Holder {
        val biding = ItemBookmarkRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(biding)
    }

    override fun onBindViewHolder(holder: BookmarkAdapter.Holder, position: Int) {
        holder.title.setText(item[position].title)
    }

    override fun getItemCount(): Int {
        return item.size
    }

    inner class Holder(val binding: ItemBookmarkRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root){
        val title = binding.title
    }
}