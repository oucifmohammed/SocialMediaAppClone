package com.example.mohbook.ui.mainscreen.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.mohbook.R
import com.example.mohbook.data.models.Post
import com.example.mohbook.databinding.FragmentCommentsBinding
import com.example.mohbook.other.Status
import com.example.mohbook.recyclerviewadapters.CommentAdapter
import com.example.mohbook.ui.mainscreen.viewmodels.CommentViewModel
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.AndroidEntryPoint
import www.sanju.motiontoast.MotionToast
import javax.inject.Inject


@AndroidEntryPoint
class CommentsFragment : Fragment() {

    private var _binding: FragmentCommentsBinding? = null
    val binding get() = _binding!!
    private val commentViewModel: CommentViewModel by viewModels()
    private val args: CommentsFragmentArgs by navArgs()
    private lateinit var registration: ListenerRegistration
    private lateinit var commentAdapter: CommentAdapter

    @Inject
    lateinit var glide: RequestManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postItem = args.postItem
        initAdapter()

        presentPostMetaData(postItem)

        binding.commentContent.addTextChangedListener {

            binding.postComment.isEnabled = !it?.trim()?.isEmpty()!!
        }

        binding.postComment.setOnClickListener {
            val commentContent = binding.commentContent.text.toString()
            commentViewModel.addComment(commentContent,postItem.id)
        }

        registration = commentViewModel.getPostReference(postItem.id).addSnapshotListener { value, error ->
            val newCommentsList = value?.toObject(Post::class.java)!!.commentList
            commentAdapter.submitList(newCommentsList)
        }

        subscribeToLiveData()
    }

    private fun subscribeToLiveData(){
        commentViewModel.addCommentStatus.observe(viewLifecycleOwner,{
            when(it.status){
                Status.LOADING -> binding.postComment.isEnabled = false

                Status.ERROR -> {
                    binding.postComment.isEnabled = true
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
                    binding.postComment.isEnabled = true
                    binding.commentContent.text?.clear()
                }
            }
        })
    }

    private fun initAdapter(){
        commentAdapter = CommentAdapter()
        binding.commentsList.apply {
            adapter = commentAdapter
            layoutManager = LinearLayoutManager(requireContext())
            hasFixedSize()
        }
    }

    private fun presentPostMetaData(postItem: Post){

        binding.apply {
            userName.text = postItem.userName
            postTextContent.text = postItem.description
            glide.load(postItem.userPhotoUrl).into(userPicture)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        registration.remove()
    }
}