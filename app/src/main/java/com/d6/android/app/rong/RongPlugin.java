package com.d6.android.app.rong;

import android.content.Context;

import com.d6.android.app.rong.bean.CustomMessage;
import com.d6.android.app.rong.bean.TipsMessage;
import com.d6.android.app.rong.plugin.D6ExtensionModule;
import com.d6.android.app.rong.provider.CustomMessageProvider;
import com.d6.android.app.rong.provider.CustomUnKnowMessageProvider;
import com.d6.android.app.rong.provider.TipsMessageProvider;

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
        RongIM.registerMessageType(CustomMessage.class);
        RongIM.registerMessageType(TipsMessage.class);
        RongIM.registerMessageType(UnknownMessage.class);
        RongIM.registerMessageTemplate(new CustomMessageProvider());
        RongIM.registerMessageTemplate(new TipsMessageProvider());
        RongIM.registerMessageTemplate(new CustomUnKnowMessageProvider());
    }
}
