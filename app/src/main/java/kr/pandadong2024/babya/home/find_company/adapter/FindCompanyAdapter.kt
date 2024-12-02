package kr.pandadong2024.babya.home.find_company.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.graphics.colorspace.Rgb
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.pandadong2024.babya.R
import android.graphics.Color
import kr.pandadong2024.babya.databinding.ItemFindCompanyRecyclerviewBinding
import kr.pandadong2024.babya.server.remote.responses.company.CompanyListResponses

class FindCompanyAdapter(
    private val items: List<CompanyListResponses>,
    private val onItemClick: (postId: Int, position : Int) -> Unit
) : RecyclerView.Adapter<FindCompanyAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemFindCompanyRecyclerviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    inner class Holder(private val binding: ItemFindCompanyRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(companyListResponses: CompanyListResponses, position: Int) {
            val data = items[position]
            binding.apply {
                companyName.text = companyListResponses.companyName
                address.text = companyListResponses.address
                logoImg.load(companyListResponses.logoImg?.get(0))

//                address.setBackgroundColor(Color.parseColor("#123445"))


                root.setOnTouchListener { view, motionEvent ->
                    Log.d("hover2", "onHover : "+ motionEvent.action)
                    when (motionEvent.action) {
                        MotionEvent.ACTION_MOVE -> root.setBackgroundColor(Color.parseColor("#F7F7F7"))
                        MotionEvent.ACTION_UP -> root.setBackgroundColor(Color.parseColor("#ffffff"))
                        MotionEvent.ACTION_DOWN -> root.setBackgroundColor(Color.parseColor("#F7F7F7"))
                        MotionEvent.ACTION_CANCEL -> root.setBackgroundColor(Color.parseColor("#ffffff"))
                    }
                    false
                }


                root.setOnClickListener {
                    if (data.companyId != null) {
                        onItemClick(data.companyId, position)
                    }
                }
            }
        }
    }

}