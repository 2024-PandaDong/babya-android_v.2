package kr.pandadong2024.babya.home.diary.diaryadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import coil.load
import kr.pandadong2024.babya.databinding.ItmeDiaryCardBinding
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryDataResponses

class DiaryMainGridViewAdapter(
    val diaryData : List<DiaryDataResponses>,
    val makeNewDisplay : (diaryData : DiaryDataResponses) -> Unit
) : BaseAdapter() {
    override fun getCount(): Int = diaryData.size

    override fun getItem(position: Int): Any {
        return diaryData[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val data = diaryData[position]
        val binding  = ItmeDiaryCardBinding.inflate(LayoutInflater.from(parent?.context))
        binding.diaryMainImage.load(data.files?.get(0)?.url)
        binding.titleText.text = data.title
        binding.writerText.text = data.title
        binding.root.setOnClickListener {
            makeNewDisplay(data)
        }
        return binding.root
    }
}