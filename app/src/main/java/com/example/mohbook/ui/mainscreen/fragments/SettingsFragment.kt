package com.example.mohbook.ui.mainscreen.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.mohbook.R
import com.example.mohbook.data.models.User
import com.example.mohbook.databinding.FragmentSettingsBinding
import com.example.mohbook.other.CropActivityResultContract
import com.example.mohbook.other.Resource
import com.example.mohbook.other.Status
import com.example.mohbook.ui.mainscreen.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: SettingsViewModel by viewModels()
    @Inject
    lateinit var glide: RequestManager
    private lateinit var cropActivityResultLauncher:ActivityResultLauncher<Any?>
    private var currentUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cropActivityResultLauncher = registerForActivityResult(CropActivityResultContract()) {
            it?.let {
                currentUri = it
                binding.userImage.setImageURI(it)
                binding.updateButton.isEnabled = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //mainViewModel = ViewModelProvider(requireActivity()).get(SettingsViewModel::class.java)
        mainViewModel.loadingUserAccount()

        mainViewModel.userAccount.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                displayingUserAccount(it)
            }
        })

        mainViewModel.updateProfileState.observe(viewLifecycleOwner,{
            updateAccountOperation(it)
        })

        binding.userImage.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }

        binding.userName.editText?.addTextChangedListener {
            binding.updateButton.isEnabled = true
        }

        binding.description.editText?.addTextChangedListener {
            binding.updateButton.isEnabled = true
        }

        binding.updateButton.setOnClickListener {
            val userName= binding.userName.editText?.text.toString()
            val description = binding.description.editText?.text.toString()
            mainViewModel.updateProfile(userName,description,currentUri)
        }

        binding.signOut.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                mainViewModel.signOut()
                delay(1000L)
                findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToStartActivity())
                activity?.finish()
            }
        }
    }

    private fun displayingUserAccount(resource: Resource<User>) {
        when (resource.status) {
            Status.LOADING -> {
                binding.apply {
                    loadingProgressBar.visibility = View.VISIBLE
                    linearLayout.visibility = View.INVISIBLE
                    updateButton.visibility = View.INVISIBLE
                }
            }
            Status.ERROR -> {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                    binding.loadingProgressBar.visibility = View.VISIBLE
                    delay(1200L)
                    binding.loadingProgressBar.visibility = View.INVISIBLE

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
                glide.load(resource.data?.photoUrl).into(binding.userImage)
                binding.apply {
                    loadingProgressBar.visibility = View.INVISIBLE
                    binding.updateButton.visibility = View.VISIBLE
                    linearLayout.visibility = View.VISIBLE
                    userName.editText?.setText(resource.data?.userName)
                    description.editText?.setText(resource.data?.description)
                    binding.updateButton.isEnabled = false
                }
            }
        }
    }

    private fun updateAccountOperation(resource: Resource<Any>){
        when (resource.status) {
            Status.LOADING -> {
                binding.apply {
                    updateButton.isEnabled = false
                    updateButton.visibility = View.INVISIBLE
                    updateProgressBar.visibility = View.VISIBLE
                }
            }
            Status.ERROR -> {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                    binding.updateProgressBar.visibility = View.VISIBLE
                    delay(1200L)
                    binding.apply {
                        updateProgressBar.visibility = View.INVISIBLE
                        updateButton.visibility = View.VISIBLE
                        updateButton.isEnabled = true
                    }

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
                binding.apply {
                    updateButton.visibility = View.VISIBLE
                    updateProgressBar.visibility = View.INVISIBLE
                    binding.updateButton.isEnabled = false
                }

                MotionToast.darkToast(
                    requireActivity(),
                    "Success",
                    "Update operation is done successfully",
                    MotionToast.TOAST_SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular)
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}