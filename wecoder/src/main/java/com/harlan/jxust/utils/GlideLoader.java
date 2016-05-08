package com.harlan.jxust.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.harlan.jxust.wecoder.R;

/**
 * Created by Harlan on 2016/4/14.
 */
public class GlideLoader implements ImageLoader {

    @Override
    public void displayImage(Context context, String s, ImageView imageView) {
        Glide.with(context)
                .load(s)
                .centerCrop()
                .crossFade()
                .error(R.drawable.empty_photo)
                .into(imageView);
    }
}
