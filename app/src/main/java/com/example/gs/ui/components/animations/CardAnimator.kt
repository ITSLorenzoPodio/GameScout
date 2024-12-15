package com.example.gs.ui.components.animations

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import com.example.gs.ui.components.SwipeableCardView
import com.example.gs.ui.components.listeners.OnSwipeListener
import com.example.gs.utils.Constants
import kotlin.math.abs
import kotlin.math.min

class CardAnimator(private val cardView: SwipeableCardView) {
    private var swipeListener: OnSwipeListener? = null

    fun setSwipeListener(listener: OnSwipeListener) {
        swipeListener = listener
    }

    /**
     * Anima il movimento della carta durante il trascinamento
     */
    fun animateCardDrag(moveX: Float, width: Int) {
        val rotation = (moveX / width) * Constants.ROTATION_FACTOR

        cardView.animate()
            .x(cardView.x)
            .rotation(rotation)
            .setDuration(0)
            .start()

        updateCardUnderneath(moveX)
    }

    /**
     * Gestisce l'animazione di swipe verso sinistra
     */
    fun swipeLeft() {
        val screenWidth = cardView.resources.displayMetrics.widthPixels.toFloat()

        // Animazione di traslazione
        ObjectAnimator.ofFloat(cardView, View.TRANSLATION_X, -screenWidth).apply {
            duration = Constants.ANIMATION_DURATION
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    resetUnderneathCard()
                    swipeListener?.onSwipeLeft()
                }
            })
            start()
        }

        // Animazione di rotazione
        cardView.animate()
            .rotation(-Constants.ROTATION_FACTOR)
            .setDuration(Constants.ANIMATION_DURATION)
            .start()
    }

    /**
     * Gestisce l'animazione di swipe verso destra
     */
    fun swipeRight() {
        val screenWidth = cardView.resources.displayMetrics.widthPixels.toFloat()

        // Animazione di traslazione
        ObjectAnimator.ofFloat(cardView, View.TRANSLATION_X, screenWidth).apply {
            duration = Constants.ANIMATION_DURATION
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    resetUnderneathCard()
                    swipeListener?.onSwipeRight()
                }
            })
            start()
        }

        // Animazione di rotazione
        cardView.animate()
            .rotation(Constants.ROTATION_FACTOR)
            .setDuration(Constants.ANIMATION_DURATION)
            .start()
    }

    /**
     * Resetta la posizione della carta alla posizione originale
     */
    fun resetPosition() {
        cardView.animate()
            .x(cardView.getOriginalX())
            .y(cardView.getOriginalY())
            .rotation(0f)
            .setDuration(Constants.ANIMATION_DURATION)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    resetUnderneathCard()
                }
            })
            .start()
    }

    /**
     * Aggiorna l'aspetto della carta sottostante durante il trascinamento
     */
    fun updateCardUnderneath(moveX: Float) {
        (cardView.parent as? FrameLayout)?.let { parent ->
            val cardIndex = parent.indexOfChild(cardView)
            if (cardIndex > 0) {
                (parent.getChildAt(cardIndex - 1) as? SwipeableCardView)?.apply {
                    // Calcola il fattore di oscuramento basato sul movimento
                    val darknessFactor = 1 - (min(
                        abs(moveX) / (width * Constants.SWIPE_THRESHOLD),
                        1f
                    ) * 0.9f)
                    setCardDarkness(darknessFactor)
                    scaleX = Constants.SCALE_FACTOR
                    scaleY = Constants.SCALE_FACTOR
                }
            }
        }
    }

    /**
     * Resetta l'aspetto della carta sottostante
     */
    private fun resetUnderneathCard() {
        (cardView.parent as? FrameLayout)?.let { parent ->
            val cardIndex = parent.indexOfChild(cardView)
            if (cardIndex > 0) {
                (parent.getChildAt(cardIndex - 1) as? SwipeableCardView)?.apply {
                    animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(Constants.RESET_ANIMATION_DURATION)
                        .start()
                    setCardDarkness(0f)
                }
            }
        }
    }

    /**
     * Imposta l'oscuramento della carta
     */
    private fun SwipeableCardView.setCardDarkness(factor: Float) {
        removeCardOverlay()
        addView(View(context).apply {
            setBackgroundColor(Color.BLACK)
            alpha = factor
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        })
    }

    /**
     * Rimuove l'overlay di oscuramento dalla carta
     */
    private fun SwipeableCardView.removeCardOverlay() {
        for (i in childCount - 1 downTo 0) {
            val child = getChildAt(i)
            if (child != null && child.background != null && child.background.alpha > 0) {
                removeView(child)
            }
        }
    }
}