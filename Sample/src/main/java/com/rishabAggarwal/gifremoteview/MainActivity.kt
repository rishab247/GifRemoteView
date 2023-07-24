package com.rishabAggarwal.gifremoteview

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.rishabaggarwal.gifremoteview.GifOptimisationStrategy
import com.rishabaggarwal.gifremoteview.GifRemoteView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val viewFlipperNotificationButton = findViewById<Button>(R.id.view_flipper_notification)
        viewFlipperNotificationButton.setOnClickListener {
            CoroutineScope(SupervisorJob()).launch {
                fireOneGifNotification(applicationContext)
                fireFourGifNotification(applicationContext)
            }
        }
    }

    private fun fireFourGifNotification(applicationContext: Context) {

        val builder = NotificationCompat.Builder(applicationContext, "Channel_ID_DEFAULT")
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .setPriority(NotificationCompat.PRIORITY_MAX)

        val bytes: ByteArray =
            Glide.with(applicationContext).`as`(ByteArray::class.java).load(R.raw.response)
                .skipMemoryCache(true).submit().get()

        val collapsedRemoteView =
            GifRemoteView(
                applicationContext.packageName,
                R.layout.notify_collapsed,
                limitRemoteViewSize = true
            )

        collapsedRemoteView.addGif(
            R.id.frame_flipper,
            bytes
        )
        collapsedRemoteView.publishGifs()


        val expandedRemoteView =
            GifRemoteView(
                applicationContext.packageName,
                R.layout.notify_expanded,
            )
        expandedRemoteView.addGif(
            R.id.frame_flipper1,
            Glide.with(applicationContext).`as`(ByteArray::class.java).load(R.raw.one)
                .skipMemoryCache(true).submit().get()
        )
        expandedRemoteView.addGif(
            R.id.frame_flipper2,
            Glide.with(applicationContext).`as`(ByteArray::class.java).load(R.raw.two)
                .skipMemoryCache(true).submit().get()
        )
        expandedRemoteView.addGif(
            R.id.frame_flipper3,
            Glide.with(applicationContext).`as`(ByteArray::class.java).load(R.raw.three)
                .skipMemoryCache(true).submit().get(),
            gifOptimisationStrategy = GifOptimisationStrategy.OPTIMISE_SMOOTHNESS
        )
        expandedRemoteView.addGif(
            R.id.frame_flipper4,
            Glide.with(applicationContext).`as`(ByteArray::class.java).load(R.raw.four)
                .skipMemoryCache(true).submit().get(),
        )


        expandedRemoteView.publishGifs()
        builder.setCustomBigContentView(expandedRemoteView)
        builder.setCustomContentView(collapsedRemoteView)
        builder.setCustomHeadsUpContentView(collapsedRemoteView)
        with(NotificationManagerCompat.from(applicationContext)) {
            notify((1..10000).random(), builder.build())
        }
    }

    private fun fireOneGifNotification(applicationContext: Context) {

        val builder = NotificationCompat.Builder(applicationContext, "Channel_ID_DEFAULT")
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .setPriority(NotificationCompat.PRIORITY_MAX)

        val bytes: ByteArray =
            Glide.with(applicationContext).`as`(ByteArray::class.java).load(R.raw.response)
                .skipMemoryCache(true).submit().get()

        val collapsedRemoteView =
            GifRemoteView(
                applicationContext.packageName,
                R.layout.notify_collapsed,
            )

        collapsedRemoteView.addGif(
            R.id.frame_flipper,
            bytes
        )


        collapsedRemoteView.publishGifs()


        val expandedRemoteView =
            GifRemoteView(
                applicationContext.packageName,
                R.layout.notify_expanded,
                limitRemoteViewSize = true
            )
        expandedRemoteView.addGif(
            R.id.frame_flipper5,
            Glide.with(applicationContext).`as`(ByteArray::class.java).load(R.raw.response)
                .skipMemoryCache(true).submit().get()
        )



        expandedRemoteView.publishGifs()
        builder.setCustomBigContentView(expandedRemoteView)
        builder.setCustomContentView(collapsedRemoteView)
        builder.setCustomHeadsUpContentView(collapsedRemoteView)
        with(NotificationManagerCompat.from(applicationContext)) {
            notify((1..10000).random(), builder.build())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "Channel_ID_DEFAULT", "test", NotificationManager.IMPORTANCE_HIGH
        )
        with(NotificationManagerCompat.from(applicationContext)) {
            createNotificationChannel(channel)
        }
    }
}