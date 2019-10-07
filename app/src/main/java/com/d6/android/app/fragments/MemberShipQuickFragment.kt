package com.d6.android.app.fragments
import android.os.Bundle
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.base.BaseFragment
import io.reactivex.disposables.Disposable

/**
 * 人工推荐
 */
class MemberShipQuickFragment : BaseFragment() {

    override fun contentViewId(): Int {
        return R.layout.layout_memebership_one
    }

    override fun showToast(msg: String) {
        super.showToast(msg)
    }

    override fun dismissDialog() {
        super.dismissDialog()
    }

    override fun onBind(disposable: Disposable) {
        super.onBind(disposable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            iLookType = it.getString(ARG_PARAM1)
//            sPlace = it.getString(ARG_PARAM2)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onFirstVisibleToUser() {

    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MemberShipQuickFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"