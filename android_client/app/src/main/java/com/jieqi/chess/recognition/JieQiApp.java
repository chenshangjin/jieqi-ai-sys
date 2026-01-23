/*
 * 应用程序入口类
 *
 * 代码生成者: Lingma AI助手
 * 生成日期: 2026年1月23日
 */

package com.jieqi.chess.recognition;

import android.app.Application;
import android.util.Log;

import org.opencv.android.OpenCVLoader;

public class JieQiApp extends Application {
    private static final String TAG = "JieQiApp";

    @Override
    public void onCreate() {
        super.onCreate();
        
        // 初始化OpenCV
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV初始化成功");
        } else {
            Log.e(TAG, "OpenCV初始化失败");
        }
    }
}