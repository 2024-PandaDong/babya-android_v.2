package kr.pandadong2024.babya.server.remote.request.diary

data class EditDiaryRequest(
    val content: String,
    val diastolicPressure: Int,
    val emoji: String,
//    val fetusComment: String,
    val isPublic: Boolean,
    val nextAppointment: String,
    val pregnancyWeeks: Int,
    val systolicPressure: Int,
    val title: String,
    val url: List<String>,
    val weight: Int
)