package com.example.crumbly.ui.main.views.adapters

import android.content.Context
import android.os.Handler
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.crumbly.R
import com.example.crumbly.ui.main.data.RecipePage
import com.example.crumbly.ui.main.views.MainFragment
import com.example.crumbly.ui.main.views.NewRecipeFragment
import java.util.*

class StepListRecyclerViewAdapter internal constructor(
    context: Context?,
    data: List<String>,
    private val isIngredients: Boolean
) :
    RecyclerView.Adapter<StepListRecyclerViewAdapter.ViewHolder?>() {
    private val mData: MutableList<String>
    private val mInflater: LayoutInflater

    // data is passed into the constructor
    init {
        mInflater = LayoutInflater.from(context)
        mData = Collections.synchronizedList(data.toMutableList())
    }

    fun getSteps() = mData

    private fun insertStep(position: Int) {
        mData.add(position, "")
        notifyItemInserted(position)
        notifyItemRangeChanged(position, mData.size)
    }

    private fun removeStep(position: Int) {
        if(position != 0 && itemCount - 1 > 0) {
            mData.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, mData.size)
        }
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.new_step_item, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(isIngredients) {
            holder.myTextView.visibility = View.GONE
        } else {
            holder.myTextView.text = "${position + 1}."
        }
        holder.editText.addTextChangedListener {
            if (holder.editText.isFocused && position < mData.size) {
                mData[position] = it.toString()
            }
        }

        holder.editText.setText(mData[position])
        holder.removeStepButton.setOnClickListener {
            removeStep(position)
        }

        holder.addStepButton.setOnClickListener {
            insertStep(position + 1)
        }

    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var myTextView: TextView
        var editText: EditText
        var removeStepButton: Button
        var addStepButton: Button

        init {
            myTextView = itemView.findViewById(R.id.stepCountTextView)
            editText = itemView.findViewById(R.id.stepDetailsTextView)
            removeStepButton = itemView.findViewById(R.id.removeButton)
            addStepButton = itemView.findViewById(R.id.insertBelowButton)
            editText.setText("");
        }
    }
}