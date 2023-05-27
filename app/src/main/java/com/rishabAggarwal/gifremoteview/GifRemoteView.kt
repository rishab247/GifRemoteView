package com.rishabAggarwal.gifremoteview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.IdRes


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

    /*
       height- is the max height in the dp
       width- is the max width in the dp
        */

    init {
        limitRemoteViewSize()
    }

    @Throws(OutOfMemoryError::class)
    public fun addGif(
        viewId: Int, bytes: ByteArray, height: Int? = null,
        width: Int? = null,
        gifOptimisationStrategy: GifOptimisationStrategy = GifOptimisationStrategy.AUTOMATIC()
    ) {
        if(bytes.size>=100*1024){
            throw OutOfMemoryError("GifRemoteView: File is larger than 100KB ")
        }
        //add warning for gif size more than 100kb and throw error for 1MB
        Log.e("TAG1", "addGif: ${bytes.size}")
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

    override fun setInt(@IdRes viewId: Int, methodName: String?, value: Int) {
        if (methodName == "setBackgroundResource") {
            try {
                val bitmap: Bitmap =
                    BitmapFactory.decodeResource(applicationContext.resources, value)

                //TODO
//                remoteViewMemoryManager.addImage(bitmap)
            } catch (e: Exception) {
                Log.e("TAG", "setInt: ${e.message}")
            }
        }
        super.setInt(viewId, methodName, value)
    }
    fun publishGifs() {
        gifManager.optimiseGifs()
        gifManager.populateGifs()
    }

    //TODO check this
    override fun setImageViewBitmap(@IdRes viewId: Int, bitmap: Bitmap) {
        Log.e("TAG1", "setImageViewResource: $viewId")

        remoteViewMemoryManager.addImage(bitmap, null, viewId)
        super.setImageViewBitmap(viewId, bitmap)
    }

    override fun setBitmap(@IdRes viewId: Int, methodName: String?, value: Bitmap) {
        Log.e("TAG1", "setBitmap: $viewId")
        remoteViewMemoryManager.addImage(value, null, viewId)
        super.setBitmap(viewId,methodName, value)
    }

    private fun limitRemoteViewSize() {
        if (limitRemoteViewSize)
            remoteViewMemoryManager.limitMaxSize()
    }

}