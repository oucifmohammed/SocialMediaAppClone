package com.example.mohbook.ui.mainscreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mohbook.R
import com.example.mohbook.data.models.User
import com.example.mohbook.databinding.FragmentSearchBinding
import com.example.mohbook.other.Resource
import com.example.mohbook.other.Status
import com.example.mohbook.ui.authscreen.recyclerviewadapters.SearchListAdapter
import com.example.mohbook.ui.mainscreen.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchAdapter: SearchListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialiseAdapter()
        searchViewModel = ViewModelProvider(requireActivity()).get(SearchViewModel::class.java)

        searchViewModel.searchState.observe(viewLifecycleOwner, {
            updateSearchState(it)
        })

        searchViewModel.searchText?.let {
            binding.userName.editText?.setText(it)
        }

        binding.userName.setEndIconOnClickListener {
            val userName = binding.userName.editText?.text.toString().trim()
            searchViewModel.searchText = userName
            searchViewModel.searchForUser(userName)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initialiseAdapter() {
        searchAdapter = SearchListAdapter()
        binding.recyclerViewList.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
            hasFixedSize()
        }
    }

    private fun updateSearchState(resource: Resource<List<User>>){
        when (resource.status) {
            Status.LOADING -> {
                binding.apply {
                    searchProgressBar.visibility = View.VISIBLE
                    recyclerViewList.visibility = View.INVISIBLE
                }
            }
            Status.ERROR -> {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                    delay(1200L)
                    binding.searchProgressBar.visibility = View.INVISIBLE
                    MotionToast.darkToast(
                        requireActivity(),
                        "Failed",
                        resource.message!!,
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular)
                    )
                }
            }
            Status.SUCCESS -> {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                    binding.apply {
                        searchAdapter.submitList(resource.data!!)
                        delay(900L)
                        searchProgressBar.visibility = View.INVISIBLE
                        recyclerViewList.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}