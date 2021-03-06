package com.d6.android.app.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.d6.android.app.recoder.AudioPlayListener;
import java.io.IOException;

/**
 * author : jinjiarui
 * time   : 2019/09/06
 * desc   :
 * version:
 */
public class AudioPlayUtils {

    private static final String TAG = AudioPlayUtils.class.getSimpleName();


    private MediaPlayer mMediaPlayer;

    private Context mContext;
    private boolean mIsStopped = false;
    private String mAudioPath;
    private boolean IsStopOrStart = false;

    private TelephonyManager mTelephonyManager;
    private PhoneStateListener mPhoneStateListener;
    public static final int MEDIA_INFO_STATE_CHANGED_PAUSED = 30008;


    public AudioPlayUtils(Context context){
        this.mContext = context;
        this.mAudioPath = "";
        initAudio();
    }

    private void initAudio(){
//        mAVOptions = new AVOptions();
//        mAVOptions.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
//        mAVOptions.setInteger(AVOptions.KEY_MEDIACODEC, 0);
//        mAVOptions.setInteger(AVOptions.KEY_START_POSITION, 0* 1000);

        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        startTelephonyListener();
    }

    public void singleAudioPlay(String audioPath){
        if(!TextUtils.equals(mAudioPath,audioPath)){
            this.mAudioPath = audioPath;
            onClickStop();
            mIsStopped = false;
            prepare();
            onClickPlay();
            Log.i("AudioPlay","path="+audioPath);
        }else{
            Log.i("AudioPlay",mAudioPath+"=audiopath");
            onTogglePlay();
        }
    }

    public void setAudioPath(String audioPath){
         if(!TextUtils.equals(mAudioPath,audioPath)){
             this.mAudioPath = audioPath;
             prepare();
         }
    }

    public void prepare() {
        try {
            if(!TextUtils.isEmpty(mAudioPath)){
                if (mMediaPlayer == null) {
                    mMediaPlayer = new MediaPlayer();
                    mMediaPlayer.setLooping(false);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
                    mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
                    mMediaPlayer.setOnErrorListener(mOnErrorListener);
                    mMediaPlayer.setOnInfoListener(mOnInfoListener);
                    mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
                    mMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
                }
                mMediaPlayer.setDataSource(mAudioPath);
                mMediaPlayer.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean onTogglePlay(){
        if(mMediaPlayer!=null&&mMediaPlayer.isPlaying()){
            onClickPause();
            IsStopOrStart = false;
        }else{
            onClickPlay();
            IsStopOrStart = true;
        }
        return IsStopOrStart;
    }

    /**
     * 播放
     */
    public void onClickPlay() {
        if (mIsStopped) {
            prepare();
            mMediaPlayer.start();
        } else {
            onClickResume();
        }
    }

    /**
     * 暂停
     */
    public void onClickPause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            if(mAudioListener!=null){
                mAudioListener.onInfo(MEDIA_INFO_STATE_CHANGED_PAUSED,-1);
            }
        }
    }

    /**
     * 重新播放
     */
    public void onClickResume() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    /**
     * 停止时再此播放需要调用onClickPlay()方法
     * 因为播放气已经release了
     */
    public void onClickStop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        mIsStopped = true;
        mMediaPlayer = null;
    }

    /**
     * 销毁
     */
    public void onDestoryAudio(){
        stopTelephonyListener();
        release();
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(null);
    }

    /**
     * 释放
     */
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mAudioPath ="";
        }
    }

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.i(TAG, "On Prepared !"+ mp.getDuration());
            mIsStopped = false;
            if(mAudioListener!=null){
                mAudioListener.onPrepared(mp.getDuration());
            }
        }

//        @Override
//        public void onPrepared(int preparedTime) {
//            Log.i(TAG, "On Prepared !");
////            mMediaPlayer.start();
//            mIsStopped = false;
//            if(mAudioListener!=null){
//                mAudioListener.onPrepared(preparedTime);
//            }
//        }
    };

    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            Log.i(TAG, "OnInfo, what = " + what + ", extra = " + extra);
            if(mAudioListener!=null){
                mAudioListener.onInfo(what,extra);
            }
            return false;
        }
//        @Override
//        public void onInfo(int what, int extra) {
//            Log.i(TAG, "OnInfo, what = " + what + ", extra = " + extra);
//            if(mAudioListener!=null){
//                mAudioListener.onInfo(what,extra);
//            }
//        }
    };

    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            Log.d(TAG, "onBufferingUpdate: " + percent + "%");
            if(mAudioListener!=null){
                mAudioListener.onBufferingUpdate(percent);
            }
        }

//        @Override
//        public void onBufferingUpdate(int percent) {
//            Log.d(TAG, "onBufferingUpdate: " + percent + "%");
//            if(mAudioListener!=null){
//                mAudioListener.onBufferingUpdate(percent);
//            }
//        }
    };

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if(mAudioListener!=null){
                mAudioListener.onCompletion();
            }
            Log.d(TAG, "Play Completed !");
            onClickStop();
        }

//        @Override
//        public void onCompletion() {
//            if(mAudioListener!=null){
//                mAudioListener.onCompletion();
//            }
//            Log.d(TAG, "Play Completed !");
//            onClickStop();
//        }
    };

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.e(TAG, what+"Error happened, errorCode = "+extra);
            switch (what) {
                case MediaPlayer.MEDIA_ERROR_IO:
                    /**
                     * SDK will do reconnecting automatically
                     */
                    Log.d(TAG, "IO Error !");
                    return false;
                case MediaPlayer.MEDIA_INFO_VIDEO_NOT_PLAYING:
                    Log.d(TAG, "failed to open player !");
                    break;
                case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                    Log.d(TAG, "failed to seek !");
                    break;
                default:
                    Log.d(TAG, "unknown error !");
                    break;
            }
            return true;
        }

//        @Override
//        public boolean onError(int errorCode) {
//            Log.e(TAG, "Error happened, errorCode = " + errorCode);
//            switch (errorCode) {
//                case PLOnErrorListener.ERROR_CODE_IO_ERROR:
//                    /**
//                     * SDK will do reconnecting automatically
//                     */
//                    Log.d(TAG, "IO Error !");
//                    return false;
//                case PLOnErrorListener.ERROR_CODE_OPEN_FAILED:
//                    Log.d(TAG, "failed to open player !");
//                    break;
//                case PLOnErrorListener.ERROR_CODE_SEEK_FAILED:
//                    Log.d(TAG, "failed to seek !");
//                    break;
//                default:
//                    Log.d(TAG, "unknown error !");
//                    break;
//            }
//            return true;
//        }
    };


    private void startTelephonyListener() {
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyManager == null) {
            Log.e(TAG, "Failed to initialize TelephonyManager!!!");
            return;
        }

        mPhoneStateListener = new PhoneStateListener() {

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.d(TAG, "PhoneStateListener: CALL_STATE_IDLE");
                        if (mMediaPlayer != null) {
                            mMediaPlayer.start();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.d(TAG, "PhoneStateListener: CALL_STATE_OFFHOOK");
                        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                            mMediaPlayer.pause();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d(TAG, "PhoneStateListener: CALL_STATE_RINGING: " + incomingNumber);
                        break;
                }
            }
        };

        try {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopTelephonyListener() {
        if (mTelephonyManager != null && mPhoneStateListener != null) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
            mTelephonyManager = null;
            mPhoneStateListener = null;
        }
    }

    private AudioPlayListener mAudioListener;

    public void setmAudioListener(AudioPlayListener mAudioListener) {
        this.mAudioListener = mAudioListener;
    }
}
