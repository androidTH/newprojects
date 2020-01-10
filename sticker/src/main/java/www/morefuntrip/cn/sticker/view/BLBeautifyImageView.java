package www.morefuntrip.cn.sticker.view;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.muzhi.camerasdk.library.filter.GPUImageView;

import java.io.IOException;
import java.io.InputStream;

import www.morefuntrip.cn.sticker.BLBitmapUtils;
import www.morefuntrip.cn.sticker.DrawableSticker;
import www.morefuntrip.cn.sticker.R;
import www.morefuntrip.cn.sticker.Sticker;
import www.morefuntrip.cn.sticker.StickerView;
import www.morefuntrip.cn.sticker.TextSticker;

/**
 * Created by Administrator on 2017/4/15.
 */

public class BLBeautifyImageView extends FrameLayout {
    private Context mContext;
    private StickerView mStickerView;
    private GPUImageView mGpuImageView;
    private FrameLayout mRootFrameLayout;
    private ImageView mImageView;

    public BLBeautifyImageView(@NonNull Context context) {
        this(context, null);
    }

    public BLBeautifyImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BLBeautifyImageView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.sticker_beautify_image_view, this, true);
        mStickerView = (StickerView) rootView.findViewById(R.id.bl_sticker_view);
//        mImageView = rootView.findViewById(R.id.bgpu);
        mGpuImageView = (GPUImageView) rootView.findViewById(R.id.bl_gpu_image_view);
        mRootFrameLayout = rootView.findViewById(R.id.root_framelayout);
        initStickerView();
    }

    /**
     * 设置网络加载图片
     *
     * @param url
     */
    public void setImageUrl(String url) {
//        Glide.with(mContext)
//                .load(url)
//                .asBitmap()
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        float width = (float) resource.getWidth();
//                        float height = (float) resource.getHeight();
//                        float ratio = width / height;
//                        mGpuImageView.setRatio(ratio);
//                        setImage(resource);
//                    }
//                });

//        Glide.with(mContext)
//                .load(url)
//                .into(mImageView);
    }

    /**
     * 设置本地路径图片
     *
     * @param path
     */
    public void setImage(String path) {
        mGpuImageView.setImage(path);
    }

    public void setImage(Bitmap bitmap) {
        float width = (float) bitmap.getWidth();
        float height = (float) bitmap.getHeight();
        float ratio = width / height;
        mGpuImageView.setRatio(ratio);
        mGpuImageView.setImage(bitmap);
    }

    public Bitmap getGPUBitmap() {
        return mGpuImageView.getCurrentBitMap();
    }

    public GPUImageView getGPUImageView() {
        return mGpuImageView;
    }

//    public void savePic(){
//        String folderName = BLCommonUtils.getApplicationName(mContext);
//        String fileName = System.currentTimeMillis() + ".jpg";
//        mGpuImageView.saveToPictures(folderName, fileName, new GPUImageView.OnPictureSavedListener() {
//            @Override
//            public void onPictureSaved(Uri uri) {
//                EventBus.getDefault().post(new SaveImageEvent(uri.getPath()));
//            }
//        });
//    }

    /**
     * 合并图片
     *
     * @return
     */
    public String getFilterImage() {
        mGpuImageView.setDrawingCacheEnabled(true);
        Bitmap editbmp = Bitmap.createBitmap(mGpuImageView.getDrawingCache());
        try {
            Bitmap fBitmap = mGpuImageView.capture();
            Bitmap bitmap = Bitmap.createBitmap(fBitmap.getWidth(), fBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas cv = new Canvas(bitmap);
            cv.drawBitmap(fBitmap, 0, 0, null);
            cv.drawBitmap(editbmp, 0, 0, null);

            //最终合并生成图片
            String path = BLBitmapUtils.saveAsBitmap(mContext, bitmap);
            bitmap.recycle();

//            Bitmap bitmap = loadBitmapFromView(mRootFrameLayout);
//            String path = BLBitmapUtils.saveAsBitmap(mContext, bitmap);
            return path;
        } catch (Exception e) {
            return "";
        }
    }


    private Bitmap loadBitmapFromView(View v) {
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }


    /**********************************StickerView相关*********************************/
    private void initStickerView() {
        mStickerView.configDefaultIcons();
        mStickerView.setLocked(false);

        mStickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerClicked(Sticker sticker) {

            }

            @Override
            public void onStickerDeleted(Sticker sticker) {

            }

            @Override
            public void onStickerDragFinished(Sticker sticker) {

            }

            @Override
            public void onStickerZoomFinished(Sticker sticker) {

            }

            @Override
            public void onStickerFlipped(Sticker sticker) {

            }

            @Override
            public void onStickerDoubleTapped(Sticker sticker) {

            }
        });
    }

    /**
     * 添加图片贴图
     * @param drawableId
     */
    public void addSticker(int drawableId) {
        if (drawableId <= 0)
            return;
        Drawable drawable = ContextCompat.getDrawable(mContext, drawableId);
        mStickerView.addSticker(new DrawableSticker(drawable));
    }

    public void addSticker(String path) {
        if (TextUtils.isEmpty(path))
            return;
        Drawable drawable = getDrawableFromAssetsFile(path);
        mStickerView.addSticker(new DrawableSticker(drawable));
    }

    /**
     * 从Assert文件夹中读取位图数据
     *
     * @param fileName
     * @return
     */
    private Drawable getDrawableFromAssetsFile(String fileName) {
        Drawable drawable = null;
        AssetManager am = mContext.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            drawable = Drawable.createFromStream(is, null);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return drawable;
    }

    /**
     * 添加文字贴图
     * @param text
     * @param color
     */
    public void addTextSticker(String text, int color){
        TextSticker sticker = new TextSticker(mContext);
        sticker.setText(text);
        sticker.setTextColor(color);
        sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        sticker.resizeText();
        mStickerView.addSticker(sticker);
    }

    /**
     * 添加文字贴图
     * @param text
     */
    public void addTextSticker(String text){
        addTextSticker(text, Color.WHITE);
    }

    public void stickerLocked(boolean lock) {
        mStickerView.setLocked(lock);
    }

}
