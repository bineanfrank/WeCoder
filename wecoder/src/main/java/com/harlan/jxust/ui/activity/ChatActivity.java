package com.harlan.jxust.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.utils.BmobLog;
import com.harlan.jxust.model.UserModel;
import com.harlan.jxust.model.i.UpdateCacheListener;
import com.harlan.jxust.ui.adapter.ChatAdapter;
import com.harlan.jxust.ui.adapter.EmojiAdapter;
import com.harlan.jxust.ui.adapter.EmojiPagerAdapter;
import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;
import com.harlan.jxust.ui.adapter.viewholder.BaseViewHolder;
import com.harlan.jxust.ui.adapter.viewholder.SendImageHolder;
import com.harlan.jxust.ui.view.EmojiEditText;
import com.harlan.jxust.utils.EmojiHelper;
import com.harlan.jxust.utils.FileUtil;
import com.harlan.jxust.utils.FileUtils;
import com.harlan.jxust.utils.ImageConfigUtil;
import com.harlan.jxust.utils.ImageSelector;
import com.harlan.jxust.utils.PreferencesUtil;
import com.harlan.jxust.utils.SnackbarUtil;
import com.harlan.jxust.wecoder.R;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.newim.bean.BmobIMAudioMessage;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMLocationMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.BmobRecordManager;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.newim.listener.ObseverListener;
import cn.bmob.newim.listener.OnRecordChangeListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;

/**
 * 聊天界面
 *
 * @author :smile
 * @project:ChatActivity
 * @date :2016-01-25-18:23
 */
public class ChatActivity extends BaseActivity implements ObseverListener {

    @Bind(R.id.id_chat_wrapper)
    View wrapper;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout sw_refresh;

    @Bind(R.id.rv_record_list)
    RecyclerView rv_record_list;

    @Bind(R.id.et_msg_box)
    EmojiEditText et_msg_box;
    @Bind(R.id.edittext_layout)
    RelativeLayout edittext_layout;

    @Bind(R.id.btn_chat_add)
    Button btn_chat_add;
    @Bind(R.id.btn_chat_emoji)
    Button btn_chat_emoji;
    @Bind(R.id.btn_press_to_speak)
    Button btn_press_to_speak;
    @Bind(R.id.btn_set_mode_voice)
    Button btn_set_mode_voice;
    @Bind(R.id.btn_set_mode_keyboard)
    Button btn_set_mode_keyboard;
    @Bind(R.id.btn_chat_send)
    Button btn_chat_send;
    @Bind(R.id.layout_more)
    LinearLayout layout_more;
    @Bind(R.id.layout_add)
    LinearLayout layout_add;
    @Bind(R.id.ll_emoji)
    LinearLayout layout_emo;

    // 语音有关
    @Bind(R.id.layout_record)
    RelativeLayout layout_record;
    @Bind(R.id.tv_voice_tips)
    TextView tv_voice_tips;
    @Bind(R.id.iv_record)
    ImageView iv_record;
    @Bind(R.id.vp_emoji)
    ViewPager vp_emoji;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private Drawable[] drawable_Anims;// 话筒动画
    BmobRecordManager recordManager;

    ChatAdapter mAdapter;
    protected LinearLayoutManager layoutManager;
    BmobIMConversation c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        c = BmobIMConversation.obtain(BmobIMClient.getInstance(), (BmobIMConversation) getBundle().getSerializable("c"));

