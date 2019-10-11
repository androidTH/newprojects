package com.d6.android.app.adapters

import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.activities.ReportActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.dialogs.SendRedFlowerDialog
import com.d6.android.app.dialogs.SendRedHeartEndDialog
import com.d6.android.app.dialogs.ShareFriendsDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Square
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.getLocalUserId
import com.d6.android.app.utils.getLoginToken
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.UserTrendView
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity

/**
 *动态
 */
class MySquareAdapter(mData: ArrayList<Square>,val type: Int) : HFRecyclerAdapter<Square>(mData, R.layout.item_list_user_square) {

    protected var mUserData: UserData? = null;

    override fun onBind(holder: ViewHolder, position: Int, data: Square) {
        val trendView = holder.bind<UserTrendView>(R.id.mTrendView)
        data.sex = mUserData?.sex
        data.age = mUserData?.age
        trendView.update(data,if (type==0) 1 else 0 )
        trendView.initGiftControl()
        val count = data.appraiseCount ?: 0
        trendView.setPraiseClick {
            if (TextUtils.equals("1", data.isupvote)) {
                cancelPraise(data, count)
            } else {
                praise(data, count)
            }
        }

        trendView.setOnCommentClick {
            clickListener?.onItemClick(trendView, position)
        }
        trendView.setOnItemClick{v,s->
            mOnItemClickListener?.onItemClick(v,position)
        }

        trendView.setFlowerClick { square, lovePoint ->
            sendFlower(square,lovePoint)
        }

        trendView.setDeleteClick {
//            val squareActionDialog = SquareActionDialog()
//            squareActionDialog.arguments = bundleOf("id" to it.userid.toString())
//            squareActionDialog.show((context as BaseActivity).supportFragmentManager, "action")
//            squareActionDialog.setDialogListener { p, s ->
//                if (p == 1) {
//                    delete(data)
//                }
//            }

            val shareDialog = ShareFriendsDialog()
            shareDialog.arguments = bundleOf("from" to "mysquare","id" to it.userid.toString(),"sResourceId" to it.id.toString())
            var squareId = it.id.toString()
            shareDialog.show((context as BaseActivity).supportFragmentManager, "action")
            shareDialog.setDialogListener { p, s ->
                if (p == 0) {
                    mUserData?.let {
                        context.startActivity<ReportActivity>("id" to squareId, "tiptype" to "2")
                    }
                } else if (p == 1) {
                    delete(data)
                }
            }
        }

        trendView.onTogglePlay {
            mOnSquareAudioTogglePlay?.onSquareAudioPlayClick(position,it)
        }
    }

    fun setUserInfo(data: UserData){
           this.mUserData = data
    }

    private fun sendFlower(square:Square,lovePoint:Int){
        isBaseActivity {
//            var dialogSendRedFlowerDialog = SendRedFlowerDialog()
//            mData?.let {
//                dialogSendRedFlowerDialog.arguments = bundleOf("ToFromType" to 4,"userId" to square.userid.toString(),"square" to square)
//            }
//            dialogSendRedFlowerDialog.show(it.supportFragmentManager,"sendflower")
//
//            dialogSendRedFlowerDialog.setDialogListener { p, s ->
//                mData?.let {
//                    var index = it.indexOf(square)
//                    it.get(index).iFlowerCount = s.toString().toInt()+square.iFlowerCount!!.toInt()
//                    it.get(index).iIsSendFlower = 1
//                    notifyItemChanged(index+1)
//                }
//            }
            Request.sendLovePoint(getLoginToken(),"${square.userid}",lovePoint,1,"${square.id}").request(it,false,success={_,Data->
                mData?.let {
                    var index = it.indexOf(square)
                    it.get(index).iLovePoint = lovePoint+square.iLovePoint!!.toInt()
                    notifyItemChanged(index+1,"dddsasdf")
                }
            }){code,msg->
                if (code == 2||code==3) {
                    var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                    mSendRedHeartEndDialog.show(it.supportFragmentManager, "redheartendDialog")
                }
            }
        }
    }

    private fun delete(square: Square){
        isBaseActivity {
            it.dialog(canCancel = false)
            Request.deleteSquare(getLocalUserId(), square.id).request(it) { _, _ ->
                it.showToast("删除成功")
                mData.remove(square)
                notifyDataSetChanged()
            }
        }
    }

    private fun praise(square: Square, count: Int) {
        isBaseActivity {
            it.dialog(canCancel = false)
            Request.addPraise(getLocalUserId(), square.id).request(it,false,success={ _, _ ->
                it.showToast("点赞成功")
                square.isupvote = "1"
                square.appraiseCount = count + 1
                notifyDataSetChanged()}
            ){code,msg->
                CustomToast.showToast(msg)
            }
        }
    }

    private fun cancelPraise(square: Square, count: Int) {
        isBaseActivity {
            it.dialog(canCancel = false)
            Request.cancelPraise(getLocalUserId(), square.id).request(it) { msg, _ ->
                it.showToast("取消点赞")
                square.isupvote = "0"
                square.appraiseCount = if (count - 1 < 0) 0 else count - 1
                if (type == 1) {//我点赞的。删除数据
                    mData.remove(square)
                }
                notifyDataSetChanged()
            }
        }
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
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

    fun setOnSquareAudioToggleClick(action:(position:Int,square:Square)->Unit) {
        this.mOnSquareAudioTogglePlay = object : OnSquareAudioTogglePlay {
            override fun onSquareAudioPlayClick(position: Int, square: Square) {
                action(position, square)
            }
        }
    }

    private var mOnSquareAudioTogglePlay:OnSquareAudioTogglePlay?=null

    interface OnSquareAudioTogglePlay{
        fun onSquareAudioPlayClick(position:Int,square: Square)
    }
}