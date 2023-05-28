package com.rishabaggarwal.gifremoteview

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.widget.RemoteViews
import com.bumptech.glide.gifdecoder.StandardGifDecoder
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter
import com.bumptech.glide.load.resource.gif.GifBitmapProvider
import com.rishabAggarwal.gifremoteview.toPx
import com.rishabaggarwal.gifremoteview.Config.OptimisationRatio
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.UUID
import java.util.stream.IntStream.range
import kotlin.math.floor
import kotlin.math.sqrt

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

    fun addGif(gifData: ByteArray, height: Int? = null, width: Int? = null) {
        setupGif(gifData, height, width)
    }

    fun replaceGif(gifData: ByteArray, height: Int? = null, width: Int? = null) {
        remoteViewMemoryManager.removeGif(identifier)
        setupGif(gifData, height, width)
    }

    private fun setupGif(gifData: ByteArray, height: Int? = null, width: Int? = null) {
        val standardGifDecoder = getGifDecoder(gifData)
        val frameCount = standardGifDecoder.frameCount
        standardGifDecoder.advance()
        timeBetweenFrame = standardGifDecoder.nextDelay
        for (i in 0 until frameCount) {
            var bitmap = standardGifDecoder.nextFrame
            if (bitmap != null) {
                bitmap = bitmap.getOptimisationFrame(height)
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
            remoteView.setInt(viewId, "setFlipInterval", timeBetweenFrame)
            remoteView.addView(viewId, v)
        }
    }

    fun optimiseGifs(
        gifOptimisationStrategy: GifOptimisationStrategy,
        optimisationPercentage: Float
    ) {
        when (gifOptimisationStrategy) {
            is GifOptimisationStrategy.AUTOMATIC -> {
                val reductionPercentage = 1 - optimisationPercentage
                val frameOptimisationPercentage = reductionPercentage * OptimisationRatio
                val frameSizeOptimisationPercentagetemp =
                    reductionPercentage * (1 - OptimisationRatio)
                optimiseForQuality(1 - frameOptimisationPercentage)

                optimiseForSmoothness(frameSizeOptimisationPercentagetemp)
            }

            GifOptimisationStrategy.OPTIMISE_QUALITY -> {
                optimiseForQuality(optimisationPercentage)
            }


            GifOptimisationStrategy.OPTIMISE_SMOOTHNESS -> {
                optimiseForSmoothness(optimisationPercentage)

            }

            GifOptimisationStrategy.OPTIMISE_LENGTH -> {
                //TODO
            }

            GifOptimisationStrategy.NONE -> {
                return
            }

        }
    }

    private fun optimiseForQuality(optimisationPercentage: Float) {
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
        for (i in noOfFrames - 1 downTo maxNoOfFrames) {
            val currentFrame = frames[i]
            remoteViewMemoryManager.removeImage(currentFrame, identifier)
            frames.remove(currentFrame)
        }
    }

    private fun optimiseForSmoothness(optimisationPercentage: Float) {
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
        }
    }

    private fun getGifDecoder(gifData: ByteArray): StandardGifDecoder {
        val gifBitmapProvider = GifBitmapProvider(BitmapPoolAdapter())
        val standardGifDecoder = StandardGifDecoder(gifBitmapProvider)
        standardGifDecoder.read(gifData)
        return standardGifDecoder
    }

    private fun Bitmap.getOptimisationFrame(height: Int? = null): Bitmap {
        // Basically shrinking it to max possible size
        var newWidth = 0
        var newHeight = height ?: getMaxHeightInPixel()
        if (this.height < newHeight) {
            newWidth = this.width
            newHeight = this.height
        } else {
            val ratio: Float = (this.height.toFloat() / this.width.toFloat())
            newWidth = (newHeight / ratio).toInt()
        }

        return Bitmap.createBitmap(this, 0, 0, newWidth, newHeight, Matrix(), false)
    }

    private fun Bitmap.getCompressedFrame(newWidth: Int, newHeight: Int): Bitmap? {
        return Bitmap.createScaledBitmap(this, newWidth, newHeight, false)
    }

    private fun Bitmap.getCompressedFrame(optimisationPercentage: Float): Bitmap {
        val scalingFactor = getScalingFactor(width, height, optimisationPercentage)


        val mat = Matrix()
        mat.setScale(scalingFactor, scalingFactor);
        return Bitmap.createBitmap(this, 0, 0, width, height, mat, false)

    }

    private fun getScalingFactor(width: Int, height: Int, optimisationPercentage: Float): Float {
        val area = width * height
        val optimisedArea = area * optimisationPercentage
        val ratio = height.toFloat() / width.toFloat()
        val newHeight = floor(sqrt(optimisedArea * ratio))
        val df = DecimalFormat("#.###")
        df.roundingMode = RoundingMode.DOWN
        return df.format(newHeight / height).toFloat()
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