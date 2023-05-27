package com.rishabAggarwal.gifremoteview

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