package kr.pandadong2024.babya.home.diary

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.ItemDiaryBannerBinding

class DiaryBannerAdapter(
    val diaryBannerDataList : List<String>,
    val context: Context
) : RecyclerView.Adapter<DiaryBannerAdapter.BannerViewHolder>() {
    inner class BannerViewHolder(private val binding : ItemDiaryBannerBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(diaryBannerData : String){
            binding.diaryBannerImage.load(R.drawable.img_banner_test)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemDiaryBannerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BannerViewHolder(binding)
    }

    override fun getItemCount(): Int  = diaryBannerDataList.size

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(diaryBannerDataList[position])
    }
}