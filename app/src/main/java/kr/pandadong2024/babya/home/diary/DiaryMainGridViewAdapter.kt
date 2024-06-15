package kr.pandadong2024.babya.home.diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import coil.load
import kr.pandadong2024.babya.databinding.ItmeDiaryCardBinding
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryData
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryStatus

class DiaryMainGridViewAdapter(
    val diaryData : DiaryStatus,
    val makeNewDisplay : (diaryData : DiaryData) -> Unit
) : BaseAdapter() {
    override fun getCount(): Int = diaryData.data!!.size

    override fun getItem(position: Int): Any {
        return diaryData.data!![position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val data = diaryData.data!![position]
        val binding  = ItmeDiaryCardBinding.inflate(LayoutInflater.from(parent?.context))
        binding.diaryMainImage.load(data.files[0].url)
        binding.titleText.text = data.diary.title
        binding.writerText.text = data.diary.title
        binding.root.setOnClickListener {
            makeNewDisplay(data.diary)
        }
        return binding.root
    }
}