package com.rishabAggarwal.gifremoteview

class GifManager {
    // setup a image scale down approch (reduce size,or reduce count) but will we do it for previour gifs
    private var remoteViewMemoryManager: RemoteViewMemoryManager = RemoteViewMemoryManager()
    private val gidDecoders: MutableList<GifCreator> = mutableListOf()
    val gifStrategy: GifStrategy = GifStrategy.AUTOMATIC

    fun addGif(viewId: Int, gifUrl: String, packageName: String) {
        val gifDecoder = GifCreator(viewId, gifUrl, packageName, remoteViewMemoryManager)
        gidDecoders.add(gifDecoder)
    }

    fun addGif(viewId: Int, gifData: ByteArray, packageName: String) {
        val gifDecoder = GifCreator(viewId, gifData, packageName, remoteViewMemoryManager)
        gidDecoders.add(gifDecoder)
    }

    fun optimiseGifs(){

    }
    fun populateGifs(){

    }

}

enum class GifStrategy {
    AUTOMATIC,
    OPTIMISE_SIZE,
    OPTIMISE_FRAMES,
    NONE
}