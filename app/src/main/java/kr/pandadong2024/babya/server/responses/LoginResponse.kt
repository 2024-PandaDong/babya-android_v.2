package kr.pandadong2024.babya.server.responses

data class LoginResponse(
    val `data`: Data,
    val message: String,
    val status: Int
)