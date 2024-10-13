package kr.pandadong2024.babya.home.main

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CompanyOffsetItemDecoration(private val padding : Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = padding
        outRect.right = padding
    }
}