package com.d6.android.app.widget.popup;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.d6.android.app.R;
import com.d6.android.app.widget.LoveHeart;
import com.d6.android.app.widget.gift.CustormAnim;
import com.d6.android.app.widget.gift.GiftControl;
import com.d6.android.app.widget.gift.GiftModel;

/**
 * author : jinjiarui
 * time   : 2019/10/13
 * desc   :
 * version:
 */
public class mLoveHeartPopu extends BasePopup<mLoveHeartPopu>{


    private LinearLayout square_gift_parent;
    private LoveHeart square_loveheart;

    @Override
    public mLoveHeartPopu setContentView(Context context, int layoutId) {
        return super.setContentView(context,layoutId);
    }

    @Override
    protected void initAttributes() {

    }

    @Override
    protected void initViews(View view, mLoveHeartPopu popup) {
         square_gift_parent = view.findViewById(R.id.square_gift_parent);
         square_loveheart = view.findViewById(R.id.square_loveheart);
         initGiftControl(view.getContext());
    }

    private OnViewListener mOnViewListener;

    public mLoveHeartPopu() {

    }

    public static mLoveHeartPopu create() {
        return new mLoveHeartPopu();
    }

    public mLoveHeartPopu(Context context) {
        setContext(context);
        setContentView(context,R.layout.layout_any);
    }

    public static mLoveHeartPopu create(Context context) {
        return new mLoveHeartPopu(context);
    }

    public mLoveHeartPopu setOnViewListener(OnViewListener listener) {
        this.mOnViewListener = listener;
        return this;
    }

    public interface OnViewListener {

        void initViews(View view,mLoveHeartPopu popup);
    }

    //礼物
    private GiftControl giftControl = null;

    public void initGiftControl(Context context){
        if(giftControl==null){
            giftControl = new GiftControl(context);
            giftControl.setGiftLayout(square_gift_parent, 1)
                    .setHideMode(false)
                    .setCustormAnim(new CustormAnim());
            giftControl.setmGiftAnimationEndListener(new GiftControl.GiftAnimationEndListener() {
                @Override
                public void getGiftCount(int giftCount) {

                }
            });
        }
    }

    private void doLoveHeartAnimation(){
        square_loveheart.showAnimationRedHeart(null);
    }

    //连击礼物数量
    public void addGiftNums(int giftnum,Boolean currentStart,Boolean JumpCombo) {
        if (giftnum == 0) {
            return;
        } else {
            GiftModel giftModel =new  GiftModel();
            giftModel.setGiftId("礼物Id").setGiftName("礼物名字").setGiftCount(giftnum).setGiftPic("")
                    .setSendUserId("1234").setSendUserName("吕靓茜").setSendUserPic("").setSendGiftTime(System.currentTimeMillis())
                    .setCurrentStart(currentStart);
            if (currentStart) {
                giftModel.setHitCombo(giftnum);
            }
            if(JumpCombo){
                giftModel.setJumpCombo(giftnum);
            }
            giftControl.loadGift(giftModel);

            doLoveHeartAnimation();
        }
    }
}
