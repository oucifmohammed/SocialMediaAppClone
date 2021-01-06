package com.example.mohbook.data.repositories

import android.content.Context
import android.net.Uri
import com.example.mohbook.data.models.Comment
import com.example.mohbook.data.models.Post
import com.example.mohbook.data.models.User
import com.example.mohbook.other.Constants.DEFAULT_USER_IMAGE
import com.example.mohbook.other.Event
import com.example.mohbook.other.Operators
import com.example.mohbook.other.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val applicationContext: Context
) {

    private val fireStore = Firebase.firestore
    private val users = Firebase.firestore.collection("users")
    private val posts = Firebase.firestore.collection("posts")
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

    private suspend fun updateProfilePicture(userId: String, uri: Uri) =
        withContext(Dispatchers.IO) {
            val user = users.document(userId).get().await().toObject(User::class.java)
            if (user?.photoUrl != DEFAULT_USER_IMAGE) {
                storage.getReferenceFromUrl(user?.photoUrl!!).delete().await()
            }

            storage.reference.child("userImages/${user.id}").putFile(uri)
                .await().metadata?.reference?.downloadUrl?.await()
        }

    suspend fun updateProfile(userName: String, description: String?, uri: Uri?): Event<Resource<Any>> {

        if (!Operators.checkForInternetConnection(applicationContext)) {
            return Event(Resource.error("Checkout your internet connection and try again", Any()))
        } else {
            return try {

                val userId = auth.currentUser?.uid!!
                val imageUrl = uri?.let {
                    updateProfilePicture(userId, it).toString()
                }

                val map = if (description == null) {
                    mutableMapOf(
                        "userName" to userName,
                    )
                } else {
                    mutableMapOf(
                        "userName" to userName,
                        "description" to description
                    )
                }

                imageUrl?.let {
                    map["photoUrl"] = it
                }
                withContext(Dispatchers.IO) {
                    users.document(userId).update(map.toMap()).await()
                }

                Event(Resource.success(Any()))
            } catch (e: Exception) {
                Event(Resource.error(e.message!!, null))
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
                            .whereLessThanOrEqualTo("userName", userName + '\uf8ff').get().await()

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

    suspend fun getUser(userId: String): Resource<User> {

        if (!Operators.checkForInternetConnection(applicationContext)) {
            return Resource.error("Checkout your internet connection and try again", null)
        }
        //else {
            return try {
                withContext(Dispatchers.IO) {
                    val user = users.document(userId).get().await().toObject(User::class.java)!!

                    if(userId!=auth.uid!!){
                        val followResult = checkIfYouFollow(userId)
                        user.following = followResult
                    }

                    Resource.success(user)
                }
            } catch (e: Exception) {
                Resource.error(e.message!!, null)
            }
        //}

    }

    suspend fun toggleLikeButton(post: Post) = withContext(Dispatchers.IO){
        try {
            var isLiked = false
            fireStore.runTransaction { transaction ->
                val uid = auth.uid!!
                val postResult = transaction.get(posts.document(post.id))
                val currentLikes = postResult.toObject(Post::class.java)?.likedBy ?: listOf()

                transaction.update(
                    posts.document(post.id),
                    "likedBy",
                    if(uid in currentLikes) currentLikes - uid else {
                        isLiked = true
                        currentLikes + uid
                    }
                )
            }.await()
            Resource.success(isLiked)
        }catch (e: Exception){
            Resource.error(e.message!!,null)
        }
    }

    suspend fun followUser(userId: String) = withContext(Dispatchers.IO){
        try {
            var follow = false
            val userDocumentReference = users.document(auth.uid!!)

            val checkResult = checkIfYouFollow(userId)

            if(checkResult){
                userDocumentReference.update("followsList",FieldValue.arrayRemove(userId)).await()
                return@withContext Event(Resource.success(follow))
            }else {
                follow = true
                userDocumentReference.update("followsList",FieldValue.arrayUnion(userId)).await()
                return@withContext Event(Resource.success(follow))
            }

        }catch (e: Exception){
            Event(Resource.error(e.message!!,null))
        }
    }

    private suspend fun checkIfYouFollow(userId: String) = withContext(Dispatchers.IO){
        val followsList = users.document(auth.uid!!).get().await().toObject(User::class.java)?.followsList

        userId in followsList!!
    }

    suspend fun addComment(commentContent: String,postId: String) = withContext(Dispatchers.IO){
        try {
            val commentAuthor = users.document(auth.uid!!).get().await().toObject(User::class.java)!!
            val postReference = posts.document(postId)

            val commentId = UUID.randomUUID().toString()
            val authorName = commentAuthor.userName
            val authorPicture = commentAuthor.photoUrl

            val comment = Comment(commentId,commentContent,authorName,authorPicture)

            postReference.update("commentList",FieldValue.arrayUnion(comment)).await()

            Resource.success(Any())

        }catch (e: Exception){
            Resource.error(e.message!!,null)
        }
    }

    fun singOut() {
        auth.signOut()
    }
}