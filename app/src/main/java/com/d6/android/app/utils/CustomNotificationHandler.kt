package com.d6.android.app.utils

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.widget.Toast
import com.d6.android.app.activities.MyDateActivity
import com.d6.android.app.base.BaseActivity
import com.umeng.message.UmengNotificationClickHandler
import com.umeng.message.entity.UMessage
import org.jetbrains.anko.startActivity

import java.util.HashMap

// 使用自定义的NotificationHandler，来结合友盟统计处理消息通知
// 参考http://bbs.umeng.com/thread-11112-1-1.html
class  CustomNotificationHandler: UmengNotificationClickHandler() {

    override fun dismissNotification(context: Context?, msg: UMessage?) {
        super.dismissNotification(context, msg)
//        Toast.makeText(context, "dismission+${msg!!.extra["type"]}", Toast.LENGTH_LONG).show()
    }

    override fun launchApp(context: Context, msg: UMessage?) {
        super.launchApp(context, msg)
    }

    override fun openActivity(context: Context, msg: UMessage) {
        super.openActivity(context, msg)
    }

    fun open(context:Context,type:String){
        (context as BaseActivity).startActivity<MyDateActivity>("type" to type)
    }

    override fun openUrl(context: Context, msg: UMessage) {
        super.openUrl(context, msg)
    }

    override fun dealWithCustomAction(context: Context?, msg: UMessage?) {
        super.dealWithCustomAction(context, msg)
//        Toast.makeText(context, "dealWithCustomAction+${msg!!.extra["type"]}", Toast.LENGTH_LONG).show()
        handleCustomAction(context, msg)
    }

    override fun autoUpdate(context: Context, msg: UMessage?) {
        super.autoUpdate(context, msg)
    }

    private fun handleCustomAction(context: Context?, msg: UMessage?) {
        if (msg != null && !TextUtils.isEmpty(msg.custom)) {
            //                mMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//                            PushMessageResponse response = GsonHelper.GsonToBean(msg.custom, PushMessageResponse.class);
            //                PushMessageResponse response = mMapper.readValue(msg.custom, PushMessageResponse.class);
            //                int type = response.getCode();
            val type = msg.extra["type"]
            val intent: Intent? = null
            if (TextUtils.equals(type,"1")) {
                (context as BaseActivity).startActivity<MyDateActivity>()
            } else if (TextUtils.equals(type,"2")) {
                (context as BaseActivity).startActivity<MyDateActivity>()
            }else if (TextUtils.equals(type,"8")){

            }else if(TextUtils.equals(type,"9")){

            }
        }
    }

    companion object {

        private val TAG = CustomNotificationHandler::class.java.name
        val MSG_TYPE_HOME = 1001
        val MSG_TYPE_NEWS_DETAILS = 1101
        val MSG_TYPE_THEME = 1102
    }

    //    private Intent getNewsDetailsIntent(Context context, PushMessageResponse response) {
    //        Intent intent = new Intent(context, NewsDetailsSecActivity.class);
    //        intent.putExtra("video_id", response.getData().getVideo_id());
    //        intent.putExtra("push_id", response.getPush_id() + "");
    //        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //        return intent;
    //    }
    //
    //    private Intent getHtmlNewsDetailsIntent(Context context, PushMessageResponse response) {
    //        Intent intent = new Intent(context, WebEventActivity.class);
    //        WebEventResponse.DataBean bean = new WebEventResponse.DataBean();
    //        bean.setPage_url(response.getData().getH5_url());
    //        bean.setPage_title(response.getData().getTitle());
    //        Bundle bundle = new Bundle();
    //        bundle.putSerializable(StaticParam.INTENT_EXTRAS, bean);
    //        bundle.putString(StaticParam.VIDEO_ID, response.getData().getVideo_id());
    //        intent.putExtras(bundle);
    //        return intent;
    //    }
    //
    //    private Intent getThemeIntent(Context context, PushMessageResponse response) {
    //        Intent intent = new Intent(context, ThemListActivity.class);
    //        intent.putExtra("them_id", response.getData().getSpecial_id());
    //        intent.putExtra("push_id", response.getPush_id() + "");
    //        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //        return intent;
    //    }
    //
    //    private Intent getHomeIntent(Context context, PushMessageResponse response) {
    //        Intent intent = new Intent(context, HomeActivity.class);
    //        intent.putExtra("push_id", response.getPush_id() + "");
    //        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //        return intent;
    //    }
}
