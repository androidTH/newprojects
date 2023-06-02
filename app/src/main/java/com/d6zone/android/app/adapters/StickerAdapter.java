package com.d6zone.android.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.d6zone.android.app.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.rong.imageloader.cache.disc.impl.UnlimitedDiskCache;
import io.rong.imageloader.cache.disc.naming.HashCodeFileNameGenerator;
import io.rong.imageloader.cache.memory.impl.LruMemoryCache;
import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.ImageLoaderConfiguration;
import io.rong.imageloader.core.assist.QueueProcessingType;
import io.rong.imageloader.core.decode.BaseImageDecoder;
import io.rong.imageloader.core.download.BaseImageDownloader;
import io.rong.imageloader.utils.StorageUtils;
import www.morefuntrip.cn.sticker.Bean.BLStickerInfo;

/**
 * 贴图列表Adapter
 */
public class StickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String DEFAULT_PATH = "assets://";
    public static  int key_data= R.id.sticker_key_data;
    public static  int key_position= R.id.sticker_key_position;

    public DisplayImageOptions mImageOption = new DisplayImageOptions.Builder()
            .cacheInMemory(true).showImageOnLoading(null)
            .build();// 下载图片显示
    private Context mContext;
    private ImageClick mImageClick = new ImageClick();
    private List<BLStickerInfo> mImagePathList = new ArrayList<BLStickerInfo>();// 图片路径列表
    private OnStickerItemClick mOnStickerItemClick;

    public StickerAdapter(Context context) {
        super();
        mContext = context;
        initImageLoader();
    }

    public void setOnStickerItemClick(OnStickerItemClick itemClick) {
        mOnStickerItemClick = itemClick;
    }

    public class ImageHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public ImageHolder(View itemView) {
            super(itemView);
            this.image = (ImageView) itemView.findViewById(R.id.sticker_img);
        }
    }

    @Override
    public int getItemCount() {
        return mImagePathList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.sticker_item, parent, false);
        ImageHolder holer = new ImageHolder(v);
        return holer;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageHolder imageHoler = (ImageHolder) holder;
        BLStickerInfo stickerInfo = mImagePathList.get(position);
        //此处默认加载assets下面的文件，如果需要加载其它资源文件，需要修改此处
        ImageLoader.getInstance().displayImage(DEFAULT_PATH + stickerInfo.getUrl(),
                imageHoler.image, mImageOption);
        imageHoler.image.setTag(key_data, stickerInfo);
        imageHoler.image.setTag(key_position, position);
        imageHoler.image.setOnClickListener(mImageClick);
    }

    public void addStickerImages(String folderPath) {
        mImagePathList.clear();
        try {
            String[] files = mContext.getAssets()
                    .list(folderPath);
            for (String name : files) {
                mImagePathList.add(new BLStickerInfo(0,folderPath + File.separator + name));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.notifyDataSetChanged();
    }

    private void initImageLoader() {
        File cacheDir = StorageUtils.getCacheDirectory(mContext);
        int MAXMEMONRY = (int) (Runtime.getRuntime().maxMemory());
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext).memoryCacheExtraOptions(480, 800).defaultDisplayImageOptions(defaultOptions)
                .diskCacheExtraOptions(480, 800, null).threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(MAXMEMONRY / 5))
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(mContext)) // default
                .imageDecoder(new BaseImageDecoder(false)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()).build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * 选择贴图
     */
    private final class ImageClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            BLStickerInfo data = (BLStickerInfo) v.getTag(key_data);
            int pos = (int) v.getTag(key_position);
            if (mOnStickerItemClick != null) {
                mOnStickerItemClick.selectedStickerItem(data,pos);
            }
        }
    }

    public interface OnStickerItemClick {
        void selectedStickerItem(BLStickerInfo path, int position);
    }
}
