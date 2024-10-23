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
import kotlin.math.max


class MainBannerAdapter(
    private val context: Context,
    private val bannerList: List<BannerResponses>
) : RecyclerView.Adapter<MainBannerAdapter.PagerViewHolder>() {
    private val newList = if (bannerList.isEmpty()) {
        listOf()
    } else {
        listOf(bannerList.last()) + bannerList + listOf(bannerList.first())
    }

    inner class PagerViewHolder(private val binding: ItemBanerCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bannerData: BannerResponses, context: Context) {
            if (bannerData.image != null) {
                if (bannerData.image.extension?.uppercase() == "SVG") {
                    bannerData.image.url?.let { binding.itemImage.loadImageFromUrl(it) }
                } else {
                    binding.itemImage.load(bannerData.image.url)
                }
            }

            binding.root.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    setPackage("com.android.chrome")
                    data = Uri.parse(bannerData.url)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val binding =
            ItemBanerCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagerViewHolder(binding)
    }

    override fun getItemCount(): Int = newList.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        if (newList.isEmpty()) return

        var p: Int = position
        if (position != 0) {
            p %= newList.size
        }
        holder.bind(newList[p], context)
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