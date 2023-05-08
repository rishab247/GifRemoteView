package com.rishabAggarwal.gifremoteview

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.system.Os
import android.util.Log
import android.widget.RemoteViews
import com.bumptech.glide.gifdecoder.StandardGifDecoder
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter
import com.bumptech.glide.load.resource.gif.GifBitmapProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.UUID
import java.util.stream.IntStream.range
import kotlin.math.ceil

class GifCreator(
    private var viewId: Int,
    private var packageName: String,
    private var remoteViewMemoryManager: RemoteViewMemoryManager,
    private var remoteView: GifRemoteView
) {
    private var timeBetweenFrame: Int = 0
    private val frames: MutableList<Bitmap> = mutableListOf()
    private var sizeofFrames = 0
    private val identifier: UUID = UUID.randomUUID()
    private val optimisationRatio = 0.85f

    fun addGif(gifData: ByteArray) {
        setupGif(gifData)
    }

    private fun setupGif(gifData: ByteArray) {
        val standardGifDecoder = getGifDecoder(gifData)
        val frameCount = standardGifDecoder.frameCount
        standardGifDecoder.advance()
        timeBetweenFrame = standardGifDecoder.nextDelay
        for (i in 0 until frameCount) {
            var bitmap = standardGifDecoder.nextFrame
            if (bitmap != null) {
                bitmap = bitmap.getOptimisationFrame()
                remoteViewMemoryManager.addImage(bitmap, identifier)
                frames.add(bitmap)
                sizeofFrames += bitmap.allocationByteCount
            }
            standardGifDecoder.advance()
        }
    }

    fun populateGif() {
        for (frame in frames) {
            val v = RemoteViews(packageName, R.layout.view_single_frame)
            v.setImageViewBitmap(R.id.frame, frame)
            Log.e("TAG1", "populateGif: ${sizeofFrames}")
            remoteView.setInt(R.id.frame_flipper, "setFlipInterval", timeBetweenFrame)

            remoteView.addView(viewId, v)
        }
    }

    fun optimiseGifs(typeOfStrategy: GifStrategy) {
        val optimisationPercentage = remoteViewMemoryManager.getRecommendedSizeOptimisation()
        Log.e("TAG1", "optimiseGifs: ${typeOfStrategy}")
        when (typeOfStrategy) {
            GifStrategy.AUTOMATIC -> {
                val reductionPercentage = 1 - optimisationPercentage
                val frameOptimisationPercentage = reductionPercentage * optimisationRatio
                val frameSizeOptimisationPercentagetemp =
                    reductionPercentage * (1 - optimisationRatio)
                Log.e(
                    "TAG1",
                    "optimiseGifs: ${frameSizeOptimisationPercentagetemp}   ${frameOptimisationPercentage}      total${1 - (frameOptimisationPercentage + frameSizeOptimisationPercentagetemp)}  optimise${optimisationPercentage}    reduction${reductionPercentage} sum${((frameOptimisationPercentage) + (frameSizeOptimisationPercentagetemp))}",
                )

                optimiseFrames(1 - frameOptimisationPercentage)
                val frameSizeOptimisationPercentage =
                    remoteViewMemoryManager.getRecommendedSizeOptimisation()
                Log.e(
                    "TAG1",
                    "optimiseGifs:  ${optimisationPercentage} ${1 - frameOptimisationPercentage} ${1 - frameSizeOptimisationPercentage}",
                )
                optimiseFrameSize(frameSizeOptimisationPercentage)
            }

            GifStrategy.OPTIMISE_FRAMES -> {
                optimiseFrames(optimisationPercentage)
            }

            GifStrategy.OPTIMISE_SIZE -> {
                optimiseFrameSize(optimisationPercentage)

            }

            GifStrategy.NONE -> {
                return
            }
        }
    }

    private fun optimiseFrames(optimisationPercentage: Float) {
        val optimisedSize = sizeofFrames * optimisationPercentage
        var currentSize = 0
        var maxNoOfFrames = 0
        val noOfFrames = frames.size
        for (i in range(0, noOfFrames)) {
            val currentFrame = frames[i]
            val currentFrameSize = currentFrame.allocationByteCount

            if (currentSize + currentFrameSize < optimisedSize) {
                currentSize += currentFrameSize
                maxNoOfFrames = i
            } else {
                break
            }

        }
        Log.e("TAG1", "optimiseFrames:for  ${currentSize} ${maxNoOfFrames}")

        for (i in noOfFrames - 1 downTo maxNoOfFrames) {
            val currentFrame = frames[i]
            remoteViewMemoryManager.removeImage(currentFrame, identifier)
            frames.remove(currentFrame)
        }
        Log.e("TAG1", "optimiseFrames: ${frames.size}")
    }

    private fun optimiseFrameSize(optimisationPercentage: Float) {
        val noOfFrames = frames.size
        var size = 0
        var oldSize = 0
        for (i in 0 until noOfFrames) {
            val currentFrame = frames[i]
            oldSize += currentFrame.allocationByteCount
            remoteViewMemoryManager.removeImage(currentFrame, identifier)
            val optimisedFrame = currentFrame.getCompressedFrame(optimisationPercentage)
            remoteViewMemoryManager.addImage(optimisedFrame, identifier)
            size += optimisedFrame.allocationByteCount
            frames[i] = optimisedFrame
            Log.e(
                "TAG1",
                "optimiseFrameSize: optimisationPercentage $optimisationPercentage currentFrame${currentFrame.byteCount}  optimisedFrame${optimisedFrame.byteCount}",
            )
        }
        Log.e("TAG1", "optimiseFrameSize1 $size   $oldSize")

    }

    private fun getGifDecoder(gifData: ByteArray): StandardGifDecoder {
        val gifBitmapProvider = GifBitmapProvider(BitmapPoolAdapter())
        val standardGifDecoder = StandardGifDecoder(gifBitmapProvider)
        standardGifDecoder.read(gifData)
        return standardGifDecoder
    }

    private fun Bitmap.getOptimisationFrame(): Bitmap {
// Basically shrinking it to max possible size

        var newWidth = 0
        var newHeight = getMaxHeightInPixel()
        // If already smaller than 480p do not scale else scale
        if (height < newHeight) {
            newWidth = width
            newHeight = height
        } else {
            val ratio: Float = (height.toFloat() / width.toFloat())
            newWidth = (newHeight / ratio).toInt()
        }
        Log.e("TAG1", "getCompressedFrame:1 ${width}   ${height}    ${newWidth}   d ${newHeight}   new${getMaxHeightInPixel()}")

        return Bitmap.createBitmap(this, 0, 0, newWidth, newHeight, Matrix(), false)
    }

    private fun Bitmap.getCompressedFrame(newWidth: Int, newHeight: Int): Bitmap? {
        return Bitmap.createScaledBitmap(this, newWidth, newHeight, false)
    }

    private fun Bitmap.getCompressedFrame(optimisationPercentage: Float): Bitmap {
        val mat = Matrix()
        mat.setScale(optimisationPercentage.toFloat(), optimisationPercentage.toFloat());
        val bitmap = Bitmap.createBitmap(this, 0, 0, width, height, mat, false)
//        Log.e(
//            "TAG1",
//            "getCompressedFrame:  ${ceil(width * optimisationPercentage).toInt()} ${width}   ${
//                ceil(height * optimisationPercentage).toInt()
//            } ${height}   ${bitmap.byteCount}",
//        )

//        val bitmap = Bitmap.createScaledBitmap(
//            this,
//            ceil(width * optimisationPercentage*0.746).toInt(),
//            ceil(width * optimisationPercentage*0.746).toInt(),
//            false
//        )
        return bitmap
    }

    private fun getMaxHeightInPixel(): Int {
        var maxHeightInDP = 0
        maxHeightInDP = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            128
        } else {
            256
        }
        return maxHeightInDP.toPx.toInt()
    }
}