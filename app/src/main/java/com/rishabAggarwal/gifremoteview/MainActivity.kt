package com.rishabAggarwal.gifremoteview

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.IOException


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewFlipperNotificationButton = findViewById<Button>(R.id.view_flipper_notification)
        viewFlipperNotificationButton.setOnClickListener {
            CoroutineScope(SupervisorJob()).launch {
                fireNotification(applicationContext)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        var channel = NotificationChannel(
            "Channel_ID_DEFAULT", "test", NotificationManager.IMPORTANCE_HIGH
        )

        channel.description = "messageBody"
        with(NotificationManagerCompat.from(applicationContext)) {
            createNotificationChannel(channel)
        }
    }

    private suspend fun fireNotification(applicationContext: Context) {

        val builder = NotificationCompat.Builder(applicationContext, "Channel_ID_DEFAULT")
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .setPriority(NotificationCompat.PRIORITY_MAX)

        val bytes: ByteArray =
            Glide.with(applicationContext).`as`(ByteArray::class.java).load(R.raw.vinyl)
                .skipMemoryCache(true).submit().get()


        val smallRemoteViews =
            GifRemoteView(
                applicationContext.packageName,
                R.layout.notify_collapsed_view_flipper,
                applicationContext,
                limitRemoteViewSize = true
            )

        smallRemoteViews.addGif(
            R.id.frame_flipper,
            bytes,
            150,
            150,
            GifOptimisationStrategy.OPTIMISE_SMOOTHNESS
        )
        smallRemoteViews.publishGifs()


        val bigRemoteViewstest =
            GifRemoteView(
                applicationContext.packageName,
                R.layout.notify,
                applicationContext,
                limitRemoteViewSize = true
            )
        bigRemoteViewstest.addGif(
            R.id.frame_flipper,
            bytes,
            150,
            100,
            GifOptimisationStrategy.OPTIMISE_SMOOTHNESS
        )
        bigRemoteViewstest.addGif(
            R.id.frame_flipperq,
            bytes,
            150,
            100,
            GifOptimisationStrategy.OPTIMISE_SMOOTHNESS
        )


        bigRemoteViewstest.publishGifs()
        builder.setCustomBigContentView(bigRemoteViewstest)
        builder.setCustomContentView(smallRemoteViews)
        builder.setCustomHeadsUpContentView(smallRemoteViews)
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(222, builder.build())
        }
    }
}