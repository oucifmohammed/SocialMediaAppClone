package com.example.mohbook.other

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.mohbook.R
import com.example.mohbook.ui.authscreen.fragments.LoginFragmentDirections
import com.example.mohbook.ui.authscreen.fragments.RegisterFragmentDirections
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast

object ActivityUtil {

    fun authOperation(
        activity: Activity,
        resource: Resource<String>,
        life: LifecycleOwner,
        context: Context,
        view: View,
        navController: NavController,
        choice: Fragments
    ) {
        when (resource.status) {
            Status.LOADING -> {
                view.visibility = View.VISIBLE
            }
            Status.ERROR -> {
                life.lifecycleScope.launch {
                    view.visibility = View.VISIBLE
                    delay(1200L)
                    view.visibility = View.INVISIBLE

                    if (resource.data?.contains("The password is invalid", ignoreCase = true)!!) {
                        MotionToast.darkToast(
                            activity,
                            "Failed",
                            "The credentials are invalid",
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(context, R.font.helvetica_regular)
                        )
                    } else if (resource.data.contains(
                            "There is no user record",
                            ignoreCase = true
                        )
                    ) {
                        MotionToast.darkToast(
                            activity,
                            "Failed",
                            "There is no account with this credentials",
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(context, R.font.helvetica_regular)
                        )
                    } else {
                        MotionToast.darkToast(
                            activity,
                            "Failed",
                            resource.data,
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(context, R.font.helvetica_regular)
                        )
                    }
                }
            }
            else -> {
                life.lifecycleScope.launch {
                    view.visibility = View.INVISIBLE

                    MotionToast.darkToast(
                        activity,
                        "Success",
                        resource.data!!,
                        MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(context, R.font.helvetica_regular)
                    )
                    delay(2260L)

                    if(choice == Fragments.LOGINFRAGMENT){
                        navController.navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity())
                    }else if(choice == Fragments.REGISTERFRAGMENT){
                        navController.navigate(RegisterFragmentDirections.actionRegisterFragmentToMainActivity())
                    }

                    activity.finish()
                }
            }
        }
    }
}

enum class Fragments{
    LOGINFRAGMENT,
    REGISTERFRAGMENT
}