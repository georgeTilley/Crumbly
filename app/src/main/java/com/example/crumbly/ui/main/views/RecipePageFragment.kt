package com.example.crumbly.ui.main.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crumbly.R
import com.example.crumbly.ui.main.viewmodels.MainViewModel
import com.example.crumbly.ui.main.views.adapters.RecylerViewAdapter

class RecipePageFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    fun init(viewModel: MainViewModel) {
        this.viewModel = viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_recipe_page, container, false)

        updateFragment(rootView)

        val layoutManager = LinearLayoutManager(context)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        val enteredRecipes = viewModel.getIngredients().filter { it != "" }
        recyclerView.adapter =
            RecylerViewAdapter(context, enteredRecipes)

        val targetImage = viewModel.getImage()
        if(targetImage != null) {
            rootView.findViewById<ImageView>(R.id.recipeImage).setImageURI(targetImage)
        }
        rootView.findViewById<Button>(R.id.backButton).setOnClickListener {
            if(viewModel.getStepCount() == 0) {
                pressBack()
            } else {
                viewModel.prePage()
                updateFragment(rootView)
            }
        }

        rootView.findViewById<Button>(R.id.nextButton).setOnClickListener {
            viewModel.nextPage()
            updateFragment(rootView)
        }
        return rootView
    }

    fun pressBack() {
        val fc = activity as FragmentChangeListener?
        fc!!.replaceFragment(FragmentEnum.MAIN_F)
    }

    fun updateFragment(rootView: View) {
        rootView.findViewById<TextView>(R.id.notesTextView).text = viewModel.getNextNotes()
        rootView.findViewById<TextView>(R.id.recipeName).text = viewModel.getRecipeName()

        val shownSteps = viewModel.getStepCount() + 1
        rootView.findViewById<TextView>(R.id.stepCount).text = "Step: $shownSteps"

    }

}