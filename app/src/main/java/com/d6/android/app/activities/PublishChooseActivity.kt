package com.d6.android.app.activities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.utils.isAuthUser
import com.d6.android.app.widget.popup.blur.FastBlur
import kotlinx.android.synthetic.main.activity_publishchoose_layout.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.startActivityForResult

class PublishChooseActivity : BaseActivity(),View.OnClickListener{

    //  https://github.com/kongzue/StackLabel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publishchoose_layout)
        immersionBar.fitsSystemWindows(true).init()
        close_button.setOnClickListener {
            onBackPressed()
        }

        rootView.getViewTreeObserver().addOnPreDrawListener(object: ViewTreeObserver.OnPreDrawListener{
            override fun onPreDraw(): Boolean {
                rootView.backgroundDrawable = BitmapDrawable(getResources(), blur())
//                var decorView = getWindow().getDecorView().findViewById(android.R.id.content) as View
//                var bitmap = getBitmapByView(decorView)//这里是将view转成bitmap
//                setBlurBackground(bitmap)//这里是模糊图片, 这个是重点我会单独讲
                return true;
            }
        })

        tv_chat.setOnClickListener (this)
        tv_voicechat.setOnClickListener(this)
        tv_dink.setOnClickListener(this)
        tv_film.setOnClickListener(this)
        tv_games.setOnClickListener(this)
        tv_meal.setOnClickListener(this)
        tv_fitnes.setOnClickListener(this)
        tv_more.setOnClickListener(this)
        tv_travel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v?.id==R.id.tv_chat){
            isAuthUser{
                startActivityForResult<PublishFindDateActivity>(10)
                finish()
            }
        }else if(v?.id==R.id.tv_voicechat){
            isAuthUser{
                startActivityForResult<VoiceChatCreateActivity>(1)
                finish()
            }
        }else if(v?.id==R.id.tv_dink){
            isAuthUser{
                startActivityForResult<PublishFindDateActivity>(10)
                finish()
            }
        }else if(v?.id==R.id.tv_film){
            isAuthUser {
                startActivityForResult<PublishFindDateActivity>(10)
                finish()
            }
        }else if(v?.id==R.id.tv_games){
            isAuthUser{
                startActivityForResult<PublishFindDateActivity>(10)
                finish()
            }
        }else if(v?.id==R.id.tv_meal){
            isAuthUser {
                startActivityForResult<PublishFindDateActivity>(10)
                finish()
            }
        }else if(v?.id==R.id.tv_fitnes){
            isAuthUser {
                startActivityForResult<PublishFindDateActivity>(10)
                finish()
            }
        }else if(v?.id==R.id.tv_travel){
            isAuthUser{
                startActivityForResult<PublishFindDateActivity>(10)
                finish()
            }
        }else if(v?.id==R.id.tv_more){
            isAuthUser{
                startActivityForResult<PublishFindDateActivity>(10)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.bottom_menu_out)
    }

    private lateinit var mBitmap: Bitmap
    private lateinit var overlay: Bitmap
    fun IsNotNullOverlay()=::overlay.isInitialized
    private fun blur(): Bitmap {
        if (IsNotNullOverlay()) {
            return overlay
        }
        val view = getWindow().getDecorView()
        view.setDrawingCacheEnabled(true)
        view.buildDrawingCache(true)
        mBitmap = view.getDrawingCache()

        val scaleFactor = 8f//图片缩放比例
        val radius = 10f//模糊程度
        val width = mBitmap.getWidth()
        val height = mBitmap.getHeight()

        overlay = Bitmap.createBitmap((width / scaleFactor).toInt(), (height / scaleFactor).toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(overlay)
        canvas.scale(1 / scaleFactor, 1 / scaleFactor)
        val paint = Paint()
        paint.flags = Paint.FILTER_BITMAP_FLAG
        canvas.drawBitmap(mBitmap, 0f, 0f, paint)

        overlay = FastBlur.doBlur(overlay, radius.toInt(), true)
        return overlay
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
}
