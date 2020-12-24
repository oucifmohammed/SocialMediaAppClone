package com.example.mohbook.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.mohbook.R
import com.example.mohbook.data.models.Post
import com.example.mohbook.data.models.User
import com.example.mohbook.other.Constants.MAX_PROGRESS_BAR_VALUE
import com.example.mohbook.other.Constants.NOTIFICATION_CHANNEL_ID
import com.example.mohbook.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.mohbook.other.Constants.NOTIFICATION_ID
import com.example.mohbook.other.Operators
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddPostService : Service() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var currentNotificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var pendingIntent: PendingIntent

    private val auth = FirebaseAuth.getInstance()
    private val users = Firebase.firestore.collection("users")
    private val posts = Firebase.firestore.collection("posts")
    private val storage = Firebase.storage

    override fun onCreate() {
        super.onCreate()
        currentNotificationBuilder = baseNotificationBuilder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        if (intent?.action.equals("Stop Service")) {
            stopSelf(startId)
        } else {
            val description = intent?.getStringExtra("desc")
            val uri = intent?.let {
                Uri.parse(it.getStringExtra("uri"))
            }

            startForegroundService(description, uri!!)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun startForegroundService(description: String?, uri: Uri) {

        currentNotificationBuilder = baseNotificationBuilder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        uploadPost(description, uri)
    }

    private fun uploadPost(description: String?, uri: Uri) {
        if (!Operators.checkForInternetConnection(this)) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(1200L)
                currentNotificationBuilder
                    .setContentText("The upload operation has failed")
                    .setSmallIcon(R.drawable.upload_fail)
                    .setContentIntent(pendingIntent)

                notificationManager.notify(NOTIFICATION_ID, currentNotificationBuilder.build())
                stopForeground(false)
            }
        } else {
            try {
                CoroutineScope(Dispatchers.Main).launch {
                    val user = withContext(Dispatchers.IO) {
                        users.document(auth.currentUser!!.uid).get().await()
                            .toObject(User::class.java)
                    }

                    storage.reference.child("postImages/${UUID.randomUUID().toString()}")
                        .putFile(uri)
                        .addOnProgressListener {
                            val progress = (100 * it.bytesTransferred) / it.totalByteCount
                            currentNotificationBuilder
                                .setProgress(MAX_PROGRESS_BAR_VALUE, progress.toInt(), false)

                            notificationManager.notify(
                                NOTIFICATION_ID,
                                currentNotificationBuilder.build()
                            )
                        }
                        .addOnSuccessListener {
                            CoroutineScope(Dispatchers.IO).launch {
                                val postPhotoUrl = withContext(Dispatchers.IO) {
                                    it.metadata?.reference?.downloadUrl?.await().toString()
                                }

                                val post = withContext(Dispatchers.Main) {
                                    if (description == null) {
                                        Post(
                                            id = user!!.id,
                                            userName = user.userName,
                                            userPhotoUrl = user.photoUrl,
                                            postPhotoUrl = postPhotoUrl
                                        )
                                    } else {
                                        Post(
                                            id = user!!.id,
                                            userName = user.userName,
                                            description = description,
                                            userPhotoUrl = user.photoUrl,
                                            postPhotoUrl = postPhotoUrl
                                        )
                                    }
                                }

                                posts.document(user!!.id).set(post).await()
                                users.document(user.id).update("posts", FieldValue.arrayUnion(post))
                                    .await()

                                withContext(Dispatchers.Main) {
                                    currentNotificationBuilder
                                        .setContentText("Upload Completed")
                                        .setSmallIcon(R.drawable.upload_done)
                                        .setProgress(0, 0, false)
                                        .setContentIntent(pendingIntent)

                                    notificationManager.notify(
                                        NOTIFICATION_ID,
                                        currentNotificationBuilder.build()
                                    )
                                    stopForeground(false)
                                }
                            }
                        }
                        .addOnCanceledListener {
                            currentNotificationBuilder
                                .setContentText("The upload operation has failed")
                                .setSmallIcon(R.drawable.upload_fail)
                            notificationManager.notify(NOTIFICATION_ID, currentNotificationBuilder.build())
                            stopForeground(false)
                        }
                }
            } catch (e: Exception) {
                currentNotificationBuilder
                    .setContentText(e.message!!)
                    .setSmallIcon(R.drawable.upload_fail)
                notificationManager.notify(NOTIFICATION_ID, currentNotificationBuilder.build())
                stopForeground(false)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                IMPORTANCE_LOW
            )
        notificationManager.createNotificationChannel(channel)
    }

}