package kr.pandadong2024.babya.home.todo_list.decoration

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CategoryItemDecoration(private val horizontalPadding : Int, private val lastPosition : Int) : RecyclerView.ItemDecoration() {

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
                Log.d("test", "it : ${position} - 0")
                outRect.right = horizontalPadding
                outRect.left = margin
            }
            //        /** 첫번째 행(row-1)에 있는 아이템인 경우 상단에 [space] 만큼의 여백을 추가한다 */
            lastPosition-1 -> {
                Log.d("test", "it : ${position} - 1")
                outRect.left = horizontalPadding
                outRect.right = margin
            }
            else -> {
                Log.d("test", "it : ${position} - e")
                outRect.left = horizontalPadding
                outRect.right = horizontalPadding
            }
        }


//        val column = position + 1      // 1부터 시작
//        if (position >= 1){
//            outRect.left = 35
//        }
//        /** 마지막 열(column-N)에 있는 아이템인 경우 우측에 [space] 만큼의 여백을 추가한다 */
//        if (column != lastPosition){
//            outRect.right = 35
//        }
//        /** 모든 아이템의 좌측과 하단에 [space] 만큼의 여백을 추가한다. */
//        outRect.left = horizontalPadding
//        outRect.right = horizontalPadding





    }
}