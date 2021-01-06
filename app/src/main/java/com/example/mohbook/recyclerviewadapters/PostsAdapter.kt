package com.example.mohbook.recyclerviewadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.mohbook.R
import com.example.mohbook.data.models.Post
import com.example.mohbook.databinding.PostImageItemBinding
import com.example.mohbook.databinding.PostItemBinding
import com.google.firebase.auth.FirebaseAuth

class PostsAdapter(
    private val type: PostItemView,
    private val interaction: Interaction? = null
) : PagingDataAdapter<Post, BaseViewHolder>(DIFF_CALLBACK) {

    val auth = FirebaseAuth.getInstance()

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return newItem == oldItem
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        return when (type) {
            PostItemView.POSTITEM -> {
                val binding =
                    PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostItemViewHolder(
                    binding,
                    interaction
                )
            }
            PostItemView.POSTIMAGEITEM -> {
                val binding =
                    PostImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostImageItem(
                    binding,
                    interaction
                )
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }


    inner class PostItemViewHolder
        (
        private val binding: PostItemBinding,
        private val interaction: Interaction?
    ) : BaseViewHolder(binding) {

        init {
            binding.likeIcon.setOnClickListener {
                interaction?.onLikeButtonToggle(
                    absoluteAdapterPosition,
                    getItem(absoluteAdapterPosition)!!
                )
            }

            binding.commentIcon.setOnClickListener {
                interaction?.onCommentButton(getItem(absoluteAdapterPosition)!!)
            }
        }

        override fun bind(item: Post) {
            binding.apply {
                Glide.with(root)
                    .load(item.postPhotoUrl)
                    .centerCrop()
                    .into(postImage)

                Glide.with(root)
                    .load(item.userPhotoUrl)
                    .into(userPicture)

                userName.text = item.userName
                userName2.text = item.userName
                postDescription.text = item.description

                val likeCount = item.likedBy.size
                likesNumber.text = when {
                    likeCount <= 0 -> "No Likes"
                    likeCount == 1 -> "Liked by 1 person"
                    else -> "Liked by $likeCount people"
                }

                val result = auth.uid!! in item.likedBy

                if (result) {
                    likeIcon.setImageResource(R.drawable.like_icon_red)
                } else {
                    likeIcon.setImageResource(R.drawable.like_icon_white)
                }
            }
        }
    }

    class PostImageItem(val binding: PostImageItemBinding, private val interaction: Interaction?) :
        BaseViewHolder(binding) {

        init {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition)
            }
        }

        override fun bind(item: Post) {
            Glide.with(binding.root)
                .load(item.postPhotoUrl)
                .centerCrop()
                .placeholder(R.drawable.profile_icon)
                .into(binding.postImageItem)
        }

    }

    interface Interaction {
        fun onItemSelected(position: Int)
        fun onLikeButtonToggle(position: Int, post: Post)
        fun onCommentButton(post: Post)
    }
}