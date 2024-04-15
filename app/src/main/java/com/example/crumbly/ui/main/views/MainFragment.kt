package com.example.crumbly.ui.main.views

import UriFileUtils
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.crumbly.R
import com.example.crumbly.ui.main.data.RecipePage
import com.example.crumbly.ui.main.viewmodels.MainViewModel
import com.example.crumbly.ui.main.views.adapters.CustomExpandableListAdapter
import com.example.crumbly.ui.main.views.adapters.RecylerViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File


class MainFragment : Fragment() {

    lateinit var mainViewModel: MainViewModel
    var adapter: ExpandableListView? = null

    fun init(mainViewModel: MainViewModel) {
        this.mainViewModel = mainViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
        rootView.findViewById<Button>(R.id.newRecipeButton).setOnClickListener {
            goToNewRecipe()
        }

        // set up the RecyclerView
        val expandableListView: ExpandableListView = rootView.findViewById(R.id.expandableListView)

        if(expandableListView.adapter == null) {
            val expandableListAdapter =
                context?.let { CustomExpandableListAdapter(it, mainViewModel, this)}
            expandableListView.setAdapter(expandableListAdapter)
        }
        return rootView
    }

    suspend fun deleteRecipe(recipePage: RecipePage) {
        mainViewModel.deleteRecipe(recipePage)
    }

    fun showRecipe(recipePage: RecipePage) {
        mainViewModel.setRecipe(recipePage)
        val fc = activity as FragmentChangeListener?
        fc!!.replaceFragment(FragmentEnum.RECIPE_PAGE_F)
    }

    private fun goToNewRecipe() {
        mainViewModel.resetPage()
        val fc = activity as FragmentChangeListener?
        fc!!.replaceFragment(FragmentEnum.NEW_RECIPE_F)
    }

    fun getBitmapFromURI(uri: Uri): Bitmap? {
        val fileUtils = UriFileUtils()
        val bitmap: Bitmap? = context?.let { fileUtils.decodeUriToBitmap(it, uri) }
        if (bitmap != null) {
            return resizeImageForImageView(bitmap)
        }
        return null
    }

    var scaleSize = 1024 / 2

    private fun resizeImageForImageView(bitmap: Bitmap): Bitmap? {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        val newWidth: Int
        val newHeight: Int
        val multFactor: Float
        if (originalHeight > originalWidth) {
            newHeight = scaleSize
            multFactor = originalWidth.toFloat() / originalHeight.toFloat()
            newWidth = (newHeight * multFactor).toInt()
        } else if (originalWidth > originalHeight) {
            newWidth = scaleSize
            multFactor = originalHeight.toFloat() / originalWidth.toFloat()
            newHeight = (newWidth * multFactor).toInt()
        } else {
            newHeight = scaleSize
            newWidth = scaleSize
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false)
    }
}