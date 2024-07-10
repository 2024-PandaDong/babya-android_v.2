package kr.pandadong2024.babya.home.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.ItemDiaryRecyclerviewBinding
import kr.pandadong2024.babya.server.remote.responses.profile.ProfileMyDiaryResponses

class ProfileDiaryAdapter(
    val item: List<ProfileMyDiaryResponses>,
    private val onItemClick: (Id : Int) -> Unit
):RecyclerView.Adapter<ProfileDiaryAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val biding = ItemDiaryRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(biding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(item[position])
    }

    override fun getItemCount(): Int {
        return item.size
    }

    inner class Holder(val binding: ItemDiaryRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(profileMyDiaryResponses: ProfileMyDiaryResponses) {
            val data = item[position]
            binding.apply {
                title.text = profileMyDiaryResponses.title

                day.text = profileMyDiaryResponses.writtenDt.substring(5 until 7) +"/"+ profileMyDiaryResponses.writtenDt.substring(8)

                when (profileMyDiaryResponses.emoji){
                    "좋음" -> binding.emoji.load(R.drawable.img_good)
                    "아픔" -> binding.emoji.load(R.drawable.img_pain)
                    "피곤" -> binding.emoji.load(R.drawable.img_tired)
                    "불안" -> binding.emoji.load(R.drawable.img_unrest)
                    else -> binding.emoji.load(R.drawable.img_normal)
                }


                root.setOnClickListener {
                    if (data.id != null){
                        onItemClick(data.id)
                    }
                }
            }
        }
    }
}