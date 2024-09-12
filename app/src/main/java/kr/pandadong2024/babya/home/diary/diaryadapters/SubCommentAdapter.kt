package kr.pandadong2024.babya.home.diary.diaryadapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.ItemCommentsBinding
import kr.pandadong2024.babya.server.remote.responses.SubCommentResponses

class SubCommentAdapter(
    private val subCommentList: List<SubCommentResponses>,
    val context: Context
) : RecyclerView.Adapter<SubCommentAdapter.SubCommentViewHolder>() {
    inner class SubCommentViewHolder(val binding: ItemCommentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setItemComments(commentData: SubCommentResponses, position: Int) {
            if(position == 0){
                Log.d("test", "pos : ${position}")
                binding.backgroundLayout.background = context.getDrawable(R.color.invisible)
            }
            binding.commentNameText.text = commentData.nickname
            binding.commentProfileImage.load(commentData.profileImg)
            binding.contentTextView.text = commentData.content
            binding.commentTimeText.text = commentData.createdAt.toString().substring(5 until 10)
            binding.replayCommentText.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCommentViewHolder {
        val binding = ItemCommentsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SubCommentViewHolder(binding)
    }

    override fun getItemCount(): Int = subCommentList.size

    override fun onBindViewHolder(holder: SubCommentViewHolder, position: Int) {
        holder.setItemComments(subCommentList[position], position)
    }
}