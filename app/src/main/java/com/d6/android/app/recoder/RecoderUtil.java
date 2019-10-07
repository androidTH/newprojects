package com.d6.android.app.recoder;

import android.graphics.Color;
import android.media.AudioFormat;
import android.os.Handler;

import com.d6.android.app.recoder.model.AudioChannel;
import com.d6.android.app.recoder.model.AudioSampleRate;
import com.d6.android.app.recoder.model.AudioSource;

public class RecoderUtil {
    private static final Handler HANDLER = new Handler();

    private RecoderUtil() {
    }

    public static void wait(int millis, Runnable callback){
        HANDLER.postDelayed(callback, millis);
    }

    public static omrecorder.AudioSource getMic(AudioSource source,
                                                AudioChannel channel,
                                                AudioSampleRate sampleRate) {
        return new omrecorder.AudioSource.Smart(
                source.getSource(),
                AudioFormat.ENCODING_PCM_16BIT,
                channel.getChannel(),
                sampleRate.getSampleRate());
    }

    public static String formatSeconds(int seconds) {
        return getTwoDecimalsValue(seconds / 3600) + ":"
                + getTwoDecimalsValue(seconds / 60) + ":"
                + getTwoDecimalsValue(seconds % 60);
    }

    private static String getTwoDecimalsValue(int value) {
        if (value >= 0 && value <= 9) {
            return "0" + value;
        } else {
            return value + "";
        }
    }

}