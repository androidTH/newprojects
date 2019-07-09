package com.d6.android.app.widget.loadmore;


import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.d6.android.app.R;

/**
 * Created by BlingBling on 2016/10/11.
 */

public final class CustomLoadMoreView extends LoadMoreView {

    @Override
    public int getLayoutId() {
        return R.layout.custome_quick_view_load_more;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.custom_load_more_loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.custom_load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.custom_load_more_load_end_view;
    }
}
