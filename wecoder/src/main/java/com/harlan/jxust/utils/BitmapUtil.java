package com.harlan.jxust.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by Harlan on 2016/4/13.
 */
public class BitmapUtil {

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     *
     * @param context
     * @param uri
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap getSmallBitmap(Context context, Uri uri, int reqWidth, int reqHeight) {
        String filePath = getPathFromUri(context, uri);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap getBitmapFromUrl(String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream(), 100 * 1024);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 100 * 1024);
            copy(in, out);
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[100 * 1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }


    public static String getPathFromUri(Context context, Uri uri) {
        String result;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static Bitmap scaleBitmap(Bitmap bm, int outputX, int outputY) {
        float oldWidth = bm.getWidth();
        float oldHeight = bm.getHeight();
        float scale = oldWidth / oldHeight;

        float newWidth = oldWidth;
        float newHeight = oldHeight;
        boolean isScale = false;
        if (scale >= 1.0f) {
            if (oldWidth > outputX) {
                newWidth = outputX;
                newHeight = (newWidth / scale);
                isScale = true;
            }
        } else {
            if (oldHeight > outputY) {
                newHeight = outputY;
                newWidth = (newHeight * scale);
                isScale = true;
            }
        }
        if (isScale) {
            final Matrix matrix = new Matrix();
            matrix.postScale(newWidth / oldWidth, newHeight / oldHeight);
            // matrix.postRotate(rotate);
            final Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0,
                    (int) oldWidth, (int) oldHeight, matrix, true);
            bm.recycle();
            bm = rotatedBitmap;
        }
        return bm;
    }
}
