package com.rishabAggarwal.gifremoteview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.IdRes
import kotlin.jvm.Throws


class GifRemoteView constructor(
    packageName: String,
    layoutId: Int,
    private val applicationContext: Context,
    private val limitRemoteViewSize: Boolean = false
) : RemoteViews(packageName, layoutId) {
    //calculate the other bitmap sizes form varous methods
    // setup gif creator class
    // add check for max remoteview size
    //funtion to override max remoteview size  according to device
    //outofmemmory error flow to the developer
    /// write size management scheme(no of frames, size of frames) for individual gif and for all gifs
    //redesign or remove memorymanager
    //max gif size check
    // custom remoteViews Size limit
    private val remoteViewMemoryManager: RemoteViewMemoryManager = RemoteViewMemoryManager()
    private val gifManager: GifManager = GifManager()

    init {
        limitRemoteViewSize()
    }
@Throws(OutOfMemoryError::class)
    public fun addGif(
        viewId: Int, bytes: ByteArray, height: Int? = null,
        width: Int? = null,
        gifOptimisationStrategy: GifOptimisationStrategy = GifOptimisationStrategy.AUTOMATIC()
    ) {
        if (bytes.size >= 100 * 1024) {
            Log.w("GifRemoteView:", " Gif File is larger than 100KB ")
        }
        if (bytes.size >= 1024 * 1024) {
            throw OutOfMemoryError("GifRemoteView: File is larger than 1MB ")
        }
        gifManager.addGif(
            viewId,
            bytes,
            applicationContext.packageName,
            this,
            height,
            width,
            gifOptimisationStrategy = gifOptimisationStrategy,
            remoteViewMemoryManager
        )


    }

    fun publishGifs() {
        gifManager.optimiseGifs()
        gifManager.populateGifs()
    }

    override fun setImageViewBitmap(@IdRes viewId: Int, bitmap: Bitmap) {
        remoteViewMemoryManager.addImage(bitmap, null, viewId)
        super.setImageViewBitmap(viewId, bitmap)
    }

    override fun setBitmap(@IdRes viewId: Int, methodName: String?, value: Bitmap) {
        remoteViewMemoryManager.addImage(value, null, viewId)
        super.setBitmap(viewId, methodName, value)
    }

    private fun limitRemoteViewSize() {
        if (limitRemoteViewSize)
            remoteViewMemoryManager.limitMaxSize()
    }

}