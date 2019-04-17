package com.example.project470_v01;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class ImageFetch {
    private static ImageLoader imageLoader = null;
    private static RequestQueue imageQueue = null;

    public static ImageLoader getImageLoader(Context ctx){
        if(imageLoader == null){
            if(imageQueue == null){
                imageQueue = Volley.newRequestQueue(ctx.getApplicationContext());
            }
            imageLoader = new ImageLoader(imageQueue, new ImageLoader.ImageCache() {
                Map<String, Bitmap> cache = new HashMap<String, Bitmap>();
                @Override
                public Bitmap getBitmap(String url) {
                    return cache.get(url);
                }
                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }
            });
        }
        return imageLoader;
    }
}