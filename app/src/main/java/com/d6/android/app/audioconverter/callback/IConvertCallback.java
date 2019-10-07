package com.d6.android.app.audioconverter.callback;

import java.io.File;

public interface IConvertCallback {
    
    void onSuccess(File convertedFile);
    
    void onFailure(Exception error);

}