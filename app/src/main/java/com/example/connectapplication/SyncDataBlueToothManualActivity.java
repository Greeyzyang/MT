package com.example.connectapplication;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.connectapplication.utils.GetPhoneInfo;
import com.example.connectapplication.utils.GetTime;
import com.example.connectapplication.utils.ScanBlueTooth;
import com.example.connectapplication.utils.Util;
import com.ryeex.ble.connector.scan.BleScanner;
import com.ryeex.ble.connector.scan.ScannedDevice;
import com.ryeex.watch.adapter.device.WatchDevice;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyncDataBlueToothManualActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_sync_data_manual_back;//返回
    private TextView phone_style_num;//手机型号
    private TextView phone_system_num;//手机系统
    private RelativeLayout select_device_releate;//选择设备布局
    private TextView phone_select_device;//选择设备号
    private EditText phone_distance;//测试距离输入
    private TextView title_bind_start;//绑定设备
    private TextView title_unbind_start;//解绑设备
    private TextView test_bind_unbind_result;//绑定或解绑设备
    private TextView title_test_start;//开始测试
    private EditText phone_user_height_num;//用户身高
    private EditText phone_user_weight_num;//用户体重
    private TextView title_sync_data_start;//开始同步
    private TextView stop_test;//停止测试
    private TextView sync_result;//同步结果
    private TextView test_time;//测试时间
    private TextView test_model;//机型
    private TextView test_mac;//mac
    private TextView test_distance;//距离
    private TextView test_all_time;//测试总次数
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

    private final int REQUEST_OK = 4;
    private boolean isComputeSyncDataManualConnected = true;
    private boolean isBind;
    private WatchDevice watchDevice;
    private boolean isStartBegin;
    private String currentdata;

    private String wrong_logs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_sync_data_bluetooth_manual);
        CommonValue.isIsSyncDataManualSecond = "ideal";
        currentdata = GetTime.getCurrentTime_Today();
        EventBus.getDefault().register(this);
        init();
        watchDevice = new WatchDevice();
        BleScanner.getInstance().addDeviceScanCallback(new BleScanner.OnDeviceScanCallback() {

            @Override
            public void onFind(ScannedDevice scannedDevice) {
                if (CommonValue.isIsSyncDataManualSecond.equals("worked")) {
                    if (phone_select_device.getText().toString().trim().equals(scannedDevice.getMac())) {
                        BleScanner.getInstance().stopScan();
                        ScanBlueTooth.startBind(scannedDevice, watchDevice);
                    }
                }
            }
        });
    }

    private void init() {
        title_sync_data_manual_back = findViewById(R.id.title_sync_data_manual_back);
        phone_style_num = findViewById(R.id.phone_style_num);
        phone_system_num = findViewById(R.id.phone_system_num);
        select_device_releate = findViewById(R.id.select_device_releate);
        phone_select_device = findViewById(R.id.phone_select_device_num);
        phone_distance = findViewById(R.id.phone_distance_num);
        title_bind_start = findViewById(R.id.title_manual_start_start);
        title_unbind_start = findViewById(R.id.title_unbind_result);
        test_bind_unbind_result = findViewById(R.id.test_bind_result);
        title_test_start = findViewById(R.id.title_start_test);
        phone_user_height_num = findViewById(R.id.phone_user_height_num);
        phone_user_weight_num = findViewById(R.id.phone_user_weight_num);
        title_sync_data_start = findViewById(R.id.title_send_message);
        stop_test = findViewById(R.id.title_end_test);
        sync_result = findViewById(R.id.sync_result);
        test_time = findViewById(R.id.phone_test_time_num);
        test_model = findViewById(R.id.phone_test_model_num);
        test_mac = findViewById(R.id.phone_test_mac_num);
        test_distance = findViewById(R.id.phone_test_distance_num);
        test_all_time = findViewById(R.id.phone_test_all_count_num);
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
        CommonValue.SYNC_DATA_TO_WATCH_ALL = 0;
        CommonValue.SYNC_DATA_TO_WATCH_SUCCESS = 0;
        CommonValue.SYNC_DATA_TO_WATCH_FAIL = 0;
        CommonValue.SYNC_DATA_TIME_ALL = 0;
        CommonValue.SYNC_DATA_TIME_SUCCESS = 0;
        CommonValue.SYNC_DATA_TIME_FAIL = 0;
        CommonValue.SYNC_DATA_HEALTH_ALL = 0;
        CommonValue.SYNC_DATA_HEALTH_SUCCESS = 0;
        CommonValue.SYNC_DATA_HEALTH_FAIL = 0;
        title_sync_data_manual_back.setOnClickListener(this);
        select_device_releate.setOnClickListener(this);
        title_bind_start.setOnClickListener(this);
        title_unbind_start.setOnClickListener(this);
        title_test_start.setOnClickListener(this);
        title_sync_data_start.setOnClickListener(this);
        stop_test.setOnClickListener(this);
        test_fail_records.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_sync_data_manual_back:
                finish();
                break;
            case R.id.select_device_releate:
                if (Util.isFastClick()) {
                    if (isComputeSyncDataManualConnected)
                        gotoSelectDevice();
                    else
                        Toast.makeText(SyncDataBlueToothManualActivity.this, "同步数据中", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.title_manual_start_start:
                if (Util.isFastClick())
                    startBindorUnbind(true);
                break;
            case R.id.test_bind_unbind_result:
                if (Util.isFastClick())
                    startBindorUnbind(false);
                break;
            case R.id.title_start_test:
                if (Util.isFastClick())
                    startBegin();
                    break;
            case R.id.title_send_message:
                if (Util.isFastClick()){
                    if(isComputeSyncDataManualConnected&&isStartBegin) {
                        isComputeSyncDataManualConnected = false;
                        CommonValue.SYNC_DATA_TO_WATCH_ALL++;
                        test_all_time.setText(CommonValue.SYNC_DATA_TO_WATCH_ALL+"次");
                        ScanBlueTooth.syncDataall(watchDevice, phone_user_height_num.getText().toString().trim(), phone_user_weight_num.getText().toString().trim());
                    }else
                        Toast.makeText(SyncDataBlueToothManualActivity.this,"同步数据中或请点击开始测试",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.title_end_test:
                if (Util.isFastClick())
                    stopTest();
                break;
            case R.id.test_fail_records:
                if(watchDevice.isLogin()){
                    ScanBlueTooth.writeDeviceLog(watchDevice,SyncDataBlueToothManualActivity.this,currentdata);
                }else{
                    Toast.makeText(SyncDataBlueToothManualActivity.this,"请确保设备绑定状态",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void gotoSelectDevice() {
        Intent in = new Intent();
        in.setClass(SyncDataBlueToothManualActivity.this, SelectDeviceActivity.class);
        startActivityForResult(in, REQUEST_OK);
        CommonValue.isIsSyncDataManualSecond = "secondisworked";
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
        if (isComputeSyncDataManualConnected) {
            if (TextUtils.isEmpty(phone_select_device.getText().toString().trim()) || phone_select_device.getText().toString().trim().equals("--")) {
                Toast.makeText(SyncDataBlueToothManualActivity.this, getResources().getString(R.string.connect_warning_select_device), Toast.LENGTH_LONG).show();
                return;
            }
            watchDevice.setMac(phone_select_device.getText().toString().trim());
            if (bind) {
                if (watchDevice.isLogin()) {
                    isBind = true;
                    Toast.makeText(SyncDataBlueToothManualActivity.this, "设备已绑定", Toast.LENGTH_LONG).show();
                    return;
                }
                test_bind_unbind_result.setText("绑定设备中……");
                ScanBlueTooth.startScan(SyncDataBlueToothManualActivity.this);
            } else {
                if (watchDevice.isLogin()) {
                    test_bind_unbind_result.setText("绑定解绑中……");
                    ScanBlueTooth.endBind(watchDevice);
                } else {
                    Toast.makeText(SyncDataBlueToothManualActivity.this, "当前是解绑状态，请检查设备是否处于未绑定状态", Toast.LENGTH_LONG).show();
                }

            }

        } else {
            Toast.makeText(SyncDataBlueToothManualActivity.this, getResources().getString(R.string.send_auto_sending), Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BindEvent event) {

        if (event.message.equals(CommonValue.BIND_SUCCESS)) {
            test_bind_unbind_result.setText("绑定设备成功");
            isBind = true;
        } else if (event.message.equals(CommonValue.BIND_ERROR)) {
            wrong_logs = wrong_logs+"\n"+event.errorinfo;
            test_bind_unbind_result.setText("绑定设备失败");
            //ConnectionDialog.showNormalDialog(NotificationAutoTestActivity.this);
        }

        if (event.message.equals(CommonValue.UNBIND_SUCCESS)) {
            test_bind_unbind_result.setText("解绑设备成功");
            isBind = false;
        } else if (event.message.equals(CommonValue.UNBIND_ERROR)) {
            wrong_logs = wrong_logs+"\n"+event.errorinfo;
            test_bind_unbind_result.setText("解绑设备失败");
        }

        if (event.message.equals("syncDataSuccess")) {
            isComputeSyncDataManualConnected = true;
        } else if (event.message.equals("syncDataError")) {
            wrong_logs = wrong_logs+"\n"+event.errorinfo;
            Log.d("yj","syncdataerror------");
            isComputeSyncDataManualConnected = true;
            CommonValue.SYNC_DATA_HEALTH_FAIL++;
        } else if (event.message.equals("syncTimeError")) {
            wrong_logs = wrong_logs+"\n"+event.errorinfo;
            Log.d("yj","syncTimeError------");
            isComputeSyncDataManualConnected = true;
            CommonValue.SYNC_DATA_TIME_FAIL++;
        } else if (event.message.equals("syncWeightError")) {
            wrong_logs = wrong_logs+"\n"+event.errorinfo;
            Log.d("yj","syncWeightError------");
            isComputeSyncDataManualConnected = true;
            CommonValue.SYNC_DATA_TO_WATCH_FAIL++;
        } else if (event.message.equals("syncHeightError")) {
            wrong_logs = wrong_logs+"\n"+event.errorinfo;
            Log.d("yj","syncHeightError------");
            isComputeSyncDataManualConnected = true;
            CommonValue.SYNC_DATA_TO_WATCH_FAIL++;
        }
    }

    private void stopTest() {
        watchDevice.setMac(phone_select_device.getText().toString().trim());
        sync_result.setText(getResources().getString(R.string.connect_phone_test_result));
        wrong_log_records.setText(wrong_logs);
        if (BleScanner.getInstance().isScanning()) {
            BleScanner.getInstance().stopScan();
        }
        getToWatchCount(send_phone_to_watch_success, send_phone_to_watch_fail, send_phone_to_watch_success_rate);
        getSyncTimeCount(sync_time_success, sync_time_fail, sync_time_success_rate);
        getSyncDataCount(sync_data_success, sync_data_fail, sync_data_success_rate);
        isComputeSyncDataManualConnected = true;
        isStartBegin = false;
    }

    private void getToWatchCount(TextView send_to_watch_success_num, TextView send_to_watch_fail_num, TextView send_to_watch_success_rate) {
        send_to_watch_success_num.setText(CommonValue.SYNC_DATA_TO_WATCH_SUCCESS + "次");
        send_to_watch_fail_num.setText(CommonValue.SYNC_DATA_TO_WATCH_FAIL + "次");
        if (CommonValue.SYNC_DATA_TO_WATCH_SUCCESS == 0 && CommonValue.SYNC_DATA_TO_WATCH_FAIL == 0) {
            send_to_watch_success_rate.setText("0%");
        } else {
            send_to_watch_success_rate.setText((CommonValue.SYNC_DATA_TO_WATCH_SUCCESS / (float)(CommonValue.SYNC_DATA_TO_WATCH_SUCCESS + CommonValue.SYNC_DATA_TO_WATCH_FAIL)) * 100 + "%");
        }
    }

    private void getSyncTimeCount(TextView sync_time_success_num, TextView sync_time_fail_num, TextView sync_time_success_rate) {
        sync_time_success_num.setText(CommonValue.SYNC_DATA_TIME_SUCCESS + "次");
        sync_time_fail_num.setText(CommonValue.SYNC_DATA_TIME_FAIL + "次");
        if (CommonValue.SYNC_DATA_TIME_SUCCESS == 0 && CommonValue.SYNC_DATA_TIME_FAIL == 0) {
            sync_time_success_rate.setText("0%");
        } else {
            sync_time_success_rate.setText((CommonValue.SYNC_DATA_TIME_SUCCESS / (float)(CommonValue.SYNC_DATA_TIME_SUCCESS + CommonValue.SYNC_DATA_TIME_FAIL)) * 100 + "%");
        }
    }

    private void getSyncDataCount(TextView sync_data_success_num, TextView sync_data_fail_num, TextView sync_data_success_rate) {
        sync_data_success_num.setText(CommonValue.SYNC_DATA_HEALTH_SUCCESS + "次");
        sync_data_fail_num.setText(CommonValue.SYNC_DATA_HEALTH_FAIL + "次");
        if (CommonValue.SYNC_DATA_HEALTH_SUCCESS == 0 && CommonValue.SYNC_DATA_HEALTH_FAIL == 0) {
            sync_data_success_rate.setText("0%");
        } else {
            sync_data_success_rate.setText((CommonValue.SYNC_DATA_HEALTH_SUCCESS / (float)(CommonValue.SYNC_DATA_HEALTH_SUCCESS + CommonValue.SYNC_DATA_HEALTH_FAIL)) * 100 + "%");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonValue.isIsSyncDataManualSecond = "ideal";
        EventBus.getDefault().unregister(SyncDataBlueToothManualActivity.this);
        BleScanner.getInstance().stopScan();
        BleScanner.getInstance().removeDeviceScanCallback(new BleScanner.OnDeviceScanCallback() {
            @Override
            public void onFind(ScannedDevice scannedDevice) {
                Log.d("yj", "remove----scannedDevice--");
            }
        });
    }

    private void startBegin() {
        if (!isStartBegin) {
            if (TextUtils.isEmpty(phone_select_device.getText().toString().trim()) || phone_select_device.getText().toString().trim().equals("--")) {
                Toast.makeText(SyncDataBlueToothManualActivity.this, getResources().getString(R.string.connect_warning_select_device), Toast.LENGTH_LONG).show();
                return;
            }
            if (isNumber(phone_distance)) {

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
                    watchDevice.setMac(phone_select_device.getText().toString().trim());
                    test_time.setText(GetTime.getCurrentTime_Today());
                    test_model.setText(GetPhoneInfo.getSystemModel());
                    test_mac.setText(phone_select_device.getText().toString().trim());
                    test_distance.setText(phone_distance.getText().toString().trim() + "米");
                    send_phone_to_watch_success.setText("--");
                    //test_scan_min, test_scan_max, test_scan_average, test_scan_succ_count, test_scan_fail_count, test_scan_succ_rate
                    send_phone_to_watch_fail.setText("--");
                    send_phone_to_watch_success_rate.setText("--");
                    sync_time_success.setText("--");
                    sync_time_fail.setText("--");
                    sync_time_success_rate.setText("--");
                    sync_data_success.setText("--");
                    sync_data_fail.setText("--");
                    sync_data_success_rate.setText("--");
                    sync_result.setText(getResources().getString(R.string.connect_phone_test_testing));
                    isStartBegin = true;
                } else {
                    Toast.makeText(SyncDataBlueToothManualActivity.this, "请先绑定设备", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(SyncDataBlueToothManualActivity.this, getResources().getString(R.string.connect_warning_input_number_style), Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            Toast.makeText(SyncDataBlueToothManualActivity.this, "请先结束上次任务", Toast.LENGTH_LONG).show();
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
}
