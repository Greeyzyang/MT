package com.example.connectapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class CommonShared {

    private static final String TOKEN_FILE = "token_file";


    public static String  ReadToken(Context context,String mac){
        SharedPreferences spf = context.getSharedPreferences(TOKEN_FILE, Context.MODE_PRIVATE);
        String token = spf.getString(mac,"");
        return token;
    }

    public static void WriteToken(Context context,String mac,String token){
        SharedPreferences spf = context.getSharedPreferences(TOKEN_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spf.edit();
        editor.putString(mac,token);
        editor.commit();
    }

    public static void clear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(TOKEN_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();

        editor.commit();
    }
}
