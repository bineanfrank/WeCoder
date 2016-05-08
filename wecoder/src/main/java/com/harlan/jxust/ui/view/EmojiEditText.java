package com.harlan.jxust.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

import com.harlan.jxust.utils.EmojiHelper;

/**
 * Created by Harlan on 2016/4/26.
 */
public class EmojiEditText extends EditText {

    public EmojiEditText(Context context) {
        super(context);
    }

    public EmojiEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!TextUtils.isEmpty(text)) {
            super.setText(EmojiHelper.replace(getContext(), text.toString()), type);
        } else {
            super.setText(text, type);
        }
    }
}
