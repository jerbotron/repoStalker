package com.bypassmobile.octo.image;


import android.content.Context;

import com.squareup.picasso.Picasso;

public final class ImageLoader {

    private static Picasso singleton;
    private static ImageCache imageCache;

    public static Picasso createImageLoader(Context context){
        if(singleton == null){
            singleton = new Picasso.Builder(context).memoryCache(getImageCache()).build();
        }
        return singleton;
    }

    private static ImageCache getImageCache() {
        if (imageCache == null) {
            imageCache = new ImageCache();
        } else {
            if (imageCache.isExpired())
                imageCache.clear();
        }
        return imageCache;
    }
}
