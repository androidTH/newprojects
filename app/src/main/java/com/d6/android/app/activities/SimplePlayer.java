package com.d6.android.app.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.d6.android.app.R;
import com.d6.android.app.widget.CustomJzvd.MyJzvdStd;
import com.d6.android.app.widget.photodrag.OnDragListener;
import com.d6.android.app.widget.photodrag.OnPhotoDragListener;
import com.d6.android.app.widget.photodrag.PhotoDragDelegate;
import com.d6.android.app.widget.photodrag.PhotoDragHelper;
import com.d6.android.app.widget.photodrag.PhotoDragRelativeLayout;

import cn.jzvd.JZDataSource;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

import static com.d6.android.app.utils.UtilKt.getProxyUrl;


public class SimplePlayer extends VideoPlayerBaseActivity implements View.OnClickListener {

    private static final String TAG = SimplePlayer.class.getSimpleName();
    private MyJzvdStd myJzvdStd;
    private PhotoDragRelativeLayout mPhotoDragRL;
    private RelativeLayout mRelativePlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_play);
        myJzvdStd = findViewById(R.id.jz_videostd);
        mPhotoDragRL = findViewById(R.id.root_videodrag);
        mRelativePlay = findViewById(R.id.activity_play);

        try{
            String videoPath = getIntent().getStringExtra("videoPath");
            String videoType = getIntent().getStringExtra("videoType");
            if(TextUtils.equals("1",videoType)){
                String proxyUrl =  getProxyUrl(this,videoPath);
                myJzvdStd.setUp(proxyUrl,"", JzvdStd.SCREEN_NORMAL,true);
            }else{
                myJzvdStd.setUp(videoPath,"", JzvdStd.SCREEN_NORMAL,true);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        mPhotoDragRL.setDragListener(new PhotoDragHelper().setOnDragListener(new PhotoDragHelper.OnDragListener(){

            @Override
            public void onAnimationEnd(boolean mSlop) {
                if (mSlop) {
                    onBackPressed();
                }
            }

            @Override
            public View getDragView() {
                return mRelativePlay;
            }

            @Override
            public void onAlpha(float alpha) {
                mPhotoDragRL.setAlpha(alpha);
            }
        }));

        myJzvdStd.setmOnBlankView(new MyJzvdStd.onBlankView() {
            @Override
            public void onClosePlayer() {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        Jzvd.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        myJzvdStd.startVideo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.goOnPlayOnPause();
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.img_fade_in, R.anim.img_fade_out);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.activity_play){
            onBackPressed();
        }
    }
}
