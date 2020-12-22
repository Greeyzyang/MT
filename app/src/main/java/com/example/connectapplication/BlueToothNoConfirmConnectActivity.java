package com.example.connectapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
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
import com.example.connectapplication.event.InitEvent;
import com.example.connectapplication.utils.CommonShared;
import com.example.connectapplication.utils.GetPhoneInfo;
import com.example.connectapplication.utils.GetTime;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BlueToothNoConfirmConnectActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_back;//back标题
    private TextView phone_style_num;//手机型号
    private TextView phone_system_num;//手机系统
    private RelativeLayout select_device_releate;//选择设备布局
    private TextView phone_select_device;//选择设备号
    private EditText phone_distance;//测试距离输入
    private EditText phone_count;//测试次数输入
    private EditText phone_space;//测试间隔输入
    private TextView phone_start_scan;//开始扫描（扫连）
    private TextView phone_start_start;//开始扫描（直连）
    private TextView phone_stop_test;//停止扫描
    private TextView phone_test_testing;//测量进行中or测量结果
    private TextView test_time;//测试时间
    private TextView test_model;//机型
    private TextView test_mac;//mac
    private TextView test_distance;//距离
    private TextView test_all_time;//测试总次数
    private TextView test_space;//间隔
    private TextView test_current_count;//当前次数
    private TextView test_scan_min;//扫描最短用时
    private TextView test_scan_max;//扫描最长用时
    private TextView test_scan_average;//扫描平均用时
    private TextView test_scan_succ_count;//扫描成功次数
    private TextView test_scan_fail_count;//扫描失败次数
    private TextView test_scan_succ_rate;//扫描成功率
    private TextView test_connect_min;//连接最短用时
    private TextView test_connect_max;//连接最长用时
    private TextView test_connect_average;//连接平均用时
    private TextView test_connect_succ_count;//连接成功次数
    private TextView test_connect_fail_count;//连接失败次数
    private TextView test_connect_succ_rate;//连接成功率
    private TextView test_fail_records;//手表日志
    private TextView wrong_log_records;//错误日志

    private List<Long> scanBeanList = new ArrayList<>();
    private List<Long> connectList = new ArrayList<>();

    private final int MSG_GET_BLUETOOTH_MAC = 10086;
    private final int MSG_GET_BLUETOOTH_MAC_FAIL = 10087;
    private final int MSG_GET_SCAN_COMPUTE = 10088;
    private final int MSG_GET_LOGIN_FIRST_COMPUTE = 10089;
    private final int MSG_GET_LOGIN_FIRST_LOGINOUT = 10090;

    private int time = 20;
    private int playCount;
    private int playspace;

    private volatile boolean isScanSuccess;

    private boolean isLoginFrist;

    private boolean isFormatConnected;

    private boolean isLoginError = true;

    private boolean isScan;


    private long startScanTime;

    private long endScanTime;

    private long startConnectTime;

    private long endConnectTime;

    private boolean isComputeScanConnected = true;

    private boolean isComputeLoginConnected = true;

    private WatchDevice watchDevice;

    private String currentday;

    private String wrong_logs = "";

    private final int REQUEST_OK = 1;
    //ExecutorService executorService;
    private Handler mHandler;
    private DeviceConnectListener deviceConnectListener = new DeviceConnectListener() {
        @Override
        public void onConnecting() {
            //setDeviceConnectStatus("正在连接...");
        }

        @Override
        public void onLoginSuccess() {
            Log.d("yj", "loginsuccess-----------");
        }

        @Override
        public void onDisconnected(BleError error) {

        }

        @Override
        public void onFailure(BleError bleError) {

        }
    };
    Handler bHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_GET_BLUETOOTH_MAC:
                    Log.d("yj", "bluetooth---mac----");
                    isScanSuccess = true;
                    //executorService.shutdown();
                    mHandler.removeCallbacks(mBackgroundRunnable);
                    bHandler.removeMessages(MSG_GET_BLUETOOTH_MAC_FAIL);
                    BleScanner.getInstance().stopScan();
                    ScannedDevice scannedDevice = (ScannedDevice) msg.obj;
                    if(isScan) {
                        endScanTime = System.currentTimeMillis(); //结束时间
                        scanBeanList.add(endScanTime - startScanTime);
                        CommonValue.SCAN_SUCCESS++;
                    }
                    ScanBlueTooth.ConnectedBluetooth(watchDevice);
                    break;
                case MSG_GET_BLUETOOTH_MAC_FAIL:
                    mHandler.removeCallbacks(mBackgroundRunnable);
                    Log.d("yj","fail------");
                    endScanTime = System.currentTimeMillis(); //结束时间
                    scanBeanList.add(endScanTime - startScanTime);
                    //executorService.shutdown();
                    BleScanner.getInstance().stopScan();
                    CommonValue.SCAN_FAIL++;
                    sendMessages(isScan, MSG_GET_SCAN_COMPUTE, playspace);
                    break;
                case MSG_GET_SCAN_COMPUTE:
                    boolean isScan = (boolean) msg.obj;
                    isComputeScanConnected = false;

                    Log.d("yj", "playCount------" + playCount);
                    if (playCount == 0) {
                        bHandler.removeCallbacksAndMessages(null);
//                        if (isScan)
//                            isComputeScanConnected = true;
//                        else
//                            isComputeLoginConnected = true;
                        isComputeScanConnected = true;
                        isLoginError = true;
                        phone_test_testing.setText(getResources().getString(R.string.connect_phone_test_result));
                        wrong_log_records.setText(wrong_logs);
                        if (isScan) {
                            getCount(scanBeanList, test_scan_min, test_scan_max, test_scan_average, test_scan_succ_count, test_scan_fail_count, test_scan_succ_rate, "scan");
                        }
                        getCount(connectList, test_connect_min, test_connect_max, test_connect_average, test_connect_succ_count, test_connect_fail_count, test_connect_succ_rate, "");
                    }

                    if (playCount > 0) {
                        if (isScan) {
                            startScanTime = System.currentTimeMillis(); //起始时间
                            isScanSuccess = false;
                            time= 20;
                            //executorService = Executors.newSingleThreadExecutor();
                            //Log.d("yj","executorService---"+executorService);
                            mHandler.post(mBackgroundRunnable);//将线程post到Handler中
                            ScanBlueTooth.startScan(BlueToothNoConfirmConnectActivity.this);

                        } else {
                            isComputeLoginConnected = false;
                            if (watchDevice.isConnected()) {
                                ScanBlueTooth.DisConnectedBlueTooth(watchDevice);
                            } else {
                                Log.d("yj", "login---handler----");
                                startConnectTime = System.currentTimeMillis();//连接起始时间
                                Log.d("yj", "loing---handler----2-2-");
                                ScanBlueTooth.ConnectedBluetooth(watchDevice);
                            }
                        }
                        CommonValue.COUNT_ALL++;
                        test_current_count.setText(CommonValue.COUNT_ALL + "次");

                    }
                    if (playCount > 0)
                        playCount--;
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bluetooth_no_confirm_connect);
        CommonValue.isConnectedNOConfirmSsecond = "worked";
        currentday = GetTime.getCurrentTime_Today();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();
        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();//创建一个HandlerThread并启动它
        mHandler = new Handler(thread.getLooper());//使用HandlerThread的looper对象创建Handler，如果使用默认的构造方法，很有可能阻塞UI线程
        watchDevice = new WatchDevice();
        BleScanner.getInstance().addDeviceScanCallback(new BleScanner.OnDeviceScanCallback() {

            @Override
            public void onFind(ScannedDevice scannedDevice) {
                Log.d("yj", "CommonValue.isnoconfrim-----" + CommonValue.isConnectedNOConfirmSsecond);
                if (CommonValue.isConnectedNOConfirmSsecond.equals("worked")) {
                    Log.d("yj","excutorService-------");
//                    executorService.execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            while (time > 0&&!isScanSuccess) {
//                                try {
//                                    Thread.sleep(1000);
//                                    time--;
//                                    Log.d("yj", "time----" + time);
//                                    if (time == 0) {
//                                        bHandler.removeMessages(MSG_GET_SCAN_COMPUTE);
//                                        bHandler.sendEmptyMessage(MSG_GET_BLUETOOTH_MAC_FAIL);
//                                    }
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    });
                    if (phone_select_device.getText().toString().trim().equals(scannedDevice.getMac())) {
                        Log.d("yj", "??????-----扫到");
                        isScanSuccess = true;
                        watchDevice.setMac(scannedDevice.getMac());
                        startConnectTime = System.currentTimeMillis(); //起始时间
                        Message msg = bHandler.obtainMessage();
                        msg.what = MSG_GET_BLUETOOTH_MAC;
                        msg.obj = scannedDevice;
                        bHandler.sendMessage(msg);
                        return;
                    }

                }
            }
        });
    }

    private void initView() {
        EventBus.getDefault().register(this);
        title_back = findViewById(R.id.title_back);
        phone_style_num = findViewById(R.id.phone_style_num);
        phone_system_num = findViewById(R.id.phone_system_num);
        select_device_releate = findViewById(R.id.select_device_releate);
        phone_select_device = findViewById(R.id.phone_select_device_num);
        phone_distance = findViewById(R.id.phone_distance_num);
        phone_count = findViewById(R.id.phone_test_count_num);
        phone_space = findViewById(R.id.phone_space_num);
        phone_start_scan = findViewById(R.id.title_start_scan);
        phone_start_start = findViewById(R.id.title_start_start);
        phone_stop_test = findViewById(R.id.title_stop_test);
        phone_test_testing = findViewById(R.id.test_result);
        test_time = findViewById(R.id.phone_test_time_num);
        test_model = findViewById(R.id.phone_test_model_num);
        test_mac = findViewById(R.id.phone_test_mac_num);
        test_distance = findViewById(R.id.phone_test_distance_num);
        test_all_time = findViewById(R.id.phone_test_all_count_num);
        test_space = findViewById(R.id.phone_test_space_num);
        test_current_count = findViewById(R.id.phone_test_current_count_num);
        test_scan_min = findViewById(R.id.phone_test_scan_min_num);
        test_scan_max = findViewById(R.id.phone_test_scan_max_num);
        test_scan_average = findViewById(R.id.phone_test_scan_average_num);
        test_scan_succ_count = findViewById(R.id.phone_test_scan_success_count_num);
        test_scan_fail_count = findViewById(R.id.phone_test_scan_fail_count_num);
        test_scan_succ_rate = findViewById(R.id.phone_test_scan_success_rate_num);
        test_connect_min = findViewById(R.id.phone_test_connect_min_num);
        test_connect_max = findViewById(R.id.phone_test_connect_max_num);
        test_connect_average = findViewById(R.id.phone_test_connect_average_num);
        test_connect_succ_count = findViewById(R.id.phone_test_connect_success_count_num);
        test_connect_fail_count = findViewById(R.id.phone_test_connect_fail_count_num);
        test_connect_succ_rate = findViewById(R.id.phone_test_connect_success_rate_num);
        test_fail_records = findViewById(R.id.test_fail_records);
        wrong_log_records = findViewById(R.id.wrong_log_records);
        phone_style_num.setText(GetPhoneInfo.getSystemModel());
        phone_system_num.setText(GetPhoneInfo.getSystemVersion());

        title_back.setOnClickListener(this);
        select_device_releate.setOnClickListener(this);
        phone_start_scan.setOnClickListener(this);
        phone_start_start.setOnClickListener(this);
        phone_stop_test.setOnClickListener(this);
        test_fail_records.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.select_device_releate:
                if (Util.isFastClick()) {
                    if (isComputeScanConnected && isComputeLoginConnected)
                        gotoSelectDevice();
                    else
                        Toast.makeText(BlueToothNoConfirmConnectActivity.this, "扫连或直连中，请稍后或者停止后点击", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.title_start_scan:
                if (Util.isFastClick())
                    initclick(true);
                break;
            case R.id.title_start_start:
                if (Util.isFastClick())
                    initclick(false);
                break;
            case R.id.title_stop_test:
                if (Util.isFastClick())
                    stopTest();
                break;
            case R.id.test_fail_records:
                if (watchDevice.isLogin()) {
                    ScanBlueTooth.writeDeviceLog(watchDevice, BlueToothNoConfirmConnectActivity.this, currentday);
                } else {
                    Toast.makeText(BlueToothNoConfirmConnectActivity.this, "请确保设备绑定状态", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void gotoSelectDevice() {
        Intent in = new Intent();
        in.setClass(BlueToothNoConfirmConnectActivity.this, SelectDeviceActivity.class);
        startActivityForResult(in, REQUEST_OK);
        CommonValue.isConnectedNOConfirmSsecond = "secondisworked";
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

    @Override
    public void finish() {
        super.finish();
        if (bHandler != null) {
            bHandler.removeCallbacksAndMessages(null);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BindEvent event) {
        if (event.message.equals(CommonValue.BLUE_CONNECT_SUCCESS)) {
            if (!isFormatConnected) {
                CommonValue.CONNECT_SUCCESS++;
                endConnectTime = System.currentTimeMillis();
                connectList.add(endConnectTime - startConnectTime);
            }
            ScanBlueTooth.DisConnectedBlueTooth(watchDevice);
            isFormatConnected = false;
        } else if (event.message.equals(CommonValue.BLUE_CONNECT_FAILURE)) {
            wrong_logs = wrong_logs + "\n" + event.errorinfo;
            isFormatConnected = false;
            CommonValue.CONNECT_FAIL++;
            endConnectTime = System.currentTimeMillis();
            connectList.add(endConnectTime - startConnectTime);
            if (!isComputeScanConnected) {
                if (watchDevice.isConnected())
                    sendMessages(isScan, MSG_GET_LOGIN_FIRST_COMPUTE, playspace);
                else {
                    isFormatConnected = true;
                    try {
                        Thread.sleep(7000);
                        ScanBlueTooth.ConnectedBluetooth(watchDevice);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                stopTest();
            }

        }

        if (event.message.equals(CommonValue.BLUE_DISCONNECT_SUCCESS)) {
            if (!isComputeScanConnected) {
                sendMessages(isScan, MSG_GET_SCAN_COMPUTE, playspace);
            }
        } else if (event.message.equals(CommonValue.BLUE_DISCONNECT_FAILURE)) {
            wrong_logs = wrong_logs + "\n" + event.errorinfo;
            if (!isComputeScanConnected) {
                if (watchDevice.isConnected())
                    ScanBlueTooth.DisConnectedBlueTooth(watchDevice);
                else {
                    isFormatConnected = true;
                    try {
                        Thread.sleep(7000);
                        ScanBlueTooth.ConnectedBluetooth(watchDevice);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(InitEvent event) {
        if (event.message.equals("initEvent")) {
            stopTest();
        }
    }


    private void getCount(List<Long> list, TextView mintext, TextView maxtext, TextView averagetext, TextView success, TextView fail, TextView rate, String scan) {
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
            mintext.setText(min + "毫秒");
            maxtext.setText(max + "毫秒");
            averagetext.setText(average + "毫秒");
        }
        if (scan.equals("scan")) {
            success.setText(CommonValue.SCAN_SUCCESS + "次");
            fail.setText(CommonValue.SCAN_FAIL + "次");
            if (CommonValue.SCAN_SUCCESS == 0 && CommonValue.SCAN_FAIL == 0) {
                rate.setText("0%");
            } else {
                float ra = (CommonValue.SCAN_SUCCESS / Float.parseFloat(phone_count.getText().toString().trim())) * 100;
                String raa = Util.getTwoFloat(ra);
                rate.setText(raa + "%");
            }
        } else {
            success.setText(CommonValue.CONNECT_SUCCESS + "次");
            fail.setText(CommonValue.CONNECT_FAIL + "次");
            if (CommonValue.CONNECT_SUCCESS == 0 && CommonValue.CONNECT_FAIL == 0) {
                rate.setText("0%");
            } else {
                float ra = (CommonValue.CONNECT_SUCCESS /Float.parseFloat(phone_count.getText().toString().trim())) * 100;
                String raa = Util.getTwoFloat(ra);
                rate.setText(raa + "%");
            }
        }


    }

    private void initclick(boolean isScan) {
        if (isComputeScanConnected && isComputeLoginConnected) {
            if (TextUtils.isEmpty(phone_select_device.getText().toString().trim()) || phone_select_device.getText().toString().trim().equals("--")) {
                Toast.makeText(BlueToothNoConfirmConnectActivity.this, getResources().getString(R.string.connect_warning_select_device), Toast.LENGTH_LONG).show();
                return;
            }
            if (isNumber(phone_distance) && isNumber(phone_count) && isNumber(phone_space)) {
                if (phone_count.getText().toString().trim().equals("0")) {
                    Toast.makeText(BlueToothNoConfirmConnectActivity.this, getResources().getString(R.string.connect_warning_play_count), Toast.LENGTH_LONG).show();
                    return;
                }
                this.isScan = isScan;
                CommonValue.SCAN_SUCCESS = 0;
                CommonValue.SCAN_FAIL = 0;
                CommonValue.COUNT_ALL = 0;
                CommonValue.CONNECT_SUCCESS = 0;
                CommonValue.CONNECT_FAIL = 0;
                watchDevice.setMac(phone_select_device.getText().toString().trim());
                watchDevice.setToken(CommonShared.ReadToken(BlueToothNoConfirmConnectActivity.this, phone_select_device.getText().toString().trim()));
                if (isScan) {
                    if (watchDevice.isConnected()) {
                        Toast.makeText(BlueToothNoConfirmConnectActivity.this, "设备绑定状态，清先点击停止测试，再开始测试", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                test_time.setText(GetTime.getCurrentTime_Today());
                test_model.setText(GetPhoneInfo.getSystemModel());
                test_mac.setText(phone_select_device.getText().toString().trim());
                test_distance.setText(phone_distance.getText().toString().trim() + "米");
                test_space.setText(phone_space.getText().toString().trim() + "秒");
                test_all_time.setText(phone_count.getText().toString().trim() + "次");
                test_current_count.setText("--");
                //test_scan_min, test_scan_max, test_scan_average, test_scan_succ_count, test_scan_fail_count, test_scan_succ_rate
                test_scan_min.setText("--");
                test_scan_max.setText("--");
                test_scan_average.setText("--");
                test_scan_succ_count.setText("--");
                test_scan_fail_count.setText("--");
                test_scan_succ_rate.setText("--");
                test_connect_min.setText("--");
                test_connect_max.setText("--");
                test_connect_average.setText("--");
                test_connect_succ_count.setText("--");
                test_connect_fail_count.setText("--");
                test_connect_succ_rate.setText("--");
                scanBeanList.clear();
                connectList.clear();
                phone_test_testing.setText(getResources().getString(R.string.connect_phone_test_testing));
                playCount = Integer.valueOf(phone_count.getText().toString().trim());
                playspace = Integer.valueOf(phone_space.getText().toString().trim());
                Log.d("yj", "playCount-----" + playCount + "----" + playspace);
                sendMessages(isScan, MSG_GET_SCAN_COMPUTE, playspace);
            } else {
                Toast.makeText(BlueToothNoConfirmConnectActivity.this, getResources().getString(R.string.connect_warning_input_number_style), Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            Toast.makeText(BlueToothNoConfirmConnectActivity.this, getResources().getString(R.string.connect_scaning_connecting), Toast.LENGTH_LONG).show();
        }
    }

    private void sendMessages(boolean isScan, int what, int playspace) {
        Message msg = bHandler.obtainMessage();
        msg.obj = isScan;
        msg.what = what;
        bHandler.sendMessageDelayed(msg, playspace * 1000);
    }

    private void stopTest() {
        watchDevice.setMac(phone_select_device.getText().toString().trim());
        watchDevice.setToken(CommonShared.ReadToken(BlueToothNoConfirmConnectActivity.this, phone_select_device.getText().toString().trim()));
        if (bHandler != null) {
            bHandler.removeCallbacksAndMessages(null);
        }
        phone_test_testing.setText(getResources().getString(R.string.connect_phone_test_result));
        wrong_log_records.setText(wrong_logs);
        getCount(scanBeanList, test_scan_min, test_scan_max, test_scan_average, test_scan_succ_count, test_scan_fail_count, test_scan_succ_rate, "scan");
        getCount(connectList, test_connect_min, test_connect_max, test_connect_average, test_connect_succ_count, test_connect_fail_count, test_connect_succ_rate, "");
        if (BleScanner.getInstance().isScanning()) {
            BleScanner.getInstance().stopScan();
        }
        isComputeScanConnected = true;
        isComputeLoginConnected = true;
        isScanSuccess = true;
        isFormatConnected = false;

        isLoginError = true;
        if(mHandler!=null)
            mHandler.removeCallbacksAndMessages(null);
        //executorService.shutdown();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bHandler != null) {
            bHandler.removeCallbacksAndMessages(null);
        }
    }

    //实现耗时操作的线程
    Runnable mBackgroundRunnable = new Runnable() {

        @Override
        public void run() {
            while(time>0&&!isScanSuccess){
                try {
                    Thread.sleep(1000);
                    time--;
                    Log.d("yj", "time----" + time);
                    if (time == 0) {
                        bHandler.removeMessages(MSG_GET_SCAN_COMPUTE);
                        bHandler.sendEmptyMessage(MSG_GET_BLUETOOTH_MAC_FAIL);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonValue.isConnectedNOConfirmSsecond = "ideal";
        EventBus.getDefault().unregister(BlueToothNoConfirmConnectActivity.this);
        BleScanner.getInstance().stopScan();
        BleScanner.getInstance().removeDeviceScanCallback(new BleScanner.OnDeviceScanCallback() {
            @Override
            public void onFind(ScannedDevice scannedDevice) {
                Log.d("yj", "remove--connect--scannedDevice--");
            }
        });
        if (bHandler != null) {
            bHandler.removeCallbacksAndMessages(null);
        }
        if(mHandler!=null)
            mHandler.removeCallbacksAndMessages(null);

    }


}