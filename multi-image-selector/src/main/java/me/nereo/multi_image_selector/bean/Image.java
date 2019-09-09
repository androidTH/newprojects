package me.nereo.multi_image_selector.bean;

import me.nereo.multi_image_selector.utils.TimeUtils;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static me.nereo.multi_image_selector.utils.TimeUtils.setTimeToMinute;
import static me.nereo.multi_image_selector.utils.TimeUtils.timeToMinute;

/**
 * 图片实体
 * Created by Nereo on 2015/4/7.
 */
public class Image {
    public String path;
    public String name;
    public long time;
    public int mediaType;
    public long mSize;
    public long duration;

    public Image(String path, String name, long time,int mimeType,long size){
        this.path = path;
        this.name = name;
        this.time = time;
        this.mediaType = mimeType;
        this.mSize = size;
        if(mediaType==MEDIA_TYPE_VIDEO){
            duration = timeToMinute(path);
        }
    }

    @Override
    public boolean equals(Object o) {
        try {
            Image other = (Image) o;
            return this.path.equalsIgnoreCase(other.path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }

    public long getGuration(){
       return duration;
    }

    public String timeToMinutes(){
        return setTimeToMinute(getGuration());
    }
}
