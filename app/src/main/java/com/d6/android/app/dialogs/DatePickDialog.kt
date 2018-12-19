package com.d6.android.app.dialogs

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import com.d6.android.app.R
import org.jetbrains.anko.support.v4.find

/**
 * 日期选择
 */
@SuppressLint("ValidFragment")
class DatePickDialog(val min: Long = 0, val max: Long = 0) : DialogFragment() {

    @JvmField
    var isCheckedStartTime:Boolean=false
    @JvmField
    var dayofMonth:String =""

    private val datePicker by lazy {
        find<DatePicker>(R.id.mDatePicker)
    }

    private val cancelView by lazy {
        find<TextView>(R.id.action_cancel)
    }

    private val sureView by lazy {
        find<TextView>(R.id.action_sure)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun isCheckedStartTime(flag:Boolean=false,day:String){
        isCheckedStartTime = flag
        dayofMonth = day
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.activity_date_pick_dialog, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (max >= 0) {
                datePicker.maxDate = System.currentTimeMillis()
            }
            if (min > 0) {
                datePicker.minDate = min
            }
        }

        if(isCheckedStartTime){
            datePicker.init(datePicker.year,datePicker.month,dayofMonth.toInt()+7, DatePicker.OnDateChangedListener { view, year, monthOfYear, dayOfMonth ->

            })
        }

        cancelView.setOnClickListener {
            dismiss()
        }

        sureView.setOnClickListener {
            onDateSetListener?.onSet(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth)
        }
    }

    fun setOnDateSetListener(listener: OnDateSetListener?) {
        this.onDateSetListener = listener
    }

    fun setOnDateSetListener(listener: (year: Int, month: Int, day: Int) -> Unit) {
        this.onDateSetListener = object : OnDateSetListener {
            override fun onSet(year: Int, month: Int, day: Int) {
                listener(year, month, day)
            }
        }
    }

    private var onDateSetListener: OnDateSetListener? = null

    interface OnDateSetListener {
        fun onSet(year: Int, month: Int, day: Int)
    }
}
