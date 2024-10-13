package kr.pandadong2024.babya.home.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.pandadong2024.babya.databinding.ItemFindCompanyRecyclerviewBinding
import kr.pandadong2024.babya.server.remote.responses.company.CompanyListResponses

class CompanyRankAdapter(val test: (Int) -> Unit) :
    RecyclerView.Adapter<CompanyRankAdapter.CompanyViewHolder>() {
    private lateinit var companyList: List<CompanyListResponses>

    inner class CompanyViewHolder(private val binding: ItemFindCompanyRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CompanyListResponses, test: (Int) -> Unit, position: Int) {
            binding.companyName.text = data.companyName
            binding.address.text = data.address
            binding.logoImg.load(data.logoImg?.get(0))
            binding.root.setOnClickListener {
                test(position)
            }
        }
    }

    fun setCompanyList(listData: List<CompanyListResponses>) {
        companyList = listData
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        holder.bind(companyList[position], test, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val binding = ItemFindCompanyRecyclerviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CompanyViewHolder(binding)
    }

    override fun getItemCount(): Int = companyList.size


}