package kr.pandadong2024.babya.server.remote.responses.diary

data class DiaryStatus(
    val data: List<DiaryPostData>?,
    val status : Boolean
)
