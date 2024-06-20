package kr.pandadong2024.babya.home.main

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import coil.request.ImageRequest
import kr.pandadong2024.babya.databinding.ItemBanerCardBinding
import kr.pandadong2024.babya.server.remote.responses.BannerResponses


class MainBannerAdapter(
    private val context: Context,
    private val bannerList: List<BannerResponses>
) : RecyclerView.Adapter<MainBannerAdapter.PagerViewHolder>() {
    inner class PagerViewHolder(private val binding : ItemBanerCardBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(bannerData : BannerResponses, context: Context){
            if(bannerData.image?.extension?.uppercase() == "SVG"){
                bannerData.image.url?.let { binding.itemImage.loadImageFromUrl(it) }
            }
            else{
                binding.itemImage.load(bannerData.image?.url)
            }

            binding.typeText.text = bannerData.subTitle
            binding.sourceText.text = bannerData.source
            binding.root.setOnClickListener{
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.setPackage("com.android.chrome")
                intent.data = Uri.parse(bannerData.url)
                context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val binding = ItemBanerCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PagerViewHolder(binding)
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        if (bannerList.isNotEmpty()) {
            var p: Int = position
            if (position != 0) {
                p %= bannerList.size
            }
            holder.bind(bannerList[p], context)
        }
    }
    fun ImageView.loadImageFromUrl(imageUrl: String) {
        val imageLoader = ImageLoader.Builder(this.context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()

        val imageRequest = ImageRequest.Builder(this.context)
            .crossfade(true)
            .crossfade(300)
            .data(imageUrl)
            .target(
                onSuccess = { result ->
                    val bitmap = (result as BitmapDrawable).bitmap
                    this.setImageBitmap(bitmap)
                },
            )
            .build()

        imageLoader.enqueue(imageRequest)
    }
}