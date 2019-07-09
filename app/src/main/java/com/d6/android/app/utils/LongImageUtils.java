package com.d6.android.app.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.d6.android.app.R;
import com.d6.android.app.adapters.CardChatManTagAdapter;
import com.d6.android.app.models.MyDate;
import com.d6.android.app.models.UserTag;
import com.d6.android.app.widget.ScreenUtil;
import com.d6.android.app.widget.frescohelper.FrescoUtils;
import com.d6.android.app.widget.frescohelper.IResult;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.utils.TimeUtils;

import static com.d6.android.app.utils.UtilKt.getLevelDrawableOfClassName;

/**
 * create by 893007592@qq.com
 * on 2018/8/2 16:40
 * Description: 图片处理工具类
 */
public class LongImageUtils {

    private static LongImageUtils instance;
    public static LongImageUtils getInstance(){
         if(instance==null){
             instance =new  LongImageUtils();
         }
         return instance;
    }

    public  String saveImage(Activity mActivity, Bitmap bitmap, String imgName) {

        String dir = Environment.getExternalStorageDirectory().getPath() + "/长图/";
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }

        String imagePath = dir + imgName + ".jpg";
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            try {
                imageFile.createNewFile();
            } catch (IOException e) {
                Log.d("test", e.getMessage());
            }
        }

        saveBitmap(bitmap, imagePath);
        insertImage(mActivity, imagePath);

        return imagePath;
    }

    /**
     * 把bitmap保存到文件中
     *
     * @param bitmap
     * @param filePath
     */
    public void saveBitmap(Bitmap bitmap, String filePath) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            Log.d("test", e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                Log.d("test", e.getMessage());
            }
        }
    }

    private MediaScannerConnection sMediaScannerConnection;

    public void insertImage(Context context, final String filePath) {
        sMediaScannerConnection = new MediaScannerConnection(context, new MediaScannerConnection.MediaScannerConnectionClient() {
            @Override
            public void onMediaScannerConnected() {
                Log.d("test", "scannerConnected, scan local path:" + filePath);
                sMediaScannerConnection.scanFile(filePath, "image/*");
            }

            @Override
            public void onScanCompleted(String path, Uri uri) {
                Log.d("test", "scan complete");
                sMediaScannerConnection.disconnect();
            }
        });
        sMediaScannerConnection.connect();
    }


    private static ArrayList<UserTag> mTags =new ArrayList<UserTag>();
    /**
     * 生成长图 (列表之类的)bitmap
     * <p>
     * 绘制一张长图
     *
     * @param activity
     * @return
     */
    public Bitmap getRecyclerItemsToBitmap(Activity activity,String type, MyDate mData, List<Bitmap> mPicsList) {

        int allItemsHeight = 0;
        int itemWidth = ScreenUtil.getScreenWidth(activity);
        List<Bitmap> bitmaps = new ArrayList<>();

        //如果有头部就写上
        View headerView = activity.getLayoutInflater().inflate(R.layout.longpic_header, null);
        RelativeLayout ll5 = headerView.findViewById(R.id.ll5);
        TextView tv_finddate_time = headerView.findViewById(R.id.tv_finddate_time);
        TextView tv_finddate_showtime = headerView.findViewById(R.id.tv_finddate_showtime);
        TextView tv_datetype_desc = headerView.findViewById(R.id.tv_datetype_desc);
        TextView date_type = headerView.findViewById(R.id.date_type);
        TextView tv_sex = headerView.findViewById(R.id.tv_sex);
        TextView tv_vip = headerView.findViewById(R.id.tv_vip);
        ImageView img_auther = headerView.findViewById(R.id.img_auther);
        RecyclerView rv_tags = headerView.findViewById(R.id.rv_tags);
        TextView tv_job = headerView.findViewById(R.id.tv_job);
        TextView tv_zuojia = headerView.findViewById(R.id.tv_zuojia);
        TextView tv_aihao = headerView.findViewById(R.id.tv_aihao);
        TextView tv_content = headerView.findViewById(R.id.tv_content);

        tv_sex.setText(mData.getAge());

        mTags.clear();
        if (!TextUtils.isEmpty(mData.getHeight())) {
            mTags.add(new UserTag("身高 " + mData.getHeight(), R.mipmap.boy_stature_icon));
        }

        if (!TextUtils.isEmpty(mData.getWeight())) {
            mTags.add(new UserTag("体重 " + mData.getWeight(), R.mipmap.boy_weight_grayicon));
        }

//        if (!TextUtils.isEmpty(mData.job)) {
//            mTags.add(UserTag("星座 " + mData.job!!, R.mipmap.boy_profession_icon))
//        }

        if(TextUtils.equals(type,"FindDateDetailActivity")){
            ll5.setVisibility(View.GONE);
            date_type.setText(String.format("觅约：%s", mData.getLooknumber()));
            tv_content.setText(mData.getLookfriendstand());
            if (!TextUtils.isEmpty(mData.getCity())) {
                mTags.add(new UserTag("地区 " + mData.getCity(), R.mipmap.boy_constellation_icon));
            }
        }else{
            ll5.setVisibility(View.VISIBLE);
            date_type.setText(String.format("%s：%s",mData.getSpeedStateStr(),mData.getSpeednumber()));
            tv_finddate_time.setText(String.format("%s时间",mData.getSpeedStateStr()));
            tv_datetype_desc.setText(String.format("%s说明",mData.getSpeedStateStr()));
            tv_finddate_showtime.setText(TimeUtilsKt.interval(mData.getCreateTime(),System.currentTimeMillis()));
            tv_content.setText(mData.getSpeedcontent());

            if (!TextUtils.isEmpty(mData.getSpeedcity())) {
                mTags.add(new UserTag("地区 " + mData.getSpeedcity(), R.mipmap.boy_constellation_icon));
            }

        }

        rv_tags.setHasFixedSize(true);
        rv_tags.setLayoutManager(new GridLayoutManager(activity, 2));//FlexboxLayoutManager(this)
        rv_tags.setAdapter(new CardChatManTagAdapter(mTags));
        if (!TextUtils.isEmpty(mData.getJob())) {
            AppUtils.Companion.setTvTag(activity, "职业 "+mData.getJob(), 0, 2, tv_job);
        } else {
            tv_job.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(mData.getZuojia())) {
            AppUtils.Companion.setTvTag(activity, "座驾 "+mData.getZuojia(), 0, 2, tv_zuojia);
        } else {
            tv_zuojia.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(mData.getHobbit())) {
            String[] mHobbies = mData.getHobbit().replace("#", ",").split(",");
            StringBuffer sb =new  StringBuffer();
            sb.append("爱好 ");
            if (mHobbies != null) {
                for (int i=0;i<mHobbies.length;i++) {
                    sb.append(mHobbies[i]);
                }
                AppUtils.Companion.setTvTag(activity, sb.toString(), 0, 2, tv_aihao);
            }
        } else {
            tv_aihao.setVisibility(View.GONE);
        }

        if (TextUtils.equals(mData.getSex(), "1")) {
            tv_sex.setSelected(false);
            tv_vip.setVisibility(View.VISIBLE);
            img_auther.setVisibility(View.GONE);
            tv_vip.setBackground(getLevelDrawableOfClassName(mData.getClassesname(),activity));
        } else {
            tv_sex.setSelected(true);
            tv_vip.setVisibility(View.GONE);
            img_auther.setVisibility(View.VISIBLE);
            if (TextUtils.equals("1", mData.getScreen())) {
                img_auther.setBackground(ContextCompat.getDrawable(activity,R.mipmap.video_big));
            } else if(TextUtils.equals("0", mData.getScreen())){
                img_auther.setVisibility(View.GONE);
            }else if(TextUtils.equals("3",mData.getScreen())){
                img_auther.setVisibility( View.GONE);
                img_auther.setBackground(ContextCompat.getDrawable(activity,R.mipmap.renzheng_big));
            }
        }

        headerView.measure(View.MeasureSpec.makeMeasureSpec(itemWidth, View.MeasureSpec.EXACTLY), 0);
        headerView.layout(0, 0, itemWidth, headerView.getMeasuredHeight());
        headerView.setDrawingCacheEnabled(true);
        headerView.buildDrawingCache();
        bitmaps.add(headerView.getDrawingCache());
        allItemsHeight += headerView.getMeasuredHeight();

        for (int i = 0; i < mPicsList.size(); i++) {
//            FileBinaryResource resource = (FileBinaryResource) Fresco.getImagePipelineFactory().getMainFileCache().getResource(new SimpleCacheKey(mPicsList.get(i)));
//            File file = resource.getFile();
//            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            View childView = LayoutInflater.from(activity).inflate(R.layout.longpic_content, null);
            ImageView imageView = childView.findViewById(R.id.imageView);
            imageView.setImageBitmap(mPicsList.get(i));
            childView.measure(View.MeasureSpec.makeMeasureSpec(itemWidth, View.MeasureSpec.EXACTLY), 0);
            childView.layout(0, 0, itemWidth, childView.getMeasuredHeight());
            childView.setDrawingCacheEnabled(true);
            childView.buildDrawingCache();
            bitmaps.add(childView.getDrawingCache());
            allItemsHeight += childView.getMeasuredHeight();
        }

        //如果有尾部就写上
        View footerView = activity.getLayoutInflater().inflate(R.layout.longpic_footer, null);
        footerView.measure(View.MeasureSpec.makeMeasureSpec(itemWidth, View.MeasureSpec.EXACTLY), 0);
        footerView.layout(0, 0, itemWidth, footerView.getMeasuredHeight());
        footerView.setDrawingCacheEnabled(true);
        footerView.buildDrawingCache();
        bitmaps.add(footerView.getDrawingCache());
        allItemsHeight += footerView.getMeasuredHeight();

        Bitmap bigBitmap = Bitmap.createBitmap(itemWidth, allItemsHeight, Bitmap.Config.ARGB_4444);
        Canvas bigCanvas = new Canvas(bigBitmap);

        bigCanvas.drawColor(ContextCompat.getColor(activity, R.color.white));
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        int iHeight = 0;
        for (int i = 0; i < bitmaps.size(); i++) {
            Bitmap bmp = bitmaps.get(i);

            bigCanvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight += bmp.getHeight();
            bmp.recycle();
        }
        for(int i=0;i<mPicsList.size();i++){
            Bitmap bmp = mPicsList.get(i);
            bmp.recycle();
        }

        if(mDoLongPicSuccess!=null){
            mDoLongPicSuccess.onLongPicSuccess();
        }
        return bigBitmap;
    }

    public void setDoLongPicSuccess(DoLongPicSuccess dolongpicSuccess){
        mDoLongPicSuccess = dolongpicSuccess;
    }

    private DoLongPicSuccess mDoLongPicSuccess;
    interface DoLongPicSuccess{
        public void onLongPicSuccess();
    }
}
