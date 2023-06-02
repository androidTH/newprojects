package com.d6zone.android.app.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6zone.android.app.R
import com.d6zone.android.app.interfaces.RequestManager
import com.d6zone.android.app.widget.ProgressDialog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.support.v4.toast

/**
 * 
 */
abstract class BaseNoBarFragment : Fragment() ,RequestManager{
    /**
     * 页面布局，必须。不能为0
     */
    abstract fun contentViewId():Int

    /**
     * fragment第一次展示触发。
     */
    abstract fun onFirstVisibleToUser()

    /**
     * 除第一次展示以外每次从hide切换为show状态时触发。
     */
    open fun onVisibleToUser(){}

    /**
     * 每次从show切换为hide状态时触发。
     */
    open fun onInvisibleToUser(){}

    val compositeDisposable = CompositeDisposable()

    private var isFirstResume = true
    private var isPrepared  = false
    private var isFirstVisible = true
    private var isFirstInVisible = true
    //改用lazy初始，第一次使用时才会初始化
    private val dialog: ProgressDialog by lazy {
        ProgressDialog(context, R.style.Theme_ProgressDialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initPrepare()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return if (contentViewId() != 0) {
            inflater?.inflate(contentViewId(), null)
        }else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isFirstResume) {
            isFirstResume = false
            return
        }
        if (userVisibleHint) {
            onVisibleToUser()
        }
//        Bugtags.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        if (userVisibleHint) {
            onInvisibleToUser()
        }
//        Bugtags.onPause(this)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false
                initPrepare()
            } else {
                onVisibleToUser()
            }
        } else {
            if (isFirstInVisible) {
                isFirstInVisible = false
            } else {
                onInvisibleToUser()
            }
        }
    }

    private @Synchronized fun initPrepare(){
        if (isPrepared) {
            onFirstVisibleToUser()
        } else {
            isPrepared = true
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
        try {
            val childFragmentManager = Fragment::class.java.getDeclaredField("mChildFragmentManager")
            childFragmentManager.isAccessible = true
            childFragmentManager.set(this, null)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    fun showDialog(msg: String = "加载中...", canCancel: Boolean = true) {
        if (context == null) {
            return
        }
        dialog.setCanceledOnTouchOutside(canCancel)
        dialog.setMessage(msg)
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    override fun dismissDialog() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun showToast(msg: String) {
        toast(msg)
    }

}