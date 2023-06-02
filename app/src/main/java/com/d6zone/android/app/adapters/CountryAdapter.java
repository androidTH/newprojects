package com.d6zone.android.app.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.d6zone.android.app.R;
import com.d6zone.android.app.base.adapters.util.ViewHolder;
import com.d6zone.android.app.models.Dict;

import me.yokeyword.indexablerv.IndexableAdapter;

/**
 *
 */

public class CountryAdapter extends IndexableAdapter<Dict> {

    @Override
    public RecyclerView.ViewHolder onCreateTitleViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_index_country, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_county, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindTitleViewHolder(RecyclerView.ViewHolder holder, String indexTitle) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setText(R.id.tv_name,indexTitle);
    }

    @Override
    public void onBindContentViewHolder(RecyclerView.ViewHolder holder, Dict entity) {
        ViewHolder viewHolder = (ViewHolder) holder;
        TextView tv_name = viewHolder.bind(R.id.tv_name);
//        tv_name.setSelected(TextUtils.equals(entity.getId(), SMApplication.cityId));
        tv_name.setText(entity.getName()+" "+entity.getCode());
    }
}
