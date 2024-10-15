package kr.pandadong2024.babya.home.diary.diaryadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.view.marginTop
import coil.load
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.ItmeDiaryCardBinding
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryDataResponses

class DiaryMainGridViewAdapter(
    private var diaryData: List<DiaryDataResponses>,
    val makeNewDisplay: (diaryId: Int, memberId: String) -> Unit
) : BaseAdapter() {
    override fun getCount(): Int = diaryData.size

    override fun getItem(position: Int): Any {
        return diaryData[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    fun setDiaryList(changeList: List<DiaryDataResponses>) {
        diaryData = changeList
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val data = diaryData[position]
        val binding = ItmeDiaryCardBinding.inflate(LayoutInflater.from(parent?.context))
        if (data.files?.get(0) == null) {
            binding.diaryMainImage.load(R.drawable.img_normal_diary)
        } else {
            binding.diaryMainImage.load(data.files[0].url)
        }
        binding.titleText.text = data.title
        binding.writerText.text = data.title
        val day = "${data.writtenDt?.substring(startIndex = 5, endIndex = 7)}/${
            data.writtenDt?.substring(
                startIndex = 8,
                endIndex = 10
            )
        }"
        binding.dayeText.text = day
        binding.root.setOnClickListener {
            if (!data.memberId.isNullOrBlank() && (data.diaryId != null)) {
                makeNewDisplay(
                    data.diaryId,
                    data.memberId
                )
            }
        }
        return binding.root
    }
}