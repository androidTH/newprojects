package com.d6.android.app.activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.d6.android.app.R;
import com.d6.android.app.adapters.StickerAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import com.d6.android.app.fragments.BLBeautifyFragment;

import www.morefuntrip.cn.sticker.BLBitmapUtils;
import www.morefuntrip.cn.sticker.Bean.BLBeautifyParam;
import www.morefuntrip.cn.sticker.Bean.BLStickerInfo;
import www.morefuntrip.cn.sticker.DrawableSticker;
import www.morefuntrip.cn.sticker.StickerView;

/*
 * Created by Administrator on 2017/4/15.
 * 美化图片
 */
public class BLBeautifyImageActivity extends AppCompatActivity implements View.OnClickListener {
    public final static String STATIC_STICKER = "Stickers";
    private RecyclerView mStickerList;

    private StickerAdapter mStickerAdapter;
    private List<String> imageList;
    private String imgPath;
    private BLBeautifyFragment mBL;
    private BLBeautifyParam mParam;
    private ImageView mImageView;
    private StickerView mStickerView;
    private RelativeLayout mRootRelativeLayout;
    private Bitmap mResoureBitmap;
    private int mBitmapWidth = 0;
    private int mBitmapHeight = 0;

    private MergeImageTask mMergeTask;
    private StickerAdapter.OnStickerItemClick mOnStickerItemClick = new StickerAdapter.OnStickerItemClick() {
        @Override
        public void selectedStickerItem(BLStickerInfo data, int pos) {
//            mBL.addSticker(data.getUrl());
             addSticker(data.getUrl());
        }
    };

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
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            drawable = Drawable.createFromStream(is, null);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return drawable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bl_activity_beautify_image);
        findViewById(R.id.edit_img_cancel).setOnClickListener(this);
        findViewById(R.id.edit_img_success).setOnClickListener(this);
        initView();
        otherLogic();
        setListener();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.edit_img_cancel){
              finish();
        }else if(v.getId() == R.id.edit_img_success){
            if (mMergeTask == null) {
//                mBL.stickerLocked(true);
//                mMergeTask = new MergeImageTask();
//                /*
//                 * 注：AsyncTask只能执行一次，如果mMergeTask不为null,
//                 * 则说明已经执行过了
//                 */
//                mMergeTask.execute(mBL);
            }

            mStickerView.setLocked(true);
            String path = getResultBitmap();
            if(!TextUtils.equals("",path)){
                mParam.getImages().remove(mParam.getIndex());
                mParam.getImages().add(mParam.getIndex(),path);
            }
            mRootRelativeLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.putExtra(BLBeautifyParam.RESULT_KEY, mParam);
                    setResult(RESULT_OK, intent);
                    try{
                        onBackPressed();
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
            },200);
        }
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    protected void initView() {
        mStickerList = findViewById(R.id.recycler_sticker);
        mStickerView = findViewById(R.id.bl_sticker_view);
        mImageView = findViewById(R.id.iv_beautify);
        mRootRelativeLayout = findViewById(R.id.root_relative);

        LinearLayoutManager stickerListLayoutManager = new LinearLayoutManager(this);
        stickerListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mStickerList.setLayoutManager(stickerListLayoutManager);
    }

    protected void otherLogic() {
        mParam = getIntent().getParcelableExtra(BLBeautifyParam.KEY);
        imageList = mParam.getImages();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id.function_detail, mBL = new BLBeautifyFragment().newInstance(imageList.get(mParam.getIndex())));
//        transaction.commit();
        //默认滤镜被选中
        onStickerClick();

        Glide.with(this)
                .asBitmap()
                .load(imageList.get(mParam.getIndex()))
                .into(new SimpleTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(final Bitmap resource, Transition<? super Bitmap> transition) {
                        mImageView.setImageBitmap(resource);
                        mImageView.post(new Runnable() {
                            @Override
                            public void run() {
                                mBitmapHeight = mImageView.getMeasuredHeight();
                                mBitmapWidth = mImageView.getMeasuredWidth();
                                RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) mStickerView.getLayoutParams();
                                params.height = mImageView.getMeasuredHeight();
                                params.width = mImageView.getMeasuredWidth();
                                mStickerView.setLayoutParams(params);
                                Log.i("onResourceReady",mBitmapHeight+"高度"+mBitmapWidth);
                            }
                        });
                    }
                });
//        Glide.with(this).load(imageList.get(mParam.getIndex())).into(mImageView);
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

    private String getResultBitmap(){
        String path = "";
        try{
            Bitmap editbmp = loadBitmapFromView(mStickerView);
            mResoureBitmap = loadBitmapFromView(mImageView);
            Bitmap bitmap = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.ARGB_8888);
            Canvas cv = new Canvas(bitmap);
            cv.drawBitmap(mResoureBitmap, 0, 0, null);
            cv.drawBitmap(editbmp, 0, 0, null);
            path = BLBitmapUtils.saveAsBitmap(this, bitmap);
            bitmap.recycle();
        }catch(Exception e){
            e.printStackTrace();
        }
        return path;
    }


    protected void setListener() {
        //Adapter中设置贴纸的路径，默认支持的是assets目录下面的，其它目录需要自行修改Window
        mStickerAdapter.addStickerImages(STATIC_STICKER);
        mStickerAdapter.setOnStickerItemClick(mOnStickerItemClick);
    }

    private void onStickerClick() {
//        mBL.stickerLocked(false);
        mStickerView.setLocked(false);
        mStickerAdapter = new StickerAdapter(this);
        mStickerList.setAdapter(mStickerAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
    }

    class MergeImageTask extends AsyncTask<Fragment, Void, BLBeautifyParam> {

        public MergeImageTask() {

        }

        @Override
        protected BLBeautifyParam doInBackground(Fragment... params) {
            BLBeautifyFragment fragment = (BLBeautifyFragment) params[0];
            String path = fragment.complete();
            mParam.getImages().remove(mParam.getIndex());
            if (path != null && !path.equals("")) {
                mParam.getImages().add(mParam.getIndex(),path);
            }
            return mParam;
        }

        @Override
        protected void onPostExecute(BLBeautifyParam param) {
            Intent intent = new Intent();
            intent.putExtra(BLBeautifyParam.RESULT_KEY, param);
            setResult(RESULT_OK, intent);
            onBackPressed();
        }
    }

}
