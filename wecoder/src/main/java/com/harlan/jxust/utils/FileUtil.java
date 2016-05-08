package com.harlan.jxust.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.Logger;

public class FileUtil {

    private static final String TAG = "WeCoder " + FileUtil.class.getSimpleName();
    private static final String ABSOLUTE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String APP_FILE = ABSOLUTE_PATH + File.separator + "WeCoder";
    private static final String IMAGE_FILE = APP_FILE + File.separator + "Image";
    private static final String AUDIO_FILE = APP_FILE + File.separator + "Audio";

    /**
     * 直接在初始化的时候，创建App的文件目录
     */
    public FileUtil() {
    }

    public static void createWeCoderDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(APP_FILE);
            file.mkdir();
            System.out.println(file.getAbsolutePath());
            File file1 = new File(AUDIO_FILE);
            file1.mkdir();
            File file2 = new File(IMAGE_FILE);
            file2.mkdir();
        }
    }

    public static String getWeCoderImageDir(){
        File file = new File(IMAGE_FILE);
        if(file.exists()){
            return IMAGE_FILE;
        }
        return null;
    }

    /**
     * 判断是否创建了APP根目录
     *
     * @return
     */
    public static boolean isAPPFileExists() {
        File file = new File(APP_FILE);
        return file.exists() && file.isDirectory();
    }

    public static Uri getImageUriFromFile() {
        if (!isAPPFileExists()) return null;
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMddHHmmss");
        String imgFileName = dateFormat.format(date) + ".jpg";
        File imgFile = new File(IMAGE_FILE, imgFileName);
        return Uri.fromFile(imgFile);
    }

    /**
     * Checks if external storage is available for read and write
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 保存图片到制定路径
     *
     * @param bitmap
     */
    public static void saveBitmap(String filename, Bitmap bitmap) {
        if (!isExternalStorageWritable()) {
            Logger.d(TAG, "SDCard is unavailable.");
            return;
        }
        if (bitmap == null) {
            Logger.d(TAG, "Bitmap is null.");
            return;
        }
        try {
            File file = new File(IMAGE_FILE, filename);
            FileOutputStream outputstream = new FileOutputStream(file);
            if ((filename.indexOf("png") != -1) || (filename.indexOf("PNG") != -1)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputstream);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputstream);
            }
            outputstream.flush();
            outputstream.close();

        } catch (FileNotFoundException e) {
            Logger.d(TAG, e.getMessage());
        } catch (IOException e) {
            Logger.d(TAG, e.getMessage());
        }
    }

    public boolean isBitmapExists(String filename) {
        File dir = new File(IMAGE_FILE);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, filename);
        return file.exists();
    }
}
