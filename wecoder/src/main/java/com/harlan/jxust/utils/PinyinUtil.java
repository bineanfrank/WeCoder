package com.harlan.jxust.utils;

import com.harlan.jxust.config.Constants;

import java.util.List;

/**
 * Created by Harlan on 2016/4/8.
 */
public class PinyinUtil {

    public static String getPinyin(String src) {
        List<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(src);
        StringBuilder sb = new StringBuilder();
        if (tokens != null && tokens.size() > 0) {
            for (HanziToPinyin.Token token : tokens) {
                sb.append(token.target);
            }
        }
        return sb.toString().toUpperCase();
    }

    public static char getTopC(String username) {
        String pinyin = getPinyin(username);
        if (pinyin.equals(Constants.NEW_FRIEND)) {
            return '↑';
        } else if (Character.isDigit(pinyin.charAt(0))) {
            return '#';
        } else {
            char header = PinyinUtil.getPinyin(pinyin).charAt(0);
            if (header < 'A' || header > 'Z') {
                return '☆';
            } else {
                return header;
            }
        }
    }
}
