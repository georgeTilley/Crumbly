package com.example.crumbly

import UriFileUtils
import com.example.crumbly.R
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.crumbly.ui.main.Destroyables
import com.example.crumbly.ui.main.viewmodels.MainViewModel
import com.example.crumbly.ui.main.viewmodels.SELECT_PICTURE
import com.example.crumbly.ui.main.views.*
import java.io.ByteArrayOutputStream
import javax.inject.Inject


class MainActivity : AppCompatActivity(), FragmentChangeListener {

    private val mainFragment = MainFragment()
    private val newRecipeFragment = NewRecipeFragment()
    private val recipeFragment = RecipePageFragment()

    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var destroyables: Destroyables

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as MainApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainFragment.init(mainViewModel)
        newRecipeFragment.init(mainViewModel)
        recipeFragment.init(mainViewModel)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, mainFragment)
            .commitNow()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyables.destroyAll()

    }

    override fun replaceFragment(fragment: FragmentEnum) {
        val fragmentToSwitchTo = when (fragment) {
            FragmentEnum.MAIN_F -> mainFragment
            FragmentEnum.NEW_RECIPE_F -> newRecipeFragment
            FragmentEnum.RECIPE_PAGE_F -> recipeFragment
        }

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragmentToSwitchTo, fragment.toString())
        fragmentTransaction.addToBackStack(fragment.toString())
        fragmentTransaction.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            data?.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            data?.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                var selectedImageUri: Uri? = data?.data

                if (null == selectedImageUri) {
                    val extras2: Bundle? = data?.extras
                    if (extras2 != null) {
                        val photo = extras2.getParcelable<Parcelable>("data")
                        selectedImageUri = getImageUri(photo as Bitmap)
                    }
                }
                val fileUtils = UriFileUtils()
                if (selectedImageUri != null) {
                    val file = fileUtils.from(applicationContext, selectedImageUri)
                    newRecipeFragment.updateImageView(file.toUri())
                }
            }
        }
    }

    private fun getImageUri(inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }
}