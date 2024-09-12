package kr.pandadong2024.babya.home.diary.diaryadapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.pandadong2024.babya.databinding.ItemCommentsBinding
import kr.pandadong2024.babya.server.remote.responses.CommentResponses

class CommentsAdapter(
    private val commentsList : List<CommentResponses>,
    private val showReplayComment : (id: Int) ->Unit
) : RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {
    inner class CommentsViewHolder(private val binding : ItemCommentsBinding) : RecyclerView.ViewHolder(binding.root){
        fun setItemComments(commentData: CommentResponses) {
            if (commentData.subCommentCnt != 0){
//                binding.subCommentRecyclerView.visibility = View.VISIBLE
                Log.d("recycler", " test in sub")
//                val subCommentList = getSubComment(commentData.commentId!!,  1, 100)
//                val subCommentAdapter = SubCommentAdapter(
//                    subCommentList = subCommentList,
////                    replayComment = { replayComment(commentData.commentId) }
//                )
//                subCommentAdapter.notifyItemRemoved(0)
//                with(binding){
//                    subCommentRecyclerView.adapter = subCommentAdapter
//                }
            }
            binding.commentNameText.text = commentData.nickname
            binding.commentProfileImage.load(commentData.profileImg)
            binding.contentTextView.text = commentData.content
            binding.commentTimeText.text = commentData.createdAt.toString().substring(5 until 10)
            binding.replayCommentText.setOnClickListener {
                commentData.commentId?.let { commentId -> showReplayComment(commentId) }
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