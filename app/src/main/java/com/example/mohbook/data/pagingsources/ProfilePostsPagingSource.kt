package com.example.mohbook.data.pagingsources

import androidx.paging.PagingSource
import com.example.mohbook.data.models.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class ProfilePostsPagingSource(
    private val fireStore: FirebaseFirestore,
    private val userId: String
) : PagingSource<QuerySnapshot, Post>() {
    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Post> {
        return try {

            val currentPage = params.key ?: fireStore.collection("posts")
                .whereEqualTo("authorId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()
            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            val nextPage = fireStore.collection("posts")
                .whereEqualTo("authorId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .startAfter(lastDocumentSnapshot)
                .limit(10)
                .get()
                .await()

            LoadResult.Page(
                data = currentPage.toObjects(Post::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}