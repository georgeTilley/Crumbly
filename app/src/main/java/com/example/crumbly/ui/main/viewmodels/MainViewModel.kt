package com.example.crumbly.ui.main.viewmodels

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.Observer
import com.example.crumbly.BR
import com.example.crumbly.ui.main.Destroyables
import com.example.crumbly.ui.main.data.RecipePage
import com.example.crumbly.ui.main.database.RecipeRepository
import com.example.crumbly.ui.main.views.adapters.RecylerViewAdapter
import javax.inject.Inject
import javax.security.auth.Destroyable

var SELECT_PICTURE = 200

class MainViewModel @Inject constructor(
    private val repository: RecipeRepository,
    destroyables: Destroyables
) : BaseObservable(), Observer<List<RecipePage>>, RecylerViewAdapter.ItemClickListener, Destroyable {

    private var currentImage: Uri? = null
    private var recipePage: RecipePage = RecipePage(0, "", currentImage, listOf(), listOf())

    @Bindable
    var recipes = mutableListOf<RecipePage>()
        set(value) {
            field = value
            notifyPropertyChanged(BR.recipes)
        }

    private var stepCount = 0

    fun resetPage() {
        currentImage = null
        recipePage = RecipePage(0, "", currentImage, listOf(), listOf())
    }

    init {
        repository.allRecipes.observeForever(this)
        destroyables.addToDestroy(this)
    }

    override fun onItemClick(view: View?, position: Int) {
        Log.d("TAG", "item clicked")
    }

    fun getNextNotes(): String {
        return try {
            recipePage.stepNotes[stepCount]
        } catch (e: ArrayIndexOutOfBoundsException) {
            recipePage.stepNotes.last()
        }
    }

    fun getIngredients() = recipePage.ingredients

    fun getRecipeName() = recipePage.recipeName

    fun getStepCount() = stepCount

    fun getImage() = recipePage.mainImage

    fun nextPage() {
        if (stepCount < recipePage.stepNotes.size - 1) {
            stepCount++
        }
    }

    fun prePage() {
        if (stepCount != 0) {
            stepCount--
        }
    }

    fun setRecipe(recipePage: RecipePage) {
        this.recipePage = recipePage
    }

    fun setImage(uri: Uri) {
        currentImage = uri
    }

    fun getUnsavedImage(): Uri? {
        return currentImage
    }

    suspend fun saveNewRecipe(
        recipeName: String,
        ingredients: List<String>,
        recipeSteps: List<String>,
        uri: Uri?
    ) {
        if(recipeName.isBlank()) {
            repository.insert(RecipePage(0, "Unnamed", uri, ingredients, recipeSteps))
        } else {
            repository.insert(RecipePage(0, recipeName, uri, ingredients, recipeSteps))
        }
    }

    suspend fun deleteRecipe(recipePage: RecipePage) {
        repository.delete(recipePage.id)
    }

    suspend fun deleteAll() {
        repository.deleteAll()
    }

    override fun onChanged(value: List<RecipePage>) {
        recipes.clear()
        recipes.addAll(value)
        notifyPropertyChanged(BR.recipes)
    }
}
