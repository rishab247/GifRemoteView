package com.rishabAggarwal.gifremoteview

import android.util.Log

class GifManager {
    // setup a image scale down approch (reduce size,or reduce count) but will we do it for previour gifs
    private var remoteViewMemoryManager: RemoteViewMemoryManager = RemoteViewMemoryManager()
    private val gidCreators: HashMap<Int, GifCreator> = hashMapOf()
    private val gifStrategy: GifStrategy = GifStrategy.AUTOMATIC()

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
        width: Int? = null
    ) {
        var gidCreator = gidCreators.getOrDefault(
            viewId,
            null
        )
        if (gidCreator != null) {
            gidCreator.replaceGif(gifData, height?.toPx?.toInt(), width?.toPx?.toInt())
        } else {
            Log.e("TAG111", "GifCreatorFactory: ${gidCreator.hashCode()}")
            gidCreator = GifCreator(viewId, packageName, remoteViewMemoryManager, remoteView)
            gidCreator.addGif(gifData, height?.toPx?.toInt(), width?.toPx?.toInt())
            gidCreators[viewId] = gidCreator
        }

    }

    fun optimiseGifs() {

        for (gidCreator in gidCreators){
            Log.e("TAG122", "GifCreatorFactory: ${gidCreator.key} ", )

        }


        val optimisationPercentage = remoteViewMemoryManager.getRecommendedSizeOptimisation()
//        Log.e("TAG1", "optimiseGifs: ${optimisationPercentage}")

        for (gidCreator in gidCreators) {

            gidCreator.value.optimiseGifs(gifStrategy, optimisationPercentage)
        }

    }

    fun populateGifs() {
        for (gidCreator in gidCreators) {
            gidCreator.value.populateGif()
        }
    }

}


sealed class GifStrategy {
    object OPTIMISE_SIZE : GifStrategy()
    object OPTIMISE_FRAMES : GifStrategy()
    //Not Recommended
    object NONE : GifStrategy()
    data class AUTOMATIC(
        val optimisationRatio: Float = Config.optimisationRatio,
    ) : GifStrategy()
}
