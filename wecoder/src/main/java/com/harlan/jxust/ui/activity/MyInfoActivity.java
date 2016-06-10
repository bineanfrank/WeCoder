package com.harlan.jxust.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.harlan.jxust.bean.AddFriendMessage;
import com.harlan.jxust.bean.Friend;
import com.harlan.jxust.bean.User;
import com.harlan.jxust.event.ChatEvent;
import com.harlan.jxust.model.UserModel;
import com.harlan.jxust.ui.view.imageview.SuperImageView;
import com.harlan.jxust.utils.ImageConfigUtil;
import com.harlan.jxust.utils.ImageSelector;
import com.harlan.jxust.utils.PinyinUtil;
import com.harlan.jxust.utils.ScreenUtil;
import com.harlan.jxust.utils.SnackbarUtil;
import com.harlan.jxust.wecoder.R;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Harlan on 2016/4/20.
 */
public class MyInfoActivity extends BaseActivity {

    @Bind(R.id.re_avatar)
    RelativeLayout rl_avatar;
    @Bind(R.id.re_name)
    RelativeLayout rl_name;
    @Bind(R.id.re_wecoderid)
    RelativeLayout rl_fxid;
    @Bind(R.id.re_sex)
    RelativeLayout rl_sex;
    @Bind(R.id.re_sign)
    RelativeLayout rl_sign;
    @Bind(R.id.iv_avatar)
    SuperImageView iv_avatar;
    @Bind(R.id.tv_name)
    TextView tv_name;
    @Bind(R.id.tv_wecoderid)
    TextView tv_wecoderid;
    @Bind(R.id.tv_sex)
    TextView tv_sex;
    @Bind(R.id.tv_sign)
    TextView tv_sign;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.rl_start_chat)
    RelativeLayout rl_start_chat;
    @Bind(R.id.rl_add_as_friend)
    RelativeLayout rl_add_as_friend;

    private User user;

    public static final int FROM_SELF = 0x1;

    private static boolean isSelf = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        initView();
    }

    private void initView() {

        Intent intent = getIntent();
        if (intent.getIntExtra("from", 0) == FROM_SELF) {
            user = UserModel.getInstance().getCurrentUser();
        } else {
            user = (User) intent.getSerializableExtra("user");
            isSelf = false;
        }

        if (!isSelf) {
            if (!isMyFriend(user.getObjectId())) {
                //不是我自己也不是我的好友
                rl_add_as_friend.setVisibility(View.VISIBLE);
                rl_start_chat.setVisibility(View.GONE);
            } else {
                //不是我自己，但是我的好友
                rl_start_chat.setVisibility(View.VISIBLE);
                rl_add_as_friend.setVisibility(View.GONE);
            }
        } else {
            rl_add_as_friend.setVisibility(View.GONE);
            rl_start_chat.setVisibility(View.GONE);
        }

        //先确定是否是当前用户还是好友在设置Toolbar，才能确定title
        setupToolbar();

        String nick = user.getNick();
        if (null != nick && nick.length() > 0) {
            tv_name.setText(nick);
        } else {
            tv_name.setText("未设置");
        }
        tv_wecoderid.setText(user.getUsername());
        if (user.getSex() == 1) {
            tv_sex.setText("男");
        } else if (user.getSex() == 0) {
            tv_sex.setText("女");
        } else {
            tv_sex.setText("未公开");
        }
        String sign = user.getSign();
        if (sign != null && sign.length() > 0) {
            tv_sign.setText(sign);
        } else {
            tv_sign.setText("未设置");
        }
        iv_avatar.loadImageDefault(user.getAvatar());
    }

    private boolean isMyFriend(String objectId) {
        List<Friend> list = UserModel.getInstance().getContacts();
        if (list != null && list.size() > 0) {
            for (Friend friend : list) {
                if (friend.getFriendUser().getObjectId().equals(objectId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setupToolbar() {
        mToolbar.setBackgroundColor(getColorPrimary());
        if (isSelf) {
            mToolbar.setTitle("个人信息");
        } else {
            mToolbar.setTitle(user.getNick() == null ? user.getObjectId() + "的个人信息" : user.getNick() + "的个人信息");
        }
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

    @OnClick(R.id.re_sex)
    public void onSexClick(View view) {

        if (!isSelf) return;

        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.context_menu_alert_dialog);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ScreenUtil.dpToPx(280);
        window.setAttributes(lp);

        TextView tv_male = (TextView) window.findViewById(R.id.tv_selection_1);
        TextView tv_female = (TextView) window.findViewById(R.id.tv_selection_2);
        tv_male.setText(R.string.gender_male);
        tv_female.setText(R.string.gender_female);

        tv_male.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (user.getSex() == 0) {
                    updateSex(1);
                }
                dlg.dismiss();
            }
        });
        tv_female.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (user.getSex() == 1) {
                    updateSex(0);
                }
                dlg.dismiss();
            }
        });
    }

    @OnClick(R.id.re_avatar)
    public void onAvatarClick(View view) {
        if (!isSelf) return;
        ImageSelector.open(this, ImageConfigUtil.getSingleChoiceImageConfig(getColorPrimaryDark(), getColorPrimary()));
    }

    @OnClick(R.id.re_name)
    public void onNickClick(View view) {
        if (!isSelf) return;
        final EditText mEditText = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("更改昵称");
        builder.setView(mEditText, ScreenUtil.dpToPx(24), ScreenUtil.dpToPx(8), ScreenUtil.dpToPx(24), ScreenUtil.dpToPx(4));
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
                String s = mEditText.getText().toString();
                if (s.length() > 0) {
                    updateName(s);
                }
            }
        });
        builder.show();
    }

    @OnClick(R.id.re_sign)
    public void onSignClick(View view) {
        if (!isSelf) return;
        final EditText mEditText = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("更改签名");
        builder.setView(mEditText, ScreenUtil.dpToPx(24), ScreenUtil.dpToPx(8), ScreenUtil.dpToPx(24), ScreenUtil.dpToPx(4));
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
                String s = mEditText.getText().toString();
                if (s.length() > 0) {
                    updateSign(s);
                }
            }
        });
        builder.show();
    }

    @OnClick(R.id.rl_start_chat)
    public void onStartChatClick(View view) {
        System.out.println("onStartChatClick" + user.getObjectId() + " " + user.getUsername() + " " + user.getAvatar());
        BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar());
        EventBus.getDefault().post(new ChatEvent(info));
    }

    @OnClick(R.id.rl_add_as_friend)
    public void onAddFriendClick(View view) {
        BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar());
        //启动一个会话，如果isTransient设置为true,则不会创建在本地会话表中创建记录，
        //设置isTransient设置为false,则会在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
        BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, true, null);
        //这个obtain方法才是真正创建一个管理消息发送的会话
        BmobIMConversation conversation = BmobIMConversation.obtain(BmobIMClient.getInstance(), c);
        AddFriendMessage msg = new AddFriendMessage();
        User currentUser = BmobUser.getCurrentUser(this, User.class);
        msg.setContent("很高兴认识你，可以加个好友吗?");//给对方的一个留言信息
        Map<String, Object> map = new HashMap<>();
        map.put("name", currentUser.getUsername());//发送者姓名，这里只是举个例子，其实可以不需要传发送者的信息过去
        map.put("avatar", currentUser.getAvatar());//发送者的头像
        map.put("uid", currentUser.getObjectId());//发送者的uid
        msg.setExtraMap(map);
        conversation.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {//发送成功
//                    SnackbarUtil.show(MyInfoActivity.this, "好友请求发送成功，等待验证");
                    Toast.makeText(MyInfoActivity.this, "好友请求发送成功，等待验证", Toast.LENGTH_SHORT).show();
                } else {//发送失败
//                    SnackbarUtil.show(MyInfoActivity.this, "发送失败！");
                    Toast.makeText(MyInfoActivity.this, "发送失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                    startActivity(ChatActivity.class, bundle, false);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            updateAvatar(pathList.get(0));
        }
    }

    private void updateAvatar(final String avatar) {
        if (avatar == null) return;

        final BmobFile file = new BmobFile(new File(avatar));
        file.upload(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                user.setAvatar(file.getFileUrl(MyInfoActivity.this));
                user.update(MyInfoActivity.this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        iv_avatar.loadImage(file.getFileUrl(MyInfoActivity.this), false, R.drawable.default_avatar);
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                SnackbarUtil.show(MyInfoActivity.this, "头像更新失败！");
            }
        });
    }

    private void updateSex(final int sex) {
        if (user == null) return;
        user.setSex(sex);
        user.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                tv_sex.setText(sex == 1 ? "男" : "女");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    private void updateName(final String name) {
        if (user == null) return;
        user.setNick(name);
        user.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                tv_name.setText(name);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    private void updateSign(final String sign) {
        if (user == null) return;
        user.setSign(sign);
        user.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                tv_sign.setText(sign);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }
}
