package com.pubnub.components.chat.util

import android.content.Context
import android.os.Build
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.decode.VideoFrameDecoder
import coil.fetch.VideoFrameFileFetcher
import coil.fetch.VideoFrameUriFetcher
import coil.request.CachePolicy

object CoilHelper {
    fun setCoil(context: Context) {
        val imageLoader = ImageLoader.Builder(context)
//            Please attach the PubNub decoder while using images stored in PubNub
//            .decoder(PubNubEncryptedImageDecoder(
//                LocalContext.current,
//                PubNubFramework.instance))

            .availableMemoryPercentage(0.25) // Use 25% of the application's available memory.
            .crossfade(true) // Show a short crossfade when loading images from network or disk.
            .componentRegistry {
                // GIFs
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder(context))
                } else {
                    add(GifDecoder())
                }

                // SVGs
                add(SvgDecoder(context))

                // Video frames
                add(VideoFrameFileFetcher(context))
                add(VideoFrameUriFetcher(context))
                add(VideoFrameDecoder(context))
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .build()
        Coil.setImageLoader(imageLoader)
    }
}
