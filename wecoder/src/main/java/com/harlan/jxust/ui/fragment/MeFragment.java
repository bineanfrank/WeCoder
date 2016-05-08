package com.harlan.jxust.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.harlan.jxust.bean.User;
import com.harlan.jxust.event.FinishEvent;
import com.harlan.jxust.model.UserModel;
import com.harlan.jxust.ui.activity.BaseActivity;
import com.harlan.jxust.ui.activity.ImageSelectorActivity;
import com.harlan.jxust.ui.activity.MyInfoActivity;
import com.harlan.jxust.ui.activity.SettingActivity;
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

/**
 * Created by Harlan on 2016/4/5.
 */
public class MeFragment extends BaseFragment {

    private static final String TAG = "MeFragment";

    @Bind(R.id.rl_setting)
    RelativeLayout rl_setting;
    @Bind(R.id.id_me_wrapper)
    View wrapperView;
    @Bind(R.id.iv_avatar)
    SuperImageView iv_avatar;
    @Bind(R.id.tv_name)
    TextView tv_name;
    @Bind(R.id.iv_sex)
    SuperImageView iv_sex;
    @Bind(R.id.tv_userid)
    TextView tv_userid;
    @Bind(R.id.rl_album)
    RelativeLayout rl_album;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    protected void initView() {

    }

    private User curUser;

    @Override
    protected void initData() {
        curUser = UserModel.getInstance().getCurrentUser();
        iv_avatar.loadImageDefault(curUser.getAvatar());
        iv_sex.loadLocalImage(curUser.getSex() == 1 ? R.drawable.ic_sex_male : R.drawable.ic_sex_female);
        tv_name.setText(TextUtils.isEmpty(curUser.getNick()) ? "未设置" : curUser.getNick());
        tv_userid.setText("WeCoder号: " + curUser.getUsername());
    }

    @OnClick(R.id.rl_myinfo)
    public void onProfileClick(View view) {
        Intent intent = new Intent(getActivity(), MyInfoActivity.class);
        intent.putExtra("from", MyInfoActivity.FROM_SELF);
        startActivity(intent);
    }

    @OnClick(R.id.rl_setting)
    public void onSettingClick(View view) {
        Intent intent = new Intent(MeFragment.this.getActivity(), SettingActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rl_album)
    public void onAlbumClick(View view) {
        ImageSelector.open(this, ImageConfigUtil.getSingleChoiceImageConfig(
                ((BaseActivity) getActivity()).getColorPrimaryDark(),
                ((BaseActivity) getActivity()).getColorPrimary()));
    }

    @Subscribe
    public void onEventMainThread(FinishEvent event) {
        getActivity().finish();
    }
}
