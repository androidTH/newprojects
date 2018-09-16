package com.d6.android.app.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import kotlinx.android.synthetic.main.activity_edit_image.*

class EditImageActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_image)
        initView()
    }

    fun initView(){
        var LLManager = LinearLayoutManager(this);
        LLManager.orientation=LinearLayoutManager.HORIZONTAL
        recycler_sticker.layoutManager=LLManager
    }
}
