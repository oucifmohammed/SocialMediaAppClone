package com.example.mohbook.ui.authscreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mohbook.databinding.FragmentRegisterBinding
import com.example.mohbook.other.ActivityUtil
import com.example.mohbook.other.Fragments
import com.example.mohbook.ui.authscreen.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        binding.login.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.register.setOnClickListener {
            val email = binding.email.editText?.text.toString()
            val userName = binding.userName.editText?.text.toString()
            val passWord = binding.password.editText?.text.toString()
            val confirmPassWord = binding.repeatPassword.editText?.text.toString()
            authViewModel.register(email, userName, passWord, confirmPassWord)
        }

        authViewModel.registrationResult.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                ActivityUtil.authOperation(
                    requireActivity(),
                    it,
                    viewLifecycleOwner,
                    requireContext(),
                    binding.progressBar,
                    findNavController(),
                    Fragments.REGISTERFRAGMENT
                )
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}