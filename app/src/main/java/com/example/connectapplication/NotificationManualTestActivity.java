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
import com.example.connectapplication.utils.NotificationUtil;
import com.example.connectapplication.utils.ScanBlueTooth;
import com.example.connectapplication.utils.Util;
import com.ryeex.ble.common.model.entity.AppNotification;
import com.ryeex.ble.connector.callback.AsyncBleCallback;
import com.ryeex.ble.connector.error.BleError;
import com.ryeex.ble.connector.scan.BleScanner;
import com.ryeex.ble.connector.scan.ScannedDevice;
import com.ryeex.watch.adapter.device.WatchDevice;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.connectapplication.utils.NotificationConst.PACKAGE_NAME_WX;

public class NotificationManualTestActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_notification_manual_back;//返回
    private TextView phone_style_num;//手机型号
    private TextView phone_system_num;//手机系统
    private RelativeLayout select_device_releate;//选择设备布局
    private TextView phone_select_device;//选择设备号
    private EditText phone_distance;//测试距离输入
    private TextView title_bind_start;//绑定设备
    private TextView title_unbind_start;//解绑设备
    private TextView test_bind_unbind_result;//绑定或解绑设备
    private TextView test_notification_selected;//第三方推送选择
    private TextView title_test_start;//开始测试
    private EditText phone_number;//测试手机号
    private RelativeLayout send_notification;//发送通知布局
    private TextView title_send_notification;//发送通知
    private RelativeLayout send_message;//发送短信布局
    private TextView title_send_message;//发送短信
    private RelativeLayout send_call;//发送来电布局
    private TextView title_send_phone_call;//发送来电
    private RelativeLayout send_answer;//发送接听布局
    private TextView title_send_phone_answer;//接听
    private RelativeLayout send_hung_up;//发送挂断布局
    private TextView title_send_phone_hung_up;//发送挂断
    private TextView stop_test;//停止测试
    private TextView test_result;//测试结果
    private TextView test_time;//测试时间
    private TextView test_model;//机型
    private TextView test_mac;//mac
    private TextView test_distance;//距离
    private TextView send_notification_all_number;//发送通知总次数
    private TextView send_notification_success_num;//发送通知成功次数
    private TextView send_notification_fail_num;//发送通知失败次数
    private TextView send_notification_success_rate;//发送通知成功率
    private TextView send_message_all_number;//发送短信总次数
    private TextView send_message_success_num;//发送短信成功次数
    private TextView send_message_fail_num;//发送短信失败次数
    private TextView send_message_success_rate;//发送短信成功率
    private TextView send_call_all_number;//发送来电总次数
    private TextView send_call_success_num;//发送来电成功次数
    private TextView send_call_fail_num;//发送来电失败次数
    private TextView send_call_success_rate;//发送来电成功率
    private TextView send_answer_all_number;//发送接听总次数
    private TextView send_answer_success_num;//发送接听成功次数
    private TextView send_answer_fail_num;//发送接听失败次数
    private TextView send_answer_success_rate;//发送接听成功率
    private TextView send_hung_all_number;//发送挂断总次数
    private TextView send_hung_success_num;//发送挂断成功次数
    private TextView send_hung_fail_num;//发送挂断失败次数
    private TextView send_hung_success_rate;//发送挂断成功率
    private TextView test_fail_records;//手表日志
    private TextView  wrong_log_records;//错误日志

    private boolean isBind;
    private boolean isStartBegin;

    private WatchDevice watchDevice;
    private final int REQUEST_OK = 3;
    private String currentday;

    private String wrong_logs="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_notification_manual);
        CommonValue.isNotificationManualSecond = "worked";
        currentday = GetTime.getCurrentTime_Today();
        EventBus.getDefault().register(this);
        init();
        watchDevice = new WatchDevice();
        BleScanner.getInstance().addDeviceScanCallback(new BleScanner.OnDeviceScanCallback() {

            @Override
            public void onFind(ScannedDevice scannedDevice) {
                if (CommonValue.isNotificationManualSecond.equals("worked")) {
                    if (phone_select_device.getText().toString().trim().equals(scannedDevice.getMac())) {
                        BleScanner.getInstance().stopScan();
                        ScanBlueTooth.startBind(scannedDevice, watchDevice);
                    }
                }
            }
        });
    }

    private void init() {
        title_notification_manual_back = findViewById(R.id.title_notification_back);
        phone_style_num = findViewById(R.id.phone_style_num);
        phone_system_num = findViewById(R.id.phone_system_num);
        select_device_releate = findViewById(R.id.select_device_releate);
        phone_select_device = findViewById(R.id.phone_select_device_num);
        phone_distance = findViewById(R.id.phone_distance_num);
        title_bind_start = findViewById(R.id.title_start_start);
        title_unbind_start = findViewById(R.id.title_unbind_start);
        test_bind_unbind_result = findViewById(R.id.test_bind_result);
        test_notification_selected = findViewById(R.id.test_notification_selected);
        title_test_start = findViewById(R.id.title_manual_start_test);
        phone_number = findViewById(R.id.phone_number);
        send_notification = findViewById(R.id.send_notification_releate);
        title_send_notification = findViewById(R.id.send_notification_current_number);
        send_message = findViewById(R.id.send_message_releate);
        title_send_message = findViewById(R.id.send_message_current_number);
        send_call = findViewById(R.id.send_call_releate);
        title_send_phone_call = findViewById(R.id.send_phone_call_current_number);
        send_answer = findViewById(R.id.send_answer_releate);
        title_send_phone_answer = findViewById(R.id.send_phone_answer_current_number);
        send_hung_up = findViewById(R.id.send_hung_up_releate);
        title_send_phone_hung_up = findViewById(R.id.send_phone_hung_up_current_number);
        stop_test = findViewById(R.id.title_unbind_end_test);
        test_result = findViewById(R.id.test_result);
        test_time = findViewById(R.id.phone_test_time_num);
        test_model = findViewById(R.id.phone_test_model_num);
        test_mac = findViewById(R.id.phone_test_mac_num);
        test_distance = findViewById(R.id.phone_test_distance_num);
        send_notification_all_number = findViewById(R.id.send_notification_all_number);
        send_notification_success_num = findViewById(R.id.send_notification_success_number);
        send_notification_fail_num = findViewById(R.id.send_notification_fail_number);
        send_notification_success_rate = findViewById(R.id.send_notification_success_rate_num);
        send_message_all_number = findViewById(R.id.send_message_all_number);
        send_message_success_num = findViewById(R.id.send_message_success_number);
        send_message_fail_num = findViewById(R.id.send_message_fail_number);
        send_message_success_rate = findViewById(R.id.send_message_success_rate);
        send_call_all_number = findViewById(R.id.send_phone_call_all_number);
        send_call_success_num = findViewById(R.id.send_phone_call_success_number);
        send_call_fail_num = findViewById(R.id.send_call_fail);
        send_call_success_rate = findViewById(R.id.send_phone_call_success_rate);
        send_answer_all_number = findViewById(R.id.send_phone_call_answer_number);
        send_answer_success_num = findViewById(R.id.send_phone_answer_success_number);
        send_answer_fail_num = findViewById(R.id.send_phone_answer_fail_number);
        send_answer_success_rate = findViewById(R.id.send_phone_answer_success_rate_num);
        send_hung_all_number = findViewById(R.id.send_phone_hung_up);
        send_hung_success_num = findViewById(R.id.send_phone_hung_up_success_number);
        send_hung_fail_num = findViewById(R.id.send_phone_hung_up_fail_number);
        send_hung_success_rate = findViewById(R.id.send_hung_up_success_rate_num);
        test_fail_records = findViewById(R.id.test_fail_records);
        wrong_log_records = findViewById(R.id.wrong_log_records);
        phone_style_num.setText(GetPhoneInfo.getSystemModel());
        phone_system_num.setText(GetPhoneInfo.getSystemVersion());
        title_notification_manual_back.setOnClickListener(this);
        select_device_releate.setOnClickListener(this);
        title_bind_start.setOnClickListener(this);
        title_unbind_start.setOnClickListener(this);
        title_test_start.setOnClickListener(this);
        send_notification.setOnClickListener(this);
        send_message.setOnClickListener(this);
        send_call.setOnClickListener(this);
        send_answer.setOnClickListener(this);
        send_hung_up.setOnClickListener(this);
        stop_test.setOnClickListener(this);
        test_fail_records.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_notification_back:
                finish();
                break;
            case R.id.select_device_releate:
                if (Util.isFastClick())
                    gotoSelectDevice();
                break;
            case R.id.title_start_start:
                if (Util.isFastClick())
                    startBindorUnbind(true);
                break;
            case R.id.title_unbind_start:
                if (Util.isFastClick())
                    startBindorUnbind(false);
                break;
            case R.id.title_manual_start_test:
                if (Util.isFastClick())
                    startBegin();
                break;
            case R.id.send_notification_releate:
                todoClick(CommonValue.TYPE_NOTIFICATION);
                break;
            case R.id.send_message_releate:
                todoClick(CommonValue.TYPE_MESSAGE);
                break;
            case R.id.send_call_releate:
                if (Util.isFastClick())
                    todoClick(CommonValue.TYPE_CALL);
                break;
            case R.id.send_answer_releate:
                if (Util.isFastClick())
                    todoClick(CommonValue.TYPE_ANSWER);
                break;
            case R.id.send_hung_up_releate:
                if (Util.isFastClick())
                    todoClick(CommonValue.TYPE_HUNG_UP);
                break;
            case R.id.title_unbind_end_test:
                stopTest();
                break;
            case R.id.test_fail_records:
                if(watchDevice.isLogin()){
                    ScanBlueTooth.writeDeviceLog(watchDevice,NotificationManualTestActivity.this,currentday);
                }else{
                    Toast.makeText(NotificationManualTestActivity.this,"请确保设备绑定状态",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void todoClick(String type) {
        watchDevice.setMac(phone_select_device.getText().toString().trim());

        if (isStartBegin) {
            if (type.equals(CommonValue.TYPE_NOTIFICATION)) {
                CommonValue.SEND_MANUAL_NOTIFICATION_ALL++;
                title_send_notification.setText(CommonValue.SEND_MANUAL_NOTIFICATION_ALL + "次");
                sendNotificationType(watchDevice);
            } else if (type.equals(CommonValue.TYPE_MESSAGE)) {
                CommonValue.SEND_MANUAL_MESSAGE_ALL++;
                title_send_message.setText(CommonValue.SEND_MANUAL_MESSAGE_ALL + "次");
                sendMessageType(watchDevice);
            } else if (type.equals(CommonValue.TYPE_CALL)) {
                CommonValue.SEND_MANUAL_CALL_ALL++;
                title_send_phone_call.setText(CommonValue.SEND_MANUAL_CALL_ALL + "次");
                sendNotification(watchDevice, CommonValue.TYPE_CALL, 1);
            } else if (type.equals(CommonValue.TYPE_ANSWER)) {
                CommonValue.SEND_MANUAL_ANSWER_ALL++;
                title_send_phone_answer.setText(CommonValue.SEND_MANUAL_ANSWER_ALL + "次");
                sendNotification(watchDevice, CommonValue.TYPE_ANSWER, 2);
            } else if (type.equals(CommonValue.TYPE_HUNG_UP)) {
                CommonValue.SEND_MANUAL_HUNG_UP_ALL++;
                title_send_phone_hung_up.setText(CommonValue.SEND_MANUAL_HUNG_UP_ALL + "次");
                sendNotification(watchDevice, CommonValue.TYPE_HUNG_UP, 3);
            }
        } else {
            Toast.makeText(NotificationManualTestActivity.this, "请先开始任务", Toast.LENGTH_LONG).show();
        }

    }

    private void gotoSelectDevice() {
        Intent in = new Intent();
        in.setClass(NotificationManualTestActivity.this, SelectDeviceActivity.class);
        startActivityForResult(in, REQUEST_OK);
        CommonValue.isNotificationManualSecond = "secondisworked";
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
        if (TextUtils.isEmpty(phone_select_device.getText().toString().trim()) || phone_select_device.getText().toString().trim().equals("--")) {
            Toast.makeText(NotificationManualTestActivity.this, getResources().getString(R.string.connect_warning_select_device), Toast.LENGTH_LONG).show();
            return;
        }
        watchDevice.setMac(phone_select_device.getText().toString().trim());
        if (bind) {
            if (watchDevice.isLogin()) {
                isBind = true;
                Toast.makeText(NotificationManualTestActivity.this, "设备已绑定", Toast.LENGTH_LONG).show();
                return;
            }
            test_bind_unbind_result.setText("绑定设备中……");
            ScanBlueTooth.startScan(NotificationManualTestActivity.this);
        } else {
            if (watchDevice.isLogin()) {
                test_bind_unbind_result.setText("绑定解绑中……");
                ScanBlueTooth.endBind(watchDevice);
            } else {
                Toast.makeText(NotificationManualTestActivity.this, "当前是解绑状态，请检查设备是否处于未绑定状态", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void stopTest() {
        watchDevice.setMac(phone_select_device.getText().toString().trim());
        test_result.setText(getResources().getString(R.string.connect_phone_test_result));
        wrong_log_records.setText(wrong_logs);
        if (BleScanner.getInstance().isScanning()) {
            BleScanner.getInstance().stopScan();
        }
        send_notification_all_number.setText(CommonValue.SEND_MANUAL_NOTIFICATION_ALL + "");
        send_message_all_number.setText(CommonValue.SEND_MANUAL_MESSAGE_ALL + "");
        send_call_all_number.setText(CommonValue.SEND_MANUAL_CALL_ALL + "");
        send_answer_all_number.setText(CommonValue.SEND_MANUAL_ANSWER_ALL + "");
        send_hung_all_number.setText(CommonValue.SEND_MANUAL_HUNG_UP_ALL + "");
        getNotificationCount(send_notification_success_num, send_notification_fail_num, send_notification_success_rate);
        getMessageCount(send_message_success_num, send_message_fail_num, send_message_success_rate);
        getCallCount(send_call_success_num, send_call_fail_num, send_call_success_rate);
        getAnswerCount(send_answer_success_num, send_answer_fail_num, send_answer_success_rate);
        getHungupCount(send_hung_success_num, send_hung_fail_num, send_hung_success_rate);
        isStartBegin = false;

    }

    private void getNotificationCount(TextView send_manual_notification_success_num, TextView send_manual_notification_fail_num, TextView send_manual_notification_success_rate) {
        send_manual_notification_success_num.setText(CommonValue.SEND_MANUAL_NOTIFICATION_SUCCESS + "次");
        send_manual_notification_fail_num.setText(CommonValue.SEND_MANUAL_NOTIFICATION_FAIL + "次");
        if (CommonValue.SEND_MANUAL_NOTIFICATION_SUCCESS == 0 && CommonValue.SEND_MANUAL_NOTIFICATION_FAIL == 0) {
            send_manual_notification_success_rate.setText("0%");
        } else {
            float ra = (CommonValue.SEND_MANUAL_NOTIFICATION_SUCCESS /(float)(CommonValue.SEND_MANUAL_NOTIFICATION_FAIL + CommonValue.SEND_MANUAL_NOTIFICATION_SUCCESS)) * 100;
            String raa = Util.getTwoFloat(ra);
            send_manual_notification_success_rate.setText(raa + "%");
        }
    }

    private void getMessageCount(TextView send_manual_message_success_num, TextView send_manual_message_fail_num, TextView send_manual_message_success_rate) {
        send_manual_message_success_num.setText(CommonValue.SEND_MANUAL_MESSAGE_SUCCESS + "次");
        send_manual_message_fail_num.setText(CommonValue.SEND_MANUAL_MESSAGE_FAIL + "次");
        if (CommonValue.SEND_MANUAL_MESSAGE_SUCCESS == 0 && CommonValue.SEND_MANUAL_MESSAGE_FAIL == 0) {
            send_manual_message_success_rate.setText("0%");
        } else {
            float ra = (CommonValue.SEND_MANUAL_MESSAGE_SUCCESS / (float)(CommonValue.SEND_MANUAL_MESSAGE_SUCCESS + CommonValue.SEND_MANUAL_MESSAGE_FAIL)) * 100;
            String raa = Util.getTwoFloat(ra);
            send_manual_message_success_rate.setText(raa + "%");
        }
    }

    private void getCallCount(TextView send_manual_call_success_num, TextView send_manual_call_fail_num, TextView send_manual_call_success_rate) {
        send_manual_call_success_num.setText(CommonValue.SEND_MANUAL_CALL_SUCCESS + "次");
        send_manual_call_fail_num.setText(CommonValue.SEND_MANUAL_CALL_FAIL + "次");
        if (CommonValue.SEND_MANUAL_CALL_SUCCESS == 0 && CommonValue.SEND_MANUAL_CALL_FAIL == 0) {
            send_manual_call_success_rate.setText("0%");
        } else {
            float ra = (CommonValue.SEND_MANUAL_CALL_SUCCESS /(float)(CommonValue.SEND_MANUAL_CALL_SUCCESS + CommonValue.SEND_MANUAL_CALL_FAIL)) * 100;
            String raa = Util.getTwoFloat(ra);
            send_manual_call_success_rate.setText(raa + "%");
        }
    }

    private void getAnswerCount(TextView send_manual_answer_success_num, TextView send_manual_answer_fail_num, TextView send_manual_answer_success_rate) {
        send_manual_answer_success_num.setText(CommonValue.SEND_MANUAL_ANSWER_SUCCESS + "次");
        send_manual_answer_fail_num.setText(CommonValue.SEND_MANUAL_ANSWER_FAIL + "次");
        if (CommonValue.SEND_MANUAL_ANSWER_SUCCESS == 0 && CommonValue.SEND_MANUAL_ANSWER_FAIL == 0) {
            send_manual_answer_success_rate.setText("0%");
        } else {
            float ra = (CommonValue.SEND_MANUAL_ANSWER_SUCCESS / (float)(CommonValue.SEND_MANUAL_ANSWER_SUCCESS + CommonValue.SEND_MANUAL_ANSWER_FAIL)) * 100;
            String raa = Util.getTwoFloat(ra);
            send_manual_answer_success_rate.setText( raa + "%");
        }
    }

    private void getHungupCount(TextView send_manual_hungup_success_num, TextView send_manual_hungup_fail_num, TextView send_manual_hungup_success_rate) {
        send_manual_hungup_success_num.setText(CommonValue.SEND_MANUAL_HUNG_UP_SUCCESS + "次");

        send_manual_hungup_fail_num.setText(CommonValue.SEND_MANUAL_HUNG_UP_FAIL + "次");
        if (CommonValue.SEND_MANUAL_HUNG_UP_SUCCESS == 0 && CommonValue.SEND_MANUAL_HUNG_UP_FAIL == 0) {
            send_manual_hungup_success_rate.setText("0%");
        } else {
            float ra = (CommonValue.SEND_MANUAL_HUNG_UP_SUCCESS /(float)(CommonValue.SEND_MANUAL_HUNG_UP_SUCCESS + CommonValue.SEND_MANUAL_HUNG_UP_FAIL)) * 100;
            String raa = Util.getTwoFloat(ra);
            send_manual_hungup_success_rate.setText(raa + "%");
        }
    }

    private void startBegin() {
        if (!isStartBegin) {
            if (TextUtils.isEmpty(phone_select_device.getText().toString().trim()) || phone_select_device.getText().toString().trim().equals("--")) {
                Toast.makeText(NotificationManualTestActivity.this, getResources().getString(R.string.connect_warning_select_device), Toast.LENGTH_LONG).show();
                return;
            }
            if (isNumber(phone_distance)) {

                if (isBind) {
                    CommonValue.SEND_MANUAL_NOTIFICATION_ALL = 0;
                    CommonValue.SEND_MANUAL_NOTIFICATION_SUCCESS = 0;
                    CommonValue.SEND_MANUAL_NOTIFICATION_FAIL = 0;
                    CommonValue.SEND_MANUAL_MESSAGE_ALL = 0;
                    CommonValue.SEND_MANUAL_MESSAGE_SUCCESS = 0;
                    CommonValue.SEND_MANUAL_MESSAGE_FAIL = 0;
                    CommonValue.SEND_MANUAL_CALL_ALL = 0;
                    CommonValue.SEND_MANUAL_CALL_SUCCESS = 0;
                    CommonValue.SEND_MANUAL_CALL_FAIL = 0;
                    CommonValue.SEND_MANUAL_ANSWER_ALL = 0;
                    CommonValue.SEND_MANUAL_ANSWER_SUCCESS = 0;
                    CommonValue.SEND_MANUAL_ANSWER_FAIL = 0;
                    CommonValue.SEND_MANUAL_HUNG_UP_ALL = 0;
                    CommonValue.SEND_MANUAL_HUNG_UP_SUCCESS = 0;
                    CommonValue.SEND_MANUAL_HUNG_UP_FAIL = 0;
                    watchDevice.setMac(phone_select_device.getText().toString().trim());
                    test_time.setText(GetTime.getCurrentTime_Today());
                    test_model.setText(GetPhoneInfo.getSystemModel());
                    test_mac.setText(phone_select_device.getText().toString().trim());
                    test_distance.setText(phone_distance.getText().toString().trim() + "米");
                    title_send_notification.setText("--");
                    //test_scan_min, test_scan_max, test_scan_average, test_scan_succ_count, test_scan_fail_count, test_scan_succ_rate
                    title_send_message.setText("--");
                    title_send_phone_call.setText("--");
                    title_send_phone_answer.setText("--");
                    title_send_phone_hung_up.setText("--");
                    test_result.setText(getResources().getString(R.string.connect_phone_test_testing));
                    isStartBegin = true;
                } else {
                    Toast.makeText(NotificationManualTestActivity.this, "请先绑定设备", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(NotificationManualTestActivity.this, getResources().getString(R.string.connect_warning_input_number_style), Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            Toast.makeText(NotificationManualTestActivity.this, "请先结束上次任务", Toast.LENGTH_LONG).show();
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
    }

    private void sendNotification(WatchDevice watchDevice, String type, int code) {
        AppNotification appNotification = new AppNotification();
        //来电
        appNotification.setType(AppNotification.Type.TELEPHONY);
        AppNotification.Telephony telephony = new AppNotification.Telephony();
        //联系人
        telephony.setContact(phone_number.getText().toString().trim());
        //来电号码
        telephony.setNumber(phone_number.getText().toString().trim());
        // CONNECTED 接听
        // DISCONNECTED 挂断
        // RINGING_UNANSWERABLE 响铃
        //根据来电状态更改
        if (code == 1)
            telephony.setStatus(AppNotification.Telephony.Status.RINGING_UNANSWERABLE);
        else if (code == 2)
            telephony.setStatus(AppNotification.Telephony.Status.CONNECTED);
        else
            telephony.setStatus(AppNotification.Telephony.Status.DISCONNECTED);
        appNotification.setTelephony(telephony);
        watchDevice.sendNotification(appNotification, new AsyncBleCallback<Void, BleError>() {
            @Override
            public void onSuccess(Void result) {
                Log.i("yj", "sendNotification onSuccess");
                if (CommonValue.TYPE_CALL.equals(type)) {
                    CommonValue.SEND_MANUAL_CALL_SUCCESS++;
                } else if (CommonValue.TYPE_ANSWER.equals(type)) {
                    CommonValue.SEND_MANUAL_ANSWER_SUCCESS++;
                } else if (CommonValue.TYPE_HUNG_UP.equals(type)) {
                    CommonValue.SEND_MANUAL_HUNG_UP_SUCCESS++;
                }

            }

            @Override
            public void onFailure(BleError error) {
                wrong_logs = wrong_logs+"\n"+error.toString();
                Log.e("yj", "sendNotification onFailure--call:" + error);
                if (CommonValue.TYPE_CALL.equals(type)) {
                    CommonValue.SEND_MANUAL_CALL_FAIL++;
                } else if (CommonValue.TYPE_ANSWER.equals(type)) {
                    CommonValue.SEND_MANUAL_ANSWER_FAIL++;
                } else if (CommonValue.TYPE_HUNG_UP.equals(type)) {
                    CommonValue.SEND_MANUAL_HUNG_UP_FAIL++;
                }

            }
        });
    }

    private void sendMessageType(WatchDevice watchDevice) {
        AppNotification appNotification = new AppNotification();
        //短信
        appNotification.setType(AppNotification.Type.SMS);
        AppNotification.Sms sms = new AppNotification.Sms();
        //联系人
        sms.setContact(phone_number.getText().toString().trim() + "---" + CommonValue.SEND_MANUAL_MESSAGE_ALL);
        //短信内容
        sms.setContent("测试短信hello--" + CommonValue.SEND_MANUAL_MESSAGE_ALL);
        //来信号码
        sms.setSender(phone_number.getText().toString().trim());
        appNotification.setSms(sms);
        watchDevice.sendNotification(appNotification, new AsyncBleCallback<Void, BleError>() {
            @Override
            public void onSuccess(Void result) {
                CommonValue.SEND_MANUAL_MESSAGE_SUCCESS++;
            }

            @Override
            public void onFailure(BleError error) {
                wrong_logs = wrong_logs+"\n"+error.toString();
                Log.e("yj", "sendNotification onFailure--notification:" + error);
                CommonValue.SEND_MANUAL_MESSAGE_FAIL++;
            }
        });
    }

    private void sendNotificationType(WatchDevice watchDevice) {
        AppNotification appNotification = new AppNotification();
        //app消息
        String title = "测试title----" + CommonValue.SEND_MANUAL_NOTIFICATION_ALL;
        String text = "测试text---" + CommonValue.SEND_MANUAL_NOTIFICATION_ALL;
        String appKey = NotificationUtil.getAppKeyByPackageName(PACKAGE_NAME_WX, "");
        appNotification.setType(AppNotification.Type.APP_MESSAGE);
        AppNotification.AppMessage appMessage = new AppNotification.AppMessage();
        appMessage.setAppId(appKey);

        if (!TextUtils.isEmpty(title)) {
            if (title.length() <= 50) {
                appMessage.setTitle(title);
            } else {
                String maxTitle = title.substring(0, 46) + "...";
                appMessage.setTitle(maxTitle);
            }
        }
        if (!TextUtils.isEmpty(text)) {
            if (text.length() <= 400) {
                appMessage.setText(text);
            } else {
                String maxText = text.substring(0, 395) + "...";
                appMessage.setText(maxText);
            }
        }
        appNotification.setAppMessage(appMessage);
        watchDevice.sendNotification(appNotification, new AsyncBleCallback<Void, BleError>() {
            @Override
            public void onSuccess(Void result) {
                CommonValue.SEND_MANUAL_NOTIFICATION_SUCCESS++;
            }

            @Override
            public void onFailure(BleError error) {
                wrong_logs = wrong_logs+"\n"+error.toString();
                Log.e("yj", "sendNotification onFailure--message:" + error);
                CommonValue.SEND_MANUAL_NOTIFICATION_FAIL++;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AndPermission.with(this)
                .runtime()
                .permission(Permission.READ_PHONE_STATE, Permission.READ_CONTACTS, Permission.READ_CALL_LOG)
                .onGranted(permissions -> {

                })
                .onDenied(permissions -> {

                })
                .start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonValue.isNotificationManualSecond = "ideal";
        EventBus.getDefault().unregister(NotificationManualTestActivity.this);
        BleScanner.getInstance().stopScan();
        BleScanner.getInstance().removeDeviceScanCallback(new BleScanner.OnDeviceScanCallback() {
            @Override
            public void onFind(ScannedDevice scannedDevice) {
                Log.d("yj", "remove----scannedDevice--");
            }
        });

    }
}
