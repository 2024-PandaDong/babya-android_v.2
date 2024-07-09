package kr.pandadong2024.babya.home.dash_board

import android.content.Context
import android.content.SharedPreferences

object TypeValue {
    const val PREF_NAME = "typeValue"
    private const val KEY_TYPE = "type"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setTypeValue(type: String?) {
        sharedPreferences.edit().apply {
            putString(KEY_TYPE, type)
            apply()
        }
    }

    fun getType(): String? {
        return sharedPreferences.getString(KEY_TYPE, "1")
    }

}