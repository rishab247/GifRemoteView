package com.rishabAggarwal.gifremoteview

import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.IdRes
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.UUID

class RemoteViewMemoryManager {

    /*  Functionality
        store  size of individual gifcreator
        store cumulative size of all gifcreator
        get size optimisation percentage
        limit the total size of remoteView

     */


    companion object {
        const val MAX_SIZE = 5000000
        const val MAX_SIZE_EXCEPTION_CASE = 2000000

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
    fun addImage(image: Bitmap, uuid: UUID?,@IdRes viewId: Int? = null) {
        Log.e("TAG1", "addImage1: $viewId", )
        if (uuid != null) {
            individualGifSize[uuid] = (individualGifSize[uuid] ?: 0) + image.byteCount
        }
        if(viewId!=null){
            if(individualimageSize.getOrDefault(viewId,null)!=null){
                removeImage(image)
            }
            individualimageSize[viewId] =(individualimageSize[viewId] ?: 0)+ image.byteCount
            Log.e("TAG1", "addImage:$currentSize  ${individualimageSize[viewId]}", )

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
        if((currentSize - image.byteCount) >= 0){
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
//todo excule normal image size from this calculation
        var totalGifSize=0
        for( i in individualGifSize){
            totalGifSize+=i.value
            Log.e("TAG1", "individualGifSize: ${i.key}  ${i.value}", )
        }
        val sizeToBeReduced =currentSize - totalGifSize

        val v= if (currentSize > currentMaxSize) {
            val df = DecimalFormat("#.###")
            df.roundingMode = RoundingMode.DOWN
            df.format((currentMaxSize-sizeToBeReduced).toFloat() / (currentSize)).toFloat()
        } else 1.toFloat()

        Log.e("TAG", "getRecommendedSizeOptimisation1: ${v}  ${currentMaxSize}   ${currentSize }   $sizeToBeReduced", )
        return v
    }

    fun limitMaxSize() {
        currentMaxSize = MAX_SIZE_EXCEPTION_CASE
    }


}