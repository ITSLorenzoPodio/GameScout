package com.example.gs.ui.components.utils

import android.view.MotionEvent
import com.example.gs.ui.components.SwipeableCardView
import com.example.gs.ui.components.animations.CardAnimator
import com.example.gs.utils.Constants
import kotlin.math.abs

class CardTouchHandler(
    private val cardView: SwipeableCardView,
    private val cardAnimator: CardAnimator
) {
    private var initialX = 0f
    private var initialY = 0f
    private var dX = 0f

    fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = cardView.x
                initialY = cardView.y
                dX = cardView.x - ev.rawX
                false
            }
            MotionEvent.ACTION_MOVE -> {
                val moved = Math.abs(ev.rawX + dX - initialX)
                moved > 10
            }
            else -> false
        }
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = cardView.x
                initialY = cardView.y
                dX = cardView.x - event.rawX
                true
            }

            MotionEvent.ACTION_MOVE -> {
                val moveX = event.rawX + dX - initialX
                val rotation = (moveX / cardView.width) * Constants.ROTATION_FACTOR

                cardView.animate()
                    .x(event.rawX + dX)
                    .rotation(rotation)
                    .setDuration(0)
                    .start()

                cardAnimator.updateCardUnderneath(moveX)
                true
            }

            MotionEvent.ACTION_UP -> {
                val moved = abs(cardView.x - initialX)
                when {
                    moved > cardView.width * Constants.SWIPE_THRESHOLD -> {
                        if (cardView.x > initialX) {
                            cardAnimator.swipeRight()
                        } else {
                            cardAnimator.swipeLeft()
                        }
                    }
                    else -> {
                        cardAnimator.resetPosition()
                    }
                }
                true
            }

            else -> false
        }
    }
}