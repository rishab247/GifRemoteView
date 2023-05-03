package com.rishabAggarwal.gifremoteview

import android.graphics.Bitmap
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.UUID

class RemoteViewMemoryManager {

    /*  Functionality
        store  size of indiviual gifcreator
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
private var individualGifSize:HashMap<UUID,Int> = hashMapOf()

    fun canAddImage(image: Bitmap): Boolean {
        return (image.byteCount + currentSize) <= currentMaxSize
    }
    fun canRemoveImage(image: Bitmap,uuid:UUID): Boolean {
        return ((currentSize - image.byteCount) >=0) && (((individualGifSize[uuid]?:0) - image.byteCount) >=0)
    }

    @Throws(OutOfMemoryError::class)
    fun addImage(image: Bitmap,uuid:UUID) {
        if (canAddImage(image)) {
            individualGifSize[uuid] = (individualGifSize[uuid] ?: 0) + image.byteCount
            currentSize += image.byteCount
        }
        else{
            ///check this
            throw OutOfMemoryError()
        }
    }
    fun removeImage(image: Bitmap,uuid:UUID) {
        if (canRemoveImage(image,uuid)) {
            individualGifSize[uuid] = (individualGifSize[uuid])!! - image.byteCount
            currentMaxSize -= image.byteCount
        }
    }

    fun getGifSize(uuid:UUID):Int?{
        return individualGifSize[uuid]
    }
    fun getTotalSize():Int{
        return currentSize
    }

    fun getRecommendedSizeOptimisation():Float{
        if(currentSize>currentMaxSize) {

            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.DOWN
            return df.format((currentMaxSize.toFloat() / currentSize.toFloat())).toFloat()
        }
        else
            return 1f
    }
    fun limitMaxSize() {
        currentMaxSize = MAX_SIZE_EXCEPTION_CASE
    }


}