package com.example.mohbook.ui.mainscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mohbook.R
import com.example.mohbook.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.bottomNavigationView.apply {
            setupWithNavController(findNavController(R.id.navHostFragment))
            setOnNavigationItemReselectedListener { /*Do nothing*/ }
            menu.getItem(2).setOnMenuItemClickListener {
                findNavController(R.id.navHostFragment).navigate(R.id.globalActionToCreatePostFragment)
                return@setOnMenuItemClickListener true
            }
        }

        findNavController(R.id.navHostFragment).addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.createPostFragment  -> {
                    binding.bottomNavigationView.visibility = View.INVISIBLE
                }

                R.id.userPostsFragment -> {
                    binding.bottomNavigationView.visibility = View.INVISIBLE
                }

                R.id.commentsFragment -> {
                    binding.bottomNavigationView.visibility = View.INVISIBLE
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }

    }
}