package com.d6zone.android.app.audioconverter.callback;

import java.io.File;

public interface IConvertCallback {
    
    void onSuccess(File convertedFile);
    
    void onFailure(Exception error);

}