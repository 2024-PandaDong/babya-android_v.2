package kr.pandadong2024.babya.home.diary.diaryadapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.ItemCommentsBinding
import kr.pandadong2024.babya.server.remote.responses.CommentResponses

class CommentsAdapter(
    private val commentsList: List<CommentResponses>,
    private val showReplayComment: (id: Int) -> Unit
) : RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {
    inner class CommentsViewHolder(private val binding: ItemCommentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setItemComments(commentData: CommentResponses) {
            binding.commentNameText.text = commentData.nickname
            if (commentData.profileImg?.isNotBlank() == true) {
                binding.commentProfileImage.load(commentData.profileImg)
            }
            else{
                Log.d("ㅅㄷㄴㅅ", " in else")
                binding.commentProfileImage.load(R.drawable.ic_basic_profile)
            }
            binding.contentTextView.text = commentData.content
            binding.commentTimeText.text = commentData.createdAt.toString().substring(5 until 10)
            binding.replayCommentText.setOnClickListener {
                commentData.commentId?.let { commentId -> showReplayComment(commentId) }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val binding =
            ItemCommentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return commentsList.size
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        holder.setItemComments(commentsList[position])
    }
}