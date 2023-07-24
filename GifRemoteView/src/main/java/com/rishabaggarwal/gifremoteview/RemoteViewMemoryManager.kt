package com.rishabaggarwal.gifremoteview

import android.graphics.Bitmap
import androidx.annotation.IdRes
import com.rishabaggarwal.gifremoteview.utils.formatData
import java.util.UUID

internal class RemoteViewMemoryManager {
    companion object {
        const val MAX_SIZE = 5000000L
        const val MAX_SIZE_EXCEPTION_CASE = 2000000L
    }

    var currentSize = 0
    private var currentMaxSize = MAX_SIZE
    private var individualGifSize: HashMap<UUID, Int> = hashMapOf()
    private var individualimageSize: HashMap<Int, Int> = hashMapOf()

    fun canAddImage(image: Bitmap): Boolean {
        return (image.byteCount + currentSize) <= currentMaxSize
    }

    fun canRemoveImage(image: Bitmap, uuid: UUID): Boolean {
        return ((currentSize - image.byteCount) >= 0) && (((individualGifSize[uuid]
            ?: 0) - image.byteCount) >= 0)
    }

    @Throws(OutOfMemoryError::class)
    fun addImage(image: Bitmap, uuid: UUID?, @IdRes viewId: Int? = null) {
        if (uuid != null) {
            individualGifSize[uuid] = (individualGifSize[uuid] ?: 0) + image.byteCount
        }

        //when a non gif image is added
        if (viewId != null) {
            if (individualimageSize.getOrDefault(viewId, null) != null) {
                removeImage(image)
            }
            individualimageSize[viewId] = (individualimageSize[viewId] ?: 0) + image.byteCount
        }
        currentSize += image.byteCount
    }

    fun removeImage(image: Bitmap, uuid: UUID) {
        if (canRemoveImage(image, uuid)) {
            individualGifSize[uuid] = (individualGifSize[uuid])!! - image.byteCount
            currentSize -= image.byteCount
        }
    }

    //this will clear Out all the images with this uid
    fun removeGif(uuid: UUID) {
        val sizeofGif = individualGifSize.getOrDefault(uuid, 0)
        if (sizeofGif != 0) {
            currentSize -= sizeofGif
            individualGifSize[uuid] = 0
        }
    }

    private fun removeImage(image: Bitmap) {
        if ((currentSize - image.byteCount) >= 0) {
            currentSize -= image.byteCount
        }
    }

    fun getGifSize(uuid: UUID): Int? {
        return individualGifSize[uuid]
    }

    fun getTotalSize(): Int {
        return currentSize
    }

    fun getRecommendedSizeOptimisation(): Float {
        var totalGifSize = 0
        for (i in individualGifSize) {
            totalGifSize += i.value
        }
        val sizeToBeReduced = currentSize - totalGifSize

        return if (currentSize > currentMaxSize) {
            formatData((currentMaxSize - sizeToBeReduced).toFloat() / (currentSize))
        } else 1.toFloat()
    }

    fun limitMaxSize() {
        currentMaxSize = MAX_SIZE_EXCEPTION_CASE
    }

    fun setMaxGifSize(size: Long) {
        currentMaxSize = size
    }


}