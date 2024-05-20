package kr.pandadong2024.babya.home.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemBanerCardBinding
import kr.pandadong2024.babya.server.responses.BannerResponses

class BannerAdapter : RecyclerView.Adapter<BannerAdapter.PagerViewHolder>() {
    inner class PagerViewHolder(private val binding : ItemBanerCardBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(bannerData : BannerResponses){
            binding.itemImage
            binding.typeText.text = bannerData.type
            binding.sourceText.text = bannerData.title
        }

    }
    private var bannerList = mutableListOf<BannerResponses>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val binding = ItemBanerCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PagerViewHolder(binding)
    }

    override fun getItemCount(): Int = Int.MAX_VALUE
    fun setList(list: List<BannerResponses>){
        bannerList = list.toMutableList()
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        if(position == 0){
            holder.bind(bannerList[position])
        }
        else {
            holder.bind(bannerList[position % bannerList.size])
        }
    }
}