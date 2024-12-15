package com.example.gs.ui.components.utils

import android.view.MotionEvent
import com.example.gs.ui.components.SwipeableCardView
import com.example.gs.ui.components.animations.CardAnimator

class CardTouchHandler(
    private val cardView: SwipeableCardView,
    private val cardAnimator: CardAnimator
) {
    fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                cardView.setDX(cardView.x - ev.rawX)
                false
            }
            MotionEvent.ACTION_MOVE -> {
                val moved = Math.abs(ev.rawX + cardView.getDX() - cardView.getOriginalX())
                moved > 10
            }
            else -> false
        }
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        // Implementation moved from SwipeableCardView
        return true
    }
}