        setupToolbar();
        initSwipeLayout();
        initVoiceView();
        initBottomView();
    }

    private void setupToolbar() {
        mToolbar.setBackgroundColor(getColorPrimary());
        mToolbar.setTitle(c.getConversationTitle());
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

    private void initSwipeLayout() {
        sw_refresh.setEnabled(true);
        layoutManager = new LinearLayoutManager(this);
        rv_record_list.setLayoutManager(layoutManager);
        mAdapter = new ChatAdapter(this, c);
        rv_record_list.setAdapter(mAdapter);
        wrapper.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                wrapper.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                sw_refresh.setRefreshing(true);
                //自动刷新
                queryMessages(null);
            }
        });
        //下拉加载
        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BmobIMMessage msg = mAdapter.getFirstMessage();
                queryMessages(msg);
            }
        });

        //设置RecyclerView的点击事件
        mAdapter.setOnRVClickListener(new OnRVClickListener() {
            @Override
            public void onItemClick(int position) {
                Logger.i("" + position);
            }

            @Override
            public boolean onItemLongClick(int position) {
                //这里省了个懒，直接长按就删除了该消息
                //c.deleteMessage(mAdapter.getItem(position));
                //mAdapter.remove(position);
                return true;
            }
        });
    }

    private void initBottomView() {
        et_msg_box.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
                    scrollToBottom();
                }
                return false;
            }
        });
        et_msg_box.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                scrollToBottom();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btn_chat_send.setVisibility(View.VISIBLE);
                    btn_set_mode_voice.setVisibility(View.VISIBLE);
                    btn_chat_add.setVisibility(View.GONE);
                } else {
                    btn_chat_add.setVisibility(View.VISIBLE);
                    btn_chat_send.setVisibility(View.GONE);
                    if (btn_set_mode_voice.getVisibility() == View.GONE)
                        btn_set_mode_keyboard.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //初始化表情显示盘
        initEmojiPanel();
    }

    private void initEmojiPanel() {
        List<View> views = new ArrayList<View>();
        for (int i = 0; i < EmojiHelper.emojiGroups.size(); i++) {
            views.add(getEmotionGridView(i));
        }
        EmojiPagerAdapter pagerAdapter = new EmojiPagerAdapter(views);
        vp_emoji.setOffscreenPageLimit(3);
        vp_emoji.setAdapter(pagerAdapter);
    }

    private View getEmotionGridView(int pos) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View emotionView = inflater.inflate(R.layout.layout_emoji_gridview, null, false);
        GridView gridView = (GridView) emotionView.findViewById(R.id.gv_emojis);
        final EmojiAdapter chatEmotionGridAdapter = new EmojiAdapter(this);
        List<String> pageEmotions = EmojiHelper.emojiGroups.get(pos);
        chatEmotionGridAdapter.setDatas(pageEmotions);
        gridView.setAdapter(chatEmotionGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String emotionText = (String) parent.getAdapter().getItem(position);
                int start = et_msg_box.getSelectionStart();
                StringBuffer sb = new StringBuffer(et_msg_box.getText());
                sb.replace(et_msg_box.getSelectionStart(), et_msg_box.getSelectionEnd(), emotionText);
                et_msg_box.setText(sb.toString());
                CharSequence info = et_msg_box.getText();
                if (info instanceof Spannable) {
                    Spannable spannable = (Spannable) info;
                    Selection.setSelection(spannable, start + emotionText.length());
                }
            }
        });
        return gridView;
    }

    /**
     * 初始化语音布局
     *
     * @param
     * @return void
     */
    private void initVoiceView() {
        btn_press_to_speak.setOnTouchListener(new VoiceTouchListener());
        initVoiceAnimRes();
        initRecordManager();
    }

    /**
     * 初始化语音动画资源
     *
     * @param
     * @return void
     * @Title: initVoiceAnimRes
     */
    private void initVoiceAnimRes() {
        drawable_Anims = new Drawable[]{
                getResources().getDrawable(R.drawable.chat_icon_voice2),
                getResources().getDrawable(R.drawable.chat_icon_voice3),
                getResources().getDrawable(R.drawable.chat_icon_voice4),
                getResources().getDrawable(R.drawable.chat_icon_voice5),
                getResources().getDrawable(R.drawable.chat_icon_voice6)};
    }

    private void initRecordManager() {
        // 语音相关管理器
        recordManager = BmobRecordManager.getInstance(this);
        // 设置音量大小监听--在这里开发者可以自己实现：当剩余10秒情况下的给用户的提示，类似微信的语音那样
        recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {

            @Override
            public void onVolumnChanged(int value) {
                iv_record.setImageDrawable(drawable_Anims[value]);
            }

            @Override
            public void onTimeChanged(int recordTime, String localPath) {
                Logger.i("voice", "已录音长度:" + recordTime);
                if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {// 1分钟结束，发送消息
                    // 需要重置按钮
                    btn_press_to_speak.setPressed(false);
                    btn_press_to_speak.setClickable(false);
                    // 取消录音框
                    layout_record.setVisibility(View.INVISIBLE);
                    // 发送语音消息
                    sendVoiceMessage(localPath, recordTime);
                    //是为了防止过了录音时间后，会多发一条语音出去的情况。
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            btn_press_to_speak.setClickable(true);
                        }
                    }, 1000);
                }
            }
        });
    }

    /**
     * 长按说话
     *
     * @author smile
     * @date 2014-7-1 下午6:10:16
     */
    class VoiceTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!FileUtil.isExternalStorageWritable()) {
                        SnackbarUtil.show(wrapper, "发送语音需要SDCard支持！");
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        layout_record.setVisibility(View.VISIBLE);
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        // 开始录音
                        recordManager.startRecording(c.getConversationId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        tv_voice_tips.setTextColor(Color.RED);
                    } else {
                        tv_voice_tips.setText(getString(R.string.voice_up_tips));
                        tv_voice_tips.setTextColor(Color.WHITE);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    layout_record.setVisibility(View.INVISIBLE);
                    try {
                        if (event.getY() < 0) {// 放弃录音
                            recordManager.cancelRecording();
                            BmobLog.i("voice", "放弃发送语音");
                        } else {
                            int recordTime = recordManager.stopRecording();
                            if (recordTime > 1) {
                                // 发送语音文件
                                sendVoiceMessage(recordManager.getRecordFilePath(c.getConversationId()), recordTime);
                            } else {// 录音时间过短，则提示录音过短的提示
                                layout_record.setVisibility(View.GONE);
                                showShortToast().show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    Toast toast;

    /**
     * 显示录音时间过短的Toast
     *
     * @return void
     * @Title: showShortToast
     */
    private Toast showShortToast() {
        if (toast == null) {
            toast = new Toast(this);
        }
        View view = LayoutInflater.from(this).inflate(R.layout.include_chat_voice_short, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        return toast;
    }

    @OnClick(R.id.et_msg_box)
    public void onEditClick(View view) {
        if (layout_more.getVisibility() == View.VISIBLE) {
            layout_add.setVisibility(View.GONE);
            layout_emo.setVisibility(View.GONE);
            layout_more.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_chat_emoji)
    public void onEmoClick(View view) {
        if (layout_more.getVisibility() == View.GONE) {
            showEditState(true);
        } else {
            if (layout_add.getVisibility() == View.VISIBLE) {
                layout_add.setVisibility(View.GONE);
                layout_emo.setVisibility(View.VISIBLE);
            } else {
                layout_more.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.btn_chat_add)
    public void onAddClick(View view) {
        if (layout_more.getVisibility() == View.GONE) {
            layout_more.setVisibility(View.VISIBLE);
            layout_add.setVisibility(View.VISIBLE);
            layout_emo.setVisibility(View.GONE);
            hideSoftInputView();
        } else {
            if (layout_emo.getVisibility() == View.VISIBLE) {
                layout_emo.setVisibility(View.GONE);
                layout_add.setVisibility(View.VISIBLE);
            } else {
                layout_more.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.btn_set_mode_voice)
    public void onVoiceClick(View view) {
        edittext_layout.setVisibility(View.GONE);
        layout_more.setVisibility(View.GONE);
        btn_set_mode_voice.setVisibility(View.GONE);
        btn_set_mode_keyboard.setVisibility(View.VISIBLE);
        btn_press_to_speak.setVisibility(View.VISIBLE);
        hideSoftInputView();
    }

    @OnClick(R.id.btn_set_mode_keyboard)
    public void onKeyClick(View view) {
        showEditState(false);
    }

    @OnClick(R.id.btn_chat_send)
    public void onSendClick(View view) {
        sendMessage();
    }

    @OnClick(R.id.tv_picture)
    public void onPictureClick(View view) {
        sendLocalImageMessage();
        // sendOtherMessage();
    }

    private static final int REQUEST_CAMERA = 0;
    private File tempFile;

    @OnClick(R.id.tv_camera)
    public void onCameraClick(View view) {
        onCamera();
    }

    private void onCamera() {
        System.out.println("onCamera");
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            tempFile = FileUtils.createTmpFile(this, "/WeCoder/Image");
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
    }


    private static boolean permission_location = false;
    private static final int PERMISSION_REQUEST = 0xf1;

    @OnClick(R.id.tv_location)
    public void onLocationClick(View view) {

        permission_location = PreferencesUtil.getInstance(this).getPerLoc();

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            if (!permission_location) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST);
            } else {
                LocationActivity.startToSelectLocationForResult(this, REQEST_LOCCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PreferencesUtil.getInstance(this).setPerLoc(true);
                LocationActivity.startToSelectLocationForResult(this, REQEST_LOCCATION);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 根据是否点击笑脸来显示文本输入框的状态
     *
     * @param isEmo 用于区分文字和表情
     * @return void
     */
    private void showEditState(boolean isEmo) {
        edittext_layout.setVisibility(View.VISIBLE);
        btn_set_mode_keyboard.setVisibility(View.GONE);
        btn_set_mode_voice.setVisibility(View.VISIBLE);
        btn_press_to_speak.setVisibility(View.GONE);
        et_msg_box.requestFocus();
        if (isEmo) {
            layout_more.setVisibility(View.VISIBLE);
            layout_more.setVisibility(View.VISIBLE);
            layout_emo.setVisibility(View.VISIBLE);
            layout_add.setVisibility(View.GONE);
            hideSoftInputView();
        } else {
            layout_more.setVisibility(View.GONE);
            showSoftInputView(et_msg_box);
        }
    }

    /**
     * 发送文本消息
     */
    private void sendMessage() {
        String text = et_msg_box.getText().toString();
        if (TextUtils.isEmpty(text.trim())) {
            SnackbarUtil.show(wrapper, "不能发送空消息！");
            return;
        }
        BmobIMTextMessage msg = new BmobIMTextMessage();
        msg.setContent(text);
        c.sendMessage(msg, listener);
    }

    /**
     * 直接发送远程图片地址
     */
    public void sendRemoteImageMessage() {
        BmobIMImageMessage image = new BmobIMImageMessage();
        image.setRemoteUrl("");
        c.sendMessage(image, listener);
    }

    /**
     * 发送本地图片地址
     */
    public void sendLocalImageMessage() {
        ImageSelector.open(this, ImageConfigUtil.getSingleChoiceImageConfigWithNoCrop(getColorPrimaryDark(), getColorPrimary()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            BmobIMImageMessage image = new BmobIMImageMessage(pathList.get(0));
            c.sendMessage(image, listener);
        } else if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (tempFile != null) {
                    BmobIMImageMessage image = new BmobIMImageMessage(tempFile.getAbsolutePath());
                    c.sendMessage(image, listener);
                }
            } else {
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            }
        } else if (requestCode == REQEST_LOCCATION) {
            String loc = data.getStringExtra(LocationActivity.ADDRESS);
            double latitude = data.getDoubleExtra(LocationActivity.LATITUDE, 0.0f);
            double longtitude = data.getDoubleExtra(LocationActivity.LONGITUDE, 0.0f);
            sendLocationMessage(loc, latitude, longtitude);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 发送语音消息
     *
     * @param local
     * @param length
     * @return void
     * @Title: sendVoiceMessage
     */
    private void sendVoiceMessage(String local, int length) {
        BmobIMAudioMessage audio = new BmobIMAudioMessage(local);
        audio.setDuration(length);
        c.sendMessage(audio, listener);
    }

    private static final int REQEST_LOCCATION = 0xa2;

    /**
     * 发送地理位置
     */
    public void sendLocationMessage(String loc, double latitude, double longtitude) {
        BmobIMLocationMessage location = new BmobIMLocationMessage(loc, latitude, longtitude);
        c.sendMessage(location, listener);
    }

    /**
     * 消息发送监听器
     */
    public MessageSendListener listener = new MessageSendListener() {

        @Override
        public void onProgress(int value) {
            super.onProgress(value);
            RecyclerView.ViewHolder holder = rv_record_list.findViewHolderForAdapterPosition(mAdapter.getItemCount() - 1);
            if (holder instanceof SendImageHolder) {
                ((SendImageHolder) holder).onProgress(value);
            }
        }

        @Override
        public void onStart(BmobIMMessage msg) {
            super.onStart(msg);
            mAdapter.addMessage(msg);
            et_msg_box.setText("");
            scrollToBottom();
        }

        @Override
        public void done(BmobIMMessage msg, BmobException e) {
            //发送完之后，需要更新状态，因为消息的状态已经改变
            RecyclerView.ViewHolder holder = rv_record_list.findViewHolderForAdapterPosition(mAdapter.getItemCount() - 1);
            ((BaseViewHolder) holder).bindData(msg);
        }
    };

    /**
     * 首次加载，可设置msg为null，下拉刷新的时候，默认取消息表的第一个msg作为刷新的起始时间点，默认按照消息时间的降序排列
     *
     * @param msg
     */
    public void queryMessages(BmobIMMessage msg) {
        c.queryMessages(msg, 10, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                sw_refresh.setRefreshing(false);
                if (e == null) {
                    if (null != list && list.size() > 0) {
                        mAdapter.addMessages(list);
                        layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                    }
                } else {
                    SnackbarUtil.show(wrapper, "消息获取失败！");
                }
            }
        });
    }

    private void scrollToBottom() {
        layoutManager.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);
    }

    /**
     * 接收到聊天消息
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        addMessage2Chat(event);
    }

    /**
     * 添加消息到聊天界面中
     *
     * @param event
     */
    private void addMessage2Chat(MessageEvent event) {
        BmobIMMessage msg = event.getMessage();
        Logger.i("接收到消息：" + msg.getContent());
        if (c != null && event != null && c.getConversationId().equals(event.getConversation().getConversationId()) //如果是当前会话的消息
                && !msg.isTransient()) {//并且不为暂态消息
            if (mAdapter.findPosition(msg) < 0) {//如果未添加到界面中
                mAdapter.addMessage(msg);
                //更新该会话下面的已读状态
                c.updateReceiveStatus(msg);
            }
            scrollToBottom();
        } else {
            Logger.i("不是与当前聊天对象的消息");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (layout_more.getVisibility() == View.VISIBLE) {
                layout_more.setVisibility(View.GONE);
                return false;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onResume() {
        //锁屏期间的收到的未读消息需要添加到聊天界面中
        addUnReadMessage();
        //添加通知监听
        BmobNotificationManager.getInstance(this).addObserver(this);
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知
        BmobNotificationManager.getInstance(this).cancelNotification();
        super.onResume();
    }

    /**
     * 添加未读的通知栏消息到聊天界面
     */
    private void addUnReadMessage() {
        List<MessageEvent> cache = BmobNotificationManager.getInstance(this).getNotificationCacheList();
        if (cache.size() > 0) {
            int size = cache.size();
            for (int i = 0; i < size; i++) {
                MessageEvent event = cache.get(i);
                addMessage2Chat(event);
            }
        }
        scrollToBottom();
    }

    @Override
    protected void onPause() {
        //取消通知栏监听
        BmobNotificationManager.getInstance(this).removeObserver(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //清理资源
        recordManager.clear();
        //更新此会话的所有消息为已读状态
        hideSoftInputView();
        c.updateLocalCache();
        super.onDestroy();
    }
}
