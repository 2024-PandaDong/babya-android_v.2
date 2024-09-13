package kr.pandadong2024.babya.home.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.pandadong2024.babya.databinding.ItemFindCompanyRecyclerviewBinding

class CompanyRankAdapter (val test: (Int) -> Unit) : RecyclerView.Adapter<CompanyRankAdapter.CompanyViewHolder>() {
    private lateinit var companyList : List<kr.pandadong2024.babya.server.remote.responses.company.CompanyDataResponses>

    inner class CompanyViewHolder(private val binding : ItemFindCompanyRecyclerviewBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data : kr.pandadong2024.babya.server.remote.responses.company.CompanyDataResponses, test: (Int) -> Unit, position: Int){
            binding.companyName.text = data.name
            binding.address.text = data.link
            binding.logoImg.load(data.link)
            binding.root.setOnClickListener {
                test(position)
            }
        }
    }

    fun setCompanyList(listData : List<kr.pandadong2024.babya.server.remote.responses.company.CompanyDataResponses>){
        companyList = listData
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        holder.bind(companyList[position], test, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val binding  = ItemFindCompanyRecyclerviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CompanyViewHolder(binding)
    }

    override fun getItemCount(): Int  = companyList.size


}