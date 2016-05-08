package com.harlan.jxust.utils;

import android.app.Activity;
import android.content.Context;

import com.harlan.jxust.wecoder.R;


/**
 * Created by lgp on 2015/6/7.
 */
public class ThemeUtil {

    public static void changeTheme(Activity activity, Theme theme) {
        if (activity == null)
            return;
        int style = R.style.AppThemeGreen;

        switch (theme) {
            case BROWN:
                style = R.style.AppThemeBrown;
                break;
            case BLUE_GREY:
                style = R.style.AppThemeBlueGray;
                break;
            case RED:
                style = R.style.AppThemeRed;
                break;
            case PURPLE:
                style = R.style.AppThemePurple;
                break;
            case TEAL:
                style = R.style.AppThemeTeal;
                break;
            case GREEN:
                style = R.style.AppThemeGreen;
                break;
            case ORANGE:
                style = R.style.AppThemeOrange;
                break;
            case INDIGO:
                style = R.style.AppThemeIngigo;
                break;
            default:
                break;
        }
        activity.setTheme(style);
    }

    public static Theme getCurrentTheme(Context context) {
        int value = PreferencesUtil.getInstance(context).getCurTheme();
        return Theme.mapValueToTheme(value);
    }

    public enum Theme {

        RED(0),
        BROWN(1),
        BLUE_GREY(2),
        GREEN(3),
        INDIGO(4),
        PURPLE(5),
        TEAL(6),
        ORANGE(7);

        private int mValue;

        Theme(int value) {
            this.mValue = value;
        }

        public static Theme mapValueToTheme(final int value) {
            for (Theme theme : Theme.values()) {
                if (value == theme.getIntValue()) {
                    return theme;
                }
            }
            // If run here, return default
            return GREEN;
        }

        static Theme getDefault() {
            return GREEN;
        }

        public int getIntValue() {
            return mValue;
        }
    }
}
