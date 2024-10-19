package com.example.gs

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.core.animation.AccelerateInterpolator
import androidx.core.view.ViewCompat
import kotlin.math.abs

class SwipeableCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var originalX: Float = 0f
    private var dX: Float = 0f
    private var swipeListener: OnSwipeListener? = null
    private val swipeThreshold = 0.3f

    init {
        originalX = x
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                originalX = x
                dX = x - event.rawX
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                animate()
                    .x(event.rawX + dX)
                    .setDuration(0)
                    .start()
                return true
            }
            MotionEvent.ACTION_UP -> {
                val moved = abs(x - originalX)
                if (moved > width * swipeThreshold) {
                    if (x > originalX) {
                        swipeRight()
                    } else {
                        swipeLeft()
                    }
                } else {
                    resetPosition()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    // Modificato da private a public per permettere l'accesso dai bottoni
    fun swipeLeft() {
        ObjectAnimator.ofFloat(this, View.TRANSLATION_X, -width.toFloat()).apply {
            duration = 300
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    swipeListener?.onSwipeLeft()
                }
            })
            start()
        }
    }

    // Modificato da private a public per permettere l'accesso dai bottoni
    fun swipeRight() {
        ObjectAnimator.ofFloat(this, View.TRANSLATION_X, width.toFloat()).apply {
            duration = 300
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    swipeListener?.onSwipeRight()
                }
            })
            start()
        }
    }

    private fun resetPosition() {
        animate()
            .x(originalX)
            .setDuration(300)
            .start()
    }

    fun setOnSwipeListener(listener: OnSwipeListener) {
        this.swipeListener = listener
    }

    interface OnSwipeListener {
        fun onSwipeLeft()
        fun onSwipeRight()
    }
}