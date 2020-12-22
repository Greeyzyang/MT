package com.example.connectapplication.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.connectapplication.R;
import com.example.connectapplication.event.BindEvent;

import org.greenrobot.eventbus.EventBus;

public class ConnectionDialog {

    public static void showNormalDialog(Context context){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        normalDialog.setIcon(R.drawable.ic_launcher_background);
        normalDialog.setTitle("友情提示");
        normalDialog.setMessage("绑定失败，点击确定后，重新初始化app，请确认手表是否已经回到未绑定状态");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        EventBus.getDefault().post(new BindEvent("initEvent",null));
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        EventBus.getDefault().post(new BindEvent("initEvent",null));
                    }
                });
        // 显示
        normalDialog.show();
    }
}
