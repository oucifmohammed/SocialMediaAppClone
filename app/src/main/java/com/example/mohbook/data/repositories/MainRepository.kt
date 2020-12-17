package com.example.mohbook.data.repositories

import android.content.Context
import android.net.Uri
import com.example.mohbook.data.models.User
import com.example.mohbook.other.Constants.DEFAULT_USER_IMAGE
import com.example.mohbook.other.Event
import com.example.mohbook.other.Operators
import com.example.mohbook.other.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val applicationContext: Context
) {

    private val users = Firebase.firestore.collection("users")
    private val auth = FirebaseAuth.getInstance()
    private val storage = Firebase.storage

    suspend fun loadingUserAccount(): Event<Resource<User>> {
        val userId = auth.currentUser?.uid!!

        if (!Operators.checkForInternetConnection(applicationContext)) {
            return Event(Resource.error("Checkout your internet connection and try again", null))
        } else {
            return try {
                val user = withContext(Dispatchers.IO) {
                    users.document(userId).get().await().toObject(User::class.java)
                }
                Event(Resource.success(user!!))
            } catch (e: Exception) {
                Event(Resource.error(e.message!!, null))
            }
        }
    }

    suspend fun updateProfilePicture(userId: String, uri: Uri) = withContext(Dispatchers.IO) {
        val user = users.document(userId).get().await().toObject(User::class.java)
        if (user?.photoUrl != DEFAULT_USER_IMAGE) {
            storage.getReferenceFromUrl(user?.photoUrl!!).delete().await()
        }

        storage.reference.child("userImages/${user.id}").putFile(uri)
            .await().metadata?.reference?.downloadUrl?.await()
    }

    suspend fun updateProfile(userName: String, description: String, uri: Uri?): Resource<Any> {

        if (!Operators.checkForInternetConnection(applicationContext)) {
            return Resource.error("Checkout your internet connection and try again", Any())
        } else if (userName.isEmpty() || description.isEmpty()) {
            return Resource.error("You have to fill all the fields", Any())
        } else {
            return try {

                val userId = auth.currentUser?.uid!!
                val imageUrl = uri?.let {
                    updateProfilePicture(userId, it).toString()
                }
                val map = mutableMapOf(
                    "userName" to userName,
                    "description" to description
                )
                imageUrl?.let {
                    map["photoUrl"] = it
                }
                withContext(Dispatchers.IO) {
                    users.document(userId).update(map.toMap()).await()
                }

                Resource.success(Any())
            } catch (e: Exception) {
                Resource.error(e.message!!, null)
            }
        }

    }

    suspend fun searchForUsers(userName: String): Resource<List<User>> {

        if (!Operators.checkForInternetConnection(applicationContext)) {
            return Resource.error("Checkout your internet connection and try again", null)
        } else if (userName.isEmpty()) {
            return Resource.error("You have to fill the field in order to make the search", null)
        } else {
            return try {
                withContext(Dispatchers.IO) {
                    val querySnapshot =
                        users.whereGreaterThanOrEqualTo("userName", userName)
                            .whereLessThanOrEqualTo("userName",userName+ '\uf8ff').get().await()

                    if (!querySnapshot.isEmpty) {
                        withContext(Dispatchers.Default) {
                            val list = mutableListOf<User>()
                            for (document in querySnapshot) {
                                list.add(document.toObject(User::class.java))
                            }
                            Resource.success(list)
                        }
                    } else {
                        Resource.error("There is no account with that name", null)
                    }
                }
            } catch (e: Exception) {
                Resource.error(e.message!!, null)
            }
        }

    }
}