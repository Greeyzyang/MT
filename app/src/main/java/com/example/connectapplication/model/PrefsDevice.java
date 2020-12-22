package com.example.connectapplication.model;

/**
 * 本地存储
 *
 * @author lijiewen
 * @date on 2020/7/6
 */
public class PrefsDevice extends PrefsEngine {

    private static final String PREFS_NAME = "com.ryeex.band";
    private static final String KEY_DEVICE_MAC = "key_device_mac";
    private static final String KEY_DEVICE_TOKEN = "key_device_token";


    public static void saveDeviceMac(String mac) {
        putString(PREFS_NAME, KEY_DEVICE_MAC, mac);
    }

    public static String getDeviceMac() {
        return getString(PREFS_NAME, KEY_DEVICE_MAC, "");
    }


    public static void saveDeviceToken(String token) {
        putString(PREFS_NAME, KEY_DEVICE_TOKEN, token);
    }


    public static String getDeviceToken() {
        return getString(PREFS_NAME, KEY_DEVICE_TOKEN, "");
    }


    public static boolean hasDevice() {
        return contains(PREFS_NAME, KEY_DEVICE_MAC);
    }

    public static void removeDevice(){
        remove(PREFS_NAME, KEY_DEVICE_MAC);
        remove(PREFS_NAME, KEY_DEVICE_TOKEN);
    }

}
