package kr.pandadong2024.babya.util

import android.os.SystemClock
import android.view.View

class OnSingleClickListener (
    private val onClickListener: (view: View) -> Unit
) : View.OnClickListener{

    companion object {
        // 버튼 사이에 허용하는 시간간격
        const val INTERVAL = 500L
    }

    // 이전 클릭 시간 기록
    private var lastClickedTime = 0L

    private fun isSafe(): Boolean {
        return System.currentTimeMillis() - lastClickedTime > INTERVAL
    }

    override fun onClick(v: View?) {
        if (isSafe() && v != null) {
            onClickListener(v)
        }
        lastClickedTime = System.currentTimeMillis()
    }
}

fun View.setOnSingleClickListener(
    onClickListener: (view: View) -> Unit
) {

    val popupClickListener = OnSingleClickListener {
        onClickListener(it)
    }
    setOnClickListener(popupClickListener)

//    val popupClickListener = OnSingleClickListener {
//
//
//        onClickListener(it)
//    }
//    setOnClickListener(OnSingleClickListener(onClickListener))
}