package kr.pandadong2024.babya.home.todo_list.decoration

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TodoIDayItemDecoration(private val lastPos : Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        Log.d( "pos", "pos : $position")
        if(position == 0){

            outRect.top = 3
            outRect.bottom = 2
        }

        else if(position != lastPos){
            outRect.bottom = 20
        }
        else{
            outRect.bottom = 3
        }


    }
}