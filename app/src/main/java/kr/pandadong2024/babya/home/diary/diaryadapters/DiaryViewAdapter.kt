package kr.pandadong2024.babya.home.diary.diaryadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.ItmeDiaryCardBinding
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryDataResponses

class DiaryViewAdapter(
    private var diaryDataList: List<DiaryDataResponses>,
    val makeNewDisplay: (diaryId: Int, memberId: String) -> Unit
) : RecyclerView.Adapter<DiaryViewAdapter.DiaryViewViewHolder>() {

    private var innerDiaryDataList = diaryDataList

    fun updateDiaryList(changeList: List<DiaryDataResponses>) {
        innerDiaryDataList = changeList
    }


    inner class DiaryViewViewHolder(val binding: ItmeDiaryCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindDiaryItem(diaryData: DiaryDataResponses) {
            if (diaryData.files?.get(0) == null) {
                binding.diaryMainImage.load(R.drawable.img_normal_diary)
            } else {
                binding.diaryMainImage.load(diaryData.files[0].url)
            }
            binding.titleText.text = diaryData.title
            binding.writerText.text = diaryData.nickname
            val day = "${diaryData.writtenDt?.substring(startIndex = 5, endIndex = 7)}/${
                diaryData.writtenDt?.substring(
                    startIndex = 8,
                    endIndex = 10
                )
            }"
            binding.dayeText.text = day
            binding.root.setOnClickListener {
                if (!diaryData.memberId.isNullOrBlank() && (diaryData.diaryId != null)) {
                    makeNewDisplay(
                        diaryData.diaryId,
                        diaryData.memberId
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewViewHolder {
        val binding =
            ItmeDiaryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewViewHolder(binding)
    }

    override fun getItemCount(): Int = innerDiaryDataList.size

    override fun onBindViewHolder(holder: DiaryViewViewHolder, position: Int) {
        holder.bindDiaryItem(innerDiaryDataList[position])
    }
}