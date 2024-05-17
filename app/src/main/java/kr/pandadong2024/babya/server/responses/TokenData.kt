package kr.pandadong2024.babya.server.responses

data class TokenData(
    val accessToken: String,
    val refreshToken: String
)