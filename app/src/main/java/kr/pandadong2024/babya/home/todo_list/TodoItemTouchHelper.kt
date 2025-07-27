package kr.pandadong2024.babya.home.todo_list

import android.graphics.Canvas
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kr.pandadong2024.babya.home.todo_list.adapter.TodoItemAdapter
import kotlin.math.max
import kotlin.math.min

class TodoItemTouchHelper : ItemTouchHelper.Callback() {

    private var currentPosition: Int? = null
    private var previousPosition: Int? = null
    private var currentDx = 0f
    private var clamp = 0f

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {
        clamp = 360f
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        currentDx = 0f
        getDefaultUIUtil().clearView(getView(viewHolder))
        previousPosition = viewHolder.adapterPosition
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        viewHolder?.let {
            currentPosition = it.adapterPosition
            getDefaultUIUtil().onSelected(getView(it))
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            val view = getView(viewHolder)
            val isClamped = getTag(viewHolder)
            val x = clampViewPositionHorizontal(view, dX, isClamped, isCurrentlyActive)

            currentDx = x
            getDefaultUIUtil().onDraw(
                c,
                recyclerView,
                getView(viewHolder),
                x,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }

    private fun clampViewPositionHorizontal(
        view: View,
        dX: Float,
        isClamped: Boolean,
        isCurrentlyActive: Boolean
    ): Float {
        // View의 가로 길이의 절반까지만 swipe 되도록
        val min: Float = -360f
        // RIGHT 방향으로 swipe 막기
        val max: Float = 0f

        val x = if (isClamped) {
            // View가 고정되었을 때 swipe되는 영역 제한
            if (isCurrentlyActive) dX - clamp else -clamp
        } else {
            dX
        }

        return min(max(min, x), max)
    }

    private fun getView(viewHolder: RecyclerView.ViewHolder): View {
        return (viewHolder as TodoItemAdapter.TodoItemViewHolder).binding.swipeView
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * 20
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        val isClamped = getTag(viewHolder)
        setTag(viewHolder, !isClamped && currentDx <= -clamp)
        return 5f
    }

    fun setClamp(clamp: Float) {
        this.clamp = clamp
    }

    private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
        viewHolder.itemView.tag = isClamped
    }

    private fun getTag(viewHolder: RecyclerView.ViewHolder): Boolean {
        return viewHolder.itemView.tag as? Boolean ?: false
    }

    fun removePreviousClamp(recyclerView: RecyclerView) {
        if (currentPosition == previousPosition)
            return
        previousPosition?.let {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
            getView(viewHolder).translationX = 10f
            setTag(viewHolder, false)
            previousPosition = null
        }
    }

}