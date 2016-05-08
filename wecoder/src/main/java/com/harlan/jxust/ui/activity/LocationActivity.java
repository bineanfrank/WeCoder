package com.harlan.jxust.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.*;
import com.harlan.jxust.ui.activity.BaseActivity;
import com.harlan.jxust.utils.PreferencesUtil;
import com.harlan.jxust.wecoder.R;

import butterknife.Bind;

/**
 * 选取地理位置、展现地理位置
 */
public class LocationActivity extends BaseActivity implements OnGetGeoCoderResultListener {

    public static final int SEND = 0;
    public static final String TYPE = "type";
    public static final String TYPE_SELECT = "select";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String TYPE_SCAN = "scan";
    public static final String ADDRESS = "address";
    private static BDLocation mLastLoc = null;
    private LocationClient mLocClient;
    private MyLocationListener mListener = new MyLocationListener();
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.map)
    MapView mMapView;
    private BaiduMap mBaiduMap;
    private BaiduReceiver mReceiver;
    private GeoCoder mGeoCoder = null;
    private BitmapDescriptor mDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.chat_location_activity_icon_geo);
    private String mIntentType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        setupToolbar();
        initBaiduMap();
    }

    private void setupToolbar() {
        mToolbar.setBackgroundColor(getColorPrimary());
        mToolbar.setTitle("位置");
        mToolbar.setTitleTextAppearance(this, android.R.style.TextAppearance_Medium);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.collapseActionView();
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initBaiduMap() {
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMaxAndMinZoomLevel(18, 13);
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new BaiduReceiver();
        registerReceiver(mReceiver, iFilter);

        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(this);

        Intent intent = getIntent();
        mIntentType = intent.getStringExtra(TYPE);
        if (mIntentType.equals(TYPE_SELECT)) {
            // 选择发送位置
            // 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);
            MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mDescriptor);
            mBaiduMap.setMyLocationConfigeration(config);
            // 定位初始化
            mLocClient = new LocationClient(this);
            mLocClient.registerLocationListener(mListener);
            LocationClientOption option = new LocationClientOption();
            option.setProdName("WeCoder");
            option.setOpenGps(true);
            option.setCoorType("bd09ll");
            option.setScanSpan(1000);
            option.setOpenGps(true);
            option.setIsNeedAddress(true);
            option.setIgnoreKillProcess(true);
            mLocClient.setLocOption(option);
            mLocClient.start();
            if (mLocClient != null && mLocClient.isStarted()) {
                mLocClient.requestLocation();
            }
            if (mLastLoc != null) {
                // 显示在地图上
                LatLng ll = new LatLng(mLastLoc.getLatitude(), mLastLoc.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        } else {
            Bundle b = intent.getExtras();
            LatLng latlng = new LatLng(b.getDouble(LATITUDE), b.getDouble(LONGITUDE));//维度在前，经度在后
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
            OverlayOptions ooA = new MarkerOptions().position(latlng).icon(mDescriptor).zIndex(9);
            mBaiduMap.addOverlay(ooA);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_location_send, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_send_location) {
            gotoChatPage();
        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoChatPage() {
        if (mLastLoc != null) {
            Intent intent = new Intent();
            intent.putExtra(LATITUDE, mLastLoc.getLatitude());// 经度
            intent.putExtra(LONGITUDE, mLastLoc.getLongitude());// 维度
            intent.putExtra(ADDRESS, mLastLoc.getAddrStr());
            setResult(RESULT_OK, intent);
            this.finish();
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult arg0) {
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            System.out.println("something wrong occurred!");
            return;
        }
        mLastLoc.setAddrStr(result.getAddress());
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
        mLastLoc = null;
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mLocClient != null && mLocClient.isStarted()) {
            // 退出时销毁定位
            mLocClient.stop();
        }
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        // 取消监听 SDK 广播
        unregisterReceiver(mReceiver);
        super.onDestroy();
        // 回收 bitmap 资源
        mDescriptor.recycle();
    }

    public static void startToSeeLocationDetail(Context ctx, double latitude, double longitude) {
        Intent intent = new Intent(ctx, LocationActivity.class);
        intent.putExtra(LocationActivity.TYPE, LocationActivity.TYPE_SCAN);
        intent.putExtra(LocationActivity.LATITUDE, latitude);
        intent.putExtra(LocationActivity.LONGITUDE, longitude);
        ctx.startActivity(intent);
    }

    public static void startToSelectLocationForResult(Activity from, int requestCode) {
        Intent intent = new Intent(from, LocationActivity.class);
        intent.putExtra(LocationActivity.TYPE, LocationActivity.TYPE_SELECT);
        from.startActivityForResult(intent, requestCode);
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;

            if (mLastLoc != null) {
                if (mLastLoc.getLatitude() == location.getLatitude() && mLastLoc.getLongitude() == location.getLongitude()) {
                    mLocClient.stop();
                    return;
                }
            }
            mLastLoc = location;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            String address = location.getAddrStr();
            if (address != null && !address.equals("")) {
                mLastLoc.setAddrStr(address);
            } else {
                // 反Geo搜索
                mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
            }
            // 显示在地图上
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(u);
            //设置按钮可点击
        }
    }

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class BaiduReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                System.out.println("SDKInitializer -----> ERROR Permission");
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                System.out.println("SDKInitializer -----> ERROR Network");
            }
        }
    }
}
