package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.*
import com.d6.android.app.extentions.request
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.sysErr
import kotlinx.android.synthetic.main.activity_my_info.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivityForResult
import java.io.File

/**
 *我的个人信息
 */
class MyInfoActivity : BaseActivity() {
    private val userData by lazy {
        intent.getSerializableExtra("data") as UserData
    }
    private var sex: String = "1"
    private var headFilePath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info)
        immersionBar.fitsSystemWindows(true).init()
        tv_back.setOnClickListener {
            finish()
        }
        tv_save.setOnClickListener {
            saveInfo()
        }

        headView.setOnClickListener {
            startActivityForResult<SelectPhotoDialog>(0)
        }

        tv_sex1.setOnClickListener {
//            val sexDialog = SelectSexDialog()
//            val b = Bundle()
//            if (sex.isNullOrEmpty()) {
//                sex = "1"
//            }
//            b.putInt("sex", sex.toInt())
//            sexDialog.arguments = b
//            sexDialog.setDialogListener { p, s ->
//                sex = p.toString()
//                tv_sex1.text = s
//            }
//            sexDialog.show(supportFragmentManager, "sex")
        }

        tv_birthday1.setOnClickListener {
            val datePickDialog = DatePickDialog()
            datePickDialog.show(supportFragmentManager,"date")
            datePickDialog.setOnDateSetListener { year, month, day ->
                datePickDialog.dismissAllowingStateLoss()
                val t = String.format("%04d-%02d-%02d",year,month,day)
                tv_birthday1.text = t
                userData.birthday = String.format("%04d-%02d-%02d",year,month,day)
            }
        }

        tv_height1.setOnClickListener {
            val selectHeightDialog = SelectHeightDialog()
            userData.height?.let {
                selectHeightDialog.arguments = bundleOf("data" to it)
            }
            selectHeightDialog.show(supportFragmentManager,"h")
            selectHeightDialog.setDialogListener { p, s ->
                tv_height1.text = s
                userData.height = s
            }
        }

        tv_weight1.setOnClickListener {
            val selectWeightDialog = SelectWeightDialog()
            userData.weight?.let {
                selectWeightDialog.arguments = bundleOf("data" to it)
            }
            selectWeightDialog.setDialogListener { p, s ->
                tv_weight1.text = s
                userData.weight = s
            }
            selectWeightDialog.show(supportFragmentManager,"w")
        }

        tv_constellation1.setOnClickListener {
            val selectConstellationDialog = SelectConstellationDialog()
            userData.constellation?.let {
                selectConstellationDialog.arguments = bundleOf("data" to it)
            }

            selectConstellationDialog.setDialogListener { p, s ->
                tv_constellation1.text = s
                userData.constellation = s
            }
            selectConstellationDialog.show(supportFragmentManager,"c")
        }

        headView.setImageURI(userData.picUrl)
        tv_nickName.setText(userData.name)
//        tv_signature1.setText(userData.signature)
//        tv_city1.setText(userData.city)
//        tv_area1.setText(userData.area)
        tv_birthday1.text = userData.birthday
        tv_height1.text = userData.height
        tv_weight1.text = userData.weight
//        tv_age1.setText(userData.age)
        sex = userData.sex ?: "1"
        tv_sex1.text = if (TextUtils.equals(sex, "1")) "男" else "女"
        tv_job1.setText(userData.job)
        tv_hobbit1.setText(userData.hobbit)
        tv_constellation1.text = userData.constellation
        tv_intro1.setText(userData.intro)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0 && data != null) {
                val path = data.getStringExtra(SelectPhotoDialog.PATH)
                startActivityForResult<CropImageActivity>(1, "scale" to 1f, "uri" to path)
            } else if (requestCode == 1) {
                headFilePath = data?.getStringExtra("path")
                headView.setImageURI("file://$headFilePath")
            }
        }
    }

    private fun saveInfo() {
        val nick = tv_nickName.text.toString().trim()
        if (nick.isEmpty()) {
            showToast("昵称不能为空!")
            return
        }
//        val city = tv_city1.text.toString().trim()
//        val area = tv_area1.text.toString().trim()
        val hobbit = tv_hobbit1.text.toString().trim()
        val job = tv_job1.text.toString().trim()
//        val age = tv_age1.text.toString().trim()
        val height = tv_height1.text.toString().trim()
        val weight = tv_weight1.text.toString().trim()
//        val signature = tv_signature1.text.toString().trim()
        val constellation = tv_constellation1.text.toString().trim()
        val intro = tv_intro1.text.toString().trim()
        userData.name = nick
        userData.sex = sex
//        userData.city = city
//        userData.area = area
        userData.hobbit = hobbit
        userData.job = job
//        userData.age = age
        userData.height = height
        userData.weight = weight
//        userData.signature = signature
        userData.constellation = constellation
        userData.intro = intro
        userData.userId = SPUtils.instance().getString(Const.User.USER_ID)
        dialog()
        if (headFilePath == null) {
            Request.updateUserInfo(userData).request(this) { msg, _ ->
                showToast(msg.toString())
                setResult(Activity.RESULT_OK)
                finish()
            }
        } else {
            Request.uploadFile(File(headFilePath)).flatMap {
                sysErr("----------------->$it")
                userData.picUrl = it
                Request.updateUserInfo(userData)
            }.request(this) { msg, _ ->
                showToast(msg.toString())
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
}
