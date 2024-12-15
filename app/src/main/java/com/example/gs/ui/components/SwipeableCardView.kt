package com.example.gs.ui.components

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
import com.example.gs.R
import kotlin.math.abs
import kotlin.math.min
import com.example.gs.ui.components.listeners.OnSwipeListener
import com.example.gs.ui.components.animations.CardAnimator
import com.example.gs.ui.components.utils.CardTouchHandler
import com.example.gs.utils.Constants

class SwipeableCardView : FrameLayout {
    private var originalX = 0f
    private var originalY = 0f
    private var dX = 0f
    private var swipeListener: OnSwipeListener? = null
    private lateinit var cardAnimator: CardAnimator
    private lateinit var touchHandler: CardTouchHandler

    // Constructors
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        cardAnimator = CardAnimator(this)
        touchHandler = CardTouchHandler(this, cardAnimator)

        with(this) {
            originalX = x
            originalY = y
            elevation = resources.displayMetrics.density * 8
            clipChildren = true
            clipToOutline = true
            background = resources.getDrawable(R.drawable.card_background, null)
            isClickable = true
        }
    }

    fun swipeLeft() {
        cardAnimator.swipeLeft()
    }

    fun swipeRight() {
        cardAnimator.swipeRight()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean =
        touchHandler.onInterceptTouchEvent(ev)

    override fun onTouchEvent(event: MotionEvent): Boolean =
        touchHandler.onTouchEvent(event)

    fun setOnSwipeListener(listener: OnSwipeListener) {
        swipeListener = listener
        cardAnimator.setSwipeListener(listener)
    }

    // Getter methods for internal state
    internal fun getOriginalX() = originalX
    internal fun getOriginalY() = originalY
    internal fun getDX() = dX
    internal fun setDX(value: Float) { dX = value }
}