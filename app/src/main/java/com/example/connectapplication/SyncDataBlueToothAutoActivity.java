package com.example.connectapplication;

import android.bluetooth.BluetoothAdapter;
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
import com.ryeex.ble.common.model.entity.Height;
import com.ryeex.ble.common.model.entity.Weight;
import com.ryeex.ble.connector.callback.AsyncBleCallback;
import com.ryeex.ble.connector.error.BleError;
import com.ryeex.ble.connector.scan.BleScanner;
import com.ryeex.ble.connector.scan.ScannedDevice;
import com.ryeex.watch.adapter.device.WatchDevice;
import com.ryeex.watch.protocol.pb.entity.PBDevice;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyncDataBlueToothAutoActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView title_sync_data_back;
    private TextView phone_style_num;//手机型号
    private TextView phone_system_num;//手机系统
    private RelativeLayout select_device_releate;//选择设备布局
    private TextView phone_select_device;//选择设备号
    private EditText phone_distance;//测试距离输入
    private EditText phone_count;//测试次数输入
    private EditText phone_space;//测试间隔输入
    private TextView title_bind_start;//绑定设备
    private TextView title_unbind_start;//解绑设备
    private TextView test_bind_unbind_result;//绑定或解绑设备
    private EditText phone_user_height_num;//用户身高
    private EditText phone_user_weight_num;//用户体重
    private TextView title_sync_data_start_to_watch;//开始同步下发到手表
    //private TextView title_sync_time;//同步时间
    //private TextView title_sync_data;//同步健康数据和运动数据
    private TextView title_unbind_end_test;//停止测试
    private TextView sync_result;//同步结果
    private TextView test_time;//测试时间
    private TextView test_model;//机型
    private TextView test_mac;//mac
    private TextView test_distance;//距离
    private TextView test_all_time;//测试总次数
    private TextView test_space;//间隔
    private TextView test_current_count;//当前次数
    private TextView send_phone_to_watch_success;//下发手表数据成功次数
    private TextView send_phone_to_watch_fail;//下发手表数据失败次数
    private TextView send_phone_to_watch_success_rate;//下发手表数据成功率
    private TextView sync_time_success;//同步时间和时区成功次数
    private TextView sync_time_fail;//同步时间和时区失败次数
    private TextView sync_time_success_rate;//同步时间和时区成功率
    private TextView sync_data_success;//同步数据成功次数
    private TextView sync_data_fail;//同步数据失败次数
    private TextView sync_data_success_rate;//同步数据成功率
    private TextView test_fail_records;//手表日志
    private TextView wrong_log_records;//错误日志

    private static final String Type_watch ="to_watch";
    private static final String Type_time = "sync_time";
    private static final String Type_data = "sync_data";

    private final int REQUEST_OK = 3;
    private boolean isComputeSyncDataConnected = true;
    private boolean isBind;
    private final int MSG_GET_SYNC_DATA_HEALTH = 10095;
    private int playCount;
    private int playspace;
    private boolean isHeightsuccess;
    private boolean isWeightsuccess;
    private String currentday;

    private String wrong_logs;

    private WatchDevice watchDevice;

    private ScannedDevice scannedDevices;
    private BluetoothAdapter mBluetooth;
    private boolean hasbinderror;
    private DeviceConnectListener deviceConnectListener = new DeviceConnectListener() {
        @Override
        public void onConnecting() {
            //setDeviceConnectStatus("正在连接...");
            Log.d("yj", "connecting------listener-----");
        }

        @Override
        public void onLoginSuccess() {
            Log.d("yj", "loginsuccess------listener-----");
        }

        @Override
        public void onDisconnected(BleError error) {
            Log.d("yj", "disconnected-----listener------");
        }

        @Override
        public void onFailure(BleError bleError) {
            Log.d("yj", "failure-----listener------");
        }
    };
    Handler sHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_GET_SYNC_DATA_HEALTH:
                    Log.d("yj","MSG_GET_SYNC_DATA_HEALTH");
                     isComputeSyncDataConnected = false;
                     Log.d("yj","playCount-----"+playCount);
                    if (playCount == 0) {
                        isComputeSyncDataConnected = true;
                        sHandler.removeCallbacksAndMessages(null);
                        sync_result.setText(getResources().getString(R.string.connect_phone_test_result));
                        wrong_log_records.setText(wrong_logs);
                        getToWatchCount(send_phone_to_watch_success, send_phone_to_watch_fail, send_phone_to_watch_success_rate);
                        getSyncTimeCount(sync_time_success,sync_time_fail,sync_time_success_rate);
                        getSyncDataCount(sync_data_success,sync_data_fail,sync_data_success_rate);
                    }

                    if(playCount>0) {
                        CommonValue.COUNT_ALL++;
                        Log.d("yj","CommonValue-----"+CommonValue.COUNT_ALL);
                        test_current_count.setText(CommonValue.COUNT_ALL + "次");
                        ScanBlueTooth.syncDataall(watchDevice,phone_user_height_num.getText().toString().trim(),phone_user_weight_num.getText().toString().trim());
                        playCount--;
                    }

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_sync_data_bluetooth_auto);
        CommonValue.isSyncDataAutoSecond = "worked";
        mBluetooth = BluetoothAdapter.getDefaultAdapter(); //获取Bluetooth适配器
        CommonShared.clear(SyncDataBlueToothAutoActivity.this);
        currentday = GetTime.getCurrentTime_Today();
        EventBus.getDefault().register(this);
        init();
        watchDevice = new WatchDevice();
        BleScanner.getInstance().addDeviceScanCallback(new BleScanner.OnDeviceScanCallback() {

            @Override
            public void onFind(ScannedDevice scannedDevice) {
                if (CommonValue.isSyncDataAutoSecond.equals("worked")) {
                    if (phone_select_device.getText().toString().trim().equals(scannedDevice.getMac())) {
                        scannedDevices = scannedDevice;
                        BleScanner.getInstance().stopScan();
                        ScanBlueTooth.startBind(scannedDevice, watchDevice);
                    }
                }
            }
        });
        watchDevice.addDeviceConnectListener(deviceConnectListener);
    }

    private void init(){
        title_sync_data_back = findViewById(R.id.title_sync_data_back);
        phone_style_num = findViewById(R.id.phone_style_num);
        phone_system_num = findViewById(R.id.phone_system_num);
        select_device_releate = findViewById(R.id.select_device_releate);
        phone_select_device = findViewById(R.id.phone_select_device_num);
        phone_distance = findViewById(R.id.phone_distance_num);
        phone_count = findViewById(R.id.phone_test_count_num);
        phone_space = findViewById(R.id.phone_space_num);
        title_bind_start = findViewById(R.id.title_start_start);
        title_unbind_start = findViewById(R.id.title_unbind_result);
        test_bind_unbind_result = findViewById(R.id.test_bind_result);
        phone_user_height_num = findViewById(R.id.phone_user_height_num);
        phone_user_weight_num = findViewById(R.id.phone_user_weight_num);
        title_sync_data_start_to_watch = findViewById(R.id.title_sync_data_to_watch_start);
//        title_sync_time = findViewById(R.id.title_sync_time);
//        title_sync_data = findViewById(R.id.title_sync_data);
        title_unbind_end_test = findViewById(R.id.title_unbind_end_test);
        sync_result = findViewById(R.id.sync_result);
        test_time = findViewById(R.id.phone_test_time_num);
        test_model = findViewById(R.id.phone_test_model_num);
        test_mac = findViewById(R.id.phone_test_mac_num);
        test_distance = findViewById(R.id.phone_test_distance_num);
        test_all_time = findViewById(R.id.phone_test_all_count_num);
        test_space = findViewById(R.id.phone_test_space_num);
        test_current_count = findViewById(R.id.phone_test_current_count_num);
        send_phone_to_watch_success = findViewById(R.id.send_phone_to_watch_success);
        send_phone_to_watch_fail = findViewById(R.id.send_phone_to_watch_fail);
        send_phone_to_watch_success_rate = findViewById(R.id.send_phone_to_watch_success_rate);
        sync_time_success = findViewById(R.id.send_phone_to_sync_time_success);
        sync_time_fail = findViewById(R.id.send_phone_to_sync_time_fail);
        sync_time_success_rate = findViewById(R.id.send_phone_to_sync_time_success_rate);
        sync_data_success = findViewById(R.id.send_phone_to_sync_data_success);
        sync_data_fail = findViewById(R.id.send_phone_to_sync_data_fail);
        sync_data_success_rate = findViewById(R.id.send_phone_to_sync_data_success_rate);
        test_fail_records = findViewById(R.id.test_fail_records);
        wrong_log_records = findViewById(R.id.wrong_log_records);
        phone_style_num.setText(GetPhoneInfo.getSystemModel());
        phone_system_num.setText(GetPhoneInfo.getSystemVersion());
        phone_distance.setText("3");
        phone_count.setText("100");
        phone_space.setText("5");
        CommonValue.SYNC_DATA_TO_WATCH_ALL = 0;
        CommonValue.SYNC_DATA_TO_WATCH_SUCCESS = 0;
        CommonValue.SYNC_DATA_TO_WATCH_FAIL = 0;
        CommonValue.SYNC_DATA_TIME_ALL = 0;
        CommonValue.SYNC_DATA_TIME_SUCCESS = 0;
        CommonValue.SYNC_DATA_TIME_FAIL = 0;
        CommonValue.SYNC_DATA_HEALTH_ALL = 0;
        CommonValue.SYNC_DATA_HEALTH_SUCCESS = 0;
        CommonValue.SYNC_DATA_HEALTH_FAIL = 0;
        CommonValue.COUNT_ALL = 0;
        title_sync_data_back.setOnClickListener(this);
        select_device_releate.setOnClickListener(this);
        title_bind_start.setOnClickListener(this);
        title_unbind_start.setOnClickListener(this);
        title_sync_data_start_to_watch.setOnClickListener(this);
        title_unbind_end_test.setOnClickListener(this);
        test_fail_records.setOnClickListener(this);
        //title_sync_time.setOnClickListener(this);
        //title_sync_data.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.title_sync_data_back:
                finish();
                break;
            case R.id.select_device_releate:
                if(Util.isFastClick()) {
                    if(isComputeSyncDataConnected)
                        gotoSelectDevice();
                    else
                        Toast.makeText(SyncDataBlueToothAutoActivity.this,"同步数据中",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.title_start_start:
                if(Util.isFastClick())
                    startBindorUnbind(true);
                break;
            case R.id.title_unbind_result:
                if(Util.isFastClick())
                    startBindorUnbind(false);
                break;
            case R.id.title_sync_data_to_watch_start:
                if(Util.isFastClick())
                    clickSyncData();
                break;
            case R.id.title_unbind_end_test:
                if(Util.isFastClick())
                    stopTest();
                break;
            case R.id.test_fail_records:
                if(watchDevice.isLogin()){
                    ScanBlueTooth.writeDeviceLog(watchDevice,SyncDataBlueToothAutoActivity.this,currentday);
                }else{
                    Toast.makeText(SyncDataBlueToothAutoActivity.this,"请确保设备绑定状态",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    private void clickSyncData() {
        if (isComputeSyncDataConnected ) {
            if (TextUtils.isEmpty(phone_select_device.getText().toString().trim()) || phone_select_device.getText().toString().trim().equals("--")) {
                Toast.makeText(SyncDataBlueToothAutoActivity.this, getResources().getString(R.string.connect_warning_select_device), Toast.LENGTH_LONG).show();
                return;
            }
            if (isNumber(phone_distance) && isNumber(phone_count) && isNumber(phone_space)) {
                if (phone_count.getText().toString().trim().equals("0")) {
                    Toast.makeText(SyncDataBlueToothAutoActivity.this, getResources().getString(R.string.connect_warning_play_count), Toast.LENGTH_LONG).show();
                    return;
                }
                if (isBind) {
                    CommonValue.SYNC_DATA_TO_WATCH_ALL = 0;
                    CommonValue.SYNC_DATA_TO_WATCH_SUCCESS = 0;
                    CommonValue.SYNC_DATA_TO_WATCH_FAIL = 0;
                    CommonValue.SYNC_DATA_TIME_ALL = 0;
                    CommonValue.SYNC_DATA_TIME_SUCCESS = 0;
                    CommonValue.SYNC_DATA_TIME_FAIL = 0;
                    CommonValue.SYNC_DATA_HEALTH_ALL = 0;
                    CommonValue.SYNC_DATA_HEALTH_SUCCESS = 0;
                    CommonValue.SYNC_DATA_HEALTH_FAIL = 0;
                    CommonValue.COUNT_ALL =0;
                    watchDevice.setMac(phone_select_device.getText().toString().trim());
                    test_time.setText(GetTime.getCurrentTime_Today());
                    test_model.setText(GetPhoneInfo.getSystemModel());
                    test_mac.setText(phone_select_device.getText().toString().trim());
                    test_distance.setText(phone_distance.getText().toString().trim() + "米");
                    test_space.setText(phone_space.getText().toString().trim() + "秒");
                    test_all_time.setText(phone_count.getText().toString().trim() + "次");
                    test_current_count.setText("--");
                    //test_scan_min, test_scan_max, test_scan_average, test_scan_succ_count, test_scan_fail_count, test_scan_succ_rate
                    send_phone_to_watch_success.setText("--");
                    send_phone_to_watch_fail.setText("--");
                    send_phone_to_watch_success_rate.setText("--");
                    sync_time_success.setText("--");
                    sync_time_fail.setText("--");
                    sync_time_success_rate.setText("--");
                    sync_data_success.setText("--");
                    sync_data_fail.setText("--");
                    sync_data_success_rate.setText("--");
                    sync_result.setText(getResources().getString(R.string.connect_phone_test_testing));
                    playCount = Integer.valueOf(phone_count.getText().toString().trim());
                    playspace = Integer.valueOf(phone_space.getText().toString().trim());
                    Log.d("yj", "playCount--sync---" + playCount + "--sync--" + playspace);
                    sHandler.sendEmptyMessage(MSG_GET_SYNC_DATA_HEALTH);

                } else {
                    Toast.makeText(SyncDataBlueToothAutoActivity.this, "请先绑定设备", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(SyncDataBlueToothAutoActivity.this, getResources().getString(R.string.connect_warning_input_number_style), Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            Toast.makeText(SyncDataBlueToothAutoActivity.this, getResources().getString(R.string.connect_scaning_connecting), Toast.LENGTH_LONG).show();
        }
    }



    protected boolean isNumber(EditText editText) {
        if (!TextUtils.isEmpty(editText.getText().toString().trim())) {
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher isNum = pattern.matcher(editText.getText().toString().trim());
            if (!isNum.matches()) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    private void gotoSelectDevice() {
        Intent in = new Intent();
        in.setClass(SyncDataBlueToothAutoActivity.this, SelectDeviceActivity.class);
        startActivityForResult(in, REQUEST_OK);
        CommonValue.isSyncDataAutoSecond = "secondisworked";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String device_mac = data.getStringExtra("discover_mac");
            phone_select_device.setText(device_mac);
        }
    }

    private void startBindorUnbind(boolean bind) {
        if (isComputeSyncDataConnected ) {
            if (TextUtils.isEmpty(phone_select_device.getText().toString().trim()) || phone_select_device.getText().toString().trim().equals("--")) {
                Toast.makeText(SyncDataBlueToothAutoActivity.this, getResources().getString(R.string.connect_warning_select_device), Toast.LENGTH_LONG).show();
                return;
            }
            watchDevice.setMac(phone_select_device.getText().toString().trim());
            if (bind) {
                if(hasbinderror) {

                }
                if (watchDevice.isLogin()) {
                    isBind =true;
                    Toast.makeText(SyncDataBlueToothAutoActivity.this, "设备已绑定", Toast.LENGTH_LONG).show();
                    return;
                }
                test_bind_unbind_result.setText("绑定设备中……");
                ScanBlueTooth.startScan(SyncDataBlueToothAutoActivity.this);
            } else {
                    test_bind_unbind_result.setText("绑定解绑中……");
                    ScanBlueTooth.endBind(watchDevice);
            }

        } else {
            Toast.makeText(SyncDataBlueToothAutoActivity.this, getResources().getString(R.string.send_auto_sending), Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BindEvent event) {
        Log.d("yj","进入onevent--------");
        if (event.message.equals(CommonValue.BIND_SUCCESS)) {
            Log.d("yj","进入onevent1--------");
            test_bind_unbind_result.setText("绑定设备成功");
            isBind = true;
            if(hasbinderror) {
                Log.d("yj","进入onevent1-1-------");
                sHandler.sendEmptyMessageDelayed(MSG_GET_SYNC_DATA_HEALTH, playspace * 1000);
                hasbinderror = false;
            }
        } else if (event.message.equals(CommonValue.BIND_ERROR)) {
            Log.d("yj","进入onevent2--------");
            wrong_logs = wrong_logs+"\n"+event.errorinfo;
            test_bind_unbind_result.setText("绑定设备失败");
            //ConnectionDialog.showNormalDialog(NotificationAutoTestActivity.this);
            String error = JosnParse.parseString(event.errorinfo);
            String bleCode = error.split("\\|")[0];
            String message = error.split("\\|")[1];
            Toast.makeText(SyncDataBlueToothAutoActivity.this,message,Toast.LENGTH_LONG).show();
            if(bleCode.equals("133")){
                Log.d("yj","进入onevent2-1-------");
                mBluetooth.disable();
                Toast.makeText(SyncDataBlueToothAutoActivity.this,"蓝牙关闭成功",Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(7000);
                    mBluetooth.enable();
                    ScanBlueTooth.startBind(scannedDevices,watchDevice);
                    Toast.makeText(SyncDataBlueToothAutoActivity.this,"蓝牙重新打开成功",Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else if(bleCode.equals("-1")&&message.equals("device had bind already")){
                Log.d("yj","进入onevent2-2-------");
                hasbinderror = true;
                ScanBlueTooth.endBind(watchDevice);
                Toast.makeText(SyncDataBlueToothAutoActivity.this,"device had bind already",Toast.LENGTH_LONG).show();
            }
        }

        if (event.message.equals(CommonValue.UNBIND_SUCCESS)) {
            Log.d("yj","进入onevent3--------");
            test_bind_unbind_result.setText("解绑设备成功");
            isBind = false;
            if(hasbinderror) {
                Log.d("yj","进入onevent3-1-------");
                try {
                    Thread.sleep(7000);
                    ScanBlueTooth.startBind(scannedDevices, watchDevice);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (event.message.equals(CommonValue.UNBIND_ERROR)) {
            Log.d("yj","进入onevent4--------");
            wrong_logs = wrong_logs+"\n"+event.errorinfo;
            test_bind_unbind_result.setText("解绑设备失败");
            String error = JosnParse.parseString(event.errorinfo);
            String bleCode = error.split("\\|")[0];
            String message = error.split("\\|")[1];
            if(hasbinderror)
                Log.d("yj","进入onevent4-1-------");
                sHandler.sendEmptyMessageDelayed(MSG_GET_SYNC_DATA_HEALTH,playspace*1000);
            Toast.makeText(SyncDataBlueToothAutoActivity.this,message,Toast.LENGTH_LONG).show();
        }

        if(event.message.equals("syncDataSuccess")){
            Log.d("yj","进入onevent5--------");
            if(!isComputeSyncDataConnected)
                Log.d("yj","进入onevent5-1-------");
                sHandler.sendEmptyMessageDelayed(MSG_GET_SYNC_DATA_HEALTH,playspace*1000);
        }else if(event.message.equals("syncDataError")){
            Log.d("yj","进入onevent6--------");
            wrong_logs = wrong_logs+"\n"+event.errorinfo;
            String error = JosnParse.parseString(event.errorinfo);
            String bleCode = error.split("\\|")[0];
            String message = error.split("\\|")[1];
            if(bleCode.equals("-1")&&message.equals("device is not connected")){
                Log.d("yj","进入onevent6-1-------");
                try {
                    Thread.sleep(7000);
                    mBluetooth.enable();
                    ScanBlueTooth.startLogin(watchDevice);
                    Toast.makeText(SyncDataBlueToothAutoActivity.this, "蓝牙重新打开成功", Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    Log.e("yj","interrupted---e");
                    e.printStackTrace();
                }
            }
            else {
                Log.d("yj","进入onevent6-2-------");
                if (!isComputeSyncDataConnected)
                    sHandler.sendEmptyMessageDelayed(MSG_GET_SYNC_DATA_HEALTH,playspace*1000);
            }
        }else if(event.message.equals("syncTimeError")){
            Log.d("yj","进入onevent7--------");
            wrong_logs = wrong_logs+"\n"+event.errorinfo;
            String error = JosnParse.parseString(event.errorinfo);
            String bleCode = error.split("\\|")[0];
            String message = error.split("\\|")[1];
            if(bleCode.equals("-1")&&message.equals("device is not connected")){
                Log.d("yj","进入onevent7-1-------");
                try {
                    Thread.sleep(7000);
                    mBluetooth.enable();
                    ScanBlueTooth.startLogin(watchDevice);
                    Toast.makeText(SyncDataBlueToothAutoActivity.this, "蓝牙重新打开成功", Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    Log.e("yj","interrupted---e");
                    e.printStackTrace();
                }
            }
            else {
                Log.d("yj","进入onevent7-2-------");
                if (!isComputeSyncDataConnected)
                    sHandler.sendEmptyMessageDelayed(MSG_GET_SYNC_DATA_HEALTH,playspace*1000);
            }
        }else if(event.message.equals("syncWeightError")){
            Log.d("yj","进入onevent8--------");
            wrong_logs = wrong_logs+"\n"+event.errorinfo;
            String error = JosnParse.parseString(event.errorinfo);
            String bleCode = error.split("\\|")[0];
            String message = error.split("\\|")[1];
            if(bleCode.equals("-1")&&message.equals("device is not connected")){
                Log.d("yj","进入onevent8-1-------");
                try {
                    Thread.sleep(7000);
                    mBluetooth.enable();
                    ScanBlueTooth.startLogin(watchDevice);
                    Toast.makeText(SyncDataBlueToothAutoActivity.this, "蓝牙重新打开成功", Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    Log.e("yj","interrupted---e");
                    e.printStackTrace();
                }
            }
            else {
                Log.d("yj","进入onevent8-2-------");
                if (!isComputeSyncDataConnected)
                    Log.d("yj","进入onevent8-2-1------");
                    sHandler.sendEmptyMessageDelayed(MSG_GET_SYNC_DATA_HEALTH,playspace*1000);
            }
        }else if(event.message.equals("syncHeightError")){
            Log.d("yj","进入onevent9--------");
            wrong_logs = wrong_logs+"\n"+event.errorinfo;
            String error = JosnParse.parseString(event.errorinfo);
            String bleCode = error.split("\\|")[0];
            String message = error.split("\\|")[1];
           if(bleCode.equals("-1")&&message.equals("device is not connected")){
               Log.d("yj","进入onevent9-1-------");
                try {
                    Thread.sleep(7000);
                    mBluetooth.enable();
                    ScanBlueTooth.startLogin(watchDevice);
                    Toast.makeText(SyncDataBlueToothAutoActivity.this, "蓝牙重新打开成功", Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    Log.e("yj","interrupted---e");
                    e.printStackTrace();
                }
            }
            else {
               Log.d("yj","进入onevent9-2-------");
                if (!isComputeSyncDataConnected)
                    Log.d("yj","进入onevent9-2-1------");
                    sHandler.sendEmptyMessageDelayed(MSG_GET_SYNC_DATA_HEALTH,playspace*1000);
            }
        }

        if(event.message.equals(CommonValue.LOGIN_SUCCESS)){
            Log.d("yj","进入onevent10--------");
            if (!isComputeSyncDataConnected)
                Log.d("yj","进入onevent10-1-------");
                sHandler.sendEmptyMessageDelayed(MSG_GET_SYNC_DATA_HEALTH,playspace*1000);
        }else if(event.message.equals(CommonValue.LOGIN_ERROR)) {
            Log.d("yj","进入onevent11--------");
            String error = JosnParse.parseString(event.errorinfo);
            String bleCode = error.split("\\|")[0];
            String message = error.split("\\|")[1];
            if (bleCode.equals("133")) {
                Log.d("yj","进入onevent11-1-------");
                mBluetooth.disable();
                Toast.makeText(SyncDataBlueToothAutoActivity.this, "蓝牙关闭成功", Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(7000);
                    mBluetooth.enable();
                    ScanBlueTooth.startLogin(watchDevice);
                    Toast.makeText(SyncDataBlueToothAutoActivity.this, "蓝牙重新打开成功", Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("yj","进入onevent11-2-------");
                try {
                    Thread.sleep(7000);
                    ScanBlueTooth.startLogin(watchDevice);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void stopTest() {
        watchDevice.setMac(phone_select_device.getText().toString().trim());
        if (sHandler != null) {
            sHandler.removeCallbacksAndMessages(null);
        }
        playCount = 0;
        playspace = 0;
        sync_result.setText(getResources().getString(R.string.connect_phone_test_result));
        wrong_log_records.setText(wrong_logs);
        if (BleScanner.getInstance().isScanning()) {
            BleScanner.getInstance().stopScan();
        }
        getToWatchCount(send_phone_to_watch_success, send_phone_to_watch_fail, send_phone_to_watch_success_rate);
        getSyncTimeCount(sync_time_success,sync_time_fail,sync_time_success_rate);
        getSyncDataCount(sync_data_success,sync_data_fail,sync_data_success_rate);
        isComputeSyncDataConnected = true;
    }

    private void getToWatchCount(TextView send_to_watch_success_num, TextView send_to_watch_fail_num, TextView send_to_watch_success_rate) {
        send_to_watch_success_num.setText(CommonValue.SYNC_DATA_TO_WATCH_SUCCESS + "次");
        send_to_watch_fail_num.setText(CommonValue.SYNC_DATA_TO_WATCH_FAIL + "次");
        if (CommonValue.SYNC_DATA_TO_WATCH_SUCCESS == 0 && CommonValue.SYNC_DATA_TO_WATCH_FAIL == 0) {
            send_to_watch_success_rate.setText("0%");
        } else {
            float ra = (CommonValue.SYNC_DATA_TO_WATCH_SUCCESS / Float.parseFloat(phone_count.getText().toString().trim())) * 100;
            String raa = Util.getTwoFloat(ra);
            send_to_watch_success_rate.setText(raa + "%");
        }
    }

    private void getSyncTimeCount(TextView sync_time_success_num, TextView sync_time_fail_num, TextView sync_time_success_rate) {
        sync_time_success_num.setText(CommonValue.SYNC_DATA_TIME_SUCCESS + "次");
        sync_time_fail_num.setText(CommonValue.SYNC_DATA_TIME_FAIL + "次");
        if (CommonValue.SYNC_DATA_TIME_SUCCESS == 0 && CommonValue.SYNC_DATA_TIME_FAIL == 0) {
            sync_time_success_rate.setText("0%");
        } else {
            float ra=(CommonValue.SYNC_DATA_TIME_SUCCESS / Float.parseFloat(phone_count.getText().toString().trim())) * 100;
            String raa = Util.getTwoFloat(ra);
            sync_time_success_rate.setText(raa + "%");
        }
    }

    private void getSyncDataCount(TextView sync_data_success_num, TextView sync_data_fail_num, TextView sync_data_success_rate) {
        sync_data_success_num.setText(CommonValue.SYNC_DATA_HEALTH_SUCCESS + "次");
        sync_data_fail_num.setText(CommonValue.SYNC_DATA_HEALTH_FAIL + "次");
        if (CommonValue.SYNC_DATA_HEALTH_SUCCESS == 0 && CommonValue.SYNC_DATA_HEALTH_FAIL == 0) {
            sync_data_success_rate.setText("0%");
        } else {
            float ra=(CommonValue.SYNC_DATA_HEALTH_SUCCESS /Float.parseFloat(phone_count.getText().toString().trim())) * 100;
            String raa = Util.getTwoFloat(ra);
            sync_data_success_rate.setText( raa+ "%");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonValue.isSyncDataAutoSecond = "ideal";
        EventBus.getDefault().unregister(SyncDataBlueToothAutoActivity.this);
        BleScanner.getInstance().stopScan();
        BleScanner.getInstance().removeDeviceScanCallback(new BleScanner.OnDeviceScanCallback() {
            @Override
            public void onFind(ScannedDevice scannedDevice) {
                Log.d("yj", "remove--syncdata--scannedDevice--");
            }
        });
        if (sHandler != null) {
            sHandler.removeCallbacksAndMessages(null);
        }
    }
}
