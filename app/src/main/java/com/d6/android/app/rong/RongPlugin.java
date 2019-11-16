package com.d6.android.app.rong;

import android.content.Context;

import com.d6.android.app.rong.bean.AppointmentMsgContent;
import com.d6.android.app.rong.bean.BusinessCardFMsgContent;
import com.d6.android.app.rong.bean.BusinessCardMMsgContent;
import com.d6.android.app.rong.bean.CommentMsgContent;
import com.d6.android.app.rong.bean.CustomMessage;
import com.d6.android.app.rong.bean.GroupUnKnowTipsMessage;
import com.d6.android.app.rong.bean.CustomSystemMessage;
import com.d6.android.app.rong.bean.LookDateMsgContent;
import com.d6.android.app.rong.bean.LoveHeartMessage;
import com.d6.android.app.rong.bean.SpeedDateMsgContent;
import com.d6.android.app.rong.bean.SquareMsgContent;
import com.d6.android.app.rong.bean.TipsMessage;
import com.d6.android.app.rong.bean.VoiceChatMsgContent;
import com.d6.android.app.rong.plugin.D6ExtensionModule;
import com.d6.android.app.rong.provider.AppointmentMsgProvider;
import com.d6.android.app.rong.provider.BusinessCardFMsgProvider;
import com.d6.android.app.rong.provider.BusinessCardMMsgProvider;
import com.d6.android.app.rong.provider.CommentMsgProvider;
import com.d6.android.app.rong.provider.CustomMessageProvider;
import com.d6.android.app.rong.provider.CustomUnKnowMessageProvider;
import com.d6.android.app.rong.provider.GroupUnKnowMessageProvider;
import com.d6.android.app.rong.provider.LookDateMsgProvider;
import com.d6.android.app.rong.provider.LoveHeartMessageProvider;
import com.d6.android.app.rong.provider.SpeedDateMsgProvider;
import com.d6.android.app.rong.provider.SquareMsgProvider;
import com.d6.android.app.rong.provider.TipsMessageProvider;
import com.d6.android.app.rong.provider.VoiceChatMessageProvider;

import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UnknownMessage;

/**
 * author : jinjiarui
 * time   : 2018/12/22
 * desc   :
 * version:
 */
public class RongPlugin {

    private Context mContext;

    private static RongPlugin mRongCloudInstance;

    public RongPlugin(Context context){
        this.mContext = context;
        initListener();
    }
    /**
     * 初始化 RongCloud.
     *
     * @param context 上下文。
     */
    public static void init(Context context) {
        if (mRongCloudInstance == null) {
            synchronized (RongPlugin.class) {
                if (mRongCloudInstance == null) {
                    mRongCloudInstance = new RongPlugin(context);
                }
            }
        }
    }

    private void initListener() {
        List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
        IExtensionModule defaultModule = null;
        if (moduleList != null) {
            for (IExtensionModule module : moduleList) {
                if (module instanceof DefaultExtensionModule) {
                    defaultModule = module;
                    break;
                }
            }
            if (defaultModule != null) {
                RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
                RongExtensionManager.getInstance().registerExtensionModule(new D6ExtensionModule());
            }
        }

        RongIM.registerMessageType(LoveHeartMessage.class);
        RongIM.registerMessageType(CustomMessage.class);
        RongIM.registerMessageType(TipsMessage.class);
        RongIM.registerMessageType(UnknownMessage.class);
        RongIM.registerMessageType(SquareMsgContent.class);
        RongIM.registerMessageType(CommentMsgContent.class);
        RongIM.registerMessageType(AppointmentMsgContent.class);
        RongIM.registerMessageType(SpeedDateMsgContent.class);
        RongIM.registerMessageType(LookDateMsgContent.class);
        RongIM.registerMessageType(BusinessCardFMsgContent.class);
        RongIM.registerMessageType(BusinessCardMMsgContent.class);
        RongIM.registerMessageType(GroupUnKnowTipsMessage.class);
        RongIM.registerMessageType(CustomSystemMessage.class);
        RongIM.registerMessageType(VoiceChatMsgContent.class);//语音连麦

        RongIM.registerMessageTemplate(new LoveHeartMessageProvider());
        RongIM.registerMessageTemplate(new CustomMessageProvider());
        RongIM.registerMessageTemplate(new TipsMessageProvider());
        RongIM.registerMessageTemplate(new CustomUnKnowMessageProvider());
        RongIM.registerMessageTemplate(new SquareMsgProvider());//动态消息
        RongIM.registerMessageTemplate(new CommentMsgProvider());//评论消息
        RongIM.registerMessageTemplate(new AppointmentMsgProvider());//约会消息
        RongIM.registerMessageTemplate(new SpeedDateMsgProvider());//急约
        RongIM.registerMessageTemplate(new LookDateMsgProvider());//密约
        RongIM.registerMessageTemplate(new BusinessCardFMsgProvider());//急约
        RongIM.registerMessageTemplate(new BusinessCardMMsgProvider());//急约
        RongIM.registerMessageTemplate(new GroupUnKnowMessageProvider());//群组消息
        RongIM.registerMessageTemplate(new VoiceChatMessageProvider());//语音连麦

    }
}
