package com.harlan.jxust.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.harlan.jxust.bean.User;
import com.harlan.jxust.model.UserModel;
import com.harlan.jxust.ui.adapter.WeCoderPagerAdapter;
import com.harlan.jxust.ui.fragment.ContactFragment;
import com.harlan.jxust.ui.fragment.MeFragment;
import com.harlan.jxust.ui.fragment.RecentFragment;
import com.harlan.jxust.ui.view.bottombar.BottomBar;
import com.harlan.jxust.ui.view.bottombar.BottomBarBadge;
import com.harlan.jxust.ui.view.bottombar.BottomBarTab;
import com.harlan.jxust.ui.view.listener.OnTabClickListener;
import com.harlan.jxust.utils.PreferencesUtil;
import com.harlan.jxust.utils.SnackbarUtil;
import com.harlan.jxust.wecoder.R;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.listener.ObseverListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.exception.BmobException;

public class MainActivity extends BaseActivity implements ObseverListener {

    private static final String TAG = MainActivity.class.getClass().getSimpleName();

    private BottomBar mBottomBar;
    private BottomBarBadge mBottomBarBadge;

    private WeCoderPagerAdapter mWeCoderAdapter;
    private List<Fragment> fragments = new ArrayList<>(3);
    private RecentFragment mWFragment;
    private ContactFragment mCFragment;
    private MeFragment mMFragment;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.content)
    ViewPager mCViewPager;

    private boolean permission_camera = false;
    private boolean permission_audio = false;

    private static final int PERMISSION_REQUEST = 0x2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBottomBar(savedInstanceState);
        setupToolbar();
        setupContent();

        connectBmobService();

        permission_audio = PreferencesUtil.getInstance(this).getPerAudio();
        permission_camera = PreferencesUtil.getInstance(this).getPerCamera();

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            if (!permission_camera || !permission_audio) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST);
            }
        }
    }

    private void connectBmobService() {
        User user = UserModel.getInstance().getCurrentUser();

        if (user == null) return;
        BmobIM.connect(user.getObjectId(), new ConnectListener() {
            @Override
            public void done(String uid, BmobException e) {
                if (e == null) {
                    Logger.i("connect success");
                } else {
                    Logger.e(e.getErrorCode() + "/" + e.getMessage());
                }
            }
        });
        //监听连接状态，也可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus status) {
                SnackbarUtil.show(mCViewPager, status.getMsg());
            }
        });
    }

    private void setupContent() {
        mCFragment = new ContactFragment();
        mMFragment = new MeFragment();
        mWFragment = new RecentFragment();

        fragments.add(mWFragment);
        fragments.add(mCFragment);
        fragments.add(mMFragment);

        mWeCoderAdapter = new WeCoderPagerAdapter(getSupportFragmentManager(), fragments);
        mCViewPager.setAdapter(mWeCoderAdapter);
        mCViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBottomBar.selectTabAtPosition(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupToolbar() {
        mToolbar.setBackgroundColor(getColorPrimary());
        mToolbar.setTitle(R.string.app_name);
        mToolbar.setTitleTextAppearance(this, android.R.style.TextAppearance_Medium);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.collapseActionView();
        setSupportActionBar(mToolbar);
    }

    private void setupBottomBar(Bundle savedInstanceState) {

        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.noNavBarGoodness();
        mBottomBar.setItems(new BottomBarTab(R.drawable.ic_wecoder_recent, "消息"),
                new BottomBarTab(R.drawable.ic_wecoder_contact, "好友"),
                new BottomBarTab(R.drawable.ic_wecoder_me, "我"));
        mBottomBarBadge = mBottomBar.makeBadgeForTabAt(0, "#FF0000", 0);
        mBottomBarBadge.setAutoShowAfterUnSelection(false);
        mBottomBar.setOnTabClickListener(new OnTabClickListener() {
            @Override
            public void onTabSelected(int i) {
                mCViewPager.setCurrentItem(i, false);
            }

            @Override
            public void onTabReSelected(int i) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // checkRedPoint();
        //添加观察者-用于是否显示通知消息
        BmobNotificationManager.getInstance(this).addObserver(this);
        //进入应用后，通知栏应取消
        BmobNotificationManager.getInstance(this).cancelNotification();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //移除观察者
        BmobNotificationManager.getInstance(this).removeObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清理导致内存泄露的资源
        BmobIM.getInstance().clear();
        //完全退出应用时需调用clearObserver来清除观察者
        BmobNotificationManager.getInstance(this).clearObserver();
    }


    /**
     * 注册消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        if (mCViewPager.getCurrentItem() != 0) {
            checkRedPoint();
        }
    }

    private void checkRedPoint() {
        int unread = (int) BmobIM.getInstance().getAllUnReadCount();
        if (unread > 0) {
            mBottomBarBadge.setCount(unread);
            mBottomBarBadge.show();
        } else {
            mBottomBarBadge.hide();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PreferencesUtil.getInstance(this).setPerCamera(true);
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                PreferencesUtil.getInstance(this).setPerAudio(true);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 注册离线消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event) {
        if (mCViewPager.getCurrentItem() != 0) {
            checkRedPoint();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        mBottomBar.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_add_friend:
                startActivity(SearchActivity.class, null, false);
                return true;
            case R.id.action_about:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}