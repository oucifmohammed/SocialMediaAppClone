package com.example.mohbook.data.pagingsources

import androidx.paging.PagingSource
import com.example.mohbook.data.models.Post
import com.example.mohbook.data.models.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class HomePostsPagingSource(
    private val fireStore: FirebaseFirestore,
    private val userId: String
) : PagingSource<QuerySnapshot, Post>() {

    private var isFirstLoad = true
    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Post> {
        return try {

            val lastDocument: DocumentSnapshot
            val currentPage: QuerySnapshot
            val nextPage: QuerySnapshot
            var followsList: List<String> = listOf()

            if(isFirstLoad){
                followsList = fireStore.collection("users").document(userId).get()
                    .await().toObject(User::class.java)?.followsList!!

                 currentPage = params.key?:fireStore.collection("posts")
                    .whereIn("authorId",followsList)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .limit(10)
                    .get()
                    .await()

                isFirstLoad = false

            }else {
                currentPage = params.key!!
            }

            lastDocument = currentPage.documents[currentPage.size() - 1]

            nextPage = fireStore.collection("posts")
                .whereIn("authorId", followsList)
                .orderBy("date", Query.Direction.DESCENDING)
                .startAfter(lastDocument)
                .limit(10)
                .get()
                .await()

            LoadResult.Page(
                data = currentPage.toObjects(Post::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }
}