package com.d6zone.android.app.widget;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.lang.ref.WeakReference;

import io.rong.imageloader.core.assist.ImageSize;
import io.rong.imageloader.core.assist.ViewScaleType;
import io.rong.imageloader.core.imageaware.NonViewAware;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.util.SystemClock;

/**
 * author : jinjiarui
 * time   : 2019/10/25
 * desc   :
 * version:
 */
public class DanMuImageWare extends NonViewAware {
    private long start;
    private int id;
    private WeakReference<IDanmakuView> danmakuViewRef;
    private BaseDanmaku danmaku;
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public DanMuImageWare(String imageUri, BaseDanmaku danmaku, int width, int height, IDanmakuView danmakuView) {
        this(imageUri, new ImageSize(width, height), ViewScaleType.FIT_INSIDE);
        if (danmaku == null) {
            throw new IllegalArgumentException("danmaku may not be null");
        }
        this.danmaku = danmaku;
        this.id = danmaku.hashCode();
        this.danmakuViewRef = new WeakReference<>(danmakuView);
        this.start = SystemClock.uptimeMillis();
    }

    @Override
    public int getId() {
        return this.id;
    }

    public String getImageUri() {
        return this.imageUri;
    }

    private DanMuImageWare(ImageSize imageSize, ViewScaleType scaleType) {
        super(imageSize, scaleType);
    }

    private DanMuImageWare(String imageUri, ImageSize imageSize, ViewScaleType scaleType) {
        super(imageUri, imageSize, scaleType);
    }

    @Override
    public boolean setImageDrawable(Drawable drawable) {
        return super.setImageDrawable(drawable);
    }

    @Override
    public boolean setImageBitmap(Bitmap bitmap) {
//        if (this.danmaku.text.toString().contains("textview")) {
//            Log.e("DFM", (SystemClock.uptimeMillis() - this.start) + "ms=====> inside" + danmaku.tag + ":" + danmaku.getActualTime() + ",url: bitmap" + (bitmap == null));
//        }
//        CircleBitmapTransform.transform(bitmap)
        this.bitmap = bitmap;
        IDanmakuView danmakuView = danmakuViewRef.get();
//        danmaku.measureResetFlag++;
        if (danmakuView != null) {
            danmakuView.invalidateDanmaku(danmaku, true);
        }
        return true;
    }
}
