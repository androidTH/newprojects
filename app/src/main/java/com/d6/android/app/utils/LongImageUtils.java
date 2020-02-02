package com.d6.android.app.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.d6.android.app.R;
import com.d6.android.app.adapters.CardChatManTagAdapter;
import com.d6.android.app.models.MyDate;
import com.d6.android.app.models.UserTag;
import com.d6.android.app.widget.CircleImageView;
import com.d6.android.app.widget.ScreenUtil;
import com.d6.android.app.widget.badge.DisplayUtil;
import com.d6.android.app.widget.frescohelper.FrescoUtils;
import com.d6.android.app.widget.frescohelper.IResult;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.utils.TimeUtils;

import static com.d6.android.app.utils.UtilKt.getLevelDrawableOfClassName;
import static com.d6.android.app.utils.UtilKt.getLocalUserHeadPic;

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

    public <T extends View> T getViewById(View view,@IdRes int resId){
        return view.findViewById(resId);
    }

    public Bitmap getScrollViewBitmap(ViewGroup viewGroup){
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            h += viewGroup.getChildAt(i).getHeight();
        }
        // 创建相应大小的bitmap
        bitmap = Bitmap.createBitmap(viewGroup.getMeasuredWidth(), h,Bitmap.Config.ARGB_4444);
        final Canvas canvas = new Canvas(bitmap);
        //获取当前主题背景颜色，设置canvas背景
        canvas.drawColor(Color.parseColor("#ffffff"));
        //画文字水印，不需要的可删去下面这行
//        drawTextToBitmap(viewGroup.getContext(), canvas, viewGroup.getMeasuredWidth(), h);
        //绘制viewGroup内容
        viewGroup.draw(canvas);
        return bitmap;
        //createWaterMaskImage为添加logo的代码，不需要的可直接返回bitmap
//        return createWaterMaskImage(bitmap, BitmapFactory.decodeResource(viewGroup.getResources(), R.drawable.icon_mark));

    }

    /**
     * 给图片添加水印
     *
     * @param context
     * @param canvas  画布
     * @param width   宽
     * @param height  高
     */
    public static void drawTextToBitmap(Context context, Canvas canvas, int width, int height) {
        //要添加的文字
        String logo = "皮卡搜";
        //新建画笔，默认style为实心
        Paint paint = new Paint();
        //设置颜色，颜色可用Color.parseColor("#6b99b9")代替
        paint.setColor(Color.parseColor("#ffffff"));
        //设置透明度
        paint.setAlpha(80);
        //抗锯齿
        paint.setAntiAlias(true);
        //画笔粗细大小
        paint.setTextSize((float) DisplayUtil.dp2px(context, 30));
        //保存当前画布状态
        canvas.save();
        //画布旋转-30度
        canvas.rotate(-30);
        //获取要添加文字的宽度
        float textWidth = paint.measureText(logo);
        int index = 0;
        //行循环，从高度为0开始，向下每隔80dp开始绘制文字
        for (int positionY = -DisplayUtil.dp2px(context, 30); positionY <= height; positionY +=  DisplayUtil.dp2px(context, 80)) {
            //设置每行文字开始绘制的位置,0.58是根据角度算出tan30°,后面的(index++ % 2) * textWidth是为了展示效果交错绘制
            float fromX = -0.58f * height + (index++ % 2) * textWidth;
            //列循环，从每行的开始位置开始，向右每隔2倍宽度的距离开始绘制（文字间距1倍宽度）
            for (float positionX = fromX; positionX < width; positionX += textWidth * 2) {
                //绘制文字
                canvas.drawText(logo, positionX, positionY, paint);
            }
        }
        //恢复画布状态
        canvas.restore();
    }

    /**
     * 添加logo水印
     *
     * @param src    原图片
     * @param logo   logo
     * @return 水印图片
     */
    public static Bitmap createWaterMaskImage(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }
        //原图宽高
        int w = src.getWidth();
        int h = src.getHeight();
        //logo宽高
        int ww = logo.getWidth();
        int wh = logo.getHeight();
        //创建一个和原图宽高一样的bitmap
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
        //创建
        Canvas canvas = new Canvas(newBitmap);
        //绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);
        //新建矩阵
        Matrix matrix = new Matrix();
        //对矩阵作缩放处理
        matrix.postScale(0.1f, 0.1f);
        //对矩阵作位置偏移，移动到底部中间的位置
        matrix.postTranslate(0.5f * w - 0.05f * ww, h - 0.1f * wh - 3);
        //将logo绘制到画布上并做矩阵变换
        canvas.drawBitmap(logo, matrix, null);
        // 保存状态
        canvas.save();// 保存
