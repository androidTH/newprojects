package com.d6.android.app.dialogs

import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.OnDialogListener
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.optString
import com.facebook.binaryresource.FileBinaryResource
import com.facebook.cache.common.SimpleCacheKey
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.controller.ControllerListener
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.android.synthetic.main.dialog_datetype_layout.*
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent


/**
 * 分享给好友
 */
class DateTypeDialog : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Dialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout(matchParent, wrapContent)
        dialog.window.setGravity(Gravity.BOTTOM)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_datetype_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

        tv_sharedate.setOnClickListener {
            dialogListener?.onClick(1,"sharedate")
            dismissAllowingStateLoss()
        }

        tv_wxcopy.setOnClickListener {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 将文本内容放到系统剪贴板里。
            cm.text = tv_wxhintnum.text.trim()
            WXAPIFactory.createWXAPI(context,"wx43d13a711f68131c").openWXApp()
            toast("微信号已复制到剪切板")
            dialogListener?.onClick(2,"wxcopy")
            dismissAllowingStateLoss()
        }
//        val resource = Fresco.getImagePipelineFactory().getMainFileCache().getResource(SimpleCacheKey(arguments.getString("pics"))) as FileBinaryResource
//        val file = resource.getFile()
//        val bitmap = BitmapFactory.decodeFile(file.getPath())
//        imageView.setImageBitmap(bitmap)
//        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
//            @Override
//            public void onFinalImageSet(
//                    String id,
//                    @Nullable ImageInfo imageInfo,
//                    @Nullable Animatable anim) {
//                if (imageInfo == null) {
//                    return;
//                }
//                QualityInfo qualityInfo = imageInfo.getQualityInfo();
//                FLog.d("Final image received! " +
//                        "Size %d x %d",
//                        "Quality level %d, good enough: %s, full quality: %s",
//                        imageInfo.getWidth(),
//                        imageInfo.getHeight(),
//                        qualityInfo.getQuality(),
//                        qualityInfo.isOfGoodEnoughQuality(),
//                        qualityInfo.isOfFullQuality());
//            }
//
//            @Override
//            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
//                FLog.d("Intermediate image received");
//            }
//
//            @Override
//            public void onFailure(String id, Throwable throwable) {
//                FLog.e(getClass(), throwable, "Error loading %s", id)
//            }
//        };
//
//        Uri uri;
//        DraweeController controller = Fresco.newDraweeControllerBuilder()
//                .setControllerListener(controllerListener)
//                .setUri(uri)
//                // other setters
//                .build();
        getData()
    }

    private fun getData() {
        isBaseActivity {
            Request.getInfo(Const.SERVICE_WECHAT_CODE).request(it) { _, data ->
                data?.let {
                    val sex = SPUtils.instance().getString(Const.User.USER_SEX)
                    if(TextUtils.equals(sex, "0")){
                        tv_wxnum.text= "添加客服微信号：${data.optString("ext5")}"
                        tv_wxhintnum.text = "${data.optString("ext5")}"
                    }else{
                        tv_wxhintnum.text = "${data.optString("ext6")}"
                        tv_wxnum.text= "添加客服微信号：${data.optString("ext6")}"
                    }
                }
            }
        }
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }

    private var dialogListener: OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }
}