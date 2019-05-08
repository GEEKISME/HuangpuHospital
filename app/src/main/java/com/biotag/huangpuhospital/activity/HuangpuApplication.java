package com.biotag.huangpuhospital.activity;

import android.app.Application;
import android.content.Context;

import com.biotag.huangpuhospital.common.CrashHandler;
import com.biotag.huangpuhospital.javabean.MyObjectBox;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;

import io.objectbox.BoxStore;

public class HuangpuApplication extends Application {
    private BoxStore boxStore;
    protected static HuangpuApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Logger.addLogAdapter(new AndroidLogAdapter());
        //box 的对象存储
        boxStore = MyObjectBox.builder().androidContext(HuangpuApplication.this).build();
        //
        //全局异常捕获
        CrashHandler crashHandler = CrashHandler.getsInstance();
        crashHandler.init(this);
        //=====================
        //bugly 异常捕获以及上传
        CrashReport.initCrashReport(getApplicationContext(),"bc3e97dca9",true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
    public static HuangpuApplication getApplication(){
        return mInstance;
    }
    public BoxStore getBoxStore(){
        return boxStore;
    }
}
