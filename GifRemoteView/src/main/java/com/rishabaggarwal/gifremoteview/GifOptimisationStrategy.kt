package com.rishabaggarwal.gifremoteview

sealed class GifOptimisationStrategy {
    // preserve original frame count but will decrease quality of frames
    object OPTIMISE_SMOOTHNESS : GifOptimisationStrategy()
    // preserve original frame quality but will trim end of the media
    object OPTIMISE_QUALITY : GifOptimisationStrategy()

    //coming Soon
    // preserve original frame quality but will decrease quality of frames
//    object OPTIMISE_LENGTH : GifOptimisationStrategy()

    //Not Recommended
    object NONE : GifOptimisationStrategy()

    //use above Strategy in different perception to get best result
    data class AUTOMATIC(
        val optimisationRatio: Float = Config.OptimisationRatio,
    ) : GifOptimisationStrategy()
}