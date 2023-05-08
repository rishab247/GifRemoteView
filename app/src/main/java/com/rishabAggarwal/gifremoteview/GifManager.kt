package com.rishabAggarwal.gifremoteview

class GifManager {
    // setup a image scale down approch (reduce size,or reduce count) but will we do it for previour gifs
    private var remoteViewMemoryManager: RemoteViewMemoryManager = RemoteViewMemoryManager()
    private val gidCreators: MutableList<GifCreator> = mutableListOf()
    private val gifStrategy: GifStrategy = GifStrategy.AUTOMATIC

//    suspend fun addGif(viewId: Int, gifUrl: String, packageName: String, remoteView: GifRemoteView) {
//        val gifDecoder = GifCreator(viewId, packageName, remoteViewMemoryManager, remoteView)
//        gifDecoder.addGif(gifUrl)
//        gidDecoders.add(gifDecoder)
//    }

      fun addGif(viewId: Int, gifData: ByteArray, packageName: String,remoteView: GifRemoteView) {
        val gifDecoder = GifCreator(viewId, packageName, remoteViewMemoryManager, remoteView)
        gifDecoder.addGif(gifData)
        gidCreators.add(gifDecoder)
    }

    fun optimiseGifs(){
        for (gidCreator in gidCreators){
            gidCreator.optimiseGifs(gifStrategy)
        }

    }
    fun populateGifs(){
        for (gidCreator in gidCreators){
            gidCreator.populateGif()
        }
    }

}

enum class GifStrategy {
    AUTOMATIC,
    OPTIMISE_SIZE,
    OPTIMISE_FRAMES,
    NONE
}