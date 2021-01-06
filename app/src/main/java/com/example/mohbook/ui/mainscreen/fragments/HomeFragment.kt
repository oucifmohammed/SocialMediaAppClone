package com.example.mohbook.ui.mainscreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mohbook.databinding.FragmentHomeBinding
import com.example.mohbook.ui.mainscreen.viewmodels.BasePostsViewModel
import com.example.mohbook.ui.mainscreen.viewmodels.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : BasePostsFragment() {

    private var _binding: FragmentHomeBinding? = null
    val binding get() = _binding!!
    val auth = FirebaseAuth.getInstance()

    override val basePostsViewModel: BasePostsViewModel
        get() {
            val vm: HomeViewModel by viewModels()
            return vm
        }

    private val homeViewModel: HomeViewModel
        get() = basePostsViewModel as HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.loadHomePosts(auth.uid!!)
        subscribeToLiveData()

        recyclerViewConfig()

        postsListStateChanges()
    }

    private fun recyclerViewConfig() {

        binding.homePosts.apply {
            adapter = postsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
            hasFixedSize()
        }
    }

    private fun postsListStateChanges(){
        postsAdapter.addLoadStateListener {loadState->
            binding.apply {
                homePosts.isVisible = loadState.refresh is LoadState.NotLoading
                loadHomePostsProgressBar.isVisible = loadState.refresh is LoadState.Loading
                loadMoreHomePostsProgressBar.isVisible = loadState.append is LoadState.Loading
            }
        }
    }

    private fun subscribeToLiveData(){
        homeViewModel.postsList.observe(viewLifecycleOwner,{
            viewLifecycleOwner.lifecycleScope.launch {
                postsAdapter.submitData(it)
            }
        })
    }

    override fun onItemSelected(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}