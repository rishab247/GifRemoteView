package com.rishabaggarwal.gifremoteview

import android.graphics.Bitmap
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.IdRes
import kotlin.jvm.Throws

/**
 * @property limitRemoteViewSize it use to limit remoteSize to 2000000 bytes
 * as some OEMs on android 13 will drop notification from system tray.
 * If your device is under android 13 or OEMs allows bigger remote view it can be set to false to increase size to 5000000 bytes
 */
class GifRemoteView constructor(
    private val packageName: String,
    layoutId: Int,
    private val limitRemoteViewSize: Boolean = true
) : RemoteViews(packageName, layoutId) {
    private val remoteViewMemoryManager: RemoteViewMemoryManager = RemoteViewMemoryManager()
    private val gifManager: GifManager = GifManager()

    init {
        limitRemoteViewSize()
    }


    /**
     *
     * @param viewId Id of viewFlipper where Gif supposed to shown
     * @param bytes Gif Data in Bytes with max limit of 1Mb but recommended limit of 100Kbs
     * @param height max Gif height in the dp
     * @param width max Gif width in the dp
     * @param gifOptimisationStrategy how a Gif should be optimised in case of overLimit
     */
    @Throws(OutOfMemoryError::class)
    fun addGif(
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
            packageName,
            this,
            height,
            width,
            gifOptimisationStrategy = gifOptimisationStrategy,
            remoteViewMemoryManager
        )
    }

    /**
     * Use to optimise the Gif according to user pref
     * and setting all the frames to ViewFlipper.
     */

    fun publishGifs() {
        gifManager.optimiseGifs()
        gifManager.populateGifs()
    }

    /**
     * @param maxSize setting maxsize this GifRemoteView can take.
     * This can be usefully in device below android 12 as most of them don't have ay limits
     */
    fun setMaxRemoteViewSize(maxSize: Long) {
        gifManager.setMaxRemoteViewSize(maxSize)
    }

    /**
     * Equivalent to calling {@link ImageView#setImageBitmap(Bitmap)}
     * @param viewId The id of the view whose bitmap should change
     * @param bitmap The new Bitmap for the drawable
     */
    override fun setImageViewBitmap(@IdRes viewId: Int, bitmap: Bitmap) {
        remoteViewMemoryManager.addImage(bitmap, null, viewId)
        super.setImageViewBitmap(viewId, bitmap)
    }

    /**
     * Call a method taking one Bitmap on a view in the layout for this RemoteViews.
     *
     * Params:
     * viewId – The id of the view on which to call the method.
     * methodName – The name of the method to call. value – The value to pass to the method.
     *
     *
     * The bitmap will be flattened into the parcel if this object is sent across processes, so it may end up using a lot of memory, and may be fairly slow.
     */
    override fun setBitmap(@IdRes viewId: Int, methodName: String?, value: Bitmap) {
        remoteViewMemoryManager.addImage(value, null, viewId)
        super.setBitmap(viewId, methodName, value)
    }

    /**
     * Reducing the size of remoteView to 2000000 depending on limitRemoteViewSize
     * passed in the constructor which is by default true as some OEMS for Android 13
     * limit remoteView size allowed in the system tray.
     */
    private fun limitRemoteViewSize() {
        if (limitRemoteViewSize)
            remoteViewMemoryManager.limitMaxSize()
    }
}