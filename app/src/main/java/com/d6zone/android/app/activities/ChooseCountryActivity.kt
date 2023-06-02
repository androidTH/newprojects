package com.d6zone.android.app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.d6zone.android.app.R
import com.d6zone.android.app.adapters.CountryAdapter
import com.d6zone.android.app.base.TitleActivity
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.models.Dict
import com.d6zone.android.app.net.Request
import kotlinx.android.synthetic.main.activity_choose_country.*
import me.yokeyword.indexablerv.IndexableLayout
import org.json.JSONArray
import org.json.JSONObject
import me.yokeyword.indexablerv.SimpleHeaderAdapter


class ChooseCountryActivity : TitleActivity() {
    private val mDicts = ArrayList<Dict>()

    private val cityAdapter:CountryAdapter by lazy {
        CountryAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_country)
        title = "选择国家和地区"

        indexableLayout.setLayoutManager(LinearLayoutManager(this))
        indexableLayout.setOverlayStyle_Center()
        indexableLayout.setCompareMode(IndexableLayout.MODE_NONE)
        indexableLayout.setStickyEnable(true)
        indexableLayout.setAdapter(cityAdapter)

        cityAdapter.setOnItemContentClickListener { _, _, _, entity ->
            val intent = Intent()
            intent.putExtra("code", entity.code?:"")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        dialog()
        getData()
    }

    private fun getData() {

        Request.findDataDict().request(this) { _, data ->
            mDicts.clear()
            data?.let {
                if (it.isString) {
                    try {
                        val json = JSONObject(it.asString)
                        json.keys().forEach {
                            val key = if (it == "a") {
                                ""
                            } else {
                                it
                            }
                            val value = json[it]
                            val local = ArrayList<Dict>()
                            if (value != null && value is JSONArray) {
                                val l = value.length()
                                (0 until l).forEach {
                                    val obj = value.optJSONObject(it)
                                    val dict = Dict(obj.optString("name"))
                                    dict.code = obj.optString("code")
                                    dict.type = key
                                    if (key == "") {
                                        local.add(dict)
                                    } else {
                                        mDicts.add(dict)
                                    }
                                }
                            }
                            if (key == "") {
                                indexableLayout.addHeaderAdapter(SimpleHeaderAdapter(cityAdapter, null, "#", local))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            cityAdapter.setDatas(mDicts, { })
        }
    }
}
