package com.example.crumbly.ui.main.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    // The flow always holds/caches latest version of data. Notifies its observers when the
    // data has changed.
    @Query("SELECT * FROM recipe_table")
    fun getAllRecipes(): LiveData<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(recipeEntity: RecipeEntity)

    @Query("DELETE FROM recipe_table")
    suspend fun deleteAll()

    @Query("DELETE FROM recipe_table WHERE id = :id")
    fun deleteRecipeById(id: Int)
}