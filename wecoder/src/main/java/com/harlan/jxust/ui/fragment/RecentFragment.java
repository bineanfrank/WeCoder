package com.harlan.jxust.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.harlan.jxust.ui.activity.ChatActivity;
import com.harlan.jxust.ui.adapter.RecentAdapter;
import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;
import com.harlan.jxust.utils.ScreenUtil;
import com.harlan.jxust.wecoder.R;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;

/**
 * Created by Harlan on 2016/4/5.
 */
public class RecentFragment extends BaseFragment {

    private static final String TAG = "RecentFragment";

    @Bind(R.id.recent)
    RecyclerView mRecents;

    RecentAdapter mRecentAdapter;
    LinearLayoutManager mLLManager;

    @Override
    protected void initView() {
        mRecentAdapter = new RecentAdapter();
        mLLManager = new LinearLayoutManager(getActivity());
        mRecents.setAdapter(mRecentAdapter);
        mRecents.setLayoutManager(mLLManager);
        mRecentAdapter.setOnRVClickListener(new OnRVClickListener() {
            @Override
            public void onItemClick(int position) {

                Logger.d("Start conversation from Recents!" + mRecentAdapter.getItem(position).getConversationTitle());
                Bundle bundle = new Bundle();
                BmobIMConversation c = mRecentAdapter.getItem(position);
                bundle.putSerializable("c", c);
                startActivity(ChatActivity.class, bundle);
            }

            @Override
            public boolean onItemLongClick(int position) {
                showDialog(position);
                return true;
            }
        });
    }

    @Override
    protected void initData() {
        query();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recent;
    }

    /**
     * 查询本地会话
     */
    public void query() {
        mRecentAdapter.bindDatas(BmobIM.getInstance().loadAllConversation());
        mRecentAdapter.notifyDataSetChanged();
    }

    private void showDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确认删除？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mRecentAdapter.getItem(position).delete();
                mRecentAdapter.remove(position);
            }
        });
        builder.show();
    }

    /**
     * 注册离线消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event) {
        //重新刷新列表
        mRecentAdapter.bindDatas(BmobIM.getInstance().loadAllConversation());
        mRecentAdapter.notifyDataSetChanged();
    }

    /**
     * 注册消息接收事件
     *
     * @param event 1、与用户相关的由开发者自己维护，SDK内部只存储用户信息
     *              2、开发者获取到信息后，可调用SDK内部提供的方法更新会话
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        //重新获取本地消息并刷新列表
        mRecentAdapter.bindDatas(BmobIM.getInstance().loadAllConversation());
        mRecentAdapter.notifyDataSetChanged();
    }
}
