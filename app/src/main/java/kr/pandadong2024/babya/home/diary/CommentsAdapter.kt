package kr.pandadong2024.babya.home.diary

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemCommentsBinding

class CommentsAdapter(private val commentsList : List<String>) : RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {
    inner class CommentsViewHolder(private val binding : ItemCommentsBinding) : RecyclerView.ViewHolder(binding.root){
        fun setItemComments(commentData: String) {
            binding.commentNameText.text = "test"

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val binding = ItemCommentsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CommentsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return commentsList.size }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        Log.d("Test", "test")
        holder.setItemComments(commentsList[position])
    }
}