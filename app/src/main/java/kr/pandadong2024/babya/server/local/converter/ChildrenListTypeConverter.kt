package kr.pandadong2024.babya.server.local.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kr.pandadong2024.babya.server.responses.ProfileChildrenData

@ProvidedTypeConverter
class ChildrenListTypeConverter {
    @TypeConverter
    fun fromProfileChildrenDataList(value: List<ProfileChildrenData>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toProfileChildrenDataList(value: String?): List<ProfileChildrenData>? {
        val listType = object : TypeToken<List<ProfileChildrenData>>() {}.type
        return Gson().fromJson(value, listType)
    }
}