package me.nereo.multi_image_selector.adapter;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.R;
import me.nereo.multi_image_selector.bean.Image;
import me.nereo.multi_image_selector.utils.TimeUtils;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

/**
 *
 */
public class GridImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;
    private boolean showCamera = true;
    private boolean showSelectIndicator = true;

    private List<Image> mImages = new ArrayList<>();
    private List<Image> mSelectedImages = new ArrayList<>();
    final int mGridWidth;

    public GridImageAdapter(Context context, boolean showCamera, int column){
        this.showCamera = showCamera;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        }else{
            width = wm.getDefaultDisplay().getWidth();
        }
        mGridWidth = width / column;

    }

    /**
     * ItemClick的回调接口
     * @author zhy
     *
     */
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }
    private OnItemClickListener mOnItemClickListener;

    /**
     * 设置OnItemClickListener
     * @param mOnItemClickListener OnItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        RecyclerView.ViewHolder viewHolder ;
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_CAMERA){
            View view = mInflater.inflate(R.layout.list_item_camera, parent, false);
            return new CameraViewHolder(view);
        }else {
            View view = mInflater.inflate(R.layout.list_item_image, parent, false);
            return new ViewHolder(view);
        }

//        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
//        System.err.println("11111----->"+position);
        //如果设置了回调，则设置点击事件
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
//                    if ()
                    mOnItemClickListener.onItemClick(holder.itemView,position);
                }
            });

        }
        if (mImages!=null && mImages.size()>0 ) {
            if (showCamera && position == 0){
                return;
            }

            Image image = mImages.get(showCamera?position-1:position);
            bindData((ViewHolder) holder, image);
        }
    }

    public Image getImage(int position){
        if (mImages == null || position>= mImages.size()){
            return null;
        }
        return mImages.get(position);
    }

    private void bindData(GridImageAdapter.ViewHolder holder,Image data){
        if(data == null) return;
        SimpleDraweeView image = holder.bind(R.id.image);
        ImageView indicator= holder.bind(R.id.checkmark);
        View mask= holder.bind(R.id.mask);

        RelativeLayout mVideoInfo = holder.bind(R.id.video_info);
        TextView mTvVideoTime = holder.bind(R.id.tv_videotime);
        // 处理单选和多选状态
        if(showSelectIndicator){
            indicator.setVisibility(View.VISIBLE);
            if(mSelectedImages.contains(data)){
                // 设置选中状态
                indicator.setImageResource(R.drawable.btn_selected);
                mask.setVisibility(View.VISIBLE);
            }else{
                // 未选择
                indicator.setImageResource(R.drawable.btn_unselected);
                mask.setVisibility(View.GONE);
            }
        }else{
            indicator.setVisibility(View.GONE);
        }

        if(data.mediaType == MEDIA_TYPE_VIDEO){
            mVideoInfo.setVisibility(View.VISIBLE);
//            mTvVideoTime.setText(TimeUtils.timeToMinute(data.path));
        }else{
            mVideoInfo.setVisibility(View.GONE);
        }

        Uri uri = Uri.parse("file://"+data.path);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setLocalThumbnailPreviewsEnabled(true)
                .setResizeOptions(new ResizeOptions(mGridWidth,mGridWidth))
                .setImageDecodeOptions(ImageDecodeOptions.newBuilder()
                        .setUseLastFrameForPreview(true)
                        .build())
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(image.getController())
                .build();
        image.setController(controller);
    }

    @Override
    public int getItemViewType(int position) {
        if(showCamera){
            return position==0 ? TYPE_CAMERA : TYPE_NORMAL;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return showCamera ? mImages.size()+1 : mImages.size();
    }

    /**
     * 显示选择指示器
     * @param b
     */
    public void showSelectIndicator(boolean b) {
        showSelectIndicator = b;
    }

    public void setShowCamera(boolean b){
        if(showCamera == b) return;

        showCamera = b;
        notifyDataSetChanged();
    }

    public boolean isShowCamera(){
        return showCamera;
    }

    /**
     * 选择某个图片，改变选择状态
     * @param image
     */
    public void select(Image image) {
        if(mSelectedImages.contains(image)){
            mSelectedImages.remove(image);
        }else{
            mSelectedImages.add(image);
        }
        notifyDataSetChanged();
    }

    /**
     * 通过图片路径设置默认选择
     * @param resultList
     */
    public void setDefaultSelected(ArrayList<String> resultList) {
        for(String path : resultList){
            Image image = getImageByPath(path);
            if(image != null){
                mSelectedImages.add(image);
            }
        }
        if(mSelectedImages.size() > 0){
            notifyDataSetChanged();
        }
    }

    private Image getImageByPath(String path){
        if(mImages != null && mImages.size()>0){
            for(Image image : mImages){
                if(image.path.equalsIgnoreCase(path)){
                    return image;
                }
            }
        }
        return null;
    }

    /**
     * 设置数据集
     * @param images
     */
    public void setData(List<Image> images) {
//        System.err.println("set data --->"+images);
        mSelectedImages.clear();

        if(images != null && images.size()>0){
            mImages.clear();
            mImages.addAll(images);
        }else{
            mImages.clear();
        }
        notifyDataSetChanged();
    }

    public static class CameraViewHolder extends RecyclerView.ViewHolder{

        public CameraViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View mConvertView;

        public ViewHolder(View itemView) {
            super(itemView);
            mConvertView = itemView;
        }

        @SuppressWarnings("unchecked")
        public <T extends View> T bind(int viewId) {// 通过ViewId得到View
            SparseArray<View> viewHolder = (SparseArray<View>) mConvertView
                    .getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<>();
                mConvertView.setTag(viewHolder);
            }

            View childView = viewHolder.get(viewId);
            if (childView == null) {
                childView = mConvertView.findViewById(viewId);
                viewHolder.put(viewId, childView);
            }
            return (T) childView;

        }
    }
}
