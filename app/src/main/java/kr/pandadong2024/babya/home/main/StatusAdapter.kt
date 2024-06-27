package kr.pandadong2024.babya.home.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kr.pandadong2024.babya.databinding.ItemBanerCardBinding
import kr.pandadong2024.babya.databinding.ItemMyStatusProfileBinding
import kr.pandadong2024.babya.databinding.ItmeMyStatusBinding
import kr.pandadong2024.babya.server.remote.responses.main.UserWeekStatus

class StatusAdapter (val userWeekStatus : UserWeekStatus) :RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    inner class StatusPagerViewHolder(private val binding : ItmeMyStatusBinding): RecyclerView.ViewHolder(binding.root){
        fun statusBind(){
//            binding.range.isEnabled = false
            binding.goodSeekBar.progress = userWeekStatus.c1!!
            binding.normalSeekBar.progress = userWeekStatus.c2!!
            binding.painSeekBar.progress = userWeekStatus.c3!!
            binding.tiredSeekBar.progress = userWeekStatus.c4!!
            binding.uneasySeekBar.progress = userWeekStatus.c5!!


        }

    }
    inner class UserPagerViewHolder(private val binding : ItemMyStatusProfileBinding): RecyclerView.ViewHolder(binding.root){

        fun userInfoBind(){
            Log.d("TAG", "userInfoBind")
//            profileBinding.dDayText.text = "text"
        }

    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(position){
            0 ->{
                (holder as StatusPagerViewHolder).statusBind()
            }
            1 ->{
                (holder as UserPagerViewHolder).userInfoBind()
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            0 ->{
                val binding  = ItmeMyStatusBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                StatusPagerViewHolder(binding)
            }

            1 ->{
                val binding  = ItemMyStatusProfileBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                UserPagerViewHolder(binding)
            }

            else ->{
                val binding  = ItemMyStatusProfileBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                UserPagerViewHolder(binding)
            }

        }

    }

    override fun getItemCount(): Int = 2


}
