package com.harlan.jxust;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.harlan.jxust.bean.User;
import com.harlan.jxust.utils.FileUtil;
import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.Bmob;

/**
 * Created by Harlan on 2016/4/6.
 */
public class WeCoderApplication extends Application {

    private static WeCoderApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);

        Bmob.DEBUG = true;
        //初始化
        Logger.init("WeCoder");
        //只有主进程运行的时候才需要初始化
        if (getApplicationInfo().packageName.equals(getMyProcessName())) {
            //im初始化
            BmobIM.init(this);
            //注册消息接收器
            BmobIM.registerDefaultMessageHandler(new WeCoderMessageHandler(this));
        }

        //初始化APP文件夹
        FileUtil.createWeCoderDir();
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

    /**
     * 获取当前运行的进程名
     *
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
