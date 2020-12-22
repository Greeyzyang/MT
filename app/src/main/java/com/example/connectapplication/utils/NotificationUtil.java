package com.example.connectapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationManagerCompat;

import com.ryeex.ble.connector.log.BleLogger;

import org.json.JSONObject;

import java.util.Set;


/**
 * Created by chenhao on 2017/9/28.
 */

public class NotificationUtil {

    public static boolean isNotificationListenerEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        if (packageNames.contains(context.getPackageName())) {
            return true;
        }
        return false;
    }

    public static void gotoSystemNotificationSetting(Activity activity) {
        try {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            activity.startActivity(intent);
        } catch (Exception e) {
        }
    }

    private static void toggleNotificationListenerService(Context context) {

        if (!isNotificationListenerEnabled(context)) {
            BleLogger.i("groot-noti", "isNotificationListenerEnabled is false and return");
            return;
        }

        /*Logger.i("groot-noti", "toggleNotificationListenerService");
        ComponentName componentName = new ComponentName(context, GrootNotificationListenerService.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        //OPPO须加requestRebind
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Logger.i("groot-noti", "toggleNotificationListenerService SDK >= 24, requestRebind");
            GrootNotificationListenerService.requestRebind(componentName);
        }*/
    }

    public static void ensureListenerRunning(Context context) {

        //该判断无效，进程被干掉，重启后需重新绑定 可用命令adb shell dumpsys notification查看All notification listeners和Live notification listeners
        //虽然Notification access监听功能依然开启，但监听的服务却是die, 所以这就是再次启动程序，无反应的原因

        /*ComponentName collectorComponent = new ComponentName(GrootApplication.getAppContext(), GrootNotificationListenerService.class);
        ActivityManager manager = (ActivityManager) GrootApplication.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        boolean collectorRunning = false;
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
        if (runningServices == null) {
            return;
        }
        for (ActivityManager.RunningServiceInfo service : runningServices) {
            if (service.service.equals(collectorComponent)) {
                if (service.pid == android.os.Process.myPid()) {
                    collectorRunning = true;
                }
            }
        }
        com.ryeex.groot.lib.log.Logger.i("groot-noti", "collectorRunning:" + collectorRunning);

        if (collectorRunning) {
            return;
        }*/

        //直接重新绑定
        toggleNotificationListenerService(context);
    }


    public static String getAppKeyByPackageName(String packageName, String groupKey) {
        String appId = "";
        if (packageName.equals(NotificationConst.PACKAGE_NAME_WX)) {
            appId = NotificationConst.KEY_APP_WX;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_QQ)) {
            appId = NotificationConst.KEY_APP_QQ;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_ALIPAY)) {
            appId = NotificationConst.KEY_APP_ALIPAY;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_WEIBO)) {
            appId = NotificationConst.KEY_APP_WEIBO;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_TAOBAO)) {
            appId = NotificationConst.KEY_APP_TAOBAO;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_TOUTIAO)) {
            appId = NotificationConst.KEY_APP_TOUTIAO;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_NETEASE_NEWS)) {
            appId = NotificationConst.KEY_APP_NETEASE_NEWS;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_ZHIHU)) {
            appId = NotificationConst.KEY_APP_ZHIHU;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_DOUYIN)) {
            appId = NotificationConst.KEY_APP_DOUYIN;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_DINGDING)) {
            appId = NotificationConst.KEY_APP_DINGDING;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_XMSF) && NotificationConst.PACKAGE_NAME_MIJIA.equals(groupKey)) {
            appId = NotificationConst.KEY_APP_MIJIA;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_MOMO)) {
            appId = NotificationConst.KEY_APP_MOMO;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_TIM)) {
            appId = NotificationConst.KEY_APP_TIM;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_FACEBOOK)) {
            appId = NotificationConst.KEY_APP_FACEBOOK;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_INSTAGRAM)) {
            appId = NotificationConst.KEY_APP_INSTAGRAM;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_KAKAO)) {
            appId = NotificationConst.KEY_APP_KAKAO;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_LINE)) {
            appId = NotificationConst.KEY_APP_LINE;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_MESSENGER)) {
            appId = NotificationConst.KEY_APP_MESSENGER;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_TWITTER)) {
            appId = NotificationConst.KEY_APP_TWITTER;
        } else if (packageName.equals(NotificationConst.PACKAGE_NAME_WHATSAPP)) {
            appId = NotificationConst.KEY_APP_WHATSAPP;
        }

        return appId;
    }


    public static boolean parseSettingValue(String phoneSettingValue) {
        boolean isPhoneOpen = false;
        try {
            JSONObject jsonObject = new JSONObject(phoneSettingValue);
            isPhoneOpen = jsonObject.optBoolean("is_open");
        } catch (Exception e) {
        }
        return isPhoneOpen;
    }

    public static String buildSettingValue(Boolean value) {
        if (value == null) {
            return null;
        }
        JSONObject phoneValueJsonObj = new JSONObject();
        try {
            phoneValueJsonObj.put("is_open", value);
        } catch (Exception e) {

        }

        return phoneValueJsonObj.toString();
    }

}
