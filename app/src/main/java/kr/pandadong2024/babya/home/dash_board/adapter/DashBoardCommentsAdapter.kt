package kr.pandadong2024.babya.home.dash_board.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.ItemCommentsBinding
import kr.pandadong2024.babya.home.diary.diaryadapters.SubCommentAdapter
import kr.pandadong2024.babya.server.remote.responses.CommentResponses
import kr.pandadong2024.babya.server.remote.responses.SubCommentResponses
import kr.pandadong2024.babya.server.remote.responses.dash_board.DashBoardCommentResponses

class DashBoardCommentsAdapter(
    private val commentsList : List<DashBoardCommentResponses>,
    val replayComment : (parentId : Int) -> Unit,
    val getSubComment : (parentId : Int, page: Int, size : Int) -> List<SubCommentResponses>,
) : RecyclerView.Adapter<DashBoardCommentsAdapter.CommentsViewHolder>() {
    inner class CommentsViewHolder(private val binding : ItemCommentsBinding) : RecyclerView.ViewHolder(binding.root){
        fun setItemComments(commentData: DashBoardCommentResponses) {
            if (commentData.subCommentCnt != 0){
                Log.d("recycler", " test in sub")
//                val subCommentList = getSubComment(commentData.commentId!!,  1, 100)
//                val subCommentAdapter = SubCommentAdapter(
//                    subCommentList = subCommentList,
//                )
//                subCommentAdapter.notifyItemRemoved(0)
            }
            binding.commentNameText.text = commentData.nickname
            if (commentData.profileImg == null){
                binding.commentProfileImage.load(R.drawable.ic_basic_profile)
            } else{
                binding.commentProfileImage.load(commentData.profileImg)
            }
            binding.contentTextView.text = commentData.content
            binding.commentTimeText.text = commentData.createdAt.substring(5 until 10)
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