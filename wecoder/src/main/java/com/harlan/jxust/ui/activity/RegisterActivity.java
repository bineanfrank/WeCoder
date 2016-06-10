package com.harlan.jxust.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.harlan.jxust.event.FinishEvent;
import com.harlan.jxust.model.UserModel;
import com.harlan.jxust.ui.adapter.listener.TextWatcher;
import com.harlan.jxust.ui.view.imageview.SuperImageView;
import com.harlan.jxust.utils.ImageConfigUtil;
import com.harlan.jxust.utils.ImageSelector;
import com.harlan.jxust.utils.SnackbarUtil;
import com.harlan.jxust.wecoder.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by Harlan on 2016/4/11.
 */
public class RegisterActivity extends BaseActivity {

    private static final String TAG = "RegisterActivity";

    @Bind(R.id.rl_avatar)
    View wrapperView;

    @Bind(R.id.siv_avatar)
    SuperImageView siv_avatar;

    @Bind(R.id.et_account)
    EditText et_account;

    @Bind(R.id.et_password)
    EditText et_password;

    @Bind(R.id.et_password_again)
    EditText et_password_again;

    @Bind(R.id.btn_register)
    Button btn_register;

    @Bind(R.id.progress)
    ProgressBar mProgressBar;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private static final int MSG_TYPE_VISIBLE = 0x1;
    private static final int MSG_TYPE_GONE = 0x2;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TYPE_GONE:
                    mProgressBar.setVisibility(View.GONE);
                    break;
                case MSG_TYPE_VISIBLE:
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupToolbar();
        et_account.addTextChangedListener(new TextWatcher(btn_register));
    }

    private void setupToolbar() {
        mToolbar.setBackgroundColor(getColorPrimary());
        mToolbar.setTitle("");
        mToolbar.setTitleTextAppearance(this, android.R.style.TextAppearance_Medium);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.collapseActionView();
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @OnClick(R.id.btn_register)
    public void onRegisterClick(View view) {
        handler.sendEmptyMessage(MSG_TYPE_VISIBLE);
        //先上传头像，获取头像的连接，并注册
        //上传失败则以空头像链接注册
        register(et_account.getText().toString(),
                et_password.getText().toString(),
                et_password_again.getText().toString());
    }

    private static String avatar;

    public void register(final String username, final String password, String pwdagain) {

        //先判空
        if (TextUtils.isEmpty(username)) {
            handler.sendEmptyMessage(MSG_TYPE_GONE);
            SnackbarUtil.show(wrapperView, "请输入账号！");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            handler.sendEmptyMessage(MSG_TYPE_GONE);
            SnackbarUtil.show(wrapperView, "请输入密码！");
            return;
        }
        if (TextUtils.isEmpty(pwdagain)) {
            handler.sendEmptyMessage(MSG_TYPE_GONE);
            SnackbarUtil.show(wrapperView, "请确认密码！");
            return;
        }

        //再判密码是否相同
        if (!password.equals(pwdagain)) {
            handler.sendEmptyMessage(MSG_TYPE_GONE);
            return;
        }

        UserModel.getInstance().register(avatar, username, password, new LogInListener() {

            @Override
            public void done(Object o, BmobException e) {
                handler.sendEmptyMessage(MSG_TYPE_GONE);
                if (e == null) {
                    EventBus.getDefault().post(new FinishEvent());
                } else {
                    SnackbarUtil.show(wrapperView, "注册失败！" + e.getMessage());
                }
            }
        });
    }

    @OnClick(R.id.siv_avatar)
    public void onAvatarClick(View view) {
        ImageSelector.open(this, ImageConfigUtil.getSingleChoiceImageConfig(getColorPrimaryDark(), getColorPrimary()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            avatar = pathList.get(0);
            siv_avatar.loadImage(avatar, false, R.drawable.default_avatar);
        }
    }

    @Subscribe
    public void onEventMainThread(FinishEvent event) {
        Intent intent = new Intent();
        intent.putExtra("username", UserModel.getInstance().getCurrentUser().getUsername());
        setResult(RESULT_OK, intent);
        finish();
    }
}
