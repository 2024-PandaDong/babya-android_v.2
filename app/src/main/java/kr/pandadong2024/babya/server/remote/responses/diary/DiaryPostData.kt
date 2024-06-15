package kr.pandadong2024.babya.server.remote.responses.diary

import com.google.gson.annotations.SerializedName

data class DiaryPostData(
    @field:SerializedName("diary")
    val diary: DiaryData,
    @field:SerializedName("files")
    val files: List<DiaryFile>
)