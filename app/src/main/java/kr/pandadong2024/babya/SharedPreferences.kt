package kr.pandadong2024.babya

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences(context: Context) {
    companion object {
        const val PREFS_NAME = "BABYA"
        const val PREF_KEY_SKIP_QUIZ = "skipQuiz"
        const val PREF_KEY_COMPLETE_QUIZ = "completeQuiz"
        const val PREF_KEY_LAST_EDIT_TIME = "lastEditTime"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)

    fun remove() {
        prefs.edit().remove(PREF_KEY_SKIP_QUIZ).apply()
        prefs.edit().remove(PREF_KEY_COMPLETE_QUIZ).apply()
        prefs.edit().remove(PREF_KEY_LAST_EDIT_TIME).apply()
    }

    var skipQuiz: Boolean
        get() = prefs.getBoolean(PREF_KEY_SKIP_QUIZ, false)
        set(value) {
            prefs.edit().putBoolean(PREF_KEY_SKIP_QUIZ, value).apply()
        }

    var lastEditTime : String
        get() = prefs.getString(PREF_KEY_LAST_EDIT_TIME, "").orEmpty()
        set(value) {
            prefs.edit().putString(PREF_KEY_LAST_EDIT_TIME, value).apply()
        }

    var completeQuiz: Boolean
        get() = prefs.getBoolean(PREF_KEY_COMPLETE_QUIZ, false)
        set(value) {
            prefs.edit().putBoolean(PREF_KEY_COMPLETE_QUIZ, value).apply()
        }
}