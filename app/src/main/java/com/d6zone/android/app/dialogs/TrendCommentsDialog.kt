package com.d6zone.android.app.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6zone.android.app.R
import com.d6zone.android.app.adapters.TrendCommentAdapter
import com.d6zone.android.app.base.BaseActivity
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.interfaces.RequestManager
import com.d6zone.android.app.models.Comment
import com.d6zone.android.app.models.Square
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.utils.Const
import com.d6zone.android.app.utils.OnDialogListener
import com.d6zone.android.app.utils.SPUtils
import com.d6zone.android.app.widget.SwipeRefreshRecyclerLayout
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_trend_comments_layout.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.toast

/**
 * 动态评论弹出窗
 */
class TrendCommentsDialog : DialogFragment(),RequestManager,SwipeRefreshRecyclerLayout.OnRefreshListener {
    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun dismissDialog() {
        if (activity is BaseActivity) {
            (activity as BaseActivity).dismissDialog()
        }
    }

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private val compositeDisposable = CompositeDisposable()

    private val mTrend by lazy {
        if (arguments!=null && arguments.containsKey("data")) {
            arguments.getSerializable("data") as Square
        } else {
            Square()
        }
    }
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private var pageNum = 1

    private val mComments = ArrayList<Comment>()
    private val squareDetailCommentAdapter by lazy {
        TrendCommentAdapter(mComments)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Dialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout(matchParent, resources.getDimensionPixelSize(R.dimen.height_300))
        dialog.window.setGravity(Gravity.BOTTOM)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_trend_comments_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setLayoutManager(LinearLayoutManager(context))
        mSwipeRefreshLayout.setAdapter(squareDetailCommentAdapter)

        squareDetailCommentAdapter.setDeleteClick {
             val commentDelDialog = CommentDelDialog()
            commentDelDialog.arguments = bundleOf("data" to it)
            commentDelDialog.show((context as BaseActivity).supportFragmentManager, "action")
            commentDelDialog.setDialogListener { p, s ->
                if (p == 1) {
                    delete(it)
                }
            }
        }

        commentCountView.text = String.format("共%s条评论",mTrend.commentCount)
//        mComments.clear()
//        if (mTrend.comments == null || mTrend.comments!!.isEmpty()) {
//            mSwipeRefreshLayout.setLoadMoreText("暂无评论")
//        } else {
//            mComments.addAll(mTrend.comments!!)
//        }
//        squareDetailCommentAdapter.notifyDataSetChanged()
        tv_close.setOnClickListener { dismissAllowingStateLoss() }
        if (activity is BaseActivity) {
            (activity as BaseActivity).dialog()
        }

        tv_trend_comment_content.setOnClickListener(View.OnClickListener {
            val commentsDialog = CommentTrendDialog()
            commentsDialog.arguments = bundleOf("id" to (mTrend.id ?: ""))
            commentsDialog.setDialogListener { p, s ->

            }
            commentsDialog.show((activity as BaseActivity).supportFragmentManager, "comm")
        })
        getData()
    }

    private fun getData() {

        Request.getSquareDetail(userId, mTrend.id?:"").request(this, success = { _, data ->
            mSwipeRefreshLayout.isRefreshing = false
            data?.let {
                commentCountView.text = String.format("共%s条评论",it.commentCount)
                mComments.clear()
                if (it.comments == null || it.comments.isEmpty()) {
                    mSwipeRefreshLayout.setLoadMoreText("暂无评论")
                } else {
                    mComments.addAll(it.comments)
                }
                squareDetailCommentAdapter.notifyDataSetChanged()
            }
        }, error = { _, _ ->
            mSwipeRefreshLayout.isRefreshing = false
        })
    }

    private fun loadData() {
        Request.getCommentList(userId,mTrend.id?:"", pageNum).request(this, success = { _, data ->
            mSwipeRefreshLayout.isRefreshing = false
            if (pageNum == 1) {
                mComments.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无评论")
                }
            } else {
                mComments.addAll(data.list.results)
            }
            squareDetailCommentAdapter.notifyDataSetChanged()
        }, error = { _, _ ->
            mSwipeRefreshLayout.isRefreshing = false
        })
    }

    private var dialogListener: OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }

    override fun onRefresh() {
        pageNum = 1
        getData()
    }

    override fun onLoadMore() {
        if (mComments.isEmpty()) {
            mSwipeRefreshLayout.setLoadMoreText("暂无评论")
            mSwipeRefreshLayout.isRefreshing = false
            return
        }
        pageNum++
        loadData()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        dismissDialog()
    }
    override fun onDetach() {
        super.onDetach()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

      private fun delete(data: Comment){
        isBaseActivity {
            it.dialog(canCancel = false)
            Request.delComments(data.id!!.toInt()).request(it) { _, _ ->
                it.showToast("删除成功")
                mComments.remove(data)
                squareDetailCommentAdapter.notifyDataSetChanged()
            }
        }
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}