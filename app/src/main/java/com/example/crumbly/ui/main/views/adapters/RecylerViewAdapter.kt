package com.example.crumbly.ui.main.views.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.crumbly.BR
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.recyclerview.widget.RecyclerView
import com.example.crumbly.R
import com.example.crumbly.ui.main.data.RecipePage
import com.example.crumbly.ui.main.database.RecipeEntity
import com.example.crumbly.ui.main.viewmodels.MainViewModel
import com.example.crumbly.ui.main.views.MainFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Collections
import javax.security.auth.Destroyable

class RecylerViewAdapter(context: Context?, val ingredients : List<String>) :
    RecyclerView.Adapter<RecylerViewAdapter.ViewHolder?>(), Destroyable {

    private val mData: List<String> = ingredients
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null

    // data is passed into the constructor
    init {
        mInflater = LayoutInflater.from(context)
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.recipe_group_item, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = mData[position]
        holder.myTextView.text = ingredient
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.count()
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var myTextView: TextView

        init {
            myTextView = itemView.findViewById(R.id.expandedListItem)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }
    }

    // convenience method for getting data at click position
    fun getItem(id: Int): String {
        return mData[id]
    }

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    override fun destroy() {
//        viewModel.removeOnPropertyChangedCallback(propertyChangedCallback)
    }
}