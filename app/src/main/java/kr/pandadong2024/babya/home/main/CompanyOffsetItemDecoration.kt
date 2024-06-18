package kr.pandadong2024.babya.home.main

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CompanyOffsetItemDecoration(padding : Int) : RecyclerView.ItemDecoration() {
    val offsetPadding = padding
    @Override
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = offsetPadding
        outRect.right = offsetPadding

    }
}