package com.d6.android.app.audioconverter.callback;

public interface ILoadCallback {
    
    void onSuccess();
    
    void onFailure(Exception error);
    
}