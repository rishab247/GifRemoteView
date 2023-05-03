package com.rishabAggarwal.gifremoteview

import android.graphics.Bitmap
import android.util.Log
import com.bumptech.glide.gifdecoder.StandardGifDecoder
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter
import com.bumptech.glide.load.resource.gif.GifBitmapProvider
import java.net.URL
import java.util.UUID

class GifCreator {
    var timeBetweenFrame:Int=0
    val frames:MutableList<Bitmap?> = mutableListOf()
    var sizeofFrames =0
      val identifier :UUID= UUID.randomUUID()
    lateinit var remoteViewMemoryManager: RemoteViewMemoryManager;
    constructor(viewId: Int, gifUrl: String, packageName: String,remoteViewMemoryManager: RemoteViewMemoryManager) {
        this.remoteViewMemoryManager =remoteViewMemoryManager

        try{
            val url = URL(gifUrl)
            val gifData: ByteArray = url.readBytes()
            setupGif(gifData)
        }
        catch (e:Exception){
            throw e
        }

    }


    constructor(viewId: Int, gifData: ByteArray, packageName: String,remoteViewMemoryManager: RemoteViewMemoryManager) {
        this.remoteViewMemoryManager =remoteViewMemoryManager

        setupGif(gifData)
    }







    private fun setupGif(gifData: ByteArray){
        val standardGifDecoder = getGifDecoder(gifData)
        val frameCount = standardGifDecoder.frameCount
        standardGifDecoder.advance()
        timeBetweenFrame = standardGifDecoder.nextDelay
        for (i in 0 until frameCount) {
            val bitmap = standardGifDecoder.nextFrame
            frames.add(bitmap)
            sizeofFrames += bitmap!!.allocationByteCount
            standardGifDecoder.advance()
        }
    }


    private fun scaleFrames(){

    }
    private fun reduceFrames(){

    }

    private fun getGifDecoder( gifData: ByteArray): StandardGifDecoder {
        val gifBitmapProvider = GifBitmapProvider(BitmapPoolAdapter())
        val standardGifDecoder = StandardGifDecoder(gifBitmapProvider)
        standardGifDecoder.read(gifData)
        return standardGifDecoder
    }

    private fun Bitmap.getCompressedFrame(width:Int): Bitmap? {

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

    private fun Bitmap.getCompressedFrame(newWidth:Int, newHeight:Int): Bitmap? {
        return Bitmap.createScaledBitmap(this, newWidth, newHeight, false)
    }
}