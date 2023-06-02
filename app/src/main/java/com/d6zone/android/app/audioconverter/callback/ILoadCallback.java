package com.d6zone.android.app.audioconverter.callback;

public interface ILoadCallback {
    
    void onSuccess();
    
    void onFailure(Exception error);
    
}