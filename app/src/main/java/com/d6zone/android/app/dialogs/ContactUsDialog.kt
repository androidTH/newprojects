package com.d6zone.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6zone.android.app.R
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.interfaces.RequestManager
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.utils.optString
import com.d6zone.android.app.utils.screenWidth
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_contact_us_layout.*
import org.jetbrains.anko.wrapContent

/**
 *
 */
class ContactUsDialog : DialogFragment(),RequestManager {
    override fun showToast(msg: String) {

    }

    override fun dismissDialog() {
    }

    override fun onBind(disposable: Disposable) {
        this.disposable = disposable
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadeDialog)
    }

    private var disposable: Disposable?=null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout((screenWidth() * 0.7f).toInt(), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_contact_us_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_close.setOnClickListener {
            dismissAllowingStateLoss()
        }
        getData()
    }

    private fun getData() {
        Request.getInfo("qrcode-weixin").request(this) { _, data ->
            data?.let {
                val url = data.optString("picUrl")
                img.setImageURI(url)
            }
        }
    }

    override fun onDestroy() {
        try {
            if (disposable != null && !disposable!!.isDisposed) {
                disposable?.dispose()
            }
        } catch (e: Exception) {

        }
        super.onDestroy()
    }
}