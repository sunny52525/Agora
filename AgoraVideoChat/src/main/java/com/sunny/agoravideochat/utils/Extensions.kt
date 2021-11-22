package com.sunny.agoravideochat.utils

import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.annotation.DimenRes


//append 0 in front if it is single digit
fun Long.appendZero(): String {
    return if (this < 10) {
        "0$this"
    } else {
        this.toString()
    }
}
fun View.setBottomMargin(@DimenRes dimensionResId: Int) {
    (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
        resources.getDimension(dimensionResId).toInt()
}
fun startBlinking(view: View) {
    val anim = AlphaAnimation(0.0f, 1.0f)
    anim.duration = 500
    anim.startOffset = 20
    anim.repeatMode = Animation.REVERSE
    anim.repeatCount = Animation.INFINITE
    view.startAnimation(anim)
}