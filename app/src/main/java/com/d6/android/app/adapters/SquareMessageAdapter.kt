package com.d6.android.app.adapters

import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.dialogs.UnKnowInfoDialog
import com.d6.android.app.models.SquareMessage
import com.d6.android.app.utils.*
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity

/**
 *
 */
class SquareMessageAdapter(mData:ArrayList<SquareMessage>): HFRecyclerAdapter<SquareMessage>(mData, R.layout.item_list_square_msg) {

    override fun onBind(holder: ViewHolder, position: Int, data: SquareMessage) {
        holder.setText(R.id.tv_name,data.title)
        holder.setText(R.id.tv_time,data.createTime?.interval())
        val headView = holder.bind<SimpleDraweeView>(R.id.headView)
        headView.setImageURI(data.userPic)

        var tv_content = holder.bind<TextView>(R.id.tv_content)
        if(data.content.isNullOrEmpty()){
            tv_content.visibility = View.GONE
        }else{
            tv_content.visibility = View.VISIBLE
        }

        tv_content.text = data.content

        val imageView = holder.bind<SimpleDraweeView>(R.id.imageView)
        val textContent = holder.bind<TextView>(R.id.tv_square_content)
        textContent.text = data.squareContent

        sysErr(data.squareContent+"--------url----->"+data.replypicUrl.isNullOrEmpty())
        if (data.replypicUrl.isNullOrEmpty()) {
            imageView.invisible()
            textContent.gone()
        } else {
            val imgs = data.replypicUrl?:""
            val urls = imgs.split(",")
            if (urls.isNotEmpty()) {
                imageView.setImageURI(urls[0])
            }
            imageView.visible()
            textContent.gone()
        }

        headView.setOnClickListener {
            val id = data.userid
            isBaseActivity {
                if (id!=null) {
                    if(data.iIsAnonymous==1){
                        var mUnknowDialog = UnKnowInfoDialog()
                        mUnknowDialog.arguments = bundleOf("otheruserId" to "${id}")
                        mUnknowDialog.show((context as BaseActivity).supportFragmentManager,"unknowDialog")
                    }else{
                        it.startActivity<UserInfoActivity>("id" to id.toString())
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
}