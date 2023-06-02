package com.d6zone.android.app.dialogs

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.d6zone.android.app.R
import com.d6zone.android.app.base.BaseActivity
import com.d6zone.android.app.utils.*
import com.tbruyelle.rxpermissions2.RxPermissions
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.find
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent
import java.io.File


/**
 * 分享好友
 */
class SelectPhotosDialog : DialogFragment() {

    private var tempFile: File? = null
    companion object {
        val PATH = "path"
    }

    private val mUserId by lazy{
        getLocalUserId()
    }

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
            inflater?.inflate(R.layout.activity_select_photo_dialog, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //拍照
        find<Button>(android.R.id.button1).onClick {
            isBaseActivity {
                RxPermissions(it).request(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe {
                    if (it) {//有权限
                        //检测路径是否存在，不存在就创建
                        dialogListener?.onClick(0,"")
                        dismissAllowingStateLoss()
                    } else {
                        toast("没有拍照或读写权限")
                    }
                }
            }
        }
        //选择相册
        find<Button>(android.R.id.button2).onClick {
            isBaseActivity {
                RxPermissions(it).request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE).subscribe {
                    if (it) {//有权限
                        dialogListener?.onClick(1,"")
                        dismissAllowingStateLoss()
                    } else {
                        toast("没有读取存储权限")
                    }
                }
            }
        }
        //取消
        find<Button>(android.R.id.button3).onClick {
            dismissAllowingStateLoss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 12) {
            if (TextUtils.equals(permissions[0],Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                //用户不同意，向用户展示该权限作用
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    (isBaseActivity {
                        it.alertDialog( "请注意", "本应用需要使用访问本地存储权限，否则无法正常使用！", false, "确定", "取消", DialogInterface.OnClickListener { _, _ ->
                            dismissAllowingStateLoss()
                        }, DialogInterface.OnClickListener { _, _ -> dismissAllowingStateLoss() }
                        )
                    })
                    return
                }
                dismissAllowingStateLoss()
            }
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

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}