package com.example.connectapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.connectapplication.constans.CommonValue;
import com.example.connectapplication.event.BindEvent;
import com.example.connectapplication.utils.CommonShared;
import com.example.connectapplication.utils.GetPhoneInfo;
import com.example.connectapplication.utils.GetTime;
import com.example.connectapplication.utils.JosnParse;
import com.example.connectapplication.utils.ScanBlueTooth;
import com.example.connectapplication.utils.Util;
import com.ryeex.ble.common.device.DeviceConnectListener;
import com.ryeex.ble.connector.error.BleError;
import com.ryeex.ble.connector.scan.BleScanner;
import com.ryeex.ble.connector.scan.ScannedDevice;
import com.ryeex.watch.adapter.device.WatchDevice;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BlueToothLongConnectActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView title_long_connect_back;
    private TextView phone_style_num;//手机型号
    private TextView phone_system_num;//手机系统
    private RelativeLayout select_device_releate;//选择设备布局
    private TextView phone_select_device;//选择设备号
    private EditText phone_distance;//测试距离输入
    private TextView title_start_start;//绑定设备
    private TextView title_unbind_start;//解绑设备
    private TextView test_bind_result;//绑定或解绑设备
    private TextView title_stop_test;//停止测试
    private TextView test_result;//测试结果
    private TextView test_time;//测试时间
    private TextView test_model;//机型
    private TextView test_mac;//mac
    private TextView test_distance;//距离
    private TextView test_all_time;//测试总时长
    private TextView test_current_timer;//当前时长
    private TextView test_connect_min;//连接最短时长
    private TextView test_connect_max;//连接最长时长
    private TextView test_connect_average;//连接平均时长
    private TextView test_disconnect_count;//连接断开次数
    private TextView test_fail_records;//手表日志
    private TextView wrong_log_records;//错误日志

    private final int REQUEST_OK = 5;

    private WatchDevice watchDevice;

    private boolean isBlueLongConnect = true;

    private boolean isBind;

    private String wrong_logs;

    private long startConnectTime;

    private long endConnectTime;

    private long startBindTime;

    private long stopTime;

    private ScannedDevice scanneddevice;

    private List<Long> connectList = new ArrayList<>();

    private final int MSG_GET_BLUETOOTH_LONG_CONNECT_SCAN= 10013;

    private boolean isloginsuccess;

    private String wrong_log = "";

    private boolean isFirstclick;

    private DeviceConnectListener deviceConnectListener = new DeviceConnectListener() {
        @Override
        public void onConnecting() {
            //setDeviceConnectStatus("正在连接...");
        }

        @Override
        public void onLoginSuccess() {

            //setDeviceConnectStatus("已连接");
            startConnectTime = System.currentTimeMillis();
            isloginsuccess = true;
            Log.d("yj","loginsuccess-----------");

        }

        @Override
        public void onDisconnected(BleError error) {

            //setDeviceConnectStatus("连接断开");
            CommonValue.DISCONNECT_COUNT++;
            test_disconnect_count.setText(CommonValue.DISCONNECT_COUNT+"次");
            if(isloginsuccess) {
                endConnectTime = System.currentTimeMillis();
                isloginsuccess = false;
                connectList.add(endConnectTime-startConnectTime);
                Log.d("yj","disconnected-----from login------");
            }else{
                Log.d("yj","disconnected-----no from login------");
                endConnectTime = System.currentTimeMillis();
                connectList.add(endConnectTime-startBindTime);
            }
            Log.d("yj","disconnected-----目前时间------"+(endConnectTime - startBindTime));
                if (TextUtils.isEmpty(CommonShared.ReadToken(BlueToothLongConnectActivity.this, phone_select_device.getText().toString().trim()))) {
                    ScanBlueTooth.startBind(scanneddevice,watchDevice);
                }else{
                    ScanBlueTooth.startLogin(watchDevice);
                }
        }

        @Override
        public void onFailure(BleError error) {
            //setDeviceConnectStatus("连接失败");
            Log.e("yj","loginfail-----error------"+error.toString());
            if (TextUtils.isEmpty(CommonShared.ReadToken(BlueToothLongConnectActivity.this, phone_select_device.getText().toString().trim()))) {
                ScanBlueTooth.startBind(scanneddevice,watchDevice);
            }else{
                ScanBlueTooth.startLogin(watchDevice);
            }
        }
    };

    Handler blHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_GET_BLUETOOTH_LONG_CONNECT_SCAN:
                    isBlueLongConnect = false;
                    BleScanner.getInstance().stopScan();
                    ScannedDevice scannedDevice = (ScannedDevice) msg.obj;
                    ScanBlueTooth.startBind(scannedDevice, watchDevice);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_bluetooth_long_connect);
        CommonShared.clear(BlueToothLongConnectActivity.this);
        EventBus.getDefault().register(this);
        initView();
        watchDevice = new WatchDevice();
        BleScanner.getInstance().addDeviceScanCallback(new BleScanner.OnDeviceScanCallback() {

            @Override
            public void onFind(ScannedDevice scannedDevice) {
                if (CommonValue.isLongBlueTooth.equals("worked")) {
                    if (phone_select_device.getText().toString().trim().equals(scannedDevice.getMac())) {
                        Log.d("yj","equls--------isscansuccess");
                        scanneddevice = scannedDevice;
                        startConnectTime = System.currentTimeMillis(); //起始时间
                        Message msg = blHandler.obtainMessage();
                        msg.what = MSG_GET_BLUETOOTH_LONG_CONNECT_SCAN;
                        msg.obj = scannedDevice;
                        blHandler.sendMessage(msg);
                    }
                }
            }
        });
        watchDevice.addDeviceConnectListener(deviceConnectListener);
    }

    private void initView() {
        title_long_connect_back = findViewById(R.id.title_long_connect_back);
        phone_style_num = findViewById(R.id.phone_style_num);
        phone_system_num = findViewById(R.id.phone_system_num);
        select_device_releate = findViewById(R.id.select_device_releate);
        phone_select_device = findViewById(R.id.phone_select_device_num);
        phone_distance = findViewById(R.id.phone_distance_num);
        title_start_start = findViewById(R.id.title_start_start);
        title_unbind_start = findViewById(R.id.title_unbind_start);
        test_bind_result = findViewById(R.id.test_bind_result);
        title_stop_test = findViewById(R.id.title_stop_test);
        test_result = findViewById(R.id.test_result);
        test_time = findViewById(R.id.phone_test_time_num);
        test_model = findViewById(R.id.phone_test_model_num);
        test_mac = findViewById(R.id.phone_test_mac_num);
        test_distance = findViewById(R.id.phone_test_distance_num);
        test_all_time = findViewById(R.id.phone_test_all_timer_num);
        test_current_timer = findViewById(R.id.phone_test_current_count_num);
        test_connect_min = findViewById(R.id.phone_test_connect_min_num);
        test_connect_max = findViewById(R.id.phone_test_connect_max_num);
        test_connect_average = findViewById(R.id.phone_test_connect_average_num);
        test_disconnect_count = findViewById(R.id.phone_test_connect_disconnect_count_num);
        test_fail_records = findViewById(R.id.test_fail_records);
        wrong_log_records = findViewById(R.id.wrong_log_records);

        phone_style_num.setText(GetPhoneInfo.getSystemModel());
        phone_system_num.setText(GetPhoneInfo.getSystemVersion());
        title_long_connect_back.setOnClickListener(this);
        select_device_releate.setOnClickListener(this);
        title_start_start.setOnClickListener(this);
        title_unbind_start.setOnClickListener(this);
        title_stop_test.setOnClickListener(this);
        test_fail_records.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_long_connect_back:
                finish();
                break;
            case R.id.select_device_releate:
                if (Util.isFastClick()) {
                    if (isBlueLongConnect)
                        gotoSelectDevice();
                    else
                        Toast.makeText(BlueToothLongConnectActivity.this, "蓝牙连接中，请稍后或者停止后点击", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.title_start_start:
                if (Util.isFastClick())
                    startBindorUnbind(true);
                break;
            case R.id.title_unbind_start:
                if (Util.isFastClick())
                    startBindorUnbind(false);
                break;
            case R.id.title_start_test:

                break;
            case R.id.title_stop_test:
                stopTest();
                break;

        }
    }

    private void stopTest() {
        watchDevice.setMac(phone_select_device.getText().toString().trim());
        watchDevice.setToken(CommonShared.ReadToken(BlueToothLongConnectActivity.this, phone_select_device.getText().toString().trim()));
        if (blHandler != null) {
            blHandler.removeCallbacksAndMessages(null);
        }
        test_result.setText(getResources().getString(R.string.connect_phone_test_result));
        wrong_log_records.setText(wrong_log);
        stopTime = System.currentTimeMillis();
        if(new Double(GetTime.getStandardDate(stopTime-startBindTime))>60) {
            String timer = String.format("%.2f", new Double(GetTime.getStandardDate(stopTime-startBindTime)) / 60);
            test_all_time.setText(timer + "小时");
        }else{
            test_all_time.setText(GetTime.getStandardDate(stopTime-startBindTime)+ "分钟");
        }
        if(connectList.size()==0)
            connectList.add(stopTime-startBindTime);
        getCount(connectList, test_connect_min, test_connect_max, test_connect_average);
        test_disconnect_count.setText(CommonValue.DISCONNECT_COUNT+"次");
        if (BleScanner.getInstance().isScanning()) {
            BleScanner.getInstance().stopScan();
        }
        isBlueLongConnect = true;
        isBind = false;
        isloginsuccess = false;
    }

    private void startBindorUnbind(boolean bind) {
        if (isBlueLongConnect ) {
            if (TextUtils.isEmpty(phone_select_device.getText().toString().trim()) || phone_select_device.getText().toString().trim().equals("--")) {
                Toast.makeText(BlueToothLongConnectActivity.this, getResources().getString(R.string.connect_warning_select_device), Toast.LENGTH_LONG).show();
                return;
            }
            watchDevice.setMac(phone_select_device.getText().toString().trim());
            if (bind) {
                isFirstclick = true;
                connectList.clear();
                if (watchDevice.isLogin()) {
                    isBind = true;
                    Toast.makeText(BlueToothLongConnectActivity.this, "设备已绑定", Toast.LENGTH_LONG).show();
                    return;
                }
                test_bind_result.setText("绑定设备中……");
                ScanBlueTooth.startScan(BlueToothLongConnectActivity.this);
            } else {
                if (watchDevice.isLogin()) {
                    test_bind_result.setText("绑定解绑中……");
                    ScanBlueTooth.endBind(watchDevice);
                } else {
                    Toast.makeText(BlueToothLongConnectActivity.this, "当前是解绑状态，请检查设备是否处于未绑定状态", Toast.LENGTH_LONG).show();
                }

            }

        } else {
            Toast.makeText(BlueToothLongConnectActivity.this, getResources().getString(R.string.send_auto_sending), Toast.LENGTH_LONG).show();
        }
    }

    private void gotoSelectDevice() {
        Intent in = new Intent();
        in.setClass(BlueToothLongConnectActivity.this, SelectDeviceActivity.class);
        startActivityForResult(in, REQUEST_OK);
        CommonValue.isLongBlueTooth = "secondisworked";
    }

    private void getCount(List<Long> list, TextView mintext, TextView maxtext, TextView averagetext) {
        Log.d("yj", "list.size-----" + list.size());
        if (list.size() > 0) {
            long max = Collections.max(list);
            long min = Collections.min(list);
            long average = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                average = (list.stream().collect(Collectors.summingLong(Long::longValue))) / list.size();
            } else {
                long a = 0;
                for (int i = 0; i < list.size(); i++) {
                    a = list.get(i) + a;
                }
                average = a / list.size();
            }
            String timer;
            if(new Double(GetTime.getStandardDate(min))>=60) {
                timer = String.format("%.2f",new Double(GetTime.getStandardDate(min)) / 60);
                mintext.setText( timer + "小时");
            }else if(new Double(GetTime.getStandardDate(min))>0&&new Double(GetTime.getStandardDate(min))<60) {
                mintext.setText(GetTime.getStandardDate(min) + "分钟");
            }
            if(new Double(GetTime.getStandardDate(max))>=60) {
                timer = String.format("%.2f",new Double(GetTime.getStandardDate(max)) / 60);
                maxtext.setText(timer + "小时");
            }else
                maxtext.setText(GetTime.getStandardDate(min) + "分钟");
            if(new Double(GetTime.getStandardDate(average))>=60) {
                timer = String.format("%.2f",new Double(GetTime.getStandardDate(average)) / 60);
                averagetext.setText(timer + "小时");
            }else
                averagetext.setText(GetTime.getStandardDate(average)+ "分钟");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String device_mac = data.getStringExtra("discover_mac");
            phone_select_device.setText(device_mac);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BindEvent event) {
        if (event.message.equals(CommonValue.BIND_SUCCESS)) {
            test_bind_result.setText("绑定设备成功");
            test_result.setText(getResources().getString(R.string.connect_phone_test_testing));
            isBind = true;
            startBindTime = System.currentTimeMillis();
            test_time.setText(GetTime.getCurrentTime_Today());
            test_model.setText(GetPhoneInfo.getSystemModel());
            test_mac.setText(phone_select_device.getText().toString().trim());
            test_distance.setText(phone_distance.getText().toString().trim() + "米");
            CommonShared.WriteToken(BlueToothLongConnectActivity.this,phone_select_device.getText().toString().trim(),watchDevice.getToken());

        } else if (event.message.equals(CommonValue.BIND_ERROR)) {
            wrong_logs = wrong_logs+"\n"+event.errorinfo;
            test_bind_result.setText("绑定设备失败");
            //ConnectionDialog.showNormalDialog(NotificationAutoTestActivity.this);
            String error = JosnParse.parseString(event.errorinfo);
            String bleCode = error.split("\\|")[0];
            String message = error.split("\\|")[1];
            Toast.makeText(BlueToothLongConnectActivity.this,message,Toast.LENGTH_LONG).show();
        }

        if (event.message.equals(CommonValue.UNBIND_SUCCESS)) {
            test_bind_result.setText("解绑设备成功");
            isBind = false;
            CommonShared.WriteToken(BlueToothLongConnectActivity.this,phone_select_device.getText().toString().trim(),"");
        } else if (event.message.equals(CommonValue.UNBIND_ERROR)) {
            wrong_logs = wrong_logs+"\n"+event.errorinfo;
            test_bind_result.setText("解绑设备失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonValue.isLongBlueTooth= "ideal";
        EventBus.getDefault().unregister(BlueToothLongConnectActivity.this);
        BleScanner.getInstance().stopScan();
        BleScanner.getInstance().stopScan();
        BleScanner.getInstance().removeDeviceScanCallback(new BleScanner.OnDeviceScanCallback() {
            @Override
            public void onFind(ScannedDevice scannedDevice) {
                Log.d("yj", "remove--long--scannedDevice--");
            }
        });
        if (blHandler != null) {
            blHandler.removeCallbacksAndMessages(null);
        }
    }
}
