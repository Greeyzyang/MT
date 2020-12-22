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
import com.example.connectapplication.utils.NotificationUtil;
import com.example.connectapplication.utils.ScanBlueTooth;
import com.example.connectapplication.utils.Util;
import com.ryeex.ble.common.device.DeviceConnectListener;
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

public class NotificationAutoTestActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_notificaiton_auto_back;//返回
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
    private TextView test_notification_selected;//第三方推送选择
    private EditText phone_number;//测试手机号
    private TextView title_send_notification;//发送通知
    private TextView title_send_message;//发送短信
    private TextView title_send_phone_call_hung_up;//发送来电挂断
    private TextView title_send_phone_call_answer_hung_up;//发送来电接听挂断
    private TextView stop_test;//停止测试
    private TextView test_result;//测试结果
    private TextView test_time;//测试时间
    private TextView test_model;//机型
    private TextView test_mac;//mac
    private TextView test_distance;//距离
    private TextView test_all_time;//测试总次数
    private TextView test_space;//间隔
    private TextView test_current_count;//当前次数
    private TextView send_auto_success_num;//发送成功次数
    private TextView send_auto_fail_num;//发送失败次数
    private TextView send_auto_success_rate;//发送成功率
    private TextView test_fail_records;//手表日志
    private TextView wrong_log_records;//错误日志
    private String notification_type;//选择推送类型
    private static final String ANSWER_HUNG_UP = "answer_hung_up";
    private static final String HUNG_UP = "hung_up";
    private static AppNotification APP_NOTIFICATION = null;
    private String errorsetp = "call_ok";


    private final int REQUEST_OK = 2;
    private boolean isComputeNotificationConnected = true;
    private boolean isComputeMessageConnected = true;
    private boolean isComputeCallhungupConnected = true;
    private boolean isComputeCallAnswerhungConnected = true;
    private boolean isBind;
    private final int MSG_GET_CALL_HUNG_UP = 10091;
    private final int MSG_GET_CALL_ANSWER_HUNG_UP = 10092;
    private final int MSG_GET_SEND_NOTIFICAITON = 10099;
    private final int MSG_GET_SEND_MESSAGE =10010;
    private final int MSG_GET_CALL_CALL_HUNG_UP = 10011;
    private final int MSG_GET_CALL_CALL_ANSWER_HUNG_UP = 10012;
    private int playCount;
    private int playspace;

    private String currentday;

    private String wrong_logs;


    private WatchDevice watchDevice;

    private String isOneThread;

    private ScannedDevice scannedDevices;

    private boolean isCallHung;

    private boolean isCallAnswerHung;

    private boolean isMessageSend;

    private boolean isNotificationSend;

    private boolean isCallAnswer;

    private boolean isCallHungHung;

    private boolean isCallAnswerHungHung;

    private int repeat;

    private BluetoothAdapter mBluetooth;

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

    Handler nHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_GET_CALL_HUNG_UP:
                    String type = (String) msg.obj;
                    sendHungup(watchDevice, APP_NOTIFICATION,type);
                    break;
                case MSG_GET_CALL_ANSWER_HUNG_UP:
                    String types = (String) msg.obj;
                    sendAnswer(watchDevice, APP_NOTIFICATION,types);
                    break;
                case MSG_GET_SEND_NOTIFICAITON:
                    if (playCount > 0) {
                        CommonValue.SEND_AUTO_ALL++;
                        test_current_count.setText(CommonValue.SEND_AUTO_ALL + "");
                        //EventBus.getDefault().post(new ProgressEvent(MSG_GET_UPDATE_CURRENT_COUNT));
                        sendNotificationType(watchDevice);
                        playCount--;
                        Log.d("yj", "playCountr--notifi---" + playCount);
                    }
                    if (playCount == 0) {
                        nHandler.removeCallbacksAndMessages(null);
                        isComputeNotificationConnected = true;
                        test_result.setText(getResources().getString(R.string.connect_phone_test_result));
                        wrong_log_records.setText(wrong_logs);
                        getCount(send_auto_success_num, send_auto_fail_num, send_auto_success_rate);
                    }
                    break;
                case MSG_GET_SEND_MESSAGE:
                    if (playCount > 0) {
                        CommonValue.SEND_AUTO_ALL++;
                        test_current_count.setText(CommonValue.SEND_AUTO_ALL + "");
                        //EventBus.getDefault().post(new ProgressEvent(MSG_GET_UPDATE_CURRENT_COUNT));
                        sendMessageType(watchDevice);
                        playCount--;
                        Log.d("yj", "playCountr--notifi---" + playCount);
                    }
                    if (playCount == 0) {
                        nHandler.removeCallbacksAndMessages(null);
                        isComputeMessageConnected= true;
                        test_result.setText(getResources().getString(R.string.connect_phone_test_result));
                        wrong_log_records.setText(wrong_logs);
                        getCount(send_auto_success_num, send_auto_fail_num, send_auto_success_rate);
                    }
                    break;
                case MSG_GET_CALL_CALL_HUNG_UP:
                    if (playCount > 0) {
                        isOneThread = "init";
                        CommonValue.SEND_AUTO_ALL++;
                        test_current_count.setText(CommonValue.SEND_AUTO_ALL + "");
                        //EventBus.getDefault().post(new ProgressEvent(MSG_GET_UPDATE_CURRENT_COUNT));
                        sendNotification(watchDevice, CommonValue.TYPE_CALL_HUNG_UP);
                        playCount--;
                        Log.d("yj", "playCountr--notifi---" + playCount);
                    }
                    if (playCount == 0) {
                        //nHandler.removeCallbacksAndMessages(null);
                          nHandler.removeMessages(MSG_GET_CALL_CALL_HUNG_UP);
                        //nHandler.removeCallbacksAndMessages(null);
                        isComputeCallhungupConnected = true;
                        test_result.setText(getResources().getString(R.string.connect_phone_test_result));
                        wrong_log_records.setText(wrong_logs);
                        getCount(send_auto_success_num, send_auto_fail_num, send_auto_success_rate);
                    }
                    break;
                case MSG_GET_CALL_CALL_ANSWER_HUNG_UP:
                    if (playCount > 0) {
                        isOneThread = "init";
                        CommonValue.SEND_AUTO_ALL++;
                        test_current_count.setText(CommonValue.SEND_AUTO_ALL + "");
                        //EventBus.getDefault().post(new ProgressEvent(MSG_GET_UPDATE_CURRENT_COUNT));
                        sendNotification(watchDevice, CommonValue.TYPE_CALL_ANSWER_HUNG_UP);
                        playCount--;
                        Log.d("yj", "playCountr--notifi---" + playCount);
                    }
                    if (playCount == 0) {
                        //nHandler.removeCallbacksAndMessages(null);
                        //nHandler.removeCallbacks(call_answer_hung_up_runnable);
                        nHandler.removeMessages(MSG_GET_CALL_CALL_ANSWER_HUNG_UP);
                        isComputeCallAnswerhungConnected = true;
                        test_result.setText(getResources().getString(R.string.connect_phone_test_result));
                        wrong_log_records.setText(wrong_logs);
                        getCount(send_auto_success_num, send_auto_fail_num, send_auto_success_rate);
                    }
                    break;
                default:
                    break;
            }
        }
    };

