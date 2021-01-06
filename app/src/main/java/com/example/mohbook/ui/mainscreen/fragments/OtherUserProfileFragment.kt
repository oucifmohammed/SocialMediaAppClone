package com.example.mohbook.ui.mainscreen.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mohbook.R
import com.example.mohbook.other.Status
import www.sanju.motiontoast.MotionToast

class OtherUserProfileFragment : ProfileFragment() {

    private val args: OtherUserProfileFragmentArgs by navArgs()
    override val userId: String
        get() = args.userId

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.followButton.visibility = View.VISIBLE
        subscribeToLiveData()

        binding.followButton.setOnClickListener {
            profileViewModel.toggleFollowButton(userId)
        }
    }

    private fun subscribeToLiveData() {
        profileViewModel.loadUserStatus.observe(viewLifecycleOwner, {

            if(it.status == Status.LOADING){
                binding.followButton.visibility = View.INVISIBLE
            }

            if (it.status == Status.SUCCESS) {
                binding.followButton.visibility = View.VISIBLE
                if (it.data!!.following) {
                    binding.followButton.apply {
                        text = "UnFollow"
                        setBackgroundColor(Color.RED)
                    }
                } else {
                    binding.followButton.apply {
                        text = "Follow"
                        setBackgroundColor(Color.green(R.color.green))
                    }
                }
            }
        })

        profileViewModel.followActionStatus.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { followResult ->
                when (followResult.status) {
                    Status.LOADING -> {
                        binding.followButton.isEnabled = false
                    }

                    Status.ERROR -> {
                        //binding.followButton.isEnabled = true
                        MotionToast.darkToast(
                            requireActivity(),
                            "Failed",
                            followResult.message!!,
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular)
                        )
                    }

                    Status.SUCCESS -> {
                        binding.followButton.apply {
                            isEnabled = true
                            if (followResult.data!!) {
                                text = "UnFollow"
                                setBackgroundColor(Color.RED)
                            } else {
                                text = "Follow"
                                setBackgroundColor(Color.green(R.color.green))
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onItemSelected(position: Int) {
        findNavController().navigate(OtherUserProfileFragmentDirections
            .actionOtherUserProfileFragmentToUserPostsFragment(position))
    }
}