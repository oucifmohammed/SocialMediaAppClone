package com.example.mohbook.recyclerviewadapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.mohbook.data.models.Comment
import com.example.mohbook.databinding.CommentItemBinding

class CommentAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Comment>() {

        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding = CommentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CommentViewHolder(
            binding,
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CommentViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Comment>) {
        differ.submitList(list)
    }

    class CommentViewHolder
    constructor(
        val binding: CommentItemBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Comment) = with(itemView) {
            binding.apply {
                Glide.with(binding.root.context).load(item.authorProfilePicture).into(userPicture)
                userName.text = item.authorName
                commentContent.text = item.content
            }
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Comment)
    }
}