//    Runnable notification_runnable = new Runnable() {
//        @Override
//        public void run() {
//            if (playCount > 0) {
//                CommonValue.SEND_AUTO_ALL++;
//                test_current_count.setText(CommonValue.SEND_AUTO_ALL + "");
//                //EventBus.getDefault().post(new ProgressEvent(MSG_GET_UPDATE_CURRENT_COUNT));
//                sendNotificationType(watchDevice);
//                nHandler.postDelayed(this, playspace * 1000);
//                playCount--;
//                Log.d("yj", "playCountr--notifi---" + playCount);
//            }
//            if (playCount == 0) {
//                nHandler.removeCallbacksAndMessages(null);
//                isComputeNotificationConnected = true;
//                test_result.setText(getResources().getString(R.string.connect_phone_test_result));
//                wrong_log_records.setText(wrong_logs);
//                getCount(send_auto_success_num, send_auto_fail_num, send_auto_success_rate);
//            }
//        }
//    };
//
//    Runnable message_runnable = new Runnable() {
//        @Override
//        public void run() {
//            if (playCount > 0) {
//                CommonValue.SEND_AUTO_ALL++;
//                test_current_count.setText(CommonValue.SEND_AUTO_ALL + "");
//                //EventBus.getDefault().post(new ProgressEvent(MSG_GET_UPDATE_CURRENT_COUNT));
//                sendMessageType(watchDevice);
//                nHandler.postDelayed(this, playspace * 1000);
//                playCount--;
//                Log.d("yj", "playCountr--notifi---" + playCount);
//            }
//            if (playCount == 0) {
//                nHandler.removeCallbacksAndMessages(null);
//                isComputeMessageConnected= true;
//                test_result.setText(getResources().getString(R.string.connect_phone_test_result));
//                wrong_log_records.setText(wrong_logs);
//                getCount(send_auto_success_num, send_auto_fail_num, send_auto_success_rate);
//            }
//        }
//    };
//
//    Runnable call_hung_up_runnable = new Runnable() {
//        @Override
//        public void run() {
//            if (playCount > 0) {
//                isOneThread = "init";
//                CommonValue.SEND_AUTO_ALL++;
//                test_current_count.setText(CommonValue.SEND_AUTO_ALL + "");
//                //EventBus.getDefault().post(new ProgressEvent(MSG_GET_UPDATE_CURRENT_COUNT));
//                sendNotification(watchDevice, CommonValue.TYPE_CALL_HUNG_UP);
//                nHandler.postDelayed(this, playspace * 1000);
//                playCount--;
//                Log.d("yj", "playCountr--notifi---" + playCount);
//            }
//            if (playCount == 0) {
//                //nHandler.removeCallbacksAndMessages(null);
//                nHandler.removeCallbacks(call_hung_up_runnable);
//                isComputeCallhungupConnected = true;
//                test_result.setText(getResources().getString(R.string.connect_phone_test_result));
//                wrong_log_records.setText(wrong_logs);
//                getCount(send_auto_success_num, send_auto_fail_num, send_auto_success_rate);
//            }
//        }
//    };
//
//    Runnable call_answer_hung_up_runnable = new Runnable() {
//        @Override
//        public void run() {
//            if (playCount > 0) {
//                isOneThread = "init";
//                CommonValue.SEND_AUTO_ALL++;
//                test_current_count.setText(CommonValue.SEND_AUTO_ALL + "");
//                //EventBus.getDefault().post(new ProgressEvent(MSG_GET_UPDATE_CURRENT_COUNT));
//                sendNotification(watchDevice, CommonValue.TYPE_CALL_ANSWER_HUNG_UP);
//                nHandler.postDelayed(this, playspace * 1000);
//                playCount--;
//                Log.d("yj", "playCountr--notifi---" + playCount);
//            }
//            if (playCount == 0) {
//                //nHandler.removeCallbacksAndMessages(null);
//                nHandler.removeCallbacks(call_answer_hung_up_runnable);
//                isComputeCallAnswerhungConnected = true;
//                test_result.setText(getResources().getString(R.string.connect_phone_test_result));
//                wrong_log_records.setText(wrong_logs);
//                getCount(send_auto_success_num, send_auto_fail_num, send_auto_success_rate);
//            }
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_notification_auto);
        CommonValue.isNotificationAutoSecond = "worked";
        mBluetooth = BluetoothAdapter.getDefaultAdapter(); //获取Bluetooth适配器
        CommonShared.clear(NotificationAutoTestActivity.this);
        currentday = GetTime.getCurrentTime_Today();
        EventBus.getDefault().register(this);
        init();
        watchDevice = new WatchDevice();
        BleScanner.getInstance().addDeviceScanCallback(new BleScanner.OnDeviceScanCallback() {

            @Override
            public void onFind(ScannedDevice scannedDevice) {
                if (CommonValue.isNotificationAutoSecond.equals("worked")) {
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

    private void init() {
        title_notificaiton_auto_back = findViewById(R.id.title_notification_auto_back);
        phone_style_num = findViewById(R.id.phone_style_num);
        phone_system_num = findViewById(R.id.phone_system_num);
        select_device_releate = findViewById(R.id.select_device_releate);
        phone_select_device = findViewById(R.id.phone_select_device_num);
        phone_distance = findViewById(R.id.phone_distance_num);
        phone_count = findViewById(R.id.phone_test_count_num);
        phone_space = findViewById(R.id.phone_space_num);
        title_bind_start = findViewById(R.id.title_bind_start);
        title_unbind_start = findViewById(R.id.title_unbind_start);
        test_bind_unbind_result = findViewById(R.id.test_bind_unbind_result);
        test_notification_selected = findViewById(R.id.test_notification_selected);
        phone_number = findViewById(R.id.phone_number);
        title_send_notification = findViewById(R.id.title_send_notification);
        title_send_message = findViewById(R.id.title_send_message);
        title_send_phone_call_hung_up = findViewById(R.id.title_send_phone_call_hung_up);
        title_send_phone_call_answer_hung_up = findViewById(R.id.title_send_phone_call_answer_hung_up);
        stop_test = findViewById(R.id.title_send_stop);
        test_result = findViewById(R.id.test_result);
        test_time = findViewById(R.id.phone_test_time_num);
        test_model = findViewById(R.id.phone_test_model_num);
        test_mac = findViewById(R.id.phone_test_mac_num);
        test_distance = findViewById(R.id.phone_test_distance_num);
        test_all_time = findViewById(R.id.phone_test_all_count_num);
        test_space = findViewById(R.id.phone_test_space_num);
        test_current_count = findViewById(R.id.phone_test_current_count_num);
        send_auto_success_num = findViewById(R.id.send_auto_success_num);
        send_auto_fail_num = findViewById(R.id.send_auto_fail_num);
        send_auto_success_rate = findViewById(R.id.send_auto_success_rate_num);
        test_fail_records = findViewById(R.id.test_fail_records);
        wrong_log_records = findViewById(R.id.wrong_log_records);
        phone_style_num.setText(GetPhoneInfo.getSystemModel());
        phone_system_num.setText(GetPhoneInfo.getSystemVersion());

        phone_distance.setText("3");
        phone_count.setText("100");
        phone_space.setText("10");
        title_notificaiton_auto_back.setOnClickListener(this);
        select_device_releate.setOnClickListener(this);
        title_bind_start.setOnClickListener(this);
        title_unbind_start.setOnClickListener(this);
        test_notification_selected.setOnClickListener(this);
        title_send_notification.setOnClickListener(this);
        title_send_message.setOnClickListener(this);
        title_send_phone_call_hung_up.setOnClickListener(this);
        title_send_phone_call_answer_hung_up.setOnClickListener(this);
        stop_test.setOnClickListener(this);
        test_fail_records.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_notification_auto_back:
                finish();
                break;
            case R.id.select_device_releate:
                if(isComputeNotificationConnected&&isComputeMessageConnected&&isComputeCallhungupConnected&&isComputeNotificationConnected)
                    gotoSelectDevice();
                else
                    Toast.makeText(NotificationAutoTestActivity.this,"有任务未停止",Toast.LENGTH_LONG).show();
                break;
            case R.id.title_bind_start:
                if(Util.isFastClick())
                    startBindorUnbind(true);
                break;
            case R.id.title_unbind_start:
                if(Util.isFastClick())
                    startBindorUnbind(false);
                break;
            case R.id.test_notification_selected:

                break;
            case R.id.title_send_notification:
                if(Util.isFastClick())
                    clickNotification(CommonValue.TYPE_NOTIFICATION);
                break;
            case R.id.title_send_message:
                if(Util.isFastClick())
                    clickNotification(CommonValue.TYPE_MESSAGE);
                break;
            case R.id.title_send_phone_call_hung_up:
                if(Util.isFastClick())
                    clickNotification(CommonValue.TYPE_CALL_HUNG_UP);
                //sendNotification(watchDevice,CommonValue.TYPE_CALL_HUNG_UP);
                break;
            case R.id.title_send_phone_call_answer_hung_up:
                if(Util.isFastClick())
                    clickNotification(CommonValue.TYPE_CALL_ANSWER_HUNG_UP);
                break;
            case R.id.title_send_stop:
                if(Util.isFastClick())
                    stopTest();
                break;
            case R.id.test_fail_records:
                if(watchDevice.isLogin()){
                    ScanBlueTooth.writeDeviceLog(watchDevice,NotificationAutoTestActivity.this,currentday);
                }else{
                    Toast.makeText(NotificationAutoTestActivity.this,"请确保设备绑定状态",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void gotoSelectDevice() {
        Intent in = new Intent();
        in.setClass(NotificationAutoTestActivity.this, SelectDeviceActivity.class);
        startActivityForResult(in, REQUEST_OK);
        CommonValue.isNotificationAutoSecond = "secondisworked";
    }

    private void startBindorUnbind(boolean bind) {
        if (isComputeNotificationConnected && isComputeMessageConnected && isComputeCallhungupConnected && isComputeCallAnswerhungConnected) {
            if (TextUtils.isEmpty(phone_select_device.getText().toString().trim()) || phone_select_device.getText().toString().trim().equals("--")) {
                Toast.makeText(NotificationAutoTestActivity.this, getResources().getString(R.string.connect_warning_select_device), Toast.LENGTH_LONG).show();
                return;
            }
            watchDevice.setMac(phone_select_device.getText().toString().trim());
            if (bind) {
                if (watchDevice.isLogin()) {
                    isBind = true;
                    Toast.makeText(NotificationAutoTestActivity.this, "设备已绑定", Toast.LENGTH_LONG).show();
                    return;
                }
                test_bind_unbind_result.setText("绑定设备中……");
                ScanBlueTooth.startScan(NotificationAutoTestActivity.this);
            } else {
                if (watchDevice.isLogin()) {
                    test_bind_unbind_result.setText("绑定解绑中……");
                    ScanBlueTooth.endBind(watchDevice);
                } else {
                    Toast.makeText(NotificationAutoTestActivity.this, "当前是解绑状态，请检查设备是否处于未绑定状态", Toast.LENGTH_LONG).show();
                }

            }

        } else {
            Toast.makeText(NotificationAutoTestActivity.this, getResources().getString(R.string.send_auto_sending), Toast.LENGTH_LONG).show();
        }
    }


    private void clickNotification(String type) {
        if (isComputeNotificationConnected && isComputeMessageConnected && isComputeCallhungupConnected && isComputeCallAnswerhungConnected) {
            if (TextUtils.isEmpty(phone_select_device.getText().toString().trim()) || phone_select_device.getText().toString().trim().equals("--")) {
                Toast.makeText(NotificationAutoTestActivity.this, getResources().getString(R.string.connect_warning_select_device), Toast.LENGTH_LONG).show();
                return;
            }
            if (isNumber(phone_distance) && isNumber(phone_count) && isNumber(phone_space)) {
                if (phone_count.getText().toString().trim().equals("0")) {
                    Toast.makeText(NotificationAutoTestActivity.this, getResources().getString(R.string.connect_warning_play_count), Toast.LENGTH_LONG).show();
                    return;
                }
                if (isBind) {
                    CommonValue.SEND_AUTO_ALL = 0;
                    CommonValue.SEND_AUTO_SUCCESS = 0;
                    CommonValue.SEND_AUTO_FAIL = 0;
                    watchDevice.setMac(phone_select_device.getText().toString().trim());
                    test_time.setText(GetTime.getCurrentTime_Today());
                    test_model.setText(GetPhoneInfo.getSystemModel());
                    test_mac.setText(phone_select_device.getText().toString().trim());
                    test_distance.setText(phone_distance.getText().toString().trim() + "米");
                    test_space.setText(phone_space.getText().toString().trim() + "秒");
                    test_all_time.setText(phone_count.getText().toString().trim() + "次");
                    test_current_count.setText("--");
                    //test_scan_min, test_scan_max, test_scan_average, test_scan_succ_count, test_scan_fail_count, test_scan_succ_rate
                    send_auto_success_num.setText("--");
                    send_auto_fail_num.setText("--");
                    send_auto_success_rate.setText("--");
                    test_result.setText(getResources().getString(R.string.connect_phone_test_testing));
                    playCount = Integer.valueOf(phone_count.getText().toString().trim());
                    playspace = Integer.valueOf(phone_space.getText().toString().trim());
                    Log.d("yj", "playCount--notifi---" + playCount + "--notifi--" + playspace);
                    if (type.equals(CommonValue.TYPE_NOTIFICATION)) {
                        isComputeNotificationConnected = false;
                        //nHandler.post(notification_runnable);
                        nHandler.sendEmptyMessageDelayed(MSG_GET_SEND_NOTIFICAITON,playspace*1000);
                    } else if (type.equals(CommonValue.TYPE_MESSAGE)) {
                        isComputeMessageConnected = false;
                        //nHandler.post(message_runnable);
                        nHandler.sendEmptyMessageDelayed(MSG_GET_SEND_MESSAGE,playspace*1000);
                    } else if (type.equals(CommonValue.TYPE_CALL_HUNG_UP)) {
                        isComputeCallhungupConnected= false;
                        //nHandler.post(call_hung_up_runnable);
                        nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_HUNG_UP,playspace*1000);
                    } else if (type.equals(CommonValue.TYPE_CALL_ANSWER_HUNG_UP)) {
                        isComputeCallAnswerhungConnected= false;
                        //nHandler.post(call_answer_hung_up_runnable);
                        nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_ANSWER_HUNG_UP,playspace*1000);
                    }
                } else {
                    Toast.makeText(NotificationAutoTestActivity.this, "请先绑定设备", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(NotificationAutoTestActivity.this, getResources().getString(R.string.connect_warning_input_number_style), Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            Toast.makeText(NotificationAutoTestActivity.this, getResources().getString(R.string.connect_scaning_connecting), Toast.LENGTH_LONG).show();
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
            test_bind_unbind_result.setText("绑定设备成功");
            isBind = true;
            CommonShared.WriteToken(NotificationAutoTestActivity.this,phone_select_device.getText().toString().trim(),watchDevice.getToken());

        } else if (event.message.equals(CommonValue.BIND_ERROR)) {
            wrong_logs = wrong_logs+"\n"+event.errorinfo;
            test_bind_unbind_result.setText("绑定设备失败");
            //ConnectionDialog.showNormalDialog(NotificationAutoTestActivity.this);
            String error = JosnParse.parseString(event.errorinfo);
            String bleCode = error.split("\\|")[0];
            String message = error.split("\\|")[1];
            Toast.makeText(NotificationAutoTestActivity.this,message,Toast.LENGTH_LONG).show();
        }

        if (event.message.equals(CommonValue.UNBIND_SUCCESS)) {
            test_bind_unbind_result.setText("解绑设备成功");
            isBind = false;
            CommonShared.clear(NotificationAutoTestActivity.this);
        } else if (event.message.equals(CommonValue.UNBIND_ERROR)) {
            wrong_logs = wrong_logs+"\n"+event.errorinfo;
            test_bind_unbind_result.setText("解绑设备失败");
            String error = JosnParse.parseString(event.errorinfo);
            String bleCode = error.split("\\|")[0];
            String message = error.split("\\|")[1];
            Toast.makeText(NotificationAutoTestActivity.this,message,Toast.LENGTH_LONG).show();
        }

        if(event.message.equals(CommonValue.LOGIN_SUCCESS)){
            nHandler.removeCallbacksAndMessages(null);
            if(isMessageSend) {
                nHandler.sendEmptyMessageDelayed(MSG_GET_SEND_MESSAGE, playspace * 1000);
                isMessageSend = false;
                return;
            }
            if(isNotificationSend){
                nHandler.sendEmptyMessageDelayed(MSG_GET_SEND_NOTIFICAITON, playspace * 1000);
                isNotificationSend = false;
                return;
            }
            if(isCallHung){
                nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_HUNG_UP, playspace * 1000);
                isCallHung = false;
                return;
            }
            if(isCallAnswerHung){
                nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_ANSWER_HUNG_UP, playspace * 1000);
                isCallAnswerHung = false;
                return;
            }
            if(isCallAnswer){
                nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_ANSWER_HUNG_UP, playspace * 1000);
                isCallAnswer = false;
                return;
            }
            if(isCallHungHung){
                nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_HUNG_UP, playspace * 1000);
                isCallHungHung = false;
                return;
            }
            if(isCallAnswerHungHung){
                nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_ANSWER_HUNG_UP, playspace * 1000);
                isCallAnswerHungHung = false;
                return;
            }

        }else if(event.message.equals(CommonValue.LOGIN_ERROR)){
            String error = JosnParse.parseString(event.errorinfo);
            String bleCode = error.split("\\|")[0];
            String message = error.split("\\|")[1];
            Log.d("yj","bleCode---login---"+bleCode);
            if(bleCode.equals("133")) {
                Log.d("yj","bleCode---login-xxxx--");
                mBluetooth.disable();
                Toast.makeText(NotificationAutoTestActivity.this, "蓝牙关闭成功", Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(7000);
                    mBluetooth.enable();
                    ScanBlueTooth.startLogin(watchDevice);
                    Toast.makeText(NotificationAutoTestActivity.this, "蓝牙重新打开成功", Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            }
            repeat++;
            if(repeat<=2) {
                try {
                    Thread.sleep(10000);
                    ScanBlueTooth.startLogin(watchDevice);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                nHandler.removeCallbacksAndMessages(null);
                if(isMessageSend) {
                    nHandler.sendEmptyMessageDelayed(MSG_GET_SEND_MESSAGE, playspace * 1000);
                    isMessageSend = false;
                    return;
                }
                if(isNotificationSend){
                    nHandler.sendEmptyMessageDelayed(MSG_GET_SEND_NOTIFICAITON, playspace * 1000);
                    isNotificationSend = false;
                    return;
                }
                if(isCallHung){
                    nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_HUNG_UP, playspace * 1000);
                    isCallHung = false;
                    return;
                }
                if(isCallAnswerHung){
                    nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_ANSWER_HUNG_UP, playspace * 1000);
                    isCallAnswerHung = false;
                    return;
                }
                if(isCallAnswer){
                    nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_ANSWER_HUNG_UP, playspace * 1000);
                    isCallAnswer = false;
                    return;
                }
                if(isCallHungHung){
                    nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_HUNG_UP, playspace * 1000);
                    isCallHungHung = false;
                    return;
                }
                if(isCallAnswerHungHung){
                    nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_ANSWER_HUNG_UP, playspace * 1000);
                    isCallAnswerHungHung = false;
                    return;
                }
            }

        }
    }

    private void sendNotification(WatchDevice watchDevice, String type) {
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
            telephony.setStatus(AppNotification.Telephony.Status.RINGING_UNANSWERABLE);
            appNotification.setTelephony(telephony);
        watchDevice.sendNotification(appNotification, new AsyncBleCallback<Void, BleError>() {
            @Override
            public void onSuccess(Void result) {
                APP_NOTIFICATION = appNotification;
                Log.i("yj", "sendNotification onSuccess");
                if (CommonValue.TYPE_CALL_HUNG_UP.equals(type)) {
                    //nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_HUNG_UP, 1000);
                    sendMessages(type, MSG_GET_CALL_HUNG_UP, playspace);

                } else if (CommonValue.TYPE_CALL_ANSWER_HUNG_UP.equals(type)) {
                    sendMessages(type,MSG_GET_CALL_ANSWER_HUNG_UP,playspace);
                }

            }

            @Override
            public void onFailure(BleError error) {
                wrong_logs = wrong_logs+"\n"+error.toString();
                Log.e("yj", "sendNotification onFailure--call:" + error);
                if(isOneThread.equals("one_answer")||isOneThread.equals("one_hung")) {

                }else {
                    CommonValue.SEND_AUTO_FAIL++;
                    isOneThread = "one_call";
                }
                String errors = JosnParse.parseString(error.toString());
                String bleCode = errors.split("\\|")[0];
                String message = errors.split("\\|")[1];
                Log.d("yj","bleCode---bind-----::"+bleCode+"====message===="+message);
                if(bleCode.equals("-1")&&message.equals("device is not connected")) {
                    if (type.equals(CommonValue.TYPE_CALL_HUNG_UP)) {
                       isCallHung = true;
                        try {
                            Thread.sleep(10000);
                            ScanBlueTooth.startLogin(watchDevice);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                       return;
                    } else if (type.equals(CommonValue.TYPE_CALL_ANSWER_HUNG_UP)) {
                        isCallAnswerHung = true;
                        try {
                            Thread.sleep(10000);
                            ScanBlueTooth.startLogin(watchDevice);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }else{
                    if (type.equals(CommonValue.TYPE_CALL_HUNG_UP)) {
                        nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_HUNG_UP, playspace * 1000);
                    } else if (type.equals(CommonValue.TYPE_CALL_ANSWER_HUNG_UP)) {
                        nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_ANSWER_HUNG_UP, playspace * 1000);
                    }
                }
            }
        });
    }

    private void sendMessages(String type, int what, int playspace) {
        Message msg = nHandler.obtainMessage();
        msg.obj = type;
        msg.what = what;
        nHandler.sendMessageDelayed(msg, playspace * 1000);
    }

    private void stopTest() {
        watchDevice.setMac(phone_select_device.getText().toString().trim());
        if (nHandler != null) {
            nHandler.removeCallbacksAndMessages(null);
        }
        playCount = 0;
        playspace = 0;
        test_result.setText(getResources().getString(R.string.connect_phone_test_result));
        wrong_log_records.setText(wrong_logs);
        if (BleScanner.getInstance().isScanning()) {
            BleScanner.getInstance().stopScan();
        }
        getCount(send_auto_success_num, send_auto_fail_num, send_auto_success_rate);
        isComputeNotificationConnected = true;
        isComputeMessageConnected = true;
        isComputeCallhungupConnected = true;
        isComputeCallAnswerhungConnected = true;
    }

    private void getCount(TextView send_auto_success_num, TextView send_auto_fail_num, TextView send_auto_success_rate) {
        send_auto_success_num.setText(CommonValue.SEND_AUTO_SUCCESS + "次");
        send_auto_fail_num.setText(CommonValue.SEND_AUTO_FAIL + "次");
        if (CommonValue.SEND_AUTO_SUCCESS == 0 && CommonValue.SEND_AUTO_FAIL == 0) {
            send_auto_success_rate.setText("0%");
        } else {
            float ra = (CommonValue.SEND_AUTO_SUCCESS / Float.parseFloat(phone_count.getText().toString().trim())) * 100;
            String raa = Util.getTwoFloat(ra);
            send_auto_success_rate.setText( raa+ "%");
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (nHandler != null) {
            nHandler.removeCallbacksAndMessages(null);
        }
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
        CommonValue.isNotificationAutoSecond = "ideal";
        EventBus.getDefault().unregister(NotificationAutoTestActivity.this);
        BleScanner.getInstance().stopScan();
        BleScanner.getInstance().removeDeviceScanCallback(new BleScanner.OnDeviceScanCallback() {
            @Override
            public void onFind(ScannedDevice scannedDevice) {
                Log.d("yj", "remove--notification--scannedDevice--");
            }
        });
        if (nHandler != null) {
            nHandler.removeCallbacksAndMessages(null);
        }
    }

    private void sendMessageType(WatchDevice watchDevice){
        AppNotification appNotification = new AppNotification();
        //短信
        appNotification.setType(AppNotification.Type.SMS);
        AppNotification.Sms sms = new AppNotification.Sms();
        //联系人
        sms.setContact(phone_number.getText().toString().trim()+"---"+CommonValue.SEND_AUTO_ALL);
        //短信内容
        sms.setContent("测试短信hello--" + CommonValue.SEND_AUTO_ALL);
        //来信号码
        sms.setSender(phone_number.getText().toString().trim());
        appNotification.setSms(sms);
        watchDevice.sendNotification(appNotification, new AsyncBleCallback<Void, BleError>() {
            @Override
            public void onSuccess(Void result) {
               CommonValue.SEND_AUTO_SUCCESS++;
               nHandler.sendEmptyMessageDelayed(MSG_GET_SEND_MESSAGE,playspace*1000);
            }

            @Override
            public void onFailure(BleError error) {
                wrong_logs = wrong_logs+"\n"+error.toString();
                Log.e("yj", "sendNotification onFailure--notification:" + error);
                CommonValue.SEND_AUTO_FAIL++;
                String errors = JosnParse.parseString(error.toString());
                String bleCode = errors.split("\\|")[0];
                String message = errors.split("\\|")[1];
                Log.d("yj","bleCode---bind-----::"+bleCode+"====message===="+message);
                if(bleCode.equals("-1")&&message.equals("device is not connected")){
                    isMessageSend = true;
                    try {
                        Thread.sleep(10000);
                        ScanBlueTooth.startLogin(watchDevice);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return;
                }else {
                    nHandler.sendEmptyMessageDelayed(MSG_GET_SEND_MESSAGE, playspace * 1000);
                }
            }
        });
    }

    private void sendNotificationType(WatchDevice watchDevice){
        AppNotification appNotification = new AppNotification();
        //app消息
        String title = "测试title----"+CommonValue.SEND_AUTO_ALL;
        String text = "测试text---" + CommonValue.SEND_AUTO_ALL;
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
                CommonValue.SEND_AUTO_SUCCESS++;
                nHandler.sendEmptyMessageDelayed(MSG_GET_SEND_NOTIFICAITON,playspace*1000);
            }

            @Override
            public void onFailure(BleError error) {
                wrong_logs = wrong_logs+"\n"+error.toString();
                Log.e("yj", "sendNotification onFailure--message:" + error);
                CommonValue.SEND_AUTO_FAIL++;
                String errors = JosnParse.parseString(error.toString());
                String bleCode = errors.split("\\|")[0];
                String message = errors.split("\\|")[1];
                if(bleCode.equals("-1")&&message.equals("device is not connected")){
                    isNotificationSend = true;
                    try {
                        Thread.sleep(10000);
                        ScanBlueTooth.startLogin(watchDevice);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return;
                }else {
                    nHandler.sendEmptyMessageDelayed(MSG_GET_SEND_NOTIFICAITON, playspace * 1000);
                }
            }
        });
    }

    private void sendAnswer(WatchDevice watchDevice, AppNotification appNotification,String type) {
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
        telephony.setStatus(AppNotification.Telephony.Status.CONNECTED);
        appNotification.setTelephony(telephony);


        watchDevice.sendNotification(appNotification, new AsyncBleCallback<Void, BleError>() {
            @Override
            public void onSuccess(Void result) {
                Log.i("yj", "sendNotification onSuccess");
                //nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_HUNG_UP, 1000);
                sendMessages(type,MSG_GET_CALL_HUNG_UP,1);
            }

            @Override
            public void onFailure(BleError error) {
                wrong_logs = wrong_logs+"\n"+error.toString();
                Log.e("yj", "sendNotification onFailure---answer:" + error);
                if(isOneThread.equals("one_call")||isOneThread.equals("one_hung")) {

                }else {
                    CommonValue.SEND_AUTO_FAIL++;
                    isOneThread = "one_answer";
                }
                String errors = JosnParse.parseString(error.toString());
                String bleCode = errors.split("\\|")[0];
                String message = errors.split("\\|")[1];
                Log.d("yj","answer-----"+CommonValue.SEND_AUTO_FAIL);
                if(bleCode.equals("-1")&&message.equals("device is not connected")){
                    isCallAnswer = true;
                    try {
                        Thread.sleep(10000);
                        ScanBlueTooth.startLogin(watchDevice);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return;
                }else {
                    nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_ANSWER_HUNG_UP, playspace * 1000);
                }
            }
        });
    }

    private void sendHungup(WatchDevice watchDevice, AppNotification appNotification,String type) {
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
        telephony.setStatus(AppNotification.Telephony.Status.DISCONNECTED);
        appNotification.setTelephony(telephony);


        watchDevice.sendNotification(appNotification, new AsyncBleCallback<Void, BleError>() {
            @Override
            public void onSuccess(Void result) {
                Log.i("yj", "sendNotification onSuccess");
                CommonValue.SEND_AUTO_SUCCESS++;
                if(type.equals(CommonValue.TYPE_CALL_HUNG_UP)) {
                    nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_HUNG_UP, playspace * 1000);
                }else if(type.equals(CommonValue.TYPE_CALL_ANSWER_HUNG_UP)) {
                    nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_ANSWER_HUNG_UP, playspace * 1000);
                }
            }

            @Override
            public void onFailure(BleError error) {
                wrong_logs = wrong_logs+"\n"+error.toString();
                Log.e("yj", "sendNotification onFailure---hung_up:" + error);
                if(isOneThread.equals("one_answer")||isOneThread.equals("one_call")) {

                }else {
                    CommonValue.SEND_AUTO_FAIL++;
                    isOneThread = "one_hung";
                }
                    Log.d("yj","hung_up-----"+CommonValue.SEND_AUTO_FAIL);
                String errors = JosnParse.parseString(error.toString());
                String bleCode = errors.split("\\|")[0];
                String message = errors.split("\\|")[1];
                if(bleCode.equals("-1")&&message.equals("device is not connected")){
                    if (type.equals(CommonValue.TYPE_CALL_HUNG_UP)) {
                        isCallHungHung = true;
                    } else {
                        isCallAnswerHungHung  = true;
                    }
                    try {
                        Thread.sleep(10000);
                        ScanBlueTooth.startLogin(watchDevice);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return;
                }else {
                    if (type.equals(CommonValue.TYPE_CALL_HUNG_UP)) {
                        nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_HUNG_UP, playspace * 1000);
                    } else {
                        nHandler.sendEmptyMessageDelayed(MSG_GET_CALL_CALL_ANSWER_HUNG_UP, playspace * 1000);
                    }
                }
            }
        });
    }
}
