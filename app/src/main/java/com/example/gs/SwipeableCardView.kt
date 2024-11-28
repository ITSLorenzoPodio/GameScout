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

/**
 * A custom view that implements swipeable card functionality similar to dating apps or card-based UIs.
 * Supports left and right swipe gestures with animations and interactions with the card underneath.
 */
class SwipeableCardView : FrameLayout {
    // Constructors
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // Properties with default values
    private var originalX = 0f
    private var originalY = 0f
    private var dX = 0f
    private var swipeListener: OnSwipeListener? = null

    // Constants
    private companion object {
        const val SWIPE_THRESHOLD = 0.3f      // to see when to autoswipe
        const val ROTATION_FACTOR = 15f       // How much the card rotates
        const val SCALE_FACTOR = 1f           // How small the card underneath is
        const val ANIMATION_DURATION = 300L    // Duration for the swipe animation
        const val RESET_ANIMATION_DURATION = 200L // Duration for reset animations
    }

    init {
        // Initialize view properties
        with(this) {
            originalX = x
            originalY = y
            elevation = 24f
            translationZ = 8f
        }
    }

    /**
     * Handles touch events for the card view.
     * Implements dragging, rotation, and swipe detection logic.
     */
    // With this, the card is touchable, and movable
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {

            //Action down is when you press for the first time
            MotionEvent.ACTION_DOWN -> {
                // Initial positions when touch begins
                originalX = x
                originalY = y
                dX = x - event.rawX
                true
            }

            //Action move if you move while pressing
            MotionEvent.ACTION_MOVE -> {
                // Moves the card when it changes position
                val moveX = event.rawX + dX - originalX
                val rotation = (moveX / width) * ROTATION_FACTOR

                // card animation
                animate()
                    .x(event.rawX + dX)
                    .rotation(rotation)
                    .setDuration(0)
                    .start()

                // Update the appearance of the card underneath
                updateCardUnderneath(moveX)
                true
            }

            //Action up when you release the finger
            MotionEvent.ACTION_UP -> {
                // Determine if swipe threshold was met
                // abs = absolute value (always positive)
                val moved = abs(x - originalX)
                when {
                    moved > width * SWIPE_THRESHOLD -> {
                        if (x > originalX) swipeRight() else swipeLeft()
                    }
                    else -> resetPosition()
                }
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    /**
     * Updates the appearance of the card underneath the current card while dragging.
     * Applies scaling and darkness effects based on drag distance.
     */
    private fun updateCardUnderneath(moveX: Float) {
        (parent as? FrameLayout)?.let { parent ->
            val cardIndex = parent.indexOfChild(this)
            if (cardIndex > 0) {
                (parent.getChildAt(cardIndex - 1) as? SwipeableCardView)?.apply {
                    // Calculate darkness factor based on movement
                    val darknessFactor = 1 - (min(abs(moveX) / (width * SWIPE_THRESHOLD), 1f) * 0.9f)
                    setDarkness(darknessFactor)
                    scaleX = SCALE_FACTOR
                    scaleY = SCALE_FACTOR
                }
            }
        }
    }

    /**
     * Applies a darkness overlay to the card with the specified intensity.
     */
    private fun setDarkness(factor: Float) {
        removeOverlay()
        addView(View(context).apply {
            setBackgroundColor(Color.BLACK)
            alpha = factor
            layoutParams = LayoutParams(width, height)
        })
    }

    /**
     * Removes any existing darkness overlay from the card.
     */
    private fun removeOverlay() {
        for (i in 0 until childCount) {
            getChildAt(i).let { child ->
                if ((child.background?.alpha ?: 0) > 0) {
                    removeView(child)

                }
            }
        }
    }

    /**
     * Animates the card sliding off to the left.
     */
    fun swipeLeft() {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()

        // Translate the card off screen to the left
        ObjectAnimator.ofFloat(this, View.TRANSLATION_X, -screenWidth).apply {
            duration = ANIMATION_DURATION
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    resetUnderneathCard()
                    swipeListener?.onSwipeLeft()
                }
            })
            start()
        }

        // Apply rotation animation
        animate()
            .rotation(-ROTATION_FACTOR)
            .setDuration(ANIMATION_DURATION)
            .start()
    }

    /**
     * Animates the card sliding off to the right.
     */
    fun swipeRight() {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()

        // Translate the card off screen to the right
        ObjectAnimator.ofFloat(this, View.TRANSLATION_X, screenWidth).apply {
            duration = ANIMATION_DURATION
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    resetUnderneathCard()
                    swipeListener?.onSwipeRight()
                }
            })
            start()
        }

        // Apply rotation animation
//        animate()
//            .rotation(ROTATION_FACTOR)
            .setDuration(ANIMATION_DURATION)
            .start()
    }

    /**
     * Resets the appearance of the card underneath to its original state.
     */
    private fun resetUnderneathCard() {
        (parent as? FrameLayout)?.let { parent ->
            val cardIndex = parent.indexOfChild(this)
            if (cardIndex > 0) {
                (parent.getChildAt(cardIndex - 1) as? SwipeableCardView)?.apply {
                    animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(RESET_ANIMATION_DURATION)
                        .start()
                    setDarkness(0f)
                }
            }
        }
    }

    /**
     * Resets the card to its original position with animation.
     */
    private fun resetPosition() {
        animate()
            .x(originalX)
            .y(originalY)
            .rotation(0f)
            .setDuration(ANIMATION_DURATION)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    resetUnderneathCard()
                }
            })
            .start()
    }

    /**
     * Sets the listener for swipe events.
     */
    fun setOnSwipeListener(listener: OnSwipeListener) {
        swipeListener = listener
    }

    /**
     * Interface defining callbacks for swipe actions.
     */
    interface OnSwipeListener {
        fun onSwipeLeft()
        fun onSwipeRight()
    }
}