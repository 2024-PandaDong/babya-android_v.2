package kr.pandadong2024.babya.home.find_company.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.pandadong2024.babya.databinding.ItemFindCompanyRecyclerviewBinding
import kr.pandadong2024.babya.server.remote.responses.company.CompanyListResponses

class FindCompanyAdapter(
    private val items: List<kr.pandadong2024.babya.server.remote.responses.company.CompanyListResponses>,
    private val onItemClick: (postId : Int) -> Unit
) : RecyclerView.Adapter<FindCompanyAdapter.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemFindCompanyRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class Holder(private val binding: ItemFindCompanyRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(CompanyListResponses: kr.pandadong2024.babya.server.remote.responses.company.CompanyListResponses) {
            val data = items[position]
            binding.apply {
                companyName.text = CompanyListResponses.companyName
                address.text = CompanyListResponses.address
                logoImg.load(CompanyListResponses.logoImg)


                root.setOnClickListener {
                    if (data.companyId != null){
                        onItemClick(data.companyId)
                    }
                }
            }
        }
    }

}