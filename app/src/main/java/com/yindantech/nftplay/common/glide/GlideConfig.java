package com.yindantech.nftplay.common.glide;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.caverock.androidsvg.SVG;
import com.yindantech.nftplay.common.base.BaseApp;
import com.yindantech.nftplay.common.glide.svg.SvgDecoder;
import com.yindantech.nftplay.common.glide.svg.SvgDrawableTranscoder;

import java.io.File;
import java.io.InputStream;


/**
 * Glide Global configuration
 */
@GlideModule
public final class GlideConfig extends AppGlideModule {

    /**
     * Local image cache file maximum
     */
    private static final int IMAGE_DISK_CACHE_MAX_SIZE = 500 * 1024 * 1024;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        // You do not need to apply for storage permission to read or write to an external cache directory
        final File diskCacheFile = new File(context.getExternalCacheDir(), "nft_image");
        if (diskCacheFile.exists() && diskCacheFile.isFile()) {
            diskCacheFile.delete();
        }
        if (!diskCacheFile.exists()) {
            diskCacheFile.mkdirs();
        }
        //Set the disk cache path
        builder.setDiskCache(() -> DiskLruCacheWrapper.create(diskCacheFile, IMAGE_DISK_CACHE_MAX_SIZE));

        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context).build();
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();

        int customMemoryCacheSize = (int) (1.2 * defaultMemoryCacheSize);
        int customBitmapPoolSize = (int) (1.2 * defaultBitmapPoolSize);
        //Set the memory cache path
        builder.setMemoryCache(new LruResourceCache(customMemoryCacheSize));
        //设置BitmapPool
        builder.setBitmapPool(new LruBitmapPool(customBitmapPoolSize));
        //Set the default configuration
        builder.setDefaultRequestOptions(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)//cache resource
        );
    }


    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        //registerComponents okhttp,svg
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(BaseApp.getHttpClient()));
        registry.register(SVG.class, PictureDrawable.class, new SvgDrawableTranscoder())
                .append(InputStream.class, SVG.class, new SvgDecoder());

    }
}
