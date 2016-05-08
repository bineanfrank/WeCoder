package com.harlan.jxust;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.harlan.jxust.bean.User;
import com.harlan.jxust.utils.FileUtil;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.bmob.newim.BmobIM;

/**
 * Created by Harlan on 2016/4/6.
 */
public class WeCoderApplication extends Application {

    private static WeCoderApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
        Logger.init("WeCoder");
        //NewIM初始化
        BmobIM.init(this);
        //初始化APP文件夹
        FileUtil.createWeCoderDir();
        //注册消息接收器
        BmobIM.registerDefaultMessageHandler(new WeCoderMessageHandler(this));
        //初始化百度地图
        SDKInitializer.initialize(this);
    }

    public static WeCoderApplication getInstance() {
        return sInstance;
    }

    private void setInstance(WeCoderApplication app) {
        setWeCoderApplication(app);
    }

    private static void setWeCoderApplication(WeCoderApplication a) {
        WeCoderApplication.sInstance = a;
    }
}
