package com.d6.android.app.adapters;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.d6.android.app.R;
import com.d6.android.app.models.MemberBean;
import com.d6.android.app.utils.AppUtils;

import java.util.List;

/**
 * author : jinjiarui
 * time   : 2018/12/25
 * desc   :
 * version:
 */
public class MemberLevelAdapter extends BaseQuickAdapter<MemberBean,BaseViewHolder> {

    public MemberLevelAdapter(int layoutResId, @Nullable List<MemberBean> data) {
        super(layoutResId, data);
    }

    public MemberLevelAdapter(@Nullable List<MemberBean> data) {
        this(R.layout.item_card_pt,data);
    }

    public MemberLevelAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, MemberBean item) {
        RelativeLayout rl_vip_top = helper.getView(R.id.rl_vip_top);
        TextView tv_ztnums = helper.getView(R.id.tv_ztnums);
        TextView tv_endtime = helper.getView(R.id.tv_endtime);
        TextView tv_english = helper.getView(R.id.tv_english);
        TextView tv_memeber_address = helper.getView(R.id.tv_memeber_address);
        TextView tv_vip_percent = helper.getView(R.id.tv_vip_percent);
        TextView tv_remark = helper.getView(R.id.tv_remark);
        TextView tv_desc = helper.getView(R.id.tv_desc);
        View view_line = helper.getView(R.id.view_line);
        View sirenline = helper.getView(R.id.view_sirenline);
        TextView tv_sirentitle = helper.getView(R.id.tv_sirentitle);

        tv_vip_percent.setVisibility(View.VISIBLE);

        tv_english.setText(item.getSEnClassesname());
        helper.setText(R.id.tv_vip_level,item.getClassesname());
        tv_vip_percent.setText(item.getSTitle());
        helper.setText(R.id.tv_memeber_price,String.valueOf(item.getIAndroidPrice()));

        if(!TextUtils.isEmpty(item.getSDesc())){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tv_desc.setText(Html.fromHtml(item.getSDesc(),Html.FROM_HTML_MODE_COMPACT));
            }else{
                tv_desc.setText(Html.fromHtml(item.getSDesc()));
            }
        }
        tv_memeber_address.setText(item.getSServiceArea());
        AppUtils.Companion.setMemberNums(mContext,1,"直推次数: "+item.getIRecommendCount(),0,5,tv_ztnums);
        AppUtils.Companion.setMemberNums(mContext,1,"有效期: "+item.getSEnableDateDesc(),0,4,tv_endtime);
        if(!TextUtils.isEmpty(item.getSRemark())){
            tv_remark.setVisibility(View.VISIBLE);
            view_line.setVisibility(View.VISIBLE);
            tv_remark.setText(item.getSRemark());
        }else{
            tv_remark.setVisibility(View.GONE);
            view_line.setVisibility(View.GONE);
        }
//        int position = (helper.getLayoutPosition() - getHeaderLayoutCount());
        if(item.getIds()==22){
            rl_vip_top.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_5r_pt));
            tv_memeber_address.setTextColor(ContextCompat.getColor(mContext,R.color.color_848484));
            tv_sirentitle.setVisibility(View.GONE);
            sirenline.setVisibility(view_line.GONE);
        }else if(item.getIds()==30){
            rl_vip_top.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_5r_group));
            helper.setText(R.id.tv_vip_level,"入群会员");
            tv_memeber_address.setTextColor(ContextCompat.getColor(mContext,R.color.color_888888));
            tv_sirentitle.setVisibility(View.GONE);
            sirenline.setVisibility(view_line.GONE);
        }else if(item.getIds()==23){
            rl_vip_top.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_5r_sliver));
            tv_memeber_address.setTextColor(ContextCompat.getColor(mContext,R.color.color_A19BB0));
            tv_sirentitle.setVisibility(View.GONE);
            sirenline.setVisibility(view_line.GONE);
        }else if(item.getIds()==24){
            rl_vip_top.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_5r_gold));
            tv_memeber_address.setTextColor(ContextCompat.getColor(mContext,R.color.color_C69F60));
            tv_sirentitle.setVisibility(View.GONE);
            sirenline.setVisibility(view_line.GONE);
        }else if(item.getIds()==25){
            rl_vip_top.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_5r_diamond));
            tv_memeber_address.setTextColor(ContextCompat.getColor(mContext,R.color.color_8170D2));
            tv_sirentitle.setVisibility(View.GONE);
            sirenline.setVisibility(view_line.GONE);
        }else if(item.getIds()==26){
            rl_vip_top.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_5r_private));
            tv_memeber_address.setTextColor(ContextCompat.getColor(mContext,R.color.color_s323432));

            tv_sirentitle.setVisibility(View.VISIBLE);
            sirenline.setVisibility(view_line.VISIBLE);

            if(!TextUtils.isEmpty(item.getSDesc())){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tv_sirentitle.setText(Html.fromHtml(item.getSRemarkTop(),Html.FROM_HTML_MODE_COMPACT));
                }else{
                    tv_sirentitle.setText(Html.fromHtml(item.getSRemarkTop()));
                }
            }

            Log.i("ffff",item.getClassesname()+"---"+ item.getSRemarkTop());
        }
              //R.color.color_848484   R.color.color_888888  R.color.color_A19BB0 R.color.color_C69F60
                                         //R.color.color_8170D2 R.color.color_323432
    }
}
