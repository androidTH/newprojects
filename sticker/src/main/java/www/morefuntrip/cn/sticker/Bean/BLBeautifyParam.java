package www.morefuntrip.cn.sticker.Bean;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/15.
 */

public class BLBeautifyParam implements Parcelable {
    public static final String KEY = "beautify_image";
    public static final String RESULT_KEY = "result_param";

    public static final int REQUEST_CODE_BEAUTIFY_IMAGE = 2;

    private List<String> images = new ArrayList<>();

    private int index;
    private String mImage;

    public BLBeautifyParam(){}

    public BLBeautifyParam(String image){
        this.mImage = image;
    }

    public BLBeautifyParam(List<String> images) {
        this.images = images;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getmImage() {
        return mImage == null ? "" : mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.images);
        dest.writeInt(this.index);
        dest.writeString(this.mImage);
    }

    protected BLBeautifyParam(Parcel in) {
        this.images = in.createStringArrayList();
        this.index = in.readInt();
        this.mImage = in.readString();
    }

    public static final Creator<BLBeautifyParam> CREATOR = new Creator<BLBeautifyParam>() {
        @Override
        public BLBeautifyParam createFromParcel(Parcel source) {
            return new BLBeautifyParam(source);
        }

        @Override
        public BLBeautifyParam[] newArray(int size) {
            return new BLBeautifyParam[size];
        }
    };
}
