package kr.pandadong2024.babya.home.dash_board.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.ItemCommentsBinding
import kr.pandadong2024.babya.server.remote.responses.dash_board.DashBoardCommentResponses

class DashBoardCommentAdapter(
    private val commentList : List<DashBoardCommentResponses>,
    val replayComment : (postId : Int) -> Unit) : RecyclerView.Adapter<DashBoardCommentAdapter.Holder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DashBoardCommentAdapter.Holder {
        val binding = ItemCommentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: DashBoardCommentAdapter.Holder, position: Int) {
        holder.setItemComments(commentList[position])
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    inner class Holder(private val binding: ItemCommentsBinding) : RecyclerView.ViewHolder(binding.root){
        fun setItemComments(commentData: DashBoardCommentResponses) {

            if (commentData.profileImg == null){
                binding.commentProfileImage.load(R.drawable.ic_basic_profile)
            }else{
                binding.commentProfileImage.load(commentData.profileImg)
            }
            binding.commentNameText.text = commentData.nickname
            binding.contentTextView.text = commentData.content
            binding.commentTimeText.text = commentData.createdAt.substring(5 until 10)
            binding.replayCommentText.setOnClickListener {
                commentData.commentId?.let { commentId -> replayComment(commentId) }
            }
        }
    }

}