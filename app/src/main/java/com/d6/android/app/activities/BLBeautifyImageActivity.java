package com.d6.android.app.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.d6.android.app.R;
import com.d6.android.app.adapters.StickerAdapter;

import java.util.List;

import com.d6.android.app.fragments.BLBeautifyFragment;

import www.morefuntrip.cn.sticker.Bean.BLBeautifyParam;
import www.morefuntrip.cn.sticker.Bean.BLStickerInfo;

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

    private MergeImageTask mMergeTask;
    private StickerAdapter.OnStickerItemClick mOnStickerItemClick = new StickerAdapter.OnStickerItemClick() {
        @Override
        public void selectedStickerItem(BLStickerInfo data, int pos) {
            mBL.addSticker(data.getUrl());
        }
    };

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
                mBL.stickerLocked(true);
                mMergeTask = new MergeImageTask();
                /*
                 * 注：AsyncTask只能执行一次，如果mMergeTask不为null,
                 * 则说明已经执行过了
                 */
                mMergeTask.execute(mBL);
            }
        }
    }

    protected void initView() {
        mStickerList = findViewById(R.id.recycler_sticker);
        LinearLayoutManager stickerListLayoutManager = new LinearLayoutManager(this);
        stickerListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mStickerList.setLayoutManager(stickerListLayoutManager);
    }

    protected void otherLogic() {
        mParam = getIntent().getParcelableExtra(BLBeautifyParam.KEY);
        imageList = mParam.getImages();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.function_detail, mBL = new BLBeautifyFragment().newInstance(imageList.get(mParam.getIndex())));
        transaction.commit();
        //默认滤镜被选中
        onStickerClick();
    }

    protected void setListener() {
        //Adapter中设置贴纸的路径，默认支持的是assets目录下面的，其它目录需要自行修改Window
        mStickerAdapter.addStickerImages(STATIC_STICKER);
        mStickerAdapter.setOnStickerItemClick(mOnStickerItemClick);
    }

    private void onStickerClick() {
        mBL.stickerLocked(false);
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
