package com.harlan.jxust.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;
import com.harlan.jxust.ui.adapter.viewholder.BaseViewHolder;
import com.harlan.jxust.ui.adapter.viewholder.ReceiveImageHolder;
import com.harlan.jxust.ui.adapter.viewholder.ReceiveLocationHolder;
import com.harlan.jxust.ui.adapter.viewholder.ReceiveVoiceHolder;
import com.harlan.jxust.ui.adapter.viewholder.ReceiveTextHolder;
import com.harlan.jxust.ui.adapter.viewholder.SendImageHolder;
import com.harlan.jxust.ui.adapter.viewholder.SendLocationHolder;
import com.harlan.jxust.ui.adapter.viewholder.SendTextHolder;
import com.harlan.jxust.ui.adapter.viewholder.SendVoiceHolder;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.v3.BmobUser;

/**
 * Created by Harlan on 2016/4/11.
 * <p/>
 * 目前只支持文本 updated at 2016/4/11
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIME_INTERVAL = 60 * 10 * 1000;

    //文本
    private static final int TYPE_RECEIVED_TEXT = 0xf0;
    private static final int TYPE_SEND_TEXT = 0xf1;

    //图片
    private static final int TYPE_RECEIVED_PICTURE = 0xf2;
    private static final int TYPE_SEND_PICTURE = 0xf3;

    //位置
    private static final int TYPE_RECEIVED_LOCATION = 0xf4;
    private static final int TYPE_SEND_LOCATION = 0xf5;

    //音频
    private static final int TYPE_RECEIVED_VOICE = 0xf6;
    private static final int TYPE_SEND_VOICE = 0xf7;

    private BmobIMConversation c;
    private List<BmobIMMessage> msgs = new ArrayList<>();
    private String currentUid = "";

    private OnRVClickListener onRVClickListener;

    public void setOnRVClickListener(OnRVClickListener onRVClickListener) {
        this.onRVClickListener = onRVClickListener;
    }

    public ChatAdapter(Context context, BmobIMConversation c) {
        try {
            currentUid = BmobUser.getCurrentUser(context).getObjectId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.c = c;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case TYPE_SEND_TEXT:
                holder = new SendTextHolder(parent.getContext(), parent, c, onRVClickListener);
                break;
            case TYPE_RECEIVED_TEXT:
                holder = new ReceiveTextHolder(parent.getContext(), parent, onRVClickListener);
                break;
            case TYPE_SEND_LOCATION:
                holder = new SendLocationHolder(parent.getContext(), parent, c, onRVClickListener);
                break;
            case TYPE_RECEIVED_LOCATION:
                holder = new ReceiveLocationHolder(parent.getContext(), parent, onRVClickListener);
                break;
            case TYPE_SEND_PICTURE:
                holder = new SendImageHolder(parent.getContext(), parent, c, onRVClickListener);
                break;
            case TYPE_RECEIVED_PICTURE:
                holder = new ReceiveImageHolder(parent.getContext(), parent, onRVClickListener);
                break;
            case TYPE_SEND_VOICE:
                holder = new SendVoiceHolder(parent.getContext(), parent, c, onRVClickListener);
                break;
            case TYPE_RECEIVED_VOICE:
                holder = new ReceiveVoiceHolder(parent.getContext(), parent, onRVClickListener);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder) holder).bindData(msgs.get(position));
        if (holder instanceof ReceiveTextHolder) {
            ((ReceiveTextHolder) holder).showTime(shouldShowTime(position));
        } else if (holder instanceof ReceiveImageHolder) {
            ((ReceiveImageHolder) holder).showTime(shouldShowTime(position));
        } else if (holder instanceof ReceiveLocationHolder) {
            ((ReceiveLocationHolder) holder).showTime(shouldShowTime(position));
        } else if (holder instanceof ReceiveVoiceHolder) {
            ((ReceiveVoiceHolder) holder).showTime(shouldShowTime(position));
        } else if (holder instanceof SendTextHolder) {
            ((SendTextHolder) holder).showTime(shouldShowTime(position));
        } else if (holder instanceof SendImageHolder) {
            ((SendImageHolder) holder).showTime(shouldShowTime(position));
        } else if (holder instanceof SendLocationHolder) {
            ((SendLocationHolder) holder).showTime(shouldShowTime(position));
        } else if (holder instanceof SendVoiceHolder) {
            ((SendVoiceHolder) holder).showTime(shouldShowTime(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        BmobIMMessage message = msgs.get(position);
        if (message.getMsgType().equals(BmobIMMessageType.IMAGE.getType())) {
            return message.getFromId().equals(currentUid) ? TYPE_SEND_PICTURE : TYPE_RECEIVED_PICTURE;
        } else if (message.getMsgType().equals(BmobIMMessageType.LOCATION.getType())) {
            return message.getFromId().equals(currentUid) ? TYPE_SEND_LOCATION : TYPE_RECEIVED_LOCATION;
        } else if (message.getMsgType().equals(BmobIMMessageType.VOICE.getType())) {
            return message.getFromId().equals(currentUid) ? TYPE_SEND_VOICE : TYPE_RECEIVED_VOICE;
        } else {
            return message.getFromId().equals(currentUid) ? TYPE_SEND_TEXT : TYPE_RECEIVED_TEXT;
        }
    }

    public int findPosition(BmobIMMessage message) {
        int index = this.getCount();
        int position = -1;
        while (index-- > 0) {
            if (message.equals(this.getItem(index))) {
                position = index;
                break;
            }
        }
        return position;
    }

    public int findPosition(long id) {
        int index = this.getCount();
        int position = -1;

        while (index-- > 0) {
            if (this.getItemId(index) == id) {
                position = index;
                break;
            }
        }
        return position;
    }

    public BmobIMMessage getFirstMessage() {
        if (msgs == null || msgs.size() <= 0) return null;
        return msgs.get(0);
    }

    public int getCount() {
        return this.msgs == null ? 0 : this.msgs.size();
    }

    public void addMessages(List<BmobIMMessage> messages) {
        msgs.addAll(0, messages);
        notifyDataSetChanged();
    }

    public void addMessage(BmobIMMessage message) {
        msgs.add(message);
        notifyItemInserted(msgs.size());
    }

    @Override
    public int getItemCount() {
        return msgs.size();
    }

    public BmobIMMessage getItem(int position) {
        return msgs == null ? null : (position >= msgs.size() ? null : msgs.get(position));
    }

    public void remove(int position) {
        if (msgs == null) return;
        msgs.remove(position);
        notifyItemRemoved(position);
    }

    private boolean shouldShowTime(int position) {
        if (position == 0) return true;
        long lastTime = msgs.get(position - 1).getCreateTime();
        long nowTime = msgs.get(position).getCreateTime();
        return nowTime - lastTime > TIME_INTERVAL;
    }
}
