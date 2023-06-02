package com.d6zone.android.app.widget;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;

/**
 * author : jinjiarui
 * time   : 2019/06/18
 * desc   :
 * version:
 */
public class MaxTextTwoLengthFilter implements InputFilter {
    private int mMaxLength;

    public MaxTextTwoLengthFilter(Context context, int maxLen){
        mMaxLength = maxLen;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart , int dend){
        // 判断是否到达最大长度
        int count = 0;
        // 之前就存在的内容
        int dindex = 0;
        while (count <= mMaxLength && dindex < dest.length()) {
            char c = dest.charAt(dindex++);
            if (c < 128) {
                count = count + 1;
            } else {
                count = count + 2;
            }
        }
        if (count > mMaxLength) {
            return dest.subSequence(0, dindex - 1);
        }
        // 从编辑框刚刚输入进去的内容
        int sindex = 0;
        while (count <= mMaxLength && sindex < source.length()) {
            char c = source.charAt(sindex++);
            if (c < 128) {
                count = count + 1;
            } else {
                count = count + 2;
            }
        }
        if (count > mMaxLength) {
            sindex--;
        }
        return source.subSequence(0, sindex);
    }
}
