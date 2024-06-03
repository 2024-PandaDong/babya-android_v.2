package kr.pandadong2024.babya.home.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.databinding.ItemRecyclerviewBinding
import kr.pandadong2024.babya.databinding.ItmeCompanyRankBinding
import kr.pandadong2024.babya.databinding.ItmeMyStatusBinding

class CompanyRankAdapter : RecyclerView.Adapter<CompanyRankAdapter.CompanyViewHolder>() {
    private var companyList = mutableListOf<String>()
    inner class CompanyViewHolder(private val binding : ItmeCompanyRankBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data : String){
            Log.d("test", "in1")
            binding.companyText.text = data
        }
    }

    fun setCompanyList(listData : MutableList<String>){
        companyList = listData
        Log.d("test", "in2")
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        holder.bind(companyList[position])
        Log.d("test", "in3")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        Log.d("test", "in4")
        val binding  = ItmeCompanyRankBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CompanyViewHolder(binding)
    }

    override fun getItemCount(): Int  = companyList.size


}