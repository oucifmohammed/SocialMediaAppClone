package com.example.mohbook.ui.mainscreen.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mohbook.MainactivityNavGraphDirections
import com.example.mohbook.R
import com.example.mohbook.data.models.Post
import com.example.mohbook.other.Status
import com.example.mohbook.recyclerviewadapters.PostsAdapter
import com.example.mohbook.recyclerviewadapters.PostItemView
import com.example.mohbook.ui.mainscreen.viewmodels.BasePostsViewModel
import com.google.firebase.auth.FirebaseAuth
import www.sanju.motiontoast.MotionToast

abstract class BasePostsFragment: Fragment(),PostsAdapter.Interaction{

    lateinit var postsAdapter: PostsAdapter
    abstract val basePostsViewModel: BasePostsViewModel
    private var postIndex: Int? = null
    private val userId = FirebaseAuth.getInstance().uid!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        subscribeToLiveData()
    }

    private fun initAdapter(){
        postsAdapter = PostsAdapter(PostItemView.POSTITEM,this)
    }

    private fun subscribeToLiveData(){
        basePostsViewModel.toggleLikeButtonStatus.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> postIndex?.let {
                    postsAdapter.peek(it)?.isLiking = true
                }

                Status.ERROR -> {
                    postIndex?.let {
                        postsAdapter.peek(it)?.isLiking = false
                    }

                    MotionToast.darkToast(
                        requireActivity(),
                        "Failed",
                        it.message!!,
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular)
                    )
                }

                Status.SUCCESS -> {
                    postIndex?.let { index ->
                        postsAdapter.peek(index)?.isLiking = false
                        if (it.data!!) {
                            val newLikedByList = postsAdapter.peek(index)!!.likedBy.plus(userId)
                            postsAdapter.peek(index)!!.likedBy = newLikedByList
                        } else {
                            val newLikedByList = postsAdapter.peek(index)!!.likedBy.minus(userId)
                            postsAdapter.peek(index)!!.likedBy = newLikedByList
                        }

                        postsAdapter.notifyItemChanged(index)
                    }
                }
            }
        })

    }

    override fun onLikeButtonToggle(position: Int, post: Post) {
        postIndex = position
        basePostsViewModel.toggleLikeButton(post)
    }

    final override fun onCommentButton(post: Post) {
        findNavController().navigate(MainactivityNavGraphDirections.globalActionToCommentFragment(post))
    }
}