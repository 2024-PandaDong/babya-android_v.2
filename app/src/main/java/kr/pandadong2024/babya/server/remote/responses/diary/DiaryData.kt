package kr.pandadong2024.babya.server.remote.responses.diary

import com.google.gson.annotations.SerializedName

data class DiaryData(
    @field:SerializedName("diary")
    val diary: Diary,
    @field:SerializedName("files")
    val files: List<DiaryFile>
)