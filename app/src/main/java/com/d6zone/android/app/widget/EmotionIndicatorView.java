package com.d6zone.android.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.d6zone.android.app.R;
import com.d6zone.android.app.widget.badge.DisplayUtil;

import java.util.ArrayList;

/**
 * Created by SiberiaDante
 * Describe: 自定义表情底部指示器
 * Time: 2017/6/26
 * GitHub: https://github.com/SiberiaDante
 * 博客园： http://www.cnblogs.com/shen-hua/
 */

public class EmotionIndicatorView extends LinearLayout {

    private Context mContext;
    private ArrayList<ImageView> mImageViews ;//所有指示器集合
    private int size = 6;
    private int marginSize=15;
    private int pointSize ;//指示器的大小
    private int marginLeft;//间距

    public EmotionIndicatorView(Context context) {
        this(context,null);
    }

    public EmotionIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmotionIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        pointSize= DisplayUtil.dp2px(context,size);
        marginLeft= DisplayUtil.dp2px(context,marginSize);
    }

    /**
     * 初始化指示器
     * @param count 指示器的数量
     */
    public void initIndicator(int count){
        mImageViews = new ArrayList<>();
        this.removeAllViews();
        LayoutParams lp ;
        for (int i = 0 ; i<count ; i++){
            ImageView pointView = new ImageView(getContext());
            pointView.setPadding(5, 0, 5, 0);
            if (i == 0){
                pointView.setImageResource(R.drawable.shape_bg_indicator_point_select);
            }else{
                pointView.setImageResource(R.drawable.shape_bg_indicator_point_nomal);
            }
            mImageViews.add(pointView);
            this.addView(pointView);
        }
    }

    /**
     * 页面移动时切换指示器
     */
    public void playByStartPointToNext(int startPosition,int nextPosition){
        if(startPosition < 0 || nextPosition < 0 || nextPosition == startPosition){
            startPosition = nextPosition = 0;
        }
        final ImageView ViewStrat = mImageViews.get(startPosition);
        final ImageView ViewNext = mImageViews.get(nextPosition);
        ViewNext.setImageResource(R.drawable.shape_bg_indicator_point_select);
        ViewStrat.setImageResource(R.drawable.shape_bg_indicator_point_nomal);
    }

}
