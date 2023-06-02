package com.d6zone.android.app.rong.plugin;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.d6zone.android.app.R;
import com.d6zone.android.app.activities.RedMoneyActivity;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

/**
 * author : jinjiarui
 * time   : 2018/12/22
 * desc   :
 * version:
 */
public class RedWalletPluginModule implements IPluginModule {

    Conversation.ConversationType conversationType;
    String targetId;

    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.send_redwallet);
    }

    @Override
    public String obtainTitle(Context context) {
        return "红包";
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        this.conversationType = rongExtension.getConversationType();
        this.targetId = rongExtension.getTargetId();
        Intent intent = new Intent(fragment.getContext(), RedMoneyActivity.class);
        Log.i("redwalletPlugin","targetId:"+targetId);
        String[] mStrs = targetId.split("_");
        if(mStrs!=null&&mStrs.length>1){
            intent.putExtra("sResourceId",targetId);
            intent.putExtra("redwallettype","noprivate");
        }else{
            intent.putExtra("sResourceId",targetId);
            if(conversationType.equals(Conversation.ConversationType.GROUP)){
                intent.putExtra("redwallettype","group");
            }else{
                intent.putExtra("redwallettype","private");
            }
        }
//        Intent intent = new Intent(fragment.getContext(), RedMoneyDesActivity.class);
        fragment.getActivity().startActivity(intent);
//        SendRedFlowerDialog dialogSendRedFlowerDialog =new  SendRedFlowerDialog();
//        Bundle bundle = new Bundle();
//        bundle.putInt("ToFromType", 3);
//        bundle.putString("userId",targetId);
//        dialogSendRedFlowerDialog.setArguments(bundle);
//        dialogSendRedFlowerDialog.show(fragment.getFragmentManager(),"sendflower");

//        SendLoveHeartDialog mSendLoveHeartDialog = new SendLoveHeartDialog();
//        Bundle bundle = new Bundle();
//        bundle.putInt("ToFromType", 1);
//        bundle.putString("userId",targetId);
//        mSendLoveHeartDialog.setArguments(bundle);
//        mSendLoveHeartDialog.show(fragment.getFragmentManager(), "sendloveheartDialog");


//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(payParams.getApiUrl())
//                .build();
//        PrePayInfoService service = retrofit.create(PrePayInfoService.class);


//        Request.INSTANCE.sendLovePoint(getLoginToken(),"${square.userid}",1,4,"${square.id}").as


//        ImgTxtMessage custommsg= new ImgTxtMessage("送你200朵小红花","200");
//        RichContentMessage richContentMessage = RichContentMessage.obtain("送花", "xxx送你3朵小红花", "https://www.rongcloud.cn/images/logo.png");
//        CustomMessage richContentMessage = CustomMessage.obtain("送你200朵小红花",GsonHelper.getGson().toJson(custommsg));
        //Conversation.ConversationType.PRIVATE 为会话类型。
//        Message myMessage = Message.obtain(targetId, conversationType, richContentMessage);
//        RongIM.getInstance().sendMessage(myMessage, null, null, new IRongCallback.ISendMessageCallback() {
//            @Override
//            public void onAttached(Message message) {
//                //消息本地数据库存储成功的回调
//            }
//
//            @Override
//            public void onSuccess(Message message) {
//                //消息通过网络发送成功的回调
//            }
//
//            @Override
//            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                //消息发送失败的回调
//            }
//        });
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }
}
