package com.example.connectapplication.utils;

import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class JosnParse {

    public static String parseString(String error){
        //BleError={code:45, bleCode:-1, message:device had bind already}
        //{code:5, bleCode:22, message:onConnectionStateChange status:22 newState:0}
        String bleCode;
        String message;
        String errorinfo = error.substring(0, error.indexOf("="));
        String result = error.substring(errorinfo.length()+1,error.length());
        Log.d("yj",":result-----"+result);
        String resultstr = result.split(",")[1];
        bleCode = resultstr.split(":")[1];
        String messages = result.split(",")[2];
        String messagestr = messages.split(":")[1];
        message = messagestr.substring(0,messagestr.length()-1);
        Log.d("yj","message------"+message);

        return bleCode +"|"+message;
    }
}
