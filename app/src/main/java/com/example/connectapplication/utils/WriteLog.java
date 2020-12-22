package com.example.connectapplication.utils;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.DiskLogStrategy;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;

public class WriteLog {

    static int mMaxFilesNum = 10;
    static HandlerThread mWriteThread = null;
    static  int mMaxFiles = 1024*500;

    public static  void startLog(Context ct, String tag){
        stopLog();

        String folder = ct.getExternalFilesDir(null)+"/WathLogs";
        ClearLogFiles(folder);

        mWriteThread = new HandlerThread("AndroidFileLogger." + folder);
        mWriteThread.start();
        try {
            //通过反射实例化DiskLogStrategy中的内部类WriteHandler
            Class<?> clazz = Class.forName("com.orhanobut.logger.DiskLogStrategy$WriteHandler");
            Constructor constructor = clazz.getDeclaredConstructor(Looper.class, String.class, int.class);
            //开启强制访问
            constructor.setAccessible(true);
            //核心：通过构造函数，传入相关属性，得到WriteHandler实例
            Handler handler = (Handler) constructor.newInstance(mWriteThread.getLooper(), folder, mMaxFiles);
            //创建缓存策略
            FormatStrategy strategy = CsvFormatStrategy.newBuilder().logStrategy(new DiskLogStrategy(handler)).build();
            DiskLogAdapter adapter = new DiskLogAdapter(strategy);
            Logger.addLogAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            mWriteThread.quit();
        }
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag(tag)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));

        i(folder);
    }
    static void ClearLogFiles(String folder){
        File file = new File(folder);
        if (false == file.exists()){
            return;
        }
        File[] subFile = file.listFiles();
        if (subFile.length <= mMaxFilesNum){
            return;
        }
        int iDelFileNum = subFile.length - mMaxFilesNum;
        Arrays.sort(subFile, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if((o1.lastModified()-o2.lastModified()>0)){
                    return 1;
                }
                return -1;
            }
        });
        for (int i=0;i<iDelFileNum;i++){
            subFile[i].delete();
        }
    }
    public  static  void stopLog(){
        Logger.clearLogAdapters();
        if (null ==mWriteThread){
            return;
        }
        mWriteThread.quit();
        mWriteThread = null;
    }

    public static void d(@NonNull String message, @Nullable Object... args) {
        Logger.d(message, args);
    }

    public static void d(@Nullable Object object) {
        Logger.d(object);
    }

    public static void e(@NonNull String message, @Nullable Object... args) {
        Logger.e(null, message, args);
    }

    public static void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
        Logger.e(throwable, message, args);
    }

    public static void i(@NonNull String message, @Nullable Object... args) {
        Logger.i(message, args);
    }

    public static void v(@NonNull String message, @Nullable Object... args) {
        Logger.v(message, args);
    }

    public static void w(@NonNull String message, @Nullable Object... args) {
        Logger.w(message, args);
    }
    /**
     * Tip: Use this for exceptional situations to log
     * ie: Unexpected errors etc
     */
    public static void wtf(@NonNull String message, @Nullable Object... args) {
        Logger.wtf(message, args);
    }

    /**
     * Formats the given json content and print it
     */
    public static void json(@Nullable String json) {
        Logger.json(json);
    }

    /**
     * Formats the given xml content and print it
     */
    public static void xml(@Nullable String xml) {
        Logger.xml(xml);
    }

}
