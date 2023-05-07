package com.rishabAggarwal.gifremoteview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.log


class GifRemoteView constructor(
    packageName: String, layoutId: Int, private val applicationContext: Context
) : RemoteViews(packageName, layoutId) {
//calculate the other bitmap sizes form varous methods
    // setup gif creator class
    // add check for max remoteview size
    //funtion to override max remoteview size  according to device
    //outofmemmory error flow to the developer
    /// write size management scheme(no of frames, size of frames) for individual gif and for all gifs
//redesign or remove memorymanager
private val remoteViewMemoryManager: RemoteViewMemoryManager = RemoteViewMemoryManager()
    val gifManager: GifManager =GifManager()
    //constructor
//    GifRemoteView( packageName: String, layoutId: Int){
//
//    }


//    public suspend fun addGif(viewId: Int, gifUrl: String) {
////        val gifManager:GifManager = GifManager()
//
//
//                gifManager.addGif(viewId, gifUrl,applicationContext.packageName,this)
//
//    }


    public fun addGif(viewId: Int, bytes: ByteArray) {
        Log.e("TAG1", "addGif: ${bytes.size}", )
        gifManager.addGif(viewId, bytes,applicationContext.packageName,this)
        gifManager.optimiseGifs()
        gifManager.populateGifs()


    }

//    private fun canAddGif(gifUrl: String) {
//
//    }

    override fun setImageViewBitmap(@IdRes viewId: Int, bitmap: Bitmap) {
        remoteViewMemoryManager.addImage(bitmap,null)
        setBitmap(viewId, "setImageBitmap", bitmap)
    }

    override fun setImageViewResource(@IdRes viewId: Int, @DrawableRes srcId: Int) {
        val bitmap: Bitmap = BitmapFactory.decodeResource(applicationContext.resources, srcId)
        remoteViewMemoryManager.addImage(bitmap,null)
        super.setImageViewResource(viewId, srcId)
    }


    //TODO
    override fun setImageViewUri(@IdRes viewId: Int, uri: Uri?) {
        //disabled
//          fun getDrawableFromUri(uri: Uri): Drawable? {
//
//            val scheme = uri.scheme
//            if (ContentResolver.SCHEME_ANDROID_RESOURCE == scheme) {
//                try {
//
//                    // Load drawable through Resources, to get the source density information
//                    val r: ContentResolver.OpenResourceIdResult =
//                        mContext.getContentResolver().getResourceId(uri)
//                    return r.r.getDrawable(r.id, applicationContext.getTheme())
//                } catch (e: Exception) {
//                    Log.w("TAG1", "Unable to open content: $uri", e)
//                }
//            } else if (ContentResolver.SCHEME_CONTENT == scheme || ContentResolver.SCHEME_FILE == scheme) {
//                try {
//                    val res: Resources? =
//                        if (ImageView.sCompatUseCorrectStreamDensity) getResources() else null
//                    val src = ImageDecoder.createSource(
//                        applicationContext.contentResolver,
//                        uri, res
//                    )
//                    return ImageDecoder.decodeDrawable(
//                        src
//                    ) { decoder: ImageDecoder, info: ImageInfo?, s: ImageDecoder.Source? ->
//                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
//                    }
//                } catch (e: IOException) {
//                    Log.w("TAG1", "Unable to open content: $uri", e)
//                }
//            } else {
//                return Drawable.createFromPath(uri.toString())
//            }
//            return null
//        }
//
//
//        currentRemoteViewSize+= bitmap.byteCount
//        super.setImageViewUri(viewId,uri)

    }
//TODO

    override fun setImageViewIcon(@IdRes viewId: Int, icon: Icon) {
        //disabled

//        currentRemoteViewSize+=0
//        val iconc :IconCompat = IconCompat()
//        val icon: Bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.profile_circle)
//        super.setImageViewIcon(viewId,icon)
    }


    override fun setInt(@IdRes viewId: Int, methodName: String?, value: Int) {
        if (methodName == "setBackgroundResource") {
            try {
                val bitmap: Bitmap =
                    BitmapFactory.decodeResource(applicationContext.resources, value)

                //TODO
//                remoteViewMemoryManager.addImage(bitmap)
            } catch (e: Exception) {
                Log.e("TAG", "setInt: ${e.message}")
            }
        }
        super.setInt(viewId, methodName, value)
    }



//    public suspend fun testGif( ) {
//          fun getGifDecoder(  ): StandardGifDecoder {
//             val bytes: ByteArray =
//              Glide.with(applicationContext).`as`(ByteArray::class.java).load(R.raw.vinyl)
//                  .skipMemoryCache(true).submit().get()
//            val gifBitmapProvider = GifBitmapProvider(BitmapPoolAdapter())
//            val standardGifDecoder = StandardGifDecoder(gifBitmapProvider)
//            standardGifDecoder.read(bytes)
//            return standardGifDecoder
//        }
//        val standardGifDecoder = getGifDecoder()
//        val frameCount = 4
//        standardGifDecoder.advance()
//        var totalSize = 0L
//        for (i in 0 until frameCount) {
//            val delay = standardGifDecoder.nextDelay
//            val bitmap = standardGifDecoder.nextFrame
//            totalSize += bitmap!!.allocationByteCount
//            val v = RemoteViews(applicationContext.packageName, R.layout.view_single_frame)
//            v.setImageViewBitmap(R.id.frame, bitmap)
//            this.addView(R.id.frame_flipper, v)
//
//            standardGifDecoder.advance()
//            this.setInt(R.id.frame_flipper, "setFlipInterval", delay)
//        }
//    }

}