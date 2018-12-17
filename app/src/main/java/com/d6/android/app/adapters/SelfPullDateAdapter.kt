package com.d6.android.app.adapters

import com.d6.android.app.R
import com.d6.android.app.activities.ReportActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.dialogs.OpenDateDialog
import com.d6.android.app.dialogs.OpenDateErrorDialog
import com.d6.android.app.dialogs.OpenDatePayPointDialog
import com.d6.android.app.dialogs.SquareActionDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.IntegralExplain
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.widget.SelfPullDateView
import kotlinx.android.synthetic.main.dialog_date_send.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.toast

/**
 *
 */
class SelfPullDateAdapter(mData:ArrayList<MyAppointment>): HFRecyclerAdapter<MyAppointment>(mData, R.layout.item_list_pull_date) {


    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    override fun onBind(holder: ViewHolder, position: Int, data: MyAppointment) {
        val view = holder.bind<SelfPullDateView>(R.id.srv_view)
        view.update(data)
        view.sendDateListener {
             signUpDate(it)
        }

        view.setDeleteClick {
            doReport(it.iAppointUserid.toString())
        }
    }

    private fun signUpDate(myAppointment:MyAppointment) {
        Request.queryAppointmentPoint(userId).request(context as BaseActivity, false, success = { msg, data ->
            val dateDialog = OpenDateDialog()
            dateDialog.arguments = bundleOf("data" to myAppointment, "explain" to data!!)
            dateDialog.show((context as BaseActivity).supportFragmentManager, "d")
        }) { code, msg ->
            if (code == 2) {
                var openErrorDialog = OpenDateErrorDialog()
                openErrorDialog.arguments = bundleOf("code" to code, "msg" to msg)
                openErrorDialog.show((context as BaseActivity).supportFragmentManager, "d")
            }
        }
    }

    private fun doReport(userid:String){
        val squareActionDialog = SquareActionDialog()
        squareActionDialog.arguments = bundleOf("id" to userid)
        squareActionDialog.show((context as BaseActivity).supportFragmentManager, "action")
        squareActionDialog.setDialogListener { p, s ->
            if (p == 0) {
                mData?.let {
                    startActivity(userId, "3")
                }
            }
        }
    }

    private fun startActivity(id:String,tipType:String){
        context.startActivity<ReportActivity>("id" to id, "tiptype" to tipType)
    }


    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}