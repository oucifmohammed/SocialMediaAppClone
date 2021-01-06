package com.example.mohbook.ui.mainscreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.mohbook.R
import com.example.mohbook.data.models.Post
import com.example.mohbook.databinding.FragmentProfileBinding
import com.example.mohbook.other.Status
import com.example.mohbook.recyclerviewadapters.PostsAdapter
import com.example.mohbook.recyclerviewadapters.PostItemView
import com.example.mohbook.ui.mainscreen.viewmodels.BasePostsViewModel
import com.example.mohbook.ui.mainscreen.viewmodels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import javax.inject.Inject

@AndroidEntryPoint
open class ProfileFragment : Fragment(), PostsAdapter.Interaction {

    private var _binding: FragmentProfileBinding? = null
    protected val binding get() = _binding!!
    protected val profileViewModel: ProfileViewModel by activityViewModels()
    protected open val userId = FirebaseAuth.getInstance().uid!!
    private lateinit var profilePostsAdapter: PostsAdapter

    @Inject
    lateinit var glide: RequestManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

        binding.followButton.visibility = View.INVISIBLE
        profileViewModel.loadUserData(userId)
        profileViewModel.loadProfilePosts(userId)


        profileViewModel.loadUserStatus.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    binding.apply {
                        loadUserProgressBar.visibility = View.VISIBLE
                        profileImage.visibility = View.INVISIBLE
                        userName.visibility = View.INVISIBLE
                        userDescription.visibility = View.INVISIBLE
                    }
                }

                Status.ERROR -> {
                    viewLifecycleOwner.lifecycleScope.launch {
                        binding.loadUserProgressBar.visibility = View.INVISIBLE
                        delay(1200L)

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

                }

                Status.SUCCESS -> {
                    binding.apply {
                        binding.loadUserProgressBar.visibility = View.INVISIBLE
                        profileImage.visibility = View.VISIBLE
                        userName.visibility = View.VISIBLE
                        userDescription.visibility = View.VISIBLE

                        userName.text = it.data!!.userName
                        userDescription.text = it.data.description
                        glide.load(it.data.photoUrl).into(profileImage)
                    }
                }
            }
        })


        profileViewModel.postsList.observe(viewLifecycleOwner) {
            viewLifecycleOwner.lifecycleScope.launch {
                profilePostsAdapter.submitData(it)
            }
        }

        postsListStateChanges()
    }

    private fun initAdapter() {
        profilePostsAdapter = PostsAdapter(PostItemView.POSTIMAGEITEM, this)
        binding.postList.apply {
            adapter = profilePostsAdapter
            layoutManager =
                GridLayoutManager(requireContext(), 3, LinearLayoutManager.VERTICAL, false)
            hasFixedSize()
        }
    }

    private fun postsListStateChanges() {
        profilePostsAdapter.addLoadStateListener { loadState ->
            binding.apply {
                postsProgressBar.isVisible = loadState.refresh is LoadState.Loading
                postList.isVisible = loadState.refresh is LoadState.NotLoading
                loadProgressBar.isVisible = loadState.append is LoadState.Loading
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(position: Int) {
        findNavController().navigate(
            ProfileFragmentDirections.actionProfileFragmentToUserPostsFragment(
                position
            )
        )
    }

    override fun onLikeButtonToggle(position: Int, post: Post) {
        TODO("Not yet implemented")
    }

    override fun onCommentButton(post: Post) {
        TODO("Not yet implemented")
    }

}