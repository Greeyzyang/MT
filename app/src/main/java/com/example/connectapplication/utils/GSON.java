package com.example.connectapplication.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * GSON工具类
 * <p>
 * 注意：需要进行发序列的javabean不能混淆，可继承GrootEntity，已对所有继承GrootEntity的类不作混淆
 *
 * @author lijiewen
 * @date on 2018/11/1
 */
public class GSON {


    /**
     * json to javabean
     *
     * @param json
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            return new Gson().fromJson(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * json to javabean
     *
     * @param json
     */
    public static <T> T parseObject(String json, Type type) {
        return new Gson().fromJson(json, type);
    }

    /**
     * json字符串转List集合
     */

    public static <T> List<T> parseObjectList(String json, Class<T> cls) {
        if (!TextUtils.isEmpty(json)) {
            List<T> list = new ArrayList<>();
            try {
                Gson gson = new Gson();
                JsonArray jsonArray = new JsonParser().parse(json).getAsJsonArray();
                for (JsonElement jsonElement : jsonArray) {
                    list.add(gson.fromJson(jsonElement, cls));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }
        return new ArrayList<>();
    }


    /**
     * 转成json字符串
     *
     * @param t
     * @return
     */
    public static String toJSONString(Object t) {
        if (t != null) {
            return new Gson().toJson(t);
        }
        return "";
    }


}
