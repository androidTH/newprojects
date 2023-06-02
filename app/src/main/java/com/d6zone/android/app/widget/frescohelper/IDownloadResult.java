package com.d6zone.android.app.widget.frescohelper;

import android.content.Context;

/**
 * author : jinjiarui
 * time   : 2019/07/09
 * desc   :
 * version:
 */
public abstract class IDownloadResult implements IResult<String> {

    private String mFilePath;

    public IDownloadResult(String filePath) {
        this.mFilePath = filePath;
    }

    public IDownloadResult(Context context) {
//        this.mFilePath = ImageFileUtils.getImageDownloadPath(context);
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void onProgress(int progress) {

    }

    @Override
    public abstract void onResult(String filePath);
}
