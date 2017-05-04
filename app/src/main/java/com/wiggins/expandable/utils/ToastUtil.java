package com.wiggins.expandable.utils;


import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wiggins.expandable.R;
import com.wiggins.expandable.app.MyApplication;

/**
 * @Description 通用Toast提示
 * @Author 一花一世界
 */
public class ToastUtil {

    private volatile static ToastUtil globalBoast = null;
    private final static int posY = 300;
    private Toast internalToast;

    private ToastUtil(Toast toast) {
        if (toast == null) {
            throw new NullPointerException("Toast requires a non-null parameter.");
        }
        internalToast = toast;
    }

    public static Context getContext() {
        return MyApplication.getContext();
    }

    public static void showText(CharSequence text) {
        ToastUtil.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static void showText(CharSequence text, int duration) {
        ToastUtil.makeText(getContext(), text, duration).show();
    }

    public static void showText(int resId) throws Resources.NotFoundException {
        ToastUtil.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
    }

    public static void showText(int resId, int duration) throws Resources.NotFoundException {
        ToastUtil.makeText(getContext(), resId, duration).show();
    }

    public static void showView(View view) {
        ToastUtil.makeView(getContext(), view, Toast.LENGTH_SHORT).show();
    }

    public static void showView(View view, int duration) {
        ToastUtil.makeView(getContext(), view, duration).show();
    }

    public static ToastUtil makeText(Context context, CharSequence text, int duration) {
        return new ToastUtil(InnerCreater(context, text, duration));
    }

    public static ToastUtil makeText(Context context, int resId, int duration) throws Resources.NotFoundException {
        return new ToastUtil(InnerCreater(context, resId, duration));
    }

    public static ToastUtil makeView(Context context, View vContent, int duration) {
        return new ToastUtil(InnerCreater(context, vContent, duration));
    }

    private static Toast InnerCreater(Context context, CharSequence info, int duration) {
        Toast returnValue = null;
        if (isContext()) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View vContent = inflater.inflate(R.layout.activity_toast, null);
            TextView mTvContent = (TextView) vContent.findViewById(R.id.tv_content);
            mTvContent.setText(info);
            mTvContent.bringToFront();
            returnValue = new Toast(context);
            returnValue.setGravity(Gravity.BOTTOM, 0, posY);
            returnValue.setDuration(duration);
            returnValue.setView(vContent);
        }
        return returnValue;
    }

    private static Toast InnerCreater(Context context, int resId, int duration) {
        Toast returnValue = null;
        if (isContext()) {
            returnValue = InnerCreater(context, context.getResources().getString(resId), duration);
        }
        return returnValue;
    }

    private static Toast InnerCreater(Context context, View vContent, int duration) {
        Toast returnValue = null;
        if (isContext() && vContent != null) {
            returnValue = new Toast(context);
            returnValue.setGravity(Gravity.CENTER, 0, posY);
            returnValue.setDuration(duration);
            returnValue.setView(vContent);
        }
        return returnValue;
    }

    public void show() {
        show(true);
    }

    public void show(boolean cancelCurrent) {
        if (cancelCurrent && (globalBoast != null)) {
            globalBoast.cancel();
        }
        if (internalToast != null) {
            internalToast.show();
        }
        globalBoast = this;
    }

    public void cancel() {
        if (internalToast != null) {
            internalToast.cancel();
        }
    }

    private static boolean isContext() {
        if (getContext() != null) {
            return true;
        } else {
            return false;
        }
    }
}
