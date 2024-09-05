package kr.pandadong2024.babya.home.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.ItemMyStatusProfileBinding
import kr.pandadong2024.babya.databinding.ItmeMyStatusBinding
import kr.pandadong2024.babya.server.remote.responses.UserDataResponses
import kr.pandadong2024.babya.server.remote.responses.main.UserWeekStatus

class StatusAdapter(
    val userWeekStatus: UserWeekStatus,
    val userData: UserDataResponses
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    inner class StatusPagerViewHolder(private val binding: ItmeMyStatusBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun statusBind() {
//            binding.range.isEnabled = false
            binding.goodSeekBar.progress = userWeekStatus.c1!!
            binding.normalSeekBar.progress = userWeekStatus.c2!!
            binding.painSeekBar.progress = userWeekStatus.c3!!
            binding.tiredSeekBar.progress = userWeekStatus.c4!!
            binding.uneasySeekBar.progress = userWeekStatus.c5!!
            binding.nameText.text = "이번주 ${userData.nickname}님의 상태는 좋음입니다."
            binding.emotionImage.load(R.drawable.img_good)

            binding.goodSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    binding.goodSeekBar.progress = userWeekStatus.c1!!
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    binding.goodSeekBar.progress = userWeekStatus.c1!!
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    binding.goodSeekBar.progress = userWeekStatus.c1!!
                }

            })


            binding.normalSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    binding.normalSeekBar.progress = userWeekStatus.c1!!
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    binding.normalSeekBar.progress = userWeekStatus.c1!!
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    binding.normalSeekBar.progress = userWeekStatus.c1!!
                }

            })

            binding.painSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    binding.painSeekBar.progress = userWeekStatus.c1!!
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    binding.painSeekBar.progress = userWeekStatus.c1!!
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    binding.painSeekBar.progress = userWeekStatus.c1!!
                }

            })

            binding.tiredSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    binding.tiredSeekBar.progress = userWeekStatus.c1!!
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    binding.tiredSeekBar.progress = userWeekStatus.c1!!
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    binding.tiredSeekBar.progress = userWeekStatus.c1!!
                }

            })

            binding.uneasySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    binding.uneasySeekBar.progress = userWeekStatus.c1!!
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    binding.uneasySeekBar.progress = userWeekStatus.c1!!
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    binding.uneasySeekBar.progress = userWeekStatus.c1!!
                }

            })


        }

    }

    inner class UserPagerViewHolder(private val binding: ItemMyStatusProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun userInfoBind() {
            binding.titleText.text = "현재 ${userData.nickname}님의 상태입니다."
            binding.ageCount.text = userData.age.toString()
            binding.marryCount.text = userData.marriedYears.toString()
            binding.dDayCount.text = userData.dDay.toString()
            binding.profileImage.load(userData.profileImg)
        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            0 -> {
                (holder as StatusPagerViewHolder).statusBind()
            }

            1 -> {
                (holder as UserPagerViewHolder).userInfoBind()
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val binding =
                    ItmeMyStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                StatusPagerViewHolder(binding)
            }

            1 -> {
                val binding = ItemMyStatusProfileBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                UserPagerViewHolder(binding)
            }

            else -> {
                val binding = ItemMyStatusProfileBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                UserPagerViewHolder(binding)
            }

        }

    }

    override fun getItemCount(): Int = 2


}
