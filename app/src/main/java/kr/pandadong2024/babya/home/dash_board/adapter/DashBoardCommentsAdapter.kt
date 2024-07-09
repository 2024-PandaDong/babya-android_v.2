package kr.pandadong2024.babya.home.dash_board.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.pandadong2024.babya.databinding.ItemCommentsBinding
import kr.pandadong2024.babya.server.remote.responses.CommentResponses
import kr.pandadong2024.babya.server.remote.responses.dash_board.DashBoardCommentResponses

class DashBoardCommentsAdapter(
    private val commentsList : List<DashBoardCommentResponses>,
    val replayComment : (parentId : Int) -> Unit
) : RecyclerView.Adapter<DashBoardCommentsAdapter.CommentsViewHolder>() {
    inner class CommentsViewHolder(private val binding : ItemCommentsBinding) : RecyclerView.ViewHolder(binding.root){
        fun setItemComments(commentData: DashBoardCommentResponses) {
            binding.commentNameText.text = commentData.nickname
            binding.commentProfileImage.load(commentData.profileImg)
            binding.contentTextView.text = commentData.content
            binding.commentTimeText.text = commentData.createdAt
            binding.replayCommentText.setOnClickListener {
                replayComment(commentData.commentId)
            }
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