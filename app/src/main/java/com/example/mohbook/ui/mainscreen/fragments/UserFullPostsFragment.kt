package com.example.mohbook.ui.mainscreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mohbook.databinding.FragmentPostsBinding
import com.example.mohbook.recyclerviewadapters.PostItemView
import com.example.mohbook.ui.mainscreen.viewmodels.BasePostsViewModel
import com.example.mohbook.ui.mainscreen.viewmodels.ProfileViewModel
import kotlinx.coroutines.launch


class UserFullPostsFragment : BasePostsFragment() {

    private var _binding: FragmentPostsBinding? = null
    val binding get() = _binding!!
    private val args: UserFullPostsFragmentArgs by navArgs()
    override val basePostsViewModel: BasePostsViewModel
        get() {
            val vm: ProfileViewModel by activityViewModels()
            return vm
        }

    private val profileViewModel: ProfileViewModel
        get() = basePostsViewModel as ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewConfig()

        viewLifecycleOwner.lifecycleScope.launch {
            postsAdapter.submitData(profileViewModel.postsList.value!!)
        }

        binding.postsList.scrollToPosition(args.listPosition)

        listStateChanges()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(position: Int) {
        TODO("Not yet implemented")
    }

    private fun recyclerViewConfig() {

        binding.postsList.apply {
            adapter = postsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
            hasFixedSize()
        }
    }

    private fun listStateChanges() {
        postsAdapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.append is LoadState.Loading
            }
        }
    }
}