package com.example.crumbly.ui.main.database


import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

//todo make primary key work not on name

@Entity(tableName = "recipe_table")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val recipeName: String,
    val mainImage: String,
    val ingredients: List<String>,
    val stepNotes: List<String>
)