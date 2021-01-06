package com.example.mohbook.recyclerviewadapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.mohbook.data.models.User
import com.example.mohbook.databinding.UserSearchItemBinding

class SearchListAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return newItem.id == oldItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return newItem == oldItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding = UserSearchItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SearchItem(
            binding,
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SearchItem -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<User>) {
        differ.submitList(list)
    }

    inner class SearchItem
    constructor(
        private val binding: UserSearchItemBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                interaction?.onItemSelected(differ.currentList[absoluteAdapterPosition])
            }
        }

        fun bind(item: User) = with(itemView) {
            binding.userName.text = item.userName
            Glide.with(binding.root).load(item.photoUrl).into(binding.userPicture)
        }
    }

    interface Interaction {
        fun onItemSelected(item: User)
    }
}