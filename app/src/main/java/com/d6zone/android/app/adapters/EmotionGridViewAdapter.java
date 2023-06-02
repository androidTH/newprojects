package com.d6zone.android.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.d6zone.android.app.R;
import com.d6zone.android.app.models.GiftBeans;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * @Created by SiberiaDante
 * @Describe：
 * @Time: 2017/6/26
 * @Email: 994537867@qq.com
 * @GitHub: https://github.com/SiberiaDante
 * @博客园： http://www.cnblogs.com/shen-hua/
 */
public class EmotionGridViewAdapter extends BaseAdapter {

	private Context context;
	private List<GiftBeans> emotionNames;
	private int itemWidth;

	public EmotionGridViewAdapter(Context context, List<GiftBeans> emotionNames, int itemWidth) {
		this.context = context;
		this.emotionNames = emotionNames;
		this.itemWidth = itemWidth;
	}
	
	@Override
	public int getCount() {
		return emotionNames.size();
	}

	@Override
	public String getItem(int position) {
		return emotionNames.get(position).getName();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		ViewHolder mViewHolder;
		if(convertView==null){
			convertView = inflater.inflate(R.layout.layout_giftitem,null);
			mViewHolder = new ViewHolder();
			mViewHolder.sampimgview_gift =  convertView.findViewById(R.id.sampimgview_gift);
			mViewHolder.mTvGiftName = convertView.findViewById(R.id.tv_giftname);
			mViewHolder.mTvGiftNums= convertView.findViewById(R.id.tv_giftnums);
			convertView.setTag(mViewHolder);
		}else{
           mViewHolder = (ViewHolder) convertView.getTag();
		}

		GiftBeans beans = emotionNames.get(position);
		mViewHolder.sampimgview_gift.setImageURI(beans.getIcon());
		mViewHolder.mTvGiftName.setText(beans.getName());
		mViewHolder.mTvGiftNums.setText(beans.getLoveNum()+" [img src=redheart_small/]");
		return convertView;
	}


	public class ViewHolder{
		SimpleDraweeView sampimgview_gift;
		TextView mTvGiftNums;
		TextView mTvGiftName;
	}
}
