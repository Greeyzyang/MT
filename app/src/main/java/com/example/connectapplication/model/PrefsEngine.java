package com.example.connectapplication.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

/**
 * 本地存储
 *
 * @author lijiewen
 * @date on 2020/7/6
 */
public class PrefsEngine {

    public static final String TAG = "PrefsEngine";

    private static Context mContext;
    private static boolean mIsInitialized;


    /**
     * 在application调用进行初始化
     *
     * @param context Application context
     */
    public static void init(Context context) {
        if (mIsInitialized) {
            return;
        }
        mContext = context.getApplicationContext();
        mIsInitialized = true;
    }


    protected static SharedPreferences getSharedPreferences(String spName) {
        if (null == mContext) {
            return null;
        } else {
            return mContext.getSharedPreferences(spName, Context.MODE_PRIVATE);
        }
    }

    /**
     * 获取全部key-value
     *
     * @param spName
     * @return
     */
    public static Map<String, ?> getAll(String spName) {
        SharedPreferences preferences = getSharedPreferences(spName);
        if (preferences != null) {
            return preferences.getAll();
        } else {
            return null;
        }
    }

    /**
     * 判断是否存在key
     *
     * @param spName
     * @param key
     * @return
     */
    public static boolean contains(String spName, String key) {
        SharedPreferences preferences = getSharedPreferences(spName);
        if (preferences != null) {
            return preferences.contains(key);
        } else {
            return false;
        }
    }


    /**
     * 移除指定key
     *
     * @param spName
     * @param key
     */
    public static void remove(String spName, String key) {
        SharedPreferences preferences = getSharedPreferences(spName);
        if (preferences != null) {
            preferences.edit()
                    .remove(key)
                    .commit();
        }
    }


    /**
     * 清除全部key
     *
     * @param spName
     */
    public static void clear(String spName) {
        SharedPreferences preferences = getSharedPreferences(spName);
        if (preferences != null) {
            preferences.edit()
                    .clear()
                    .commit();
        }
    }


    /**
     * 同步保存string类型value
     *
     * @param spName
     * @param key
     * @param value
     */
    public static void putString(String spName, String key, String value) {
        SharedPreferences preferences = getSharedPreferences(spName);
        if (preferences != null) {
            preferences.edit()
                    .putString(key, value)
                    .commit();
        }
    }

    /**
     * 获取string类型value
     *
     * @param spName
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(String spName, String key, String defValue) {
        SharedPreferences preferences = getSharedPreferences(spName);
        if (preferences != null) {
            return preferences.getString(key, defValue);
        } else {
            return defValue;
        }
    }


    /**
     * 同步保存Set<String>类型value
     *
     * @param spName
     * @param key
     * @param values
     */
    public static void putStringSet(String spName, String key, Set<String> values) {
        SharedPreferences preferences = getSharedPreferences(spName);
        if (preferences != null) {
            preferences.edit()
                    .putStringSet(key, values)
                    .commit();
        }
    }

    /**
     * 获取Set<String>类型value
     *
     * @param spName
     * @param key
     * @param defValues
     * @return
     */
    public static Set<String> getStringSet(String spName, String key, Set<String> defValues) {
        SharedPreferences preferences = getSharedPreferences(spName);
        if (preferences != null) {
            return preferences.getStringSet(key, defValues);
        } else {
            return defValues;
        }
    }


    /**
     * 同步保存int类型value
     *
     * @param spName
     * @param key
     * @param value
     */
    public static void putInt(String spName, String key, int value) {
        SharedPreferences preferences = getSharedPreferences(spName);
        if (preferences != null) {
            preferences.edit()
                    .putInt(key, value)
                    .commit();
        }
    }


    /**
     * 获取int类型value
     *
     * @param spName
     * @param key
     * @param defValue
     * @return
     */
    public static int getInt(String spName, String key, int defValue) {
        SharedPreferences preferences = getSharedPreferences(spName);
        if (preferences != null) {
            return preferences.getInt(key, defValue);
        } else {
            return defValue;
        }
    }


    /**
     * 同步保存long类型value
     *
     * @param spName
     * @param key
     * @param value
     */
    public static void putLong(String spName, String key, long value) {
        SharedPreferences preferences = getSharedPreferences(spName);
        if (preferences != null) {
            preferences.edit()
                    .putLong(key, value)
                    .commit();
        }
    }


    /**
     * 获取long类型value
     *
     * @param spName
     * @param key
     * @param defValue
     * @return
     */
    public static long getLong(String spName, String key, long defValue) {
        SharedPreferences preferences = getSharedPreferences(spName);
        if (preferences != null) {
            return preferences.getLong(key, defValue);
        } else {
            return defValue;
        }
    }

    /**
     * 同步保存float类型value
     *
     * @param spName
     * @param key
     * @param value
     */
    public static void putFloat(String spName, String key, float value) {
        SharedPreferences preferences = getSharedPreferences(spName);
        if (preferences != null) {
            preferences.edit()
                    .putFloat(key, value)
                    .commit();
        }
    }

    /**
     * 获取float类型value
     *
     * @param spName
     * @param key
     * @param defValue
     * @return
     */
    public static float getFloat(String spName, String key, float defValue) {
        SharedPreferences preferences = getSharedPreferences(spName);
        if (preferences != null) {
            return preferences.getFloat(key, defValue);
        } else {
            return defValue;
        }
    }


    /**
     * 保存boolean
     *
     * @param spName
     * @param key
     * @param value
     */
    public static void putBoolean(String spName, String key, boolean value) {
        SharedPreferences preferences = getSharedPreferences(spName);
        if (preferences != null) {
            preferences.edit()
                    .putBoolean(key, value)
                    .commit();
        }
    }


    /**
     * 获取boolean类型value
     *
     * @param spName
     * @param key
     * @param defValue
     * @return
     */
    public static boolean getBoolean(String spName, String key, boolean defValue) {
        SharedPreferences preferences = getSharedPreferences(spName);
        if (preferences != null) {
            return preferences.getBoolean(key, defValue);
        }
        return false;
    }


}
