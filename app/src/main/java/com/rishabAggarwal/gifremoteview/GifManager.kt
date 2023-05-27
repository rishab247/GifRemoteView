package com.rishabAggarwal.gifremoteview

import android.util.Log

class GifManager {
    // setup a image scale down approch (reduce size,or reduce count) but will we do it for previour gifs
    private lateinit var remoteViewMemoryManager: RemoteViewMemoryManager
    private val gifCreators: HashMap<Int, GifCreator> = hashMapOf()
    private val gifOptimisationStrategyMapping: HashMap<Int,GifOptimisationStrategy> = hashMapOf()
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
        gifOptimisationStrategy:GifOptimisationStrategy = GifOptimisationStrategy.AUTOMATIC(),
        remoteViewMemoryManager: RemoteViewMemoryManager
    ) {
        this.remoteViewMemoryManager = remoteViewMemoryManager
        var gifCreator = gifCreators.getOrDefault(
            viewId,
            null
        )
        if (gifCreator != null) {
            gifCreator.replaceGif(gifData, height?.toPx?.toInt(), width?.toPx?.toInt())
        } else {
            Log.e("TAG111", "GifCreatorFactory: ${gifCreator.hashCode()}")
            gifCreator = GifCreator(viewId, packageName, remoteViewMemoryManager, remoteView)
            gifCreator.addGif(gifData, height?.toPx?.toInt(), width?.toPx?.toInt())
            gifCreators[viewId] = gifCreator
        }
        gifOptimisationStrategyMapping[viewId] = gifOptimisationStrategy

    }

    fun optimiseGifs() {
        val optimisationPercentage = remoteViewMemoryManager.getRecommendedSizeOptimisation()
        for (gifCreator in gifCreators) {
            gifCreator.value.optimiseGifs(gifOptimisationStrategyMapping.getOrDefault(gifCreator.key,GifOptimisationStrategy.AUTOMATIC()), optimisationPercentage)
        }

    }

    fun populateGifs() {
        for (gifCreator in gifCreators) {
            gifCreator.value.populateGif()
        }
    }

}


sealed class GifOptimisationStrategy {
    object OPTIMISE_SMOOTHNESS : GifOptimisationStrategy()
    object OPTIMISE_QUALITY : GifOptimisationStrategy()
    object OPTIMISE_LENGTH : GifOptimisationStrategy()
    //Not Recommended
    object NONE : GifOptimisationStrategy()
    data class AUTOMATIC(
        val optimisationRatio: Float = Config.optimisationRatio,
    ) : GifOptimisationStrategy()
}
