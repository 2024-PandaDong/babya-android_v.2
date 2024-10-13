package kr.pandadong2024.babya.home.policy.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PolicyCategoryItemDecoration(private val horizontalPadding : Int, private val verticalPadding: Int, private val lastPosition : Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val margin = 45
        val position = parent.getChildAdapterPosition(view)
        when (position) {
            0 -> {
                outRect.right = horizontalPadding
                outRect.left = margin

            }
            lastPosition-1 -> {
                outRect.left = horizontalPadding
                outRect.right = margin
                outRect.top = verticalPadding
                outRect.bottom = verticalPadding
            }
            else -> {
                outRect.left = horizontalPadding
                outRect.right = horizontalPadding
            }
        }
    }
}