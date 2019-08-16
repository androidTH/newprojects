package com.d6.android.app.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

/**
 * author : jinjiarui
 * time   : 2019/06/18
 * desc   :
 * version:
 */
public class MaxEditTextWatcher implements TextWatcher {
    /**
     * 都算一位或 中文算两位
     */
    public static final int ALL_ONE = 0;
    public static final int CHINESE_TWO = 1;
    private int mType;
    private int mMaxLen;
    private Context mContext;
    private EditText etText;
    private TextChangedCallBack mCallBack;

    public MaxEditTextWatcher(int type, int maxlen, Context context, EditText editText) {
        this(type, maxlen, context, editText, null);
    }

    public MaxEditTextWatcher(int type, int maxLen, Context context, EditText editText, TextView textView) {
        this(type, maxLen, context, editText, textView, null);
    }

    public MaxEditTextWatcher(int type, int maxLen, Context context, EditText editText, TextView textView,
                              TextChangedCallBack callBack) {
        mType = type;
        mMaxLen = maxLen;
        mContext = context;
        etText = editText;
        mCallBack = callBack;
        if (mType == CHINESE_TWO) {
            etText.setFilters(new InputFilter[]{new MaxTextTwoLengthFilter(mContext, mMaxLen)});
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

        if (mCallBack != null) {
            mCallBack.changed(editable);
        }
    }

    public interface TextChangedCallBack {
        void changed(Editable editable);
    }
}
