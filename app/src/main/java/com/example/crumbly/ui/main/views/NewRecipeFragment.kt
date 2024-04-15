package com.example.crumbly.ui.main.views

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crumbly.MainActivity
import com.example.crumbly.R
import com.example.crumbly.databinding.ImageChooserDialogBinding
import com.example.crumbly.ui.main.viewmodels.MainViewModel
import com.example.crumbly.ui.main.viewmodels.SELECT_PICTURE
import com.example.crumbly.ui.main.views.adapters.StepListRecyclerViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class NewRecipeFragment : Fragment() {

    private var stepsAdapter: StepListRecyclerViewAdapter? = null
    private var ingredientsAdapter: StepListRecyclerViewAdapter? = null

    val steps = mutableListOf("")

    lateinit var mainViewModel: MainViewModel
    lateinit var imageView: ImageView
    lateinit var imageViewButton: Button
    lateinit var titleEditText: EditText

    fun init(mainViewModel: MainViewModel) {
        this.mainViewModel = mainViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val rootView = inflater.inflate(R.layout.fragment_new_recipe, container, false)
        // set up the RecyclerView
        stepsAdapter = StepListRecyclerViewAdapter(rootView.context, steps, false)
        ingredientsAdapter = StepListRecyclerViewAdapter(rootView.context, steps, true)
        val stepsLayoutManager = LinearLayoutManager(rootView.context)
        val ingredientsLayoutManager = LinearLayoutManager(rootView.context)

        val stepsRecyclerView: RecyclerView = rootView.findViewById(R.id.stepsRv)
        stepsRecyclerView.layoutManager = stepsLayoutManager
        stepsRecyclerView.adapter = stepsAdapter

        val ingredientsRecyclerView: RecyclerView = rootView.findViewById(R.id.ingredientsRv)
        ingredientsRecyclerView.layoutManager = ingredientsLayoutManager
        ingredientsRecyclerView.adapter = ingredientsAdapter

        rootView.findViewById<Button>(R.id.recipeImageButton).setOnClickListener {
            imageChooser()
        }
        titleEditText = rootView.findViewById(R.id.recipeNameEditText)

        imageViewButton = rootView.findViewById(R.id.recipeImageButton)
        imageView = rootView.findViewById(R.id.recipeImage)
        imageView.visibility = View.INVISIBLE
        rootView.findViewById<Button>(R.id.saveButton).setOnClickListener {
            val tempStepsAdapter = stepsAdapter
            val tempIngredientsAdapter = ingredientsAdapter
            if (tempStepsAdapter != null && tempIngredientsAdapter != null) {
                CoroutineScope(SupervisorJob()).launch {
                    mainViewModel.saveNewRecipe(
                        rootView.findViewById<EditText>(R.id.recipeNameEditText).text.toString(),
                        tempIngredientsAdapter.getSteps(),
                        tempStepsAdapter.getSteps(),
                        mainViewModel.getUnsavedImage()
                    )
                    pressBack()
                }
            }
        }

        rootView.findViewById<Button>(R.id.backButton).setOnClickListener {
            pressBack()
        }
        return rootView
    }

    private fun pressBack() {
        titleEditText.text.clear()
        val fc = activity as FragmentChangeListener?
        fc!!.replaceFragment(FragmentEnum.MAIN_F)
    }

    private fun imageChooser() {
        val builder: AlertDialog.Builder? = context?.let { AlertDialog.Builder(it) }
        if (builder != null) {
            builder.setTitle("Add a new image")
            builder.setCancelable(false)
            val dialogView = ImageChooserDialogBinding.inflate(layoutInflater)
            builder.setView(dialogView.root)
            builder.setPositiveButton("Cancel",
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    // When the user click yes button then app will close
                    dialog?.cancel()
                })

            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()

            dialogView.galleryButton.setOnClickListener {
                openGallery()
                alertDialog.cancel()
            }
            dialogView.cameraButton.setOnClickListener {
                openCamera()
                alertDialog.cancel()
            }

        }
    }

    private fun openGallery() {
        // create an instance of the
        // intent of the type image
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.type = "image/*"

        // pass the constant to compare it
        // with the returned requestCode
        ActivityCompat.startActivityForResult(
            activity as MainActivity,
            Intent.createChooser(intent, "Select Picture"),
            SELECT_PICTURE,
            null
        )
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        ActivityCompat.startActivityForResult(
            activity as MainActivity,
            intent,
            SELECT_PICTURE,
            null
        )
    }

    fun updateImageView(uri: Uri) {
        mainViewModel.setImage(uri)
        imageView.visibility = View.VISIBLE
        imageView.setImageURI(uri)
        imageViewButton.visibility = View.GONE
        imageView.setOnClickListener {
            imageChooser()
        }
    }
}