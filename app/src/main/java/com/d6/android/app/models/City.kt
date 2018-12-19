package com.d6.android.app.models

import com.google.gson.annotations.SerializedName
import me.yokeyword.indexablerv.IndexableEntity

/**
 * Created on 2018/1/15.
 */
data class City(val ids: String?,@SerializedName("paramName")val name: String? = "") : IndexableEntity {

    var paramKey: String? = "" //paramKey 1:海外地市 0：国内地市
    var isValid: String? = ""//isValid 1 热门地市，0 普通地市
    var isSelected: Boolean = false

    override fun setFieldIndexBy(indexField: String?) {

    }

    override fun setFieldPinyinIndexBy(pinyin: String?) {

    }

    override fun getFieldIndexBy(): String {
        return ""
    }
}