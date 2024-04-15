package com.example.crumbly.ui.main.database


import android.net.Uri
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.crumbly.ui.main.data.RecipePage
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class RecipeRepository @Inject constructor(private val recipeDao: RecipeDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allRecipes: LiveData<List<RecipePage>> = recipeDao.getAllRecipes().map { recipeEntityList ->
        recipeEntityList.map {
            RecipePage(
                it.id,
                it.recipeName,
                Uri.parse(it.mainImage),
                it.ingredients,
                it.stepNotes
            )
        }
    }

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(recipe: RecipePage) {
        recipeDao.insert(
            RecipeEntity(
                0,
                recipe.recipeName,
                recipe.mainImage?.toString() ?: "",
                recipe.ingredients,
                recipe.stepNotes
            )
        )
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(id: Int) {
        recipeDao.deleteRecipeById(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() {
        recipeDao.deleteAll()
    }

}