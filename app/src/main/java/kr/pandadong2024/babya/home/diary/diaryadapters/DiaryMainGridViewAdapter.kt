package kr.pandadong2024.babya.home.diary.diaryadapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import coil.load
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.ItmeDiaryCardBinding
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryDataResponses

class DiaryMainGridViewAdapter(
    private var diaryData : List<DiaryDataResponses>,
    val makeNewDisplay : (diaryId : Int, memberId : String) -> Unit
) : BaseAdapter() {
    private val TAG = "DiaryMainGridViewAdapter"
    override fun getCount(): Int = diaryData.size

    override fun getItem(position: Int): Any {
        return diaryData[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }
    fun setDiaryList(changeList : List<DiaryDataResponses>){
        diaryData = changeList
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val data = diaryData[position]
        val binding  = ItmeDiaryCardBinding.inflate(LayoutInflater.from(parent?.context))
        Log.d(TAG, "data : $data")
        if(data.files?.get(0) == null){
            binding.diaryMainImage.load(R.drawable.img_normal_diary)
        } else{
            binding.diaryMainImage.load(data.files.get(0).url)
        }
        binding.titleText.text = data.title
        binding.writerText.text = data.title
        binding.dayeText.text = "${data.writtenDt?.substring(startIndex = 5, endIndex = 7)}/${data.writtenDt?.substring(startIndex = 8, endIndex = 10)}"
        binding.root.setOnClickListener {
            if (!data.memberId.isNullOrBlank() && (data.diaryId != null)){
                makeNewDisplay(
                    data.diaryId,
                    data.memberId
                )
            }
        }
        return binding.root
    }
}