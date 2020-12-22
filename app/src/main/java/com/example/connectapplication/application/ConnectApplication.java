package com.example.connectapplication.application;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.example.connectapplication.utils.GetTime;
import com.example.connectapplication.utils.LogToFileUtils;
import com.example.connectapplication.utils.WriteLog;
import com.example.connectapplication.utils.WriteLogToFile;
import com.ryeex.ble.connector.BleEngine;
import com.ryeex.ble.connector.log.BleLogCallback;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static androidx.core.app.ActivityCompat.requestPermissions;

public class ConnectApplication extends MultiDexApplication {
    private static final char VERBOSE = 'v';

    private static final char DEBUG = 'd';

    private static final char INFO = 'i';

    private static final char WARN = 'w';

    private static final char ERROR = 'e';
    private long currentday;

    @Override
    public void onCreate() {
        super.onCreate();
        currentday = System.currentTimeMillis();
        closeAndroidPDialog();
        //checkPermission();
        WriteLogToFile.init(this);
        WriteLog.startLog(this,"WatchSdkLog");
        BleEngine.init(this, new BleLogCallback() {
            @Override
            public void verbose(String tag, String msg) {
                //Log.v(tag, msg);
                try {
                    //if(checkPermission())
                       // WriteLogToFile.writeToFile(VERBOSE, tag, msg, currentday);
                        //LogToFileUtils.write(GetTime.getCurrentTime_Today()+":::"+tag+"-------"+msg);
                    WriteLog.v(msg);
                } catch (Exception e) {
                    Log.e("yj", "e---verbose--" + e.toString());
                }
            }

            @Override
            public void debug(String tag, String msg) {
                //Log.d(tag, msg);
                try {
                    //if(checkPermission())
                      //  WriteLogToFile.writeToFile(DEBUG, tag, msg, currentday);
                    //LogToFileUtils.write(GetTime.getCurrentTime_Today()+":::"+tag+"-------"+msg);
                    WriteLog.d(msg);
                } catch (Exception e) {
                    Log.e("yj", "e---debug--" + e.toString());
                }
            }

            @Override
            public void info(String tag, String msg) {
                //Log.i(tag, msg);
                try {
                    //if(checkPermission())
                       // WriteLogToFile.writeToFile(INFO, tag, msg, currentday);
                    //LogToFileUtils.write(GetTime.getCurrentTime_Today()+":::"+tag+"-------"+msg);
                    WriteLog.i(msg);
                } catch (Exception e) {
                    Log.e("yj", "e---info--" + e.toString());
                }
            }

            @Override
            public void warn(String tag, String msg) {
                //Log.w(tag, msg);
                try {
                    //if(checkPermission())
                     //   WriteLogToFile.writeToFile(WARN, tag, msg, currentday);
                    //LogToFileUtils.write(GetTime.getCurrentTime_Today()+":::"+tag+"-------"+msg);
                    WriteLog.w(msg);
                } catch (Exception e) {
                    Log.e("yj", "e---warn--" + e.toString());
                }
            }

            @Override
            public void error(String tag, String msg) {
                Log.e(tag, msg);
                try {
                    //if(checkPermission())
                    //WriteLogToFile.writeToFile(ERROR, tag, msg, currentday);
                    //LogToFileUtils.write(GetTime.getCurrentTime_Today()+":::"+tag+"-------"+msg);
                    WriteLog.e(msg);
                } catch (Exception e) {
                    Log.e("yj", "e---error--" + e.toString());
                }
            }
        });
    }

    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            //Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            //mHiddenApiWarningShown.setAccessible(true);
            //mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
