package me.nereo.multi_image_selector.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by dmcBig on 2017/7/4.
 */

public class FolderImage implements Parcelable {

    public String name;

    public  int count;

    ArrayList<Image> medias=new ArrayList<>();

    protected FolderImage(Parcel in) {
        name = in.readString();
        count = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(count);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FolderImage> CREATOR = new Creator<FolderImage>() {
        @Override
        public FolderImage createFromParcel(Parcel in) {
            return new FolderImage(in);
        }

        @Override
        public FolderImage[] newArray(int size) {
            return new FolderImage[size];
        }
    };

    public void addMedias(Image media){
        medias.add(media);
    }

    public FolderImage(String name) {
        this.name=name;
    }

    public ArrayList<Image> getMedias(){
        return  this.medias;
    }


}
