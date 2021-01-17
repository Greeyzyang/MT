package com.example.connectapplication;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.connectapplication.utils.WriteLogToFile;
import com.ryeex.ble.connector.handler.BleHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView title_back;

    private TextView blueToothConnecet;

    private TextView blueToothNoConfirmConnected;

    private TextView notification_auto_test;

    private TextView notification_manual_test;

    private TextView syncDataBlueTooth_auto;

    private TextView syncDataBlueTooth_manual;

    private TextView update_title;

    private TextView long_bluetooth;

    private String fileDir = WriteLogToFile.logPath + File.separator + "watchupdate/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        BleHandler.getWorkerHandler().post(new Runnable() {
            @Override
            public void run() {
                copyAssets("85-10.bin");
                copyAssets("0.3.10.bin");
                copyAssets("0.2.85.bin");
                copyAssets("10-85.bin");
            }
        });
    }

    private void initView(){
        title_back = findViewById(R.id.title_menu_back);
        blueToothConnecet = findViewById(R.id.title_bluetooth_into);
        blueToothNoConfirmConnected = findViewById(R.id.title_bluetooth_no_confirm_into);
        notification_auto_test = findViewById(R.id.title_notification_auto_into);
        notification_manual_test = findViewById(R.id.title_notification_manual_into);
        syncDataBlueTooth_auto = findViewById(R.id.title_sync_data_bluetooth_auto_into);
        syncDataBlueTooth_manual = findViewById(R.id.title_sync_data_bluetooth_manual_into);
        update_title = findViewById(R.id.title_menu_update_into);
        long_bluetooth = findViewById(R.id.title_menu_long_bluetooth_into);
        title_back.setOnClickListener(this);
        blueToothConnecet.setOnClickListener(this);
        blueToothNoConfirmConnected.setOnClickListener(this);
        notification_auto_test.setOnClickListener(this);
        notification_manual_test.setOnClickListener(this);
        syncDataBlueTooth_auto.setOnClickListener(this);
        syncDataBlueTooth_manual.setOnClickListener(this);
        update_title.setOnClickListener(this);
        long_bluetooth.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_menu_back:
                finish();
                break;
            case R.id.title_bluetooth_into:
                gotoOtherActivity(BlueToothConnectActivity.class);
                break;
            case R.id.title_bluetooth_no_confirm_into:
                gotoOtherActivity(BlueToothNoConfirmConnectActivity.class);
                break;
            case R.id.title_notification_auto_into:
                gotoOtherActivity(NotificationAutoTestActivity.class);
                break;
            case R.id.title_notification_manual_into:
                gotoOtherActivity(NotificationManualTestActivity.class);
                break;
            case R.id.title_sync_data_bluetooth_auto_into:
                gotoOtherActivity(SyncDataBlueToothAutoActivity.class);
                break;
            case R.id.title_sync_data_bluetooth_manual_into:
                gotoOtherActivity(SyncDataBlueToothManualActivity.class);
                break;
            case R.id.title_menu_update_into:
                gotoOtherActivity(UpdateActivity.class);
                break;
            case R.id.title_menu_long_bluetooth_into:
                gotoOtherActivity(BlueToothLongConnectActivity.class);
                break;
        }
    }

    private void gotoOtherActivity(Class classes){
        Intent in = new Intent();
        in.setClass(MainActivity.this,classes);
        startActivity(in);
    }

    private void copyAssets(String fileName) {
        try {
            File files = new File(fileDir);
            if (!files.exists()) {
                files.mkdirs();
            }
            File file = new File(files, fileName);
            InputStream is = null;
            try {
                AssetManager manager = getAssets();
                if (manager == null) {
                    return;
                }
                is = manager.open(fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (is == null) {
                return;
            }
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {
                // buffer字节
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();// 刷新缓冲区
            is.close();
            fos.close();
        } catch (Exception e) {
            Log.e("yj","copy---exception---"+e.toString());
            e.printStackTrace();
        }
    }
}
