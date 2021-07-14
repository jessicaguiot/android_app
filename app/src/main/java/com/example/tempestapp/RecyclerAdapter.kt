package com.example.tempestapp

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView

class RecyclerAdapter(private val listPosts: MutableList<Posts>, private var isDarkMode: Boolean) : RecyclerView.Adapter<RecyclerAdapter.PostViewHolder>() {

    private var black = -16777216
    private var white = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.titleTextView.text = listPosts[position].title
        holder.bodyTextView.text = listPosts[position].body

        if (isDarkMode) {
            holder.bodyTextView.setTextColor(white)
            holder.titleTextView.setTextColor(white)
            holder.staticTitleTextView.setTextColor(white)
            holder.staticBodyTextView.setTextColor(white)
            holder.itemView.setBackgroundColor(black)
        } else {
            holder.bodyTextView.setTextColor(black)
            holder.titleTextView.setTextColor(black)
            holder.staticTitleTextView.setTextColor(black)
            holder.staticBodyTextView.setTextColor(black)
            holder.itemView.setBackgroundColor(white)
        }
    }

    override fun getItemCount(): Int {
        return listPosts.size
    }

    fun swapItems(newList: MutableList<Posts>) {
        listPosts.addAll(newList)
        notifyDataSetChanged()
    }

    fun setBackground(isChecked: Boolean){
        isDarkMode = isChecked
        notifyDataSetChanged()
    }

    inner class PostViewHolder(view:View) : RecyclerView.ViewHolder(view) {

        var titleTextView: AppCompatTextView
        var bodyTextView: AppCompatTextView
        var staticTitleTextView: AppCompatTextView
        var staticBodyTextView: AppCompatTextView

        init {
            titleTextView = view.findViewById(R.id.titleTextView) as AppCompatTextView
            bodyTextView = view.findViewById(R.id.bodyTextView) as AppCompatTextView
            staticTitleTextView = view.findViewById(R.id.staticTitleTextView) as AppCompatTextView
            staticBodyTextView = view.findViewById(R.id.staticBodyTextView) as AppCompatTextView
        }
    }

}