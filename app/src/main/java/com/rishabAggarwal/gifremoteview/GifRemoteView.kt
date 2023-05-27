package com.rishabAggarwal.gifremoteview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.log


class GifRemoteView constructor(
    packageName: String, layoutId: Int, private val applicationContext: Context
) : RemoteViews(packageName, layoutId) {
    //calculate the other bitmap sizes form varous methods
    // setup gif creator class
    // add check for max remoteview size
    //funtion to override max remoteview size  according to device
    //outofmemmory error flow to the developer
    /// write size management scheme(no of frames, size of frames) for individual gif and for all gifs
//redesign or remove memorymanager
    //max gif size check
    private val remoteViewMemoryManager: RemoteViewMemoryManager = RemoteViewMemoryManager()
    private val gifManager: GifManager = GifManager()

    /*
       height- is the max height in the dp
       width- is the max width in the dp
        */
    public fun addGif(
        viewId: Int, bytes: ByteArray, height: Int? = null,
        width: Int? = null
    ) {

        //add warning for gif size more than 100kb and throw error for 1MB
        Log.e("TAG1", "addGif: ${bytes.size}")
        gifManager.addGif(viewId, bytes, applicationContext.packageName, this, height, width)


    }

    fun publishGifs() {
        gifManager.optimiseGifs()
        gifManager.populateGifs()
    }

    override fun setImageViewBitmap(@IdRes viewId: Int, bitmap: Bitmap) {
        Log.e("TAG1", "setImageViewResource: $viewId")

        remoteViewMemoryManager.addImage(bitmap, null, viewId)
        setBitmap(viewId, "setImageBitmap", bitmap)
    }

}