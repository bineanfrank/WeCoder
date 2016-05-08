package com.harlan.jxust.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;

import com.harlan.jxust.bean.User;
import com.harlan.jxust.model.UserModel;
import com.harlan.jxust.wecoder.R;


/**
 * Created by Harlan on 2016/4/5.
 */
public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);        // 隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // 隐藏状态栏
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                User user = UserModel.getInstance().getCurrentUser();
                //User user = null;
                if (user == null) {
                    startActivity(LoginActivity.class, null, true);
                } else {
                    startActivity(MainActivity.class, null, true);
                }
            }
        }, 2000);
    }
}
