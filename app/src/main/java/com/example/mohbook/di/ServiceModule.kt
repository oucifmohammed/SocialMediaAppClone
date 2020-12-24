package com.example.mohbook.di

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.mohbook.R
import com.example.mohbook.other.Constants.NOTIFICATION_CHANNEL_ID
import com.example.mohbook.services.AddPostService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Singleton

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceScoped
    fun provideNotificationManager(@ApplicationContext context: Context) = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

    @Provides
    @ServiceScoped
    fun provideNotificationBuilder(@ApplicationContext context: Context): NotificationCompat.Builder =
        NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Mohbook")
            .setContentText("Uploading your post")
            .setSmallIcon(R.drawable.upload)

    @Provides
    @ServiceScoped
    fun providePendingIntent(@ApplicationContext context: Context): PendingIntent =
        PendingIntent.getService(
            context,
            0,
            Intent(context, AddPostService::class.java).also {
                it.action = "Stop Service"
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
}