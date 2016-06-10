package com.harlan.jxust.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.harlan.jxust.bean.Friend;
import com.harlan.jxust.bean.User;
import com.harlan.jxust.event.ChatEvent;
import com.harlan.jxust.model.UserModel;
import com.harlan.jxust.ui.activity.NewFriendActivity;
import com.harlan.jxust.ui.adapter.ContactAdapter;
import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;
import com.harlan.jxust.ui.activity.ChatActivity;
import com.harlan.jxust.ui.view.SideBar;
import com.harlan.jxust.utils.SnackbarUtil;
import com.harlan.jxust.wecoder.R;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Harlan on 2016/4/9.
 */
public class ContactFragment extends BaseFragment {

    private static final String TAG = "ContactFragment";

    @Bind(R.id.contact)
    RecyclerView mContacts;

    ContactAdapter mContactAdapter;
    LinearLayoutManager mLLManager;

    @Bind(R.id.id_contact_wrapper)
    View view;

    @Bind(R.id.progress)
    ProgressBar mProgressbar;

    @Bind(R.id.sidebar)
    SideBar sideBar;

    private static final int MSG_TYPE_VISIBLE = 0x1;
    private static final int MSG_TYPE_GONE = 0x2;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TYPE_GONE:
                    mProgressbar.setVisibility(View.GONE);
                    break;
                case MSG_TYPE_VISIBLE:
                    mProgressbar.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initView() {
        mContactAdapter = new ContactAdapter();
        mLLManager = new LinearLayoutManager(getActivity());
        mContacts.setAdapter(mContactAdapter);
        mContacts.setLayoutManager(mLLManager);
        sideBar.setRecyclerView(mContacts);
        mContactAdapter.setOnRVClickListener(new OnRVClickListener() {
            @Override
            public void onItemClick(int position) {
                System.out.println("position = " + position);
                if (position == 0) {
//                    SnackbarUtil.show(view, "HeaderView Clicked");
                    startActivity(NewFriendActivity.class, null);
                } else if (position == mContactAdapter.getItemCount() - 1) {
//                    SnackbarUtil.show(view, "FooterView Clicked");
                } else {
                    Friend friend = mContactAdapter.getItem(position);
                    User user = friend.getFriendUser();
                    BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar());
                    //启动一个会话，实际上就是在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
                    BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, null);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("c", c);
                    startActivity(ChatActivity.class, bundle);
                }
            }

            @Override
            public boolean onItemLongClick(final int position) {
                if (position != 0) {
                    UserModel.getInstance().deleteFriend(mContactAdapter.getItem(position), new DeleteListener() {
                        @Override
                        public void onSuccess() {
                            mContactAdapter.remove(position);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                        }
                    });
                    return true;

                }
                return true;
            }
        });
    }

    /**
     * 刷新列表
     *
     * @param list
     */
    private void refresh(List<Friend> list) {
        Collections.sort(list, new Comparator<Friend>() {
            @Override
            public int compare(Friend lhs, Friend rhs) {
                char py1 = lhs.getFriendUser().getTopc().charAt(0);
                char py2 = rhs.getFriendUser().getTopc().charAt(0);
                if (py1 == py2) return 0;
                return py1 < py2 ? -1 : 1;
            }
        });
        mContactAdapter.setDatas(list);
        mContactAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onEventMainThread(ChatEvent event) {
        final BmobIMUserInfo info = event.info;
        //如果需要更新用户资料，开发者只需要传新的info进去就可以了
        Logger.d("Chat with User + " + info.getName());
        BmobIM.getInstance().startPrivateConversation(info, new ConversationListener() {
            @Override
            public void done(BmobIMConversation c, BmobException e) {
                if (e == null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("c", c);
                    startActivity(ChatActivity.class, bundle);
                } else {
                    SnackbarUtil.show(view, e.getMessage() + "(" + e.getErrorCode() + ")");
                }
            }
        });
    }

    @Override
    protected void initData() {
        handler.sendEmptyMessage(MSG_TYPE_VISIBLE);
        List<Friend> list = UserModel.getInstance().getContacts();
        if (list == null || list.size() <= 0) {
            UserModel.getInstance().queryFriends(new FindListener<Friend>() {
                @Override
                public void onSuccess(List<Friend> list) {
                    //SnackbarUtil.show(view, "共找到" + list.size() + "位联系人");
                    refresh(list);
                    handler.sendEmptyMessage(MSG_TYPE_GONE);
                }

                @Override
                public void onError(int i, String s) {
                    SnackbarUtil.show(view, "Error: " + s);
                    handler.sendEmptyMessage(MSG_TYPE_GONE);
                }
            });
        } else {
            //SnackbarUtil.show(view, "从缓存上共找到" + list.size() + "位联系人");
            refresh(list);
            handler.sendEmptyMessage(MSG_TYPE_GONE);
        }
    }
}
