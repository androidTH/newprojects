package me.nereo.multi_image_selector.utils;

import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间处理工具
 * Created by Nereo on 2015/4/8.
 */
public class TimeUtils {

    public static String timeFormat(long timeMillis, String pattern){
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
        return format.format(new Date(timeMillis));
    }

    public static String formatPhotoDate(long time){
        return timeFormat(time, "yyyy-MM-dd");
    }

    public static String formatPhotoDate(String path){
        File file = new File(path);
        if(file.exists()){
            long time = file.lastModified();
            return formatPhotoDate(time);
        }
        return "1970-01-01";
    }

    public static long timeToMinute(String path) {
        MediaMetadataRetriever retr = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            retr = new MediaMetadataRetriever();
        }
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            long second = 0;
            if (file.exists() && file.length() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                    retr.setDataSource(path);
                    second = Long.valueOf(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
                }
            }
            return second;
//            if (second < 60) {
//                return "00:" + oneToTwo(second);
//            } else if (second < 3600) {
//                return oneToTwo(second / 60) + ":" + oneToTwo(second % 60);
//            } else {
//                return oneToTwo(second / 3600) + ":" + oneToTwo((second % 3600) / 60) + ":" + oneToTwo((second % 3600) % 60);
//            }
        }else {
            return 0;
        }
    }

    public static String setTimeToMinute(long second){
        if (second < 60) {
            return "00:" + oneToTwo(second);
        } else if (second < 3600) {
            return oneToTwo(second / 60) + ":" + oneToTwo(second % 60);
        } else {
            return oneToTwo(second / 3600) + ":" + oneToTwo((second % 3600) / 60) + ":" + oneToTwo((second % 3600) % 60);
        }
    }

    private static String oneToTwo(long num) {
        if (num < 10) {
            return "0" + num;
        } else {
            return num + "";
        }
    }

}
