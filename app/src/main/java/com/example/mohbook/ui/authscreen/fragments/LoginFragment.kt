package com.example.mohbook.ui.authscreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mohbook.databinding.FragmentLoginBinding
import com.example.mohbook.other.ActivityUtil
import com.example.mohbook.other.Fragments
import com.example.mohbook.ui.authscreen.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity())
            requireActivity().finish()
        }

        binding.register.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        binding.login.setOnClickListener {
            val email = binding.email.editText?.text.toString()
            val passWord = binding.password.editText?.text.toString()
            authViewModel.login(email, passWord)
        }

        authViewModel.loginResult.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                ActivityUtil.authOperation(
                    requireActivity(),
                    it,
                    viewLifecycleOwner,
                    requireContext(),
                    binding.progressBar,
                    findNavController(),
                    Fragments.LOGINFRAGMENT
                )
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}