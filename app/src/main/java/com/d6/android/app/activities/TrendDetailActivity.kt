package com.d6.android.app.activities

import android.app.Activity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.ImagePagerAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.CommentTrendDialog
import com.d6.android.app.dialogs.TrendCommentsDialog
import com.d6.android.app.dialogs.TrendContentDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Square
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.sysErr
import com.d6.android.app.utils.toTime
import kotlinx.android.synthetic.main.activity_trend_detail.*
import org.jetbrains.anko.bundleOf

/**
 * 评论详情页
 */
class TrendDetailActivity : BaseActivity(), ViewPager.OnPageChangeListener {
    companion object {
        const val CURRENT_POSITION = "position"
        const val URLS = "urls"
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private val urls by lazy {
        if (intent.hasExtra(URLS)) {
            intent.getStringArrayListExtra(URLS)
        } else {
            emptyList<String>()
        }
    }

    private val mUrls = ArrayList<String>()

    private val mTrend by lazy {
        if (intent.hasExtra("data")) {
            intent.getSerializableExtra("data") as Square
        } else {
            Square()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trend_detail)

        tv_close.setOnClickListener {
            finish()
        }

        val position = intent.getIntExtra(CURRENT_POSITION, 0)
        val temp = if (urls.isEmpty()) {
            if (mTrend.imgUrl.isNullOrEmpty()) {
                urls
            } else {
                val images = mTrend.imgUrl?.split(",")
                images ?: urls
            }
        } else {
            urls
        }
        mUrls.clear()
        mUrls.addAll(temp)
        val adapter = ImagePagerAdapter(supportFragmentManager, mUrls)
        mViewPager.adapter = adapter
        mViewPager.addOnPageChangeListener(this)
        mViewPager.currentItem = position
        tv_count.text = String.format("%d/%d", position + 1, mUrls.size)

        headView.setImageURI(mTrend.picUrl)
        tv_trend_uname.text = mTrend.name
        tv_trend_uage.text = mTrend.age
        tv_trend_uage.isSelected = TextUtils.equals("0", mTrend.sex)
        tv_time.text = mTrend.updatetime.toTime("MM.dd")
        tv_content.text = mTrend.content
//        tv_appraise.text = mTrend.appraiseCount.toString()
        tv_appraise.isSelected = TextUtils.equals(mTrend.isupvote, "1")
//        tv_comment.text = mTrend.commentCount.toString()
        tv_comment.text = if ((mTrend.commentCount ?: 0) > 0) {
            mTrend.commentCount.toString()
        } else {
            ""
        }
        tv_appraise.text = if ((mTrend.appraiseCount ?: 0) > 0) {
            mTrend.appraiseCount.toString()
        } else {
            ""
        }

        tv_comment_content.setOnClickListener {
            val commentsDialog = CommentTrendDialog()
            commentsDialog.arguments = bundleOf("id" to (mTrend.id ?: ""))
            commentsDialog.setDialogListener { p, s ->
                whenComment()
            }
            commentsDialog.show(supportFragmentManager, "comm")
        }

        tv_appraise.setOnClickListener {
            if (TextUtils.equals("1", mTrend.isupvote)) {
                cancelPraise(mTrend)
            } else {
                praise(mTrend)
            }
        }

        tv_content.setOnClickListener {
//            val line = tv_content.lineCount
            //ellipsisCount>0说明没有显示全部，存在省略部分。
//            val count = tv_content.layout.getEllipsisCount(line-1)
//            sysErr("----------->$count")
//            if (line>2 || count > 0 ) {
//                val trendContentDialog = TrendContentDialog()
//                trendContentDialog.arguments = bundleOf("data" to mTrend)
//                trendContentDialog.show(supportFragmentManager, "con")
//            }

            val trendContentDialog = TrendContentDialog()
            trendContentDialog.arguments = bundleOf("data" to mTrend)
            trendContentDialog.show(supportFragmentManager, "con")
        }

        tv_comment.setOnClickListener {
            val trendCommentsDialog = TrendCommentsDialog()
            trendCommentsDialog.arguments = bundleOf("data" to mTrend)
            trendCommentsDialog.show(supportFragmentManager, "c")
        }

        rl_root.viewTreeObserver.addOnGlobalLayoutListener {
            sysErr("-----rl_root-${rl_root.bottom}------------")
            onLayoutChangedListener?.onChanged()
        }
    }

    private fun whenComment() {
        val trendCommentsDialog = TrendCommentsDialog()
        trendCommentsDialog.arguments = bundleOf("data" to mTrend)
        trendCommentsDialog.show(supportFragmentManager, "c")
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        tv_count.text = String.format("%d/%d", position + 1, mUrls.size)
    }

    private fun praise(square: Square) {
        dialog()
        Request.addPraise(userId, square.id).request(this) { msg, _ ->
            showToast("点赞成功")
            mTrend.isupvote = "1"
            mTrend.appraiseCount = (mTrend.appraiseCount ?: 0) + 1
            tv_appraise.isSelected = TextUtils.equals(mTrend.isupvote, "1")
            tv_appraise.text = mTrend.appraiseCount.toString()
            setResult(Activity.RESULT_OK)
        }

    }

    private fun cancelPraise(square: Square) {
        dialog()
        Request.cancelPraise(userId, square.id).request(this) { msg, _ ->
            showToast("取消点赞")
            mTrend.isupvote = "0"
            mTrend.appraiseCount = if (((mTrend.appraiseCount
                            ?: 0) - 1) < 0) 0 else (mTrend.appraiseCount ?: 0) - 1
            tv_appraise.text = mTrend.appraiseCount.toString()
            tv_appraise.isSelected = TextUtils.equals(mTrend.isupvote, "1")
            setResult(Activity.RESULT_OK)
        }
    }

    override fun onDestroy() {
        try {
            if (mViewPager != null) {
                mViewPager!!.removeOnPageChangeListener(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
    private var onLayoutChangedListener:OnLayoutChangedListener?=null
    fun setOnLayoutChangedListener(listener: OnLayoutChangedListener?) {
        this.onLayoutChangedListener = listener
    }
    public interface OnLayoutChangedListener{
        fun onChanged()
    }
}
