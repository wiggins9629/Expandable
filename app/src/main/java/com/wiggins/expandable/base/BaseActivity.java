package com.wiggins.expandable.base;

import android.app.Activity;
import android.os.Bundle;

import com.wiggins.expandable.app.MyApplication;

/**
 * @Description 所有Activity的基类
 * @Author 一花一世界
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.finishActivity(this);
    }
}
