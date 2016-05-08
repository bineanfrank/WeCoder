package com.harlan.jxust.ui.adapter.listener;

import android.text.Editable;
import android.widget.Button;

/**
 * Created by Harlan on 2016/4/11.
 */
public class TextWatcher implements android.text.TextWatcher {

    private Button mButton;

    public TextWatcher(Button mButton) {
        this.mButton = mButton;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int length = s.length();
        boolean btn_enabled = length > 0;
        if (mButton != null) {
            mButton.setEnabled(btn_enabled);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
