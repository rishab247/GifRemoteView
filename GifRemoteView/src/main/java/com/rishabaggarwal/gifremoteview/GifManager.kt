package com.rishabaggarwal.gifremoteview

import android.util.Log
import com.rishabaggarwal.gifremoteview.utils.toPx

class GifManager {
    private lateinit var remoteViewMemoryManager: RemoteViewMemoryManager
    private val gifCreators: HashMap<Int, GifCreator> = hashMapOf()
    private val gifOptimisationStrategyMapping: HashMap<Int, GifOptimisationStrategy> = hashMapOf()

    /*
    height- is the max height in the dp
    width- is the max width in the dp
     */
    fun addGif(
        viewId: Int,
        gifData: ByteArray,
        packageName: String,
        remoteView: GifRemoteView,
        height: Int? = null,
        width: Int? = null,
        gifOptimisationStrategy: GifOptimisationStrategy = GifOptimisationStrategy.AUTOMATIC(),
        remoteViewMemoryManager: RemoteViewMemoryManager
    ) {
        this.remoteViewMemoryManager = remoteViewMemoryManager
        var gifCreator = gifCreators.getOrDefault(
            viewId, null
        )
        if (gifCreator != null) {
            gifCreator.replaceGif(gifData, height?.toPx?.toInt(), width?.toPx?.toInt())
        } else {
            gifCreator = GifCreator(viewId, packageName, remoteViewMemoryManager, remoteView)
            gifCreator.addGif(gifData, height?.toPx?.toInt(), width?.toPx?.toInt())
            gifCreators[viewId] = gifCreator
        }
        gifOptimisationStrategyMapping[viewId] = gifOptimisationStrategy

    }

    fun optimiseGifs() {
        val optimisationPercentage = remoteViewMemoryManager.getRecommendedSizeOptimisation()
        Log.e("TAG1", "optimiseGifs: ${optimisationPercentage}   ${remoteViewMemoryManager.currentSize}", )
        for (gifCreator in gifCreators) {
            gifCreator.value.optimiseGifs(
                gifOptimisationStrategyMapping.getOrDefault(
                    gifCreator.key, GifOptimisationStrategy.AUTOMATIC()
                ), optimisationPercentage
            )
        }

    }

    fun populateGifs() {
        for (gifCreator in gifCreators) {
            gifCreator.value.populateGif()
        }
    }
    fun setMaxRemoteViewSize(size:Long){
        if (size >= RemoteViewMemoryManager.MAX_SIZE) {
            Log.w("GifRemoteView:", "App stability can not be assured if size of increased beyond MAX_SIZE ")
        }
        remoteViewMemoryManager.setMaxGifSize(size)
    }
}



