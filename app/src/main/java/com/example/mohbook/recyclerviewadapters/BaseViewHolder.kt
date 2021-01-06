package com.example.mohbook.recyclerviewadapters

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.mohbook.data.models.Post

abstract class BaseViewHolder(binding: ViewBinding): RecyclerView.ViewHolder(binding.root){

    abstract fun bind(item: Post)
}

enum class PostItemView{
    POSTITEM,
    POSTIMAGEITEM
}