package com.rishabAggarwal.gifremoteview

import android.graphics.Bitmap
import android.util.Log
import android.widget.RemoteViews
import com.bumptech.glide.gifdecoder.StandardGifDecoder
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter
import com.bumptech.glide.load.resource.gif.GifBitmapProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.UUID
import kotlin.math.log

class GifCreator(
    private var viewId: Int,
    private var packageName: String,
    private var remoteViewMemoryManager: RemoteViewMemoryManager,
    private var remoteView: GifRemoteView
) {
    private var timeBetweenFrame: Int = 0
    private val frames: MutableList<Bitmap?> = mutableListOf()
    private var sizeofFrames = 0
    private val identifier: UUID = UUID.randomUUID()


//    suspend fun addGif(gifUrl: String) {
//        downloadGif(gifUrl)
//    }

    fun addGif(gifData: ByteArray) {
        setupGif(gifData)
    }

    private suspend fun downloadGif(gifUrl: String) {
        withContext(Dispatchers.IO) {
            val url = URL(gifUrl)
            val gifData: ByteArray = url.readBytes()
            setupGif(gifData)
        }
    }

    private fun setupGif(gifData: ByteArray) {
        val standardGifDecoder = getGifDecoder(gifData)
        val frameCount = standardGifDecoder.frameCount
        standardGifDecoder.advance()
        timeBetweenFrame = standardGifDecoder.nextDelay
        for (i in 0 until frameCount) {
            val bitmap = standardGifDecoder.nextFrame
            if (bitmap != null) {
                remoteViewMemoryManager.addImage(bitmap, identifier)
                frames.add(bitmap)
                sizeofFrames += bitmap.allocationByteCount
            }
            standardGifDecoder.advance()
        }
    }


    fun populateGif() {
        for (frame in frames.subList(0, 3)) {
            val v = RemoteViews(packageName, R.layout.view_single_frame)
            v.setImageViewBitmap(R.id.frame, frame)
            Log.e("TAG1", "populateGif: ${sizeofFrames}")
            remoteView.setInt(R.id.frame_flipper, "setFlipInterval", timeBetweenFrame)

            remoteView.addView(viewId, v)
        }
    }


    fun optimiseGifs(typeOfStrategy: GifStrategy, optimisationPercentage: Float) {

        when (typeOfStrategy) {
            GifStrategy.AUTOMATIC -> {

            }

            GifStrategy.OPTIMISE_FRAMES -> {

            }

            GifStrategy.OPTIMISE_SIZE -> {

            }

            GifStrategy.NONE -> {
                return
            }
        }
    }

    private fun scaleFrames(optimisationPercentage: Float) {
        val optimisedSize = sizeofFrames* optimisationPercentage

    }

    private fun reduceFrames(optimisationPercentage: Float) {

    }

    private fun getGifDecoder(gifData: ByteArray): StandardGifDecoder {
        val gifBitmapProvider = GifBitmapProvider(BitmapPoolAdapter())
        val standardGifDecoder = StandardGifDecoder(gifBitmapProvider)
        standardGifDecoder.read(gifData)
        return standardGifDecoder
    }

    private fun Bitmap.getCompressedFrame(width: Int): Bitmap? {

        var newWidth = width
        var newHeight = 0
        // If already smaller than 480p do not scale else scale
        if (width < newWidth) {
            newWidth = width
            newHeight = height
        } else {
            val ratio: Float = (width.toFloat() / height.toFloat())
            newHeight = (newWidth / ratio).toInt()
        }
        Log.e("TAG1", "getCompressedFrame: ${width}   ${height}    ${newWidth}   d ${newHeight}")

        return Bitmap.createScaledBitmap(this, newWidth, newHeight, false)
    }

    private fun Bitmap.getCompressedFrame(newWidth: Int, newHeight: Int): Bitmap? {
        return Bitmap.createScaledBitmap(this, newWidth, newHeight, false)
    }
}