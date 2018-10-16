package com.d6.android.app.adapters

import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.dialogs.OpenDateDialog
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.widget.SelfPullDateView
import org.jetbrains.anko.bundleOf

/**
 *
 */
class SelfPullDateAdapter(mData:ArrayList<MyAppointment>): HFRecyclerAdapter<MyAppointment>(mData, R.layout.item_list_pull_date) {

    override fun onBind(holder: ViewHolder, position: Int, data: MyAppointment) {
        val view = holder.bind<SelfPullDateView>(R.id.srv_view)
        view.update(data)
        view.sendDateListener {
             signUpDate(it)
        }
    }

    private fun signUpDate(myAppointment:MyAppointment) {
        isBaseActivity {
            val dateDialog = OpenDateDialog()
            dateDialog.arguments= bundleOf("data" to myAppointment)
            dateDialog.show(it.supportFragmentManager, "d")
//            it.dialog()
//            Request.signUpdate(userId,myAppointment.iAppointUserid.toString(),"").request(it,success = { msg, data ->
//                val dateDialog = OpenDateDialog()
//                dateDialog.show(it.supportFragmentManager, "d")
//            }) { code, msg ->
//                val dateErrorDialog = DateErrorDialog()
//                dateErrorDialog.show(it.supportFragmentManager, "d")
//            }
        }
    }


    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}