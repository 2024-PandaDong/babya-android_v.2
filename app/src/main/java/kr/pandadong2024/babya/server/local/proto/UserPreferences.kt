package kr.pandadong2024.babya.server.local.proto

data class UserPreferences (
    val accessToken: String,
    val refreshToken: String,
)