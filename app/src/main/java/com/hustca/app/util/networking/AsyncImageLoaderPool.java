package com.hustca.app.util.networking;

import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by hamster on 16/7/23.
 *
 * To avoid multiple downloading tasks operating on one ImageView
 */
public class AsyncImageLoaderPool {
    private static AsyncImageLoaderPool mInstance = new AsyncImageLoaderPool();
    private static ArrayList<AsyncImageLoader.RawImageLoader> mLoaders;

    private AsyncImageLoaderPool() { mLoaders = new ArrayList<>(5); }

    public static void add(AsyncImageLoader.RawImageLoader loader) {
        mLoaders.add(loader);
    }

    public static void remove(AsyncImageLoader.RawImageLoader loader) {
        mLoaders.remove(loader);
    }

    public static ArrayList<AsyncImageLoader.RawImageLoader> getAll(ImageView view) {
        ArrayList<AsyncImageLoader.RawImageLoader> selectedLoaders = new ArrayList<>(5);
        for (AsyncImageLoader.RawImageLoader loader : mLoaders) {
            if (loader.getImageView() == view) {
                selectedLoaders.add(loader);
            }
        }
        return selectedLoaders;
    }

    public static void cancelAll(ImageView view) {
        ArrayList<AsyncImageLoader.RawImageLoader> loaders = getAll(view);
        for (AsyncImageLoader.RawImageLoader loader : loaders) {
            /* Allow it to finish downloading. Just don't draw it */
            loader.cancel(false);
        }
    }
}
