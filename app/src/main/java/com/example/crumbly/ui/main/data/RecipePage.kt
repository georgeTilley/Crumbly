package com.example.crumbly.ui.main.data

import android.net.Uri

data class RecipePage(
    val id: Int,
    val recipeName: String,
    val mainImage: Uri?,
    val ingredients: List<String>,
    val stepNotes: List<String>
)
