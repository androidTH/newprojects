package com.d6.android.app.adapters

import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.activities.ReportActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.dialogs.SendRedFlowerDialog
import com.d6.android.app.dialogs.ShareFriendsDialog
import com.d6.android.app.dialogs.SquareActionDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Square
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.showTips
import com.d6.android.app.widget.TrendView
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity

/**
 *
 */
class SquareAdapter(mData: ArrayList<Square>) : HFRecyclerAdapter<Square>(mData, R.layout.item_list_square) {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    override fun onBind(holder: ViewHolder, position: Int, data: Square) {
        val trendView = holder.bind<TrendView>(R.id.mTrendView)
        trendView.update(data)
        trendView.setPraiseClick {
            if (TextUtils.equals("1", data.isupvote)) {
                cancelPraise(data)
            } else {
                praise(data)
            }
        }

        trendView.setOnCommentClick {
            clickListener?.onItemClick(trendView,position)
        }

        trendView.setOnItemClick {v,s->
            mOnItemClickListener?.onItemClick(v,position)
        }

        trendView.setDeleteClick {
            val shareDialog = ShareFriendsDialog()
            shareDialog.arguments = bundleOf("from" to "square","id" to it.userid.toString(),"sResourceId" to it.id.toString())
            shareDialog.show((context as BaseActivity).supportFragmentManager, "action")
            shareDialog.setDialogListener { p, s ->
                if (p == 0) {
                    mData?.let {
                        startActivity(data.id!!, "2")
                    }
                } else if (p == 1) {
                    delete(data)
                }
            }
        }

        trendView.sendFlowerClick {
            var dialogSendRedFlowerDialog = SendRedFlowerDialog()
            dialogSendRedFlowerDialog.arguments= bundleOf("ToFromType" to 2,"userId" to it.userid.toString(),"square" to it)
            dialogSendRedFlowerDialog.show((context as BaseActivity).supportFragmentManager,"sendflower")
        }
    }


    //举报
    private fun startActivity(id:String,tipType:String){
        context.startActivity<ReportActivity>("id" to id, "tiptype" to tipType)
    }

    private fun praise(square: Square) {
        isBaseActivity {
            it.dialog()
            Request.addPraise(userId, square.id).request(it,true) { msg, data ->
                it.showToast("点赞成功")
                showTips(data,"","")
                square.isupvote = "1"
                square.appraiseCount = (square.appraiseCount?:0) + 1
                notifyDataSetChanged()
            }
        }
    }

    //删除动态
    private fun delete(square: Square){
        isBaseActivity {
            it.dialog(canCancel = false)
            Request.deleteSquare(userId, square.id).request(it) { _, _ ->
                it.showToast("删除成功")
                mData.remove(square)
                notifyDataSetChanged()
            }
        }
    }

    private fun cancelPraise(square: Square) {
        isBaseActivity {
            it.dialog()
            Request.cancelPraise(userId, square.id).request(it) { msg, _ ->
                it.showToast("取消点赞")
                square.isupvote = "0"
                square.appraiseCount = if (((square.appraiseCount?:0) - 1) < 0) 0 else (square.appraiseCount?:0) - 1
                notifyDataSetChanged()
            }
        }
    }

    private var clickListener: OnItemClickListener? = null

    fun setOnCommentClick(l: (p: Int) -> Unit) {
        clickListener = object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                l(position)
            }
        }
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}