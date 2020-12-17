package com.example.mohbook.ui.mainscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mohbook.R
import com.example.mohbook.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Used to remove the shadow that is on BottomAppBar
        binding.bottomNavigationView.background = null

        //Used this instruction to forbid the click on the "blank" icon
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false

        binding.bottomNavigationView.setupWithNavController(findNavController(R.id.navHostFragment))

        //We used this instruction to forbid the "reloading" of the fragments when "re-selecting" theme.
        binding.bottomNavigationView.setOnNavigationItemReselectedListener { /*Do nothing*/ }
    }
}