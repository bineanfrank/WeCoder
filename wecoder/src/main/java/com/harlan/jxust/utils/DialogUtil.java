package com.harlan.jxust.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.harlan.jxust.wecoder.R;

/**
 * Created by Harlan on 2016/4/20.
 */
public class DialogUtil {

    public static AlertDialog.Builder makeDialogBuilderByTheme(Context context, ThemeUtil.Theme theme) {
        AlertDialog.Builder builder;
        int style = R.style.RedDialogTheme;
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
        builder = new AlertDialog.Builder(context, style);
        return builder;
    }

    public static AlertDialog.Builder makeDialogBuilder(Context context) {
        ThemeUtil.Theme theme = ThemeUtil.getCurrentTheme(context);
        return makeDialogBuilderByTheme(context, theme);
    }
}
