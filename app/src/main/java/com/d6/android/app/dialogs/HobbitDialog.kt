package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.utils.OnDialogListener
import com.d6.android.app.utils.screenWidth
import com.d6.android.app.widget.LabelsView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_hobbit_layout.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 约会宣言
 */
class HobbitDialog : DialogFragment(), RequestManager {

    private var mLabels:ArrayList<String> = ArrayList()

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadeDialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout((screenWidth() * 0.9f).toInt(), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(true)
    }

    var dateDeclarationViewView1: TextView? = null
    fun dateDeclarationView(dateDeclarationViewView: TextView) {
        dateDeclarationViewView1 = dateDeclarationViewView
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_hobbit_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            if (arguments.containsKey("data")) {
                et_hobbit_content.setText(arguments.getString("data"))
            }
        }
        if(dateDeclarationViewView1!=null) {
            et_hobbit_content.setText(dateDeclarationViewView1!!.text.toString())
        }
        tv_cancel_hobbit.setOnClickListener {
            dismissAllowingStateLoss()
        }

        tv_sure_hobbit.setOnClickListener {
            if(mLabels.isNotEmpty()){
                var hobbit:String=""
                for(str in mLabels){
                    hobbit+= str+"#"
                }
                dialogListener?.onClick(0,hobbit.substring(0,hobbit.length-1))
            }
            dismissAllowingStateLoss()
        }

        add_hobbit_tv.setOnClickListener(View.OnClickListener {
            if(mLabels.size < 3){
                mLabels.add(et_hobbit_content.text.toString().trim())
                labelsView.setLabels(mLabels, R.layout.layout_tag)
                et_hobbit_content.text.clear()
            }
        })
        labelsView.setOnLabelClickListener(object:LabelsView.OnLabelClickListener {
            override fun onLabelClick(label: TextView, data: Any, position: Int) {
                labelsView.removeView(label.parent as View)
                mLabels.remove(data)
            }
        }, R.layout.layout_tag)

        et_hobbit_content.addTextChangedListener(object :TextWatcher{
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if(it.length>0){
                        add_hobbit_tv.backgroundDrawable = ContextCompat.getDrawable(context,R.drawable.shape_right4r_orange)
                    }else{
                        add_hobbit_tv.backgroundDrawable = ContextCompat.getDrawable(context,R.drawable.shape_right4r_cd)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
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

    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun dismissDialog() {
        if (context is BaseActivity) {
            (context as BaseActivity).dismissDialog()
        }
    }

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}