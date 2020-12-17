package com.example.mohbook.data.repositories

import android.content.Context
import android.util.Patterns
import com.example.mohbook.data.models.User
import com.example.mohbook.other.Operators
import com.example.mohbook.other.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityScoped
class AuthRepository @Inject constructor(
    private val applicationContext: Context
) {

    private val auth = FirebaseAuth.getInstance()
    private val users = FirebaseFirestore.getInstance().collection("users")

    suspend fun login(email: String, passWord: String): Resource<String> {
        if (!Operators.checkForInternetConnection(applicationContext)) {
            return Resource.error("", "Checkout your internet connection and try again")
        } else if (email.trim().isEmpty() || passWord.trim().isEmpty()) {
            return Resource.error("", "You need to fill the fields")
        } else {
            return try {
                withContext(Dispatchers.IO) {
                    auth.signInWithEmailAndPassword(email, passWord).await()
                    Resource.success("login completed successfully")
                }
            } catch (e: Exception) {
                Resource.error("", e.message!!)
            }
        }
    }

    suspend fun register(
        email: String,
        username: String,
        passWord: String,
        confirmPassWord: String
    ): Resource<String> {

        if (!Operators.checkForInternetConnection(applicationContext)) {
            return Resource.error("", "Checkout your internet connection and try again")
        } else if (email.trim().isEmpty() || username.trim().isEmpty() || passWord.trim()
                .isEmpty() || confirmPassWord.trim().isEmpty()
        ) {
            return Resource.error("", "You need to fill the fields")
        } else if (passWord != confirmPassWord) {
            return Resource.error("", "The two passwords are not identical")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Resource.error("", "There is an error in email field")
        } else {
            return try {
                withContext(Dispatchers.IO) {
                    val result = auth.createUserWithEmailAndPassword(email, passWord).await()
                    val uid = result.user?.uid!!
                    val user = User(uid, username)
                    users.document(uid).set(user).await()
                    Resource.success("Registration completed successfully")
                }

            } catch (e: Exception) {
                return Resource.error("", e.message!!)
            }
        }
    }
}