package com.harlan.jxust.utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author :smile
 * @project:TimeUtil
 * @date :2016-01-26-17:27
 */
public class TimeUtil {

    public final static String FORMAT_TIME = "HH:mm";
    public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
    public final static String FORMAT_DATE_TIME_SECOND = "yyyy-MM-dd HH:mm:ss";
    public final static String FORMAT_MONTH_DAY_TIME = "MM-dd HH:mm";

    private final static String TAG = "TimeUtils";

    private final static String PATTERN = "yyyy-MM-dd";

    public static String timeFormat(long timeMillis, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
        return simpleDateFormat.format(new Date(timeMillis));
    }

    static StringBuffer result = new StringBuffer();

    public static String getTimeString(long milliseconds) {
        result.delete(0, result.length());

        long time = System.currentTimeMillis() - (milliseconds * 1000);
        long mill = (long) Math.ceil(time / 1000);//秒前

        long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前

        long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时

        long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前

        if (day - 1 > 0 && day - 1 < 30) {
            result.append(day + "天");
        }
        else if (day - 1 >= 30) {
            result.append(Math.round((day - 1) / 30) + "个月");
        }
        else if (hour - 1 > 0) {
            if (hour >= 24) {
                result.append("1天");
            }
            else {
                result.append(hour + "小时");
            }
        }
        else if (minute - 1 > 0) {
            if (minute == 60) {
                result.append("1小时");
            }
            else {
                result.append(minute + "分钟");
            }
        }
        else if (mill - 1 > 0) {
            if (mill == 60) {
                result.append("1分钟");
            }
            else {
                result.append(mill + "秒");
            }
        }
        else {
            result.append("刚刚");
        }
        if (!result.toString().equals("刚刚")) {
            result.append("前");
        }
        return result.toString();
    }


    public static String formatPhotoDate(long time) {
        return timeFormat(time, PATTERN);
    }

    public static String formatPhotoDate(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            long time = file.lastModified();
            return formatPhotoDate(time);
        }
        return "1970-01-01";
    }

    public static String getFormatToday(String dateFormat) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(currentTime);
    }

    public static Date stringToDate(String dateStr, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String dateToString(Date date, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
    }

    public static String getChatTime(boolean hasYear, long timesamp) {
        long clearTime = timesamp;
        String result;
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(clearTime);
        int temp = Integer.parseInt(sdf.format(today))
                - Integer.parseInt(sdf.format(otherDay));
        switch (temp) {
            case 0:
                result = "今天 " + getHourAndMin(clearTime);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(clearTime);
                break;
            case 2:
                result = "前天 " + getHourAndMin(clearTime);
                break;
            default:
                result = getTime(hasYear, clearTime);
                break;
        }
        return result;
    }

    public static String getTime(boolean hasYear, long time) {
        String pattern = FORMAT_DATE_TIME;
        if (!hasYear) {
            pattern = FORMAT_MONTH_DAY_TIME;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date(time));
    }

    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_TIME);
        return format.format(new Date(time));
    }
}
