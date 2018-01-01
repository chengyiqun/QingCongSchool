package com.example.xpb.qingcongschool.app
import com.blankj.utilcode.util.Utils


import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.stetho.Stetho
import org.litepal.LitePalApplication


/**
 * Created by ${yf} on 2017/2/16.
 */
class MyApplication : LitePalApplication() {//继承了那个数据库的 application，以添加自己的初始化操作，然后定义到manifests

    override fun onCreate() {
        super.onCreate()
        //初始化Fresco
        Fresco.initialize(this)
        //初始化工具类
        Utils.init(this)

        //初始化com.facebook.stetho:stetho
        Stetho.initializeWithDefaults(this);

        scale=getScale()
        screenwidth=getWidth()
        //statusBarHeight=getstatusBarHeight1()
        //toolbarHeight=getToolbarHeight()
        //screenHeight=getHeight()
        courceLayoutHeightPX=com.example.xpb.qingcongschool.util.Utils.dip2px(courceLayoutHeightDPI)
    }

    private fun getWidth(): Int {
        val resources = this.resources
        val displayMetrics = resources.displayMetrics
        return displayMetrics.widthPixels
    }

    private fun getScale(): Float {
        return this.resources.displayMetrics.density
    }

    /*private fun getstatusBarHeight1():Int{
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            return resources.getDimensionPixelSize(resourceId)
        }else{
            return -1
        }
    }*/

    /*private fun getToolbarHeight():Int{
        return com.example.xpb.qingcongschool.util.Utils.dip2px(56f)
    }*/

    /*private fun getHeight():Int{
        return ScreenUtils.getScreenHeight()
    }*/



    companion object {
        val packagename = "com.example.xpb.qingcongschool"
        var screenwidth = -1
        //var screenHeight = -1
        var scale = 0f
        //var statusBarHeight = -1//状态栏高度
        //var toolbarHeight = -1//toolbar高度
        val courceLayoutHeightDPI=600f//课程展示VIew的高度
        var courceLayoutHeightPX= -1
    }
}
