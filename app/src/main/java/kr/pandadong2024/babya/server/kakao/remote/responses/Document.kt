package kr.pandadong2024.babya.server.kakao.remote.responses

data class Document(
    val place_name: String,
    val address_name: String,
    val road_address_name: String,
    val category_name: String,
    val phone: String,
    val place_url : String,
    val x: String,
    val y: String
)
