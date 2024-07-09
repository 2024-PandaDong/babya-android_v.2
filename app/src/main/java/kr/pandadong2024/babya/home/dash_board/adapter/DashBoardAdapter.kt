package kr.pandadong2024.babya.home.dash_board.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemDashBoardRecyclerviewBinding
import kr.pandadong2024.babya.server.remote.responses.dash_board.DashBoardResponses

class DashBoardAdapter(
    private val items: List<DashBoardResponses>,
    private val onItemClick: (postId : Int) -> Unit
) : RecyclerView.Adapter<DashBoardAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemDashBoardRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class Holder(private val binding: ItemDashBoardRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dashBoardResponse: DashBoardResponses) {
            val data = items[position]
            binding.apply {
                title.text = dashBoardResponse.title
                name.text = dashBoardResponse.nickname
                body.text = dashBoardResponse.content
                ago.text = dashBoardResponse.createdAt.toString()  // 적절한 형식으로 변환 필요
                views.text = dashBoardResponse.view.toString()
                comment.text = dashBoardResponse.commentCnt.toString()

                root.setOnClickListener {
                    if (data.postId != null){
                        onItemClick(data.postId)
                    }
                }
            }
        }
    }
}
