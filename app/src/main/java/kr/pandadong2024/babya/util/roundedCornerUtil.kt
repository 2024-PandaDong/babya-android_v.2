package kr.pandadong2024.babya.util

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView

fun roundLeft(iv: ImageView, curveRadius : Float)  : ImageView {

    iv.outlineProvider = object : ViewOutlineProvider() {

        override fun getOutline(view: View?, outline: Outline?) {
            outline?.setRoundRect(0, 0, (view!!.width+curveRadius).toInt(), (view.height).toInt(), curveRadius)
        }
    }

    iv.clipToOutline = true
    return iv
}

fun roundAll(iv: ImageView, curveRadius : Float)  : ImageView {

    iv.outlineProvider = object : ViewOutlineProvider() {

        override fun getOutline(view: View?, outline: Outline?) {
            outline?.setRoundRect(0, 0, view!!.width, view.height, curveRadius)
        }
    }

    iv.clipToOutline = true
    return iv
}

fun roundTop(iv: ImageView, curveRadius : Float)  : ImageView {

    iv.outlineProvider = object : ViewOutlineProvider() {

        override fun getOutline(view: View?, outline: Outline?) {
            outline?.setRoundRect(0, 0, view!!.width, (view.height+curveRadius).toInt(), curveRadius)
        }
    }

    iv.clipToOutline = true
    return iv
}