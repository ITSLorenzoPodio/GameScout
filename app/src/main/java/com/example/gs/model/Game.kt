package com.example.gs.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Game(
    val id: Int = 0,
    val title: String = "",
    val imageUrl: String = "",
    val genre: String = "",
    val platforms: String = "",
    val userScore: String = "",
    val description: String = "",
    val originalPrice: Double = 0.0,
    val currentPrice: Double = 0.0,
    val discount: Double = 0.0,
    val rating: Double = 0.0,
    val url: String = ""
) : Parcelable