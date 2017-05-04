package com.wiggins.expandable.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.wiggins.expandable.app.MyApplication;

/**
 * @Description dp和px转换
 * @Author 一花一世界
 */
public class DensityUtil {

    public static Context getContext() {
        return MyApplication.getContext();
    }

    /**
     * @Description 获取屏幕宽度
     */
    public static int getScreenWidth() {
        DisplayMetrics displayMetric = Resources.getSystem().getDisplayMetrics();
        return displayMetric.widthPixels;
    }

    /**
     * @Description 获取屏幕高度
     */
    public static int getScreenHeight() {
        DisplayMetrics displayMetric = Resources.getSystem().getDisplayMetrics();
        return displayMetric.heightPixels;
    }

    /**
     * @Description 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * @Description 根据手机的分辨率将 sp 值转换为 px 值，保证文字大小不变
     */
    public static int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * @Description 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @Description 获取Android设置屏幕相关信息
     */
    public static void getScreenProperty() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;// 屏幕宽度（像素）
        int height = dm.heightPixels; // 屏幕高度（像素）
        float density = dm.density;//屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;//屏幕密度dpi（120 / 160 / 240）
        //屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);//屏幕宽度(dp)
        int screenHeight = (int) (height / density);//屏幕高度(dp)
        LogUtil.e(Constant.LOG_TAG, "屏幕宽度:" + width + "   屏幕高度:" + height
                + "   屏幕密度:" + density + "   屏幕密度dpi:" + densityDpi
                + "   屏幕宽度:" + screenWidth + "    屏幕高度:" + screenHeight);
    }
}
