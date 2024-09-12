package kr.pandadong2024.babya.util

import android.os.SystemClock
import android.view.View

class OnSingleClickListener (
    private val onClickListener: (view: View) -> Unit
) : View.OnClickListener{

    companion object {
        // 버튼 사이에 허용하는 시간간격
        const val INTERVAL = 200L
    }

    // 이전 클릭 시간 기록
    private var lastClickedTime = 0L

    override fun onClick(view: View) {
        // 클릭 시간
        val onClickedTime = SystemClock.elapsedRealtime()
        // 간격보다 작으면 클릭 no
        if ((onClickedTime-lastClickedTime) < INTERVAL) { return }

        lastClickedTime = onClickedTime
        onClickListener.invoke(view)
    }
}

fun View.setOnSingleClickListener(
    onClickListener: (view: View) -> Unit
) {
    setOnClickListener(OnSingleClickListener(onClickListener))
}