package com.d6zone.android.app.models

import me.yokeyword.indexablerv.IndexableEntity

/**
 *
 */
data class Dict(val name: String? = "") : IndexableEntity {

    var code: String? = ""
    var type: String? = ""

    override fun setFieldIndexBy(indexField: String?) {
        this.type = indexField
    }

    override fun setFieldPinyinIndexBy(pinyin: String?) {

    }

    override fun getFieldIndexBy(): String {
        return type ?: ""
    }
}