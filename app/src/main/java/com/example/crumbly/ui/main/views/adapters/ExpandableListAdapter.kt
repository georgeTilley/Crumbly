package com.example.crumbly.ui.main.views.adapters

import android.annotation.SuppressLint
import android.app.PendingIntent.getActivity
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.opengl.Visibility
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.Observable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crumbly.BR
import com.example.crumbly.R
import com.example.crumbly.ui.main.data.RecipePage
import com.example.crumbly.ui.main.viewmodels.MainViewModel
import com.example.crumbly.ui.main.views.MainFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class CustomExpandableListAdapter(
    private val context: Context, private val viewModel: MainViewModel,
    private val fragment: MainFragment,
) : BaseExpandableListAdapter() {

    private var propertyChangedCallback: Observable.OnPropertyChangedCallback
    private val recipeList: MutableList<RecipePage> = Collections.synchronizedList(viewModel.recipes).toMutableList()

    init {

        propertyChangedCallback = object : Observable.OnPropertyChangedCallback() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onPropertyChanged(
                sender: Observable?,
                propertyId: Int
            ) {
                if (propertyId == BR.recipes && sender is MainViewModel) {
                    recipeList.clear()
                    recipeList.addAll(Collections.synchronizedList(viewModel.recipes).toList())
                    notifyDataSetChanged()
                }
            }

        }

        viewModel.addOnPropertyChangedCallback(propertyChangedCallback)
    }

    override fun getChild(listPosition: Int, expandedListPosition: Int): String {
        return recipeList[listPosition].recipeName
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    @SuppressLint("InflateParams")
    override fun getChildView(
        listPosition: Int, expandedListPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup
    ): View {
        var convertView = convertView
        if (convertView == null) {
            val layoutInflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView =
                layoutInflater.inflate(R.layout.expandable_child_view, null)
        }

        if (convertView != null) {
            val layoutManager = LinearLayoutManager(context)
            val recyclerView = convertView.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = layoutManager
            val enteredRecipes = recipeList[listPosition].ingredients.filter { it != "" }
            recyclerView.adapter =
                RecylerViewAdapter(context, enteredRecipes)
            val image = convertView.findViewById<ImageView>(R.id.expandableImage)
            val bitmap = recipeList[listPosition].mainImage?.let { fragment.getBitmapFromURI(it) }
            image.setImageBitmap(bitmap)

            val deleteButton = convertView.findViewById<Button>(R.id.deleteButton)
            deleteButton.setOnClickListener {
                CoroutineScope(SupervisorJob()).launch {
                    fragment.deleteRecipe(recipeList[listPosition])
                }
            }

            val viewButton = convertView.findViewById<Button>(R.id.viewButton)
            viewButton.setOnClickListener {
                fragment.showRecipe(recipeList[listPosition])
            }
        }

        return convertView!!
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return 1
    }

    override fun getGroup(listPosition: Int): String {
        return recipeList[listPosition].recipeName
    }

    override fun getGroupCount(): Int {
        return recipeList.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(
        listPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup
    ): View {
        var convertView = convertView
        val listTitle = getGroup(listPosition)
        if (convertView == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView =
                layoutInflater.inflate(R.layout.recipe_list_item, null)
        }
        val listTitleTextView = convertView
            ?.findViewById<View>(R.id.recipeName) as TextView
        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = listTitle
        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}