//        canvas.save(Canvas.ALL_SAVE_FLAG);// 保存
        // 恢复状态
        canvas.restore();
        return newBitmap;
    }

    public Bitmap getJoinGroupBitmap(Activity activity,String name,String groupId,List<Bitmap> mBitmaps){
        int itemWidth = ScreenUtil.getScreenWidth(activity);
        View mJoinGrooupLayout = activity.getLayoutInflater().inflate(R.layout.joingroup_share, null);
        CircleImageView groupheaderView = getViewById(mJoinGrooupLayout,R.id.groupheaderview_share);
        SimpleDraweeView iv_joingroup_qcode = getViewById(mJoinGrooupLayout,R.id.iv_joingroup_qcode__share);

        if(mBitmaps!=null&&mBitmaps.size()>0){
            groupheaderView.setImageBitmap(mBitmaps.get(1));
            iv_joingroup_qcode.setImageBitmap(mBitmaps.get(0));
        }
        TextView tv_groupname_share = getViewById(mJoinGrooupLayout,R.id.tv_groupname_share);
        tv_groupname_share.setText(name);
        TextView tv_groupnumber_share = getViewById(mJoinGrooupLayout,R.id.tv_groupnumber_share);
        tv_groupnumber_share.setText(groupId);
        mJoinGrooupLayout.measure(View.MeasureSpec.makeMeasureSpec(itemWidth , View.MeasureSpec.EXACTLY), 0);
        mJoinGrooupLayout.layout(0, 0, itemWidth, mJoinGrooupLayout.getMeasuredHeight());
        mJoinGrooupLayout.setDrawingCacheEnabled(true);
        mJoinGrooupLayout.buildDrawingCache();
        mJoinGrooupLayout.setDrawingCacheBackgroundColor(Color.WHITE);
        return mJoinGrooupLayout.getDrawingCache();
    }

    public Bitmap getInviteGoodFriendsBitmap(Activity activity,String name,String content,List<Bitmap> mBitmaps){
        int itemWidth = ScreenUtil.getScreenWidth(activity);
        View mShareFriendsLayout = activity.getLayoutInflater().inflate(R.layout.share_friends_layout, null);
        CircleImageView headerView = getViewById(mShareFriendsLayout,R.id.iv_invitationfriends_circleheadView);
        headerView.setImageBitmap(mBitmaps.get(0));
        TextView  tv_invitationfriends_username = getViewById(mShareFriendsLayout,R.id.tv_invitationfriends_username);
        TextView tv_invitationfriends_desc = getViewById(mShareFriendsLayout,R.id.tv_invitationfriends_desc);
        ImageView iv_invitationfriends_qcode = getViewById(mShareFriendsLayout,R.id.iv_invitationfriends_qcode);
        tv_invitationfriends_username.setText(name);
        tv_invitationfriends_desc.setText(content);
        iv_invitationfriends_qcode.setImageBitmap(mBitmaps.get(0));
        mShareFriendsLayout.measure(View.MeasureSpec.makeMeasureSpec(itemWidth , View.MeasureSpec.EXACTLY), 0);
        mShareFriendsLayout.layout(0, 0, itemWidth, mShareFriendsLayout.getMeasuredHeight());
        mShareFriendsLayout.setDrawingCacheEnabled(true);
        mShareFriendsLayout.buildDrawingCache();
        mShareFriendsLayout.setDrawingCacheBackgroundColor(Color.WHITE);
        return mShareFriendsLayout.getDrawingCache();
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
            mTags.add(new UserTag("身高 " + mData.getHeight(), R.mipmap.boy_stature_icon,2));
        }

        if (!TextUtils.isEmpty(mData.getWeight())) {
            mTags.add(new UserTag("体重 " + mData.getWeight(), R.mipmap.boy_weight_grayicon,2));
        }

        if (!TextUtils.isEmpty(mData.getXingzuo())) {
            mTags.add(new UserTag("星座 " + mData.getXingzuo(), R.mipmap.boy_profession_icon,2));
        }

        if(TextUtils.equals(type,"FindDateDetailActivity")){
            ll5.setVisibility(View.GONE);
            date_type.setText(String.format("觅约：%s", mData.getLooknumber()));
            tv_content.setText(mData.getLookfriendstand());
            if (!TextUtils.isEmpty(mData.getCity())) {
                mTags.add(new UserTag("地区 " + mData.getCity(), R.mipmap.boy_constellation_icon,2));
            }
        }else{
            ll5.setVisibility(View.VISIBLE);
            date_type.setText(String.format("%s：%s",mData.getSpeedStateStr(),mData.getSpeednumber()));
            tv_finddate_time.setText(String.format("%s时间",mData.getSpeedStateStr()));
            tv_datetype_desc.setText(String.format("%s说明",mData.getSpeedStateStr()));
            tv_finddate_showtime.setText(TimeUtilsKt.interval(mData.getCreateTime(),System.currentTimeMillis()));
            tv_content.setText(mData.getSpeedcontent());

            if (!TextUtils.isEmpty(mData.getSpeedcity())) {
                mTags.add(new UserTag("地区 " + mData.getSpeedcity(), R.mipmap.boy_constellation_icon,2));
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
