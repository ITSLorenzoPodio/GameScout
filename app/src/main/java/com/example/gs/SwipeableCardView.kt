package com.example.gs

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import kotlin.math.abs
import kotlin.math.min

class SwipeableCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var originalX: Float = 0f
    private var originalY: Float = 0f
    private var dX: Float = 0f
    private var swipeListener: OnSwipeListener? = null
    private val swipeThreshold = 0.3f
    private val rotationFactor = 15f
    private val scaleFactorOnSwipe = 1f

    init {
        originalX = x
        originalY = y
        elevation = 24f
        translationZ = 8f
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                originalX = x
                originalY = y
                dX = x - event.rawX
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val moveX = event.rawX + dX - originalX
                val rotation = (moveX / width) * rotationFactor

                animate()
                    .x(event.rawX + dX)
                    .rotation(rotation)
                    .setDuration(0)
                    .start()

                updateCardUnderneath(moveX)
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

    private fun updateCardUnderneath(moveX: Float) {
        val parent = parent as? FrameLayout ?: return
        val cardIndex = parent.indexOfChild(this)
        if (cardIndex > 0) {
            val cardUnderneath = parent.getChildAt(cardIndex - 1) as? SwipeableCardView
            cardUnderneath?.let {
                val darknessFactor = 1-(min(abs(moveX) / (width * swipeThreshold), 1f) * 0.9f)
                it.setDarkness(darknessFactor)
                it.scaleX = scaleFactorOnSwipe
                it.scaleY = scaleFactorOnSwipe
            }
        }
    }

    private fun setDarkness(factor: Float) {
        val overlay = View(context).apply {
            setBackgroundColor(Color.BLACK)
            alpha = factor
        }
        overlay.layoutParams = LayoutParams(width, height)
        removeOverlay()
        addView(overlay)
    }

    private fun removeOverlay() {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.background != null && child.background.alpha > 0) {
                removeView(child)
                break
            }
        }
    }

    fun swipeLeft() {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        ObjectAnimator.ofFloat(this, View.TRANSLATION_X, -screenWidth).apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    resetUnderneathCard()
                    swipeListener?.onSwipeLeft()
                }
            })
            start()
        }
        animate()
            .rotation(-rotationFactor)
            .setDuration(300)
            .start()
    }

    fun swipeRight() {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        ObjectAnimator.ofFloat(this, View.TRANSLATION_X, screenWidth).apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    resetUnderneathCard()
                    swipeListener?.onSwipeRight()
                }
            })
            start()
        }
        animate()
            .rotation(rotationFactor)
            .setDuration(300)
            .start()
    }

    private fun resetUnderneathCard() {
        val parent = parent as? FrameLayout ?: return
        val cardIndex = parent.indexOfChild(this)
        if (cardIndex > 0) {
            val cardUnderneath = parent.getChildAt(cardIndex - 1) as? SwipeableCardView
            cardUnderneath?.let {
                it.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .start()
                it.setDarkness(0f)
            }
        }
    }

    private fun resetPosition() {
        animate()
            .x(originalX)
            .y(originalY)
            .rotation(0f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    resetUnderneathCard()
                }
            })
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