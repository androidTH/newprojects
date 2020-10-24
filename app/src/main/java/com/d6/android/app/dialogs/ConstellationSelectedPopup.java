package com.d6.android.app.dialogs;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.d6.android.app.R;
import com.d6.android.app.adapters.MemberChooseQuickDateAdapter;
import com.d6.android.app.adapters.XinZuoQuickDateAdapter;
import com.d6.android.app.models.MemberChoose;
import com.d6.android.app.utils.Const;
import com.d6.android.app.utils.SPUtils;
import com.d6.android.app.widget.popup.BasePopup;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by jinjiarui on 2017/8/4.
 */

public class ConstellationSelectedPopup extends BasePopup<ConstellationSelectedPopup>  {
    private static final String TAG = ConstellationSelectedPopup.class.getSimpleName();

    private String mLocalUseSex = SPUtils.Companion.instance().getString(Const.User.USER_SEX,"1");

    //, new MemberChoose("中级会员","中级会员及以上",28),new MemberChoose("高级会员","高级会员及以上",29)
    private MemberChoose constellations[] = {new MemberChoose("不限","不限",0),new MemberChoose("只看会员","只看会员",31)};
//    private String constellationsmen[] = {MemberChoose("不限","不限",0), MemberChoose("只看会员","只看会员",0), "普通会员及以上", "入群会员及以上","白银会员及以上","黄金会员及以上","钻石会员及以上","私人订制及以上"};
    //new MemberChoose("普通会员","普通会员及以上",22) ,new MemberChoose("入群会员","入群会员及以上",30),new MemberChoose("白银会员","白银会员及以上",23),new MemberChoose("黄金会员","黄金会员及以上",24),new MemberChoose("钻石会员","钻石会员及以上",25),new MemberChoose("私人订制","私人订制及以上",26)
    private MemberChoose constellationsmen[] = {new MemberChoose("不限","不限",0), new MemberChoose("只看会员","只看会员",27)};
    private Context mContext;
    private RecyclerView mRvConstellation;
    private MemberChooseQuickDateAdapter mAdapter;
    private int position;

    public static ConstellationSelectedPopup create(Context context) {
        return new ConstellationSelectedPopup(context);
    }

    protected ConstellationSelectedPopup(Context context) {
        mContext = context;
        setContext(context);
    }

    @Override
    protected void initAttributes() {
        if(TextUtils.equals(mLocalUseSex,"0")){
            setContentView(R.layout.popup_date_constellation_layout, ViewGroup.LayoutParams.MATCH_PARENT,mContext.getResources().getDimensionPixelSize(R.dimen.height_103));
        }else{
            setContentView(R.layout.popup_date_constellation_layout, ViewGroup.LayoutParams.MATCH_PARENT,mContext.getResources().getDimensionPixelSize(R.dimen.height_103));
        }
        setFocusAndOutsideEnable(true)
                .setBackgroundDimEnable(true)
                .setDimValue(0.5f);
    }

    @Override
    protected void initViews(View view, ConstellationSelectedPopup basePopup) {
        mRvConstellation = findViewById(R.id.rv_date_constelation);
        mRvConstellation.setHasFixedSize(true);
        mRvConstellation.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        if(TextUtils.equals(mLocalUseSex,"0")){
            mRvConstellation.setAdapter(mAdapter = new MemberChooseQuickDateAdapter(Arrays.asList(constellationsmen)));
        }else{
            mRvConstellation.setAdapter(mAdapter = new MemberChooseQuickDateAdapter(Arrays.asList(constellations)));
        }

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(TextUtils.equals(mLocalUseSex,"0")){
                    onPopupItemClick(view, constellationsmen[position].getId(),constellationsmen[position].getContent());
                }else{
                    onPopupItemClick(view, constellations[position].getId(),constellations[position].getContent());
                }
            }
        });
    }

    private void onPopupItemClick(View view,int position,String content){
        if (mOnPopupItemClickListener != null) {
            mOnPopupItemClickListener.onPopupItemClick(this, position,content);
        }
        dismiss();
    }
}
