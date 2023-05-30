package com.d6.android.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.d6.android.app.R;
import com.d6.android.app.models.push.PushInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;

public class MipushActivity extends UmengNotifyClickActivity {

    private static String TAG = MipushActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mipush);
    }

    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);  //此方法必须调用，否则无法统计打开数
        String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        Log.i(TAG, body);
        Message message = Message.obtain();
        message.obj = body;
        handler.sendMessage(message);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String body = (String) msg.obj;
            Gson gson = new GsonBuilder().create();
            PushInfo info = gson.fromJson(body, PushInfo.class);
            String url = info.getBody().getActivity();//获取你定义的那个参数
            if (url != null) {//跳转到过度页
                Intent intent = new Intent();
                intent.setClassName(MipushActivity.this, url);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
            finish();
        }
    };
}
