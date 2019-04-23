package com.d6.android.app.adapters

import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.SquareMessage
import com.d6.android.app.utils.*
import com.facebook.drawee.view.SimpleDraweeView

/**
 *
 */
class SquareMessageAdapter(mData:ArrayList<SquareMessage>): HFRecyclerAdapter<SquareMessage>(mData, R.layout.item_list_square_msg) {

    override fun onBind(holder: ViewHolder, position: Int, data: SquareMessage) {
        holder.setText(R.id.tv_content,data.content)
        holder.setText(R.id.tv_name,data.title)
        holder.setText(R.id.tv_time,data.createTime?.interval())
        val headView = holder.bind<SimpleDraweeView>(R.id.headView)
        headView.setImageURI(data.userPic)

        val imageView = holder.bind<SimpleDraweeView>(R.id.imageView)
        val textContent = holder.bind<TextView>(R.id.tv_square_content)
        textContent.text = data.squareContent

        sysErr(data.toString()+"--------url----->"+data.replypicUrl.isNullOrEmpty())
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
    }
}