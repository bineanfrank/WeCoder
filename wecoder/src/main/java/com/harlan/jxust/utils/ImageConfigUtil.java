package com.harlan.jxust.utils;

/**
 * Created by Harlan on 2016/4/14.
 */
public class ImageConfigUtil {

    private static ImageConfig.Builder getImageConfigBuilder(int toolbarColor, int titleBgColor) {
        ImageConfig.Builder mBuilder = new ImageConfig.Builder(new GlideLoader());
        mBuilder.steepToolBarColor(toolbarColor)
                .titleBgColor(titleBgColor);

        return mBuilder;
    }

    public static ImageConfig getSingleChoiceImageConfig(int toolbarColor, int titleBgColor) {
        return getImageConfigBuilder(toolbarColor, titleBgColor)
                .singleSelect()
                .showCamera()
                .crop()
                .filePath("/WeCoder/Image")
                .build();
    }

    public static ImageConfig getSingleChoiceImageConfigWithNoCrop(int toolbarColor, int titleBgColor) {
        return getImageConfigBuilder(toolbarColor, titleBgColor)
                .singleSelect()
                .filePath("/WeCoder/Image")
                .build();
    }

    public static ImageConfig getMultiChoiceImageConfig(int toolbarColor, int titleBgColor){
        return getImageConfigBuilder(toolbarColor, titleBgColor)
                .mutiSelect()
                .mutiSelectMaxSize(9)
                .showCamera()
                .filePath("/WeCoder/Image")
                .build();
    }

    public static ImageConfig getLimitChoiceImageConfig(int limit, int toolbarColor, int titleBgColor){
        return getImageConfigBuilder(toolbarColor, titleBgColor)
                .mutiSelect()
                .mutiSelectMaxSize(limit)
                .showCamera()
                .filePath("/WeCoder/Image")
                .build();
    }
}
