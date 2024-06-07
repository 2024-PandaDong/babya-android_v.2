package kr.pandadong2024.babya.home.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.pandadong2024.babya.databinding.ItemRecyclerviewBinding
import kr.pandadong2024.babya.databinding.ItmeCompanyRankBinding
import kr.pandadong2024.babya.databinding.ItmeMyStatusBinding
import kr.pandadong2024.babya.server.remote.responses.CompanyDataResponses

class CompanyRankAdapter (val test: (Int) -> Unit) : RecyclerView.Adapter<CompanyRankAdapter.CompanyViewHolder>() {
    private lateinit var companyList : List<CompanyDataResponses>

    inner class CompanyViewHolder(private val binding : ItmeCompanyRankBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data : CompanyDataResponses, test: (Int) -> Unit, position: Int){
            binding.companyText.text = data.name
            binding.companyImage.load(data.link)
            binding.root.setOnClickListener {
                test(position)
            }
        }
    }

    fun setCompanyList(listData : List<CompanyDataResponses>){
        companyList = listData
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        holder.bind(companyList[position], test, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val binding  = ItmeCompanyRankBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CompanyViewHolder(binding)
    }

    override fun getItemCount(): Int  = companyList.size


}