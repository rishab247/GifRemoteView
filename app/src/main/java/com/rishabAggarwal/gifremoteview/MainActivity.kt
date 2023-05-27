package com.rishabAggarwal.gifremoteview

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RemoteViews
import androidx.annotation.AnyRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.gifdecoder.StandardGifDecoder
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter
import com.bumptech.glide.load.engine.bitmap_recycle.LruArrayPool
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.resource.gif.GifBitmapProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


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

        channel = NotificationChannel(
            "Channel_ID_LOW", "testlow", NotificationManager.IMPORTANCE_DEFAULT
        )
        var att = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        channel.setSound(
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.getPackageName() + "/"),
            att
        )
        channel.enableVibration(false)
        channel.description = "messageBodylow"
        with(NotificationManagerCompat.from(applicationContext)) {
            createNotificationChannel(channel)

        }


        channel = NotificationChannel(
            "Channel_ID_LOW1", "testlow1", NotificationManager.IMPORTANCE_HIGH
        )
        att = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        channel.setSound(
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"),
            att
        )
        channel.enableVibration(false)
        channel.description = "messageBodylow1"
        with(NotificationManagerCompat.from(applicationContext)) {
            createNotificationChannel(channel)

        }
    }

    private suspend fun fireNotification(applicationContext: Context) {

        val builder = NotificationCompat.Builder(applicationContext, "Channel_ID_DEFAULT")
//            .setLargeIcon(Conte/**/xtCompat.getDrawable(applicationContext, R.drawable.photo)?.toBitmap())
            .setSmallIcon(android.R.drawable.ic_menu_search)
//            .setLargeIcon(
//                ContextCompat.getDrawable(
//                    applicationContext, R.drawable.photo1
//                )?.toBitmap()
//            ).setContentTitle("notificationTitle11").setContentText("notificationContent11")
            .setPriority(NotificationCompat.PRIORITY_MAX)

//
//        val bigRemoteViews = RemoteViews(applicationContext.packageName, R.layout.notify)
//        val smallRemoteViews =
//            RemoteViews(applicationContext.packageName, R.layout.notify_collapsed_view_flipper)
//        val standardGifDecoder = setupgifdecoder(applicationContext)
//        val frameCount = standardGifDecoder.frameCount
//        standardGifDecoder.advance()
//        var totalSize = 0L
//        for (i in 0 until frameCount) {
//            val delay = standardGifDecoder.nextDelay
//            val bitmap = standardGifDecoder.nextFrame?.getCompressedFrame()
//            totalSize += bitmap!!.allocationByteCount
//            val v = RemoteViews(applicationContext.packageName, R.layout.view_single_frame)
//            v.setImageViewBitmap(R.id.frame, bitmap)
//            bigRemoteViews.addView(R.id.frame_flipper, v)
//            smallRemoteViews.addView(R.id.frame_flipper, v)
//
//            standardGifDecoder.advance()
//            bigRemoteViews.setInt(R.id.frame_flipper, "setFlipInterval", delay)
//            smallRemoteViews.setInt(R.id.frame_flipper, "setFlipInterval", delay)
//        }
        val bytes: ByteArray =
            Glide.with(applicationContext).`as`(ByteArray::class.java).load(R.raw.response)
                .skipMemoryCache(true).submit().get()
        val bigRemoteViewstest =
            GifRemoteView(applicationContext.packageName, R.layout.notify, applicationContext,limitRemoteViewSize = true)

        val smallRemoteViews =
            GifRemoteView(applicationContext.packageName, R.layout.notify_collapsed_view_flipper, applicationContext,limitRemoteViewSize = true)

        smallRemoteViews.addGif(R.id.frame_flipper, bytes, 150, 150,GifOptimisationStrategy.OPTIMISE_SMOOTHNESS)
        smallRemoteViews.publishGifs()
        Log.e("TAG11", "fireNotification: start", )
        bigRemoteViewstest.addGif(R.id.frame_flipper, bytes, 150, 100,GifOptimisationStrategy.OPTIMISE_SMOOTHNESS)
        bigRemoteViewstest.addGif(R.id.frame_flipperq, bytes, 150, 100,GifOptimisationStrategy.OPTIMISE_SMOOTHNESS)
//        bigRemoteViewstest.setImageViewBitmap(R.id.image2,
//            AppCompatResources.getDrawable(this.baseContext, R.drawable.largeimage)?.toBitmap()!! )
        bigRemoteViewstest.setBitmap(R.id.image2,"setImageBitmap", AppCompatResources.getDrawable(this.baseContext, R.drawable.image)?.toBitmap(210.toPx.toInt(),210.toPx.toInt(),Bitmap.Config.RGB_565)!!  )

//        bigRemoteViewstest.setImageViewBitmap(R.id.image2,
//            AppCompatResources.getDrawable(this.baseContext, R.drawable.largeimage)?.toBitmap()!! )

        bigRemoteViewstest.publishGifs()
        builder.setCustomBigContentView(bigRemoteViewstest)
        builder.setCustomContentView(smallRemoteViews)
        builder.setCustomHeadsUpContentView(smallRemoteViews)
        with(NotificationManagerCompat.from(applicationContext)) {

            notify(222, builder.build())


        }


    }

    private suspend fun setupgifdecoder(applicationContext: Context): StandardGifDecoder {
        val bytes: ByteArray =
            Glide.with(applicationContext).`as`(ByteArray::class.java).load(R.raw.response)
                .skipMemoryCache(true).submit().get()

        val memorySizeCalculator = MemorySizeCalculator.Builder(applicationContext).build()
        val bitmapPoolSize = memorySizeCalculator.bitmapPoolSize.toLong()
        val bitmapPool =
            if (bitmapPoolSize > 0) LruBitmapPool(bitmapPoolSize) else BitmapPoolAdapter()
        val arrayPool = LruArrayPool(memorySizeCalculator.arrayPoolSizeInBytes)
//        val gifBitmapProvider = GifBitmapProvider(bitmapPool, arrayPool)
        val pool = BitmapPoolAdapter()
        val gifBitmapProvider = GifBitmapProvider(pool)
         val standardGifDecoder = StandardGifDecoder(gifBitmapProvider)
        standardGifDecoder.read(bytes)
        return standardGifDecoder
    }

    private fun Bitmap.getCompressedFrame(): Bitmap? {

        var newWidth = 400
        var newHeight = 0
        // If already smaller than 480p do not scale else scale
        if (width < newWidth) {
            newWidth = width
            newHeight = height
        } else {
            val ratio: Float = (width.toFloat() / height.toFloat())
            newHeight = (newWidth / ratio).toInt()
        }
//        Log.e("TAG1", "getCompressedFrame: ${width}   ${height}    ${newWidth}   d ${newHeight}")

        return Bitmap.createScaledBitmap(this, newWidth, newHeight, false)
    }
}