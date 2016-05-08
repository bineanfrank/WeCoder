package com.harlan.jxust.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.harlan.jxust.utils.EmojiHelper;
import com.harlan.jxust.wecoder.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harlan on 2016/4/26.
 */
public class EmojiAdapter extends BaseAdapter {
    private Context context;
    private List<String> datas = new ArrayList<>();

    public EmojiAdapter(Context ctx) {
        this.context = ctx;
    }

    public void setDatas(List<String> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View conView, ViewGroup parent) {
        if (conView == null) {
            conView = View.inflate(context, R.layout.item_chat_emoji, null);
        }
        ImageView emotionImageView = (ImageView) conView.findViewById(R.id.emoji);
        String emotion = (String) getItem(position);
        emotion = emotion.substring(1, emotion.length() - 1);
        Bitmap bitmap = EmojiHelper.getEmojiDrawable(context, emotion);
        emotionImageView.setImageBitmap(bitmap);
        return conView;
    }
}
