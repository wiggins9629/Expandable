package com.wiggins.expandable.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    public static Context mContext;
    private static List<Activity> activityList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

    /**
     * @Description 添加Activity到activityList，在onCreate()中调用
     */
    public static void addActivity(Activity activity) {
        if (activityList != null && activityList.size() > 0) {
            if (!activityList.contains(activity)) {
                activityList.add(activity);
            }
        } else {
            activityList.add(activity);
        }
    }

    /**
     * @Description 结束Activity到activityList，在onDestroy()中调用
     */
    public static void finishActivity(Activity activity) {
        if (activity != null && activityList != null && activityList.size() > 0) {
            activityList.remove(activity);
        }
    }

    /**
     * @Description 结束所有Activity
     */
    public static void finishAllActivity() {
        if (activityList != null && activityList.size() > 0) {
            for (Activity activity : activityList) {
                if (null != activity) {
                    activity.finish();
                }
            }
        }
        activityList.clear();
    }
}
