package com.harlan.jxust.ui.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.harlan.jxust.bean.User;
import com.harlan.jxust.model.UserModel;
import com.harlan.jxust.model.i.QueryUserListener;
import com.harlan.jxust.ui.activity.MyInfoActivity;
import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;
import com.harlan.jxust.ui.view.imageview.SuperImageView;
import com.harlan.jxust.utils.EmojiHelper;
import com.harlan.jxust.utils.ScreenUtil;
import com.harlan.jxust.utils.TimeUtil;
import com.harlan.jxust.wecoder.R;

import java.util.List;

import butterknife.Bind;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by Harlan on 2016/4/6.
 */
public class RecentViewHolder extends BaseViewHolder {

    @Bind(R.id.siv_avatar)
    public SuperImageView siv_avatar;

    @Bind(R.id.tv_name)
    public TextView tv_name;

    @Bind(R.id.tv_time)
    public TextView tv_time;

    @Bind(R.id.tv_unread)
    public TextView tv_unread;

    @Bind(R.id.tv_content)
    public TextView tv_content;

    //@Bind(R.id.msg_state)
    //public ImageView msg_state;

    public RecentViewHolder(Context context, ViewGroup root, OnRVClickListener mListener) {
        super(context, root, R.layout.item_recent_chat, mListener);
    }

    @Override
    public void bindData(Object o) {
        final BmobIMConversation conversation = (BmobIMConversation) o;
        List<BmobIMMessage> msgs = conversation.getMessages();
        BmobIMMessage lastMsg;
        if (msgs != null && msgs.size() > 0) {
            lastMsg = msgs.get(0);
            String content = lastMsg.getContent();
            String fromId = lastMsg.getFromId();
            System.out.println(fromId);
            if (lastMsg.getMsgType().equals(BmobIMMessageType.TEXT.getType())) {
                tv_content.setText(EmojiHelper.replace(context, content));
            } else if (lastMsg.getMsgType().equals(BmobIMMessageType.IMAGE.getType())) {
                tv_content.setText("[图片]");
            } else if (lastMsg.getMsgType().equals(BmobIMMessageType.VOICE.getType())) {
                tv_content.setText("[语音]");
            } else if (lastMsg.getMsgType().equals(BmobIMMessageType.LOCATION.getType())) {
                tv_content.setText("[位置]" + content);
            } else {//开发者自定义的消息类型，需要自行处理
                tv_content.setText("[好友添加]");
            }
            tv_time.setText(TimeUtil.getChatTime(false, lastMsg.getCreateTime()));
        }

        siv_avatar.loadImage(conversation.getConversationIcon(), false, R.drawable.default_avatar);

        siv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                User user = conversation.get
//                Intent intent = new Intent(context, MyInfoActivity.class);
//                intent.putExtra("user", user);
//                startActivity(intent, null);
            }
        });

        //会话标题
        tv_name.setText(conversation.getConversationTitle());

        //查询指定未读消息数
        long unread = BmobIM.getInstance().getUnReadCount(conversation.getConversationId());
        if (unread > 0) {
            tv_unread.setVisibility(View.VISIBLE);
            if (unread > 99) {
                tv_unread.setText("99+");
            } else {
                tv_unread.setText(String.valueOf(unread));
            }
        } else {
            tv_unread.setVisibility(View.GONE);
        }
    }
}
