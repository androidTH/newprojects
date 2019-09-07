package com.d6.android.app.recoder;

/**
 * author : jinjiarui
 * time   : 2019/09/07
 * desc   :
 * version:
 */
public interface AudioPlayListener {
    void onPrepared(int var1);
    void onInfo(int var1, int var2);
    void onCompletion();
    void onBufferingUpdate(int var1);
}
