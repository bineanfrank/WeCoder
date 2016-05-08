package com.harlan.jxust.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.harlan.jxust.bean.User;
import com.harlan.jxust.event.FinishEvent;
import com.harlan.jxust.model.UserModel;
import com.harlan.jxust.ui.adapter.listener.TextWatcher;
import com.harlan.jxust.utils.SnackbarUtil;
import com.harlan.jxust.wecoder.R;

import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by Harlan on 2016/4/5.
 */
public class LoginActivity extends BaseActivity {

    @Bind(R.id.et_account)
    EditText et_account;

    @Bind(R.id.et_password)
    EditText et_password;

    @Bind(R.id.btn_login)
    Button btn_login;

    @Bind(R.id.tv_register)
    TextView tv_register;

    @Bind(R.id.tv_wenti)
    TextView tv_wenti;

    @Bind(R.id.id_login_wrapper)
    View wrapperView;

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
        setContentView(R.layout.activity_login);
        initView();
        setupToolbar();
    }

    private void setupToolbar() {
        mToolbar.setBackgroundColor(getColorPrimary());
        mToolbar.setTitle("");
        mToolbar.collapseActionView();
        setSupportActionBar(mToolbar);
    }

    private void initView() {
        et_account.addTextChangedListener(new TextWatcher(btn_login));
    }

    @OnClick(R.id.btn_login)
    public void onLoginClick(final View view) {
        //hideKeyboard();
        hideSoftInputView();
        handler.sendEmptyMessage(MSG_TYPE_VISIBLE);
        UserModel.getInstance().login(et_account.getText().toString(), et_password.getText().toString(), new LogInListener() {
            @Override
            public void done(Object o, BmobException e) {
                handler.sendEmptyMessage(MSG_TYPE_GONE);
                if (e == null) {
                    User user = (User) o;
                    //更新当前用户资料，此时应该将UserModel里暂存的联系人删除
                    //因为这是新登陆的用户，不能使用缓存中的用户
                    UserModel.getInstance().clearContacts();
                   // System.out.println(user.getUsername() + "'s Topc = " + user.getTopc() + " and id = " + user.getId());
                    BmobIM.getInstance().updateUserInfo(new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar()));
                    startActivity(MainActivity.class, null, true);
                } else {
                    SnackbarUtil.show(wrapperView, "Error: " + e.getMessage());
                }
            }
        });
    }

    private static final int REGISTER_REQUEST_CODE = 0x1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REGISTER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                SnackbarUtil.show(wrapperView, "注册成功，请输入密码登陆", true);
                String username = data.getStringExtra("username");
                et_account.setText(username);
                et_password.setText("");
            } else {
                SnackbarUtil.show(wrapperView, "注册失败！");
                et_account.setText("");
                et_password.setText("");
            }
        }
    }

    @OnClick(R.id.tv_register)
    public void onRegisterClick(View view) {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(registerIntent, REGISTER_REQUEST_CODE);
    }

    @Subscribe
    public void onEventMainThread(FinishEvent event) {
        finish();
    }
}
