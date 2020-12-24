package com.example.mohbook.ui.mainscreen.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.navigation.fragment.findNavController
import com.example.mohbook.databinding.FragmentPostBinding
import com.example.mohbook.other.CropActivityResultContract
import com.example.mohbook.services.AddPostService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentUri: Uri
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cropActivityResultLauncher = registerForActivityResult(CropActivityResultContract()) {
            it?.let {
                currentUri = it
                binding.postImage.setImageURI(it)
                binding.postButton.isEnabled = true
                binding.chooseButton.visibility = View.INVISIBLE
                binding.postImage.isClickable = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPostBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.postButton.setOnClickListener {
            val description = binding.postDescription.editText?.text.toString().trim()

            val intent = Intent(requireContext(),AddPostService::class.java).apply {
                putExtra("desc",description)
                putExtra("uri",currentUri.toString())
            }

            requireContext().startService(intent)
            findNavController().popBackStack()
        }

        binding.chooseButton.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }

        binding.postImage.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}