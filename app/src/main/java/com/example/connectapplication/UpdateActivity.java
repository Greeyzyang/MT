package com.example.connectapplication;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.connectapplication.adapter.PopupWindowAdapter;
import com.example.connectapplication.constans.CommonValue;
import com.example.connectapplication.event.BindEvent;
import com.example.connectapplication.event.UpdateEvent;
import com.example.connectapplication.utils.CommonShared;
import com.example.connectapplication.utils.GetPhoneInfo;
import com.example.connectapplication.utils.GetTime;
import com.example.connectapplication.utils.ScanBlueTooth;
import com.example.connectapplication.utils.Util;
import com.example.connectapplication.utils.WriteLogToFile;
import com.ryeex.ble.common.utils.FileUtil;
import com.ryeex.ble.connector.handler.BleHandler;
import com.ryeex.ble.connector.scan.BleScanner;
import com.ryeex.ble.connector.scan.ScannedDevice;
import com.ryeex.watch.adapter.device.WatchDevice;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_update_back;//返回
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
    private RelativeLayout select_version_upgrade_releate;//选择固件升级版本布局
    private TextView select_version_upgrade_num;//选择固件升级版本号
    private RelativeLayout select_version_downgrade_releate;//选择固件降级版本布局
    private TextView select_version_downgrade_num;//选择固件降级版本号
    private RelativeLayout select_version_upgrade_releate1;//选择资源升级版本布局
    private TextView select_version_upgrade_num1;//选择资源升级版本号
    private RelativeLayout select_version_downgrade_releate1;//选择资源降级版本布局
    private TextView select_version_downgrade_num1;//选择资源降级版本号
    private TextView title_start_test;//固件升级
    private TextView stop_test;//停止测试
    private TextView test_update_progress_selected;//升级进度
    private TextView test_time;//测试时间
    private TextView test_model;//机型
    private TextView test_mac;//mac
    private TextView test_distance;//距离
    private TextView test_all_time;//测试总次数
    private TextView test_space;//间隔
    private TextView test_current_count;//当前次数
    private TextView update_success;//升级成功次数
    private TextView update_failure;//升级失败次数
    private TextView update_success_rate;//升级成功率
    private TextView update_max_time;//升级最长用时
    private TextView update_min_time;//升级最短用时
    private TextView update_average_time;//升级平均用时
    private TextView downgrade_success;//降级成功次数
    private TextView downgrade_failure;//降级失败次数
    private TextView downgrade_max_time;//降级最长时间
    private TextView downgrade_min_time;//降级最短时间
    private TextView downgrade_average_time;//降级平均时间
    private TextView downgrade_success_rate;//降级成功率
    private TextView wrong_log_records;//错误日志
    private TextView test_fail_records;//手表日志

    private final int REQUEST_OK = 5;

    private boolean isComputeUpdated = true;
    private WatchDevice watchDevice;
    private boolean isBind;
    private String currentday;
    private String wrong_logs;
    private long update_on_time;
    private boolean isDownGrade;
    private List<Long> timeupdateList = new ArrayList<>();
    private List<Long> timedownlist = new ArrayList<>();
    private List<String> filenames = new ArrayList<>();
    private int playCount;
    private int playspace;

    private ListPopupWindow listPopupWindow;
    private ListPopupWindow dlistPopupWindow;
    private ListPopupWindow listPopupWindow1;
    private ListPopupWindow dlistPopupWindow1;

    private PopupWindowAdapter popupWindowAdapter;

    private String updatefilename;

    private String downgradefilename;

    private String updatefilename1;

    private String downgradefilename1;
    private String fileDir = WriteLogToFile.logPath + File.separator + "watchupdate/";

    private String filePath = WriteLogToFile.logPath + File.separator + "errorLog--" + Util.getTodayStr() + ".txt";

    private final int MSG_GET_UPDATE_INFO = 10096;
    private final int MSG_GET_UPDATE_LOGIN = 100097;
    Handler uHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_GET_UPDATE_INFO:
                    Log.d("yj", "MSG_GET_UPDATE_INFO11111111111");
                    isComputeUpdated = false;
                    Log.d("yj", "handler-----msg---isDownGrade---" + isDownGrade);
                    Log.d("yj", "playCount-----msg---playCount---" + playCount);
                    if (playCount == 0) {
                        uHandler.removeCallbacksAndMessages(null);
                        isComputeUpdated = true;
                        test_update_progress_selected.setText("升级成功");
                        wrong_log_records.setText(wrong_logs);
                        FileUtil.appendString(filePath, wrong_logs + " " + Util.getFormatTime(System.currentTimeMillis() / 1000, "yyyy-MM-dd HH:mm:ss"));

                        getUpdateCount(timeupdateList, update_max_time, update_min_time, update_average_time, update_success, update_failure, update_success_rate);
                        getDownGradeCount(timedownlist, downgrade_max_time, downgrade_min_time, downgrade_average_time, downgrade_success, downgrade_failure, downgrade_success_rate);
                    }
                    if (playCount > 0) {
                        if (!isDownGrade) {
                            playCount--;
                            CommonValue.UPDATE_ALL_COUNT++;
                            test_current_count.setText(CommonValue.UPDATE_ALL_COUNT + "次");
                            update_on_time = 0;
                            Log.d("yj", "updatefilename1---" + updatefilename1);
                            Log.d("yj", "updatefilename---" + updatefilename);
                            if (updatefilename1 == null) {
                                ScanBlueTooth.UpdateVersion(watchDevice, select_version_upgrade_num.getText().toString().trim(), fileDir, updatefilename);

                            } else {
                                ScanBlueTooth.UpdateVersion1(watchDevice, select_version_upgrade_num.getText().toString().trim(), fileDir, updatefilename, updatefilename1);
                            }
                        } else {
                            update_on_time = 0;
                            if (updatefilename1 == null) {
                                ScanBlueTooth.UpdateVersion(watchDevice, select_version_downgrade_num.getText().toString().trim(), fileDir, downgradefilename);
                            } else {
                                ScanBlueTooth.UpdateVersion1(watchDevice, select_version_downgrade_num.getText().toString().trim(), fileDir, downgradefilename, downgradefilename1);
                            }

                        }
                    }
                    break;
                case MSG_GET_UPDATE_LOGIN:
                    Log.d("yj", "MSG_GET_UPDATE_LOGIN22222222222");
                    ScanBlueTooth.startLogin(watchDevice);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_update_ota);
        getAllname(fileDir);
        currentday = GetTime.getCurrentTime_Today();
        popupWindowAdapter = new PopupWindowAdapter(UpdateActivity.this, filenames);
        getUpdatePopWindow();
        getDowngradePopWindow();
        getUpdatePopWindow1();
        getDowngradePopWindow1();
        CommonValue.isUpdateSecond = "worked";
        EventBus.getDefault().register(this);
        init();

        watchDevice = new WatchDevice();
        BleScanner.getInstance().addDeviceScanCallback(new BleScanner.OnDeviceScanCallback() {

            @Override
            public void onFind(ScannedDevice scannedDevice) {
                Log.d("yj", "CommonValue.isSecond-----" + CommonValue.isSecond);
                if (CommonValue.isUpdateSecond.equals("worked")) {
                    if (phone_select_device.getText().toString().trim().equals(scannedDevice.getMac())) {
                        BleScanner.getInstance().stopScan();
                        ScanBlueTooth.startBind(scannedDevice, watchDevice);
                    }
                }
            }
        });
    }

    private void init() {
        title_update_back = findViewById(R.id.title_update_back);
        phone_style_num = findViewById(R.id.phone_style_num);
        phone_system_num = findViewById(R.id.phone_system_num);
        select_device_releate = findViewById(R.id.select_device_releate);
        phone_select_device = findViewById(R.id.phone_select_device_num);
        phone_distance = findViewById(R.id.phone_distance_num);
        phone_count = findViewById(R.id.phone_test_count_num);
        phone_space = findViewById(R.id.phone_space_num);
        title_bind_start = findViewById(R.id.title_bind_start);
        title_unbind_start = findViewById(R.id.title_unbind_start);
        test_bind_unbind_result = findViewById(R.id.test_bind_result);
        select_version_upgrade_releate = findViewById(R.id.select_version_upgrade_releate);
        select_version_upgrade_num = findViewById(R.id.select_version_upgrade_num);
        select_version_downgrade_releate = findViewById(R.id.select_version_downgrade_releate);
        select_version_downgrade_num = findViewById(R.id.select_version_downgrade_num);
        select_version_upgrade_releate1 = findViewById(R.id.select_version_upgrade_releate1);
        select_version_upgrade_num1 = findViewById(R.id.select_version_upgrade_num1);
        select_version_downgrade_releate1 = findViewById(R.id.select_version_downgrade_releate1);
        select_version_downgrade_num1 = findViewById(R.id.select_version_downgrade_num1);
        title_start_test = findViewById(R.id.title_start_test);
        stop_test = findViewById(R.id.title_test_stop);
        test_update_progress_selected = findViewById(R.id.test_update_progress_selected);
        test_time = findViewById(R.id.phone_test_time_num);
        test_model = findViewById(R.id.phone_test_model_num);
        test_mac = findViewById(R.id.phone_test_mac_num);
        test_distance = findViewById(R.id.phone_test_distance_num);
        test_all_time = findViewById(R.id.phone_test_all_count_num);
        test_space = findViewById(R.id.phone_test_space_num);
        test_current_count = findViewById(R.id.phone_test_current_count_num);
        update_success = findViewById(R.id.update_success);
        update_failure = findViewById(R.id.update_failure);
        update_success_rate = findViewById(R.id.update_success_rate);
        update_max_time = findViewById(R.id.update_max_time);
        update_min_time = findViewById(R.id.update_min_time);
        update_average_time = findViewById(R.id.update_average_time);
        downgrade_success = findViewById(R.id.downgrade_success);
        downgrade_failure = findViewById(R.id.downgrade_failure);
        downgrade_success_rate = findViewById(R.id.downgrade_success_rate);
        downgrade_max_time = findViewById(R.id.downgrade_max_time);
        downgrade_min_time = findViewById(R.id.downgrade_min_time);
        downgrade_average_time = findViewById(R.id.downgrade_average_time);
        wrong_log_records = findViewById(R.id.wrong_log_records);
        test_fail_records = findViewById(R.id.test_fail_records);
        phone_style_num.setText(GetPhoneInfo.getSystemModel());
        phone_system_num.setText(GetPhoneInfo.getSystemVersion());
        phone_distance.setText("3");
        phone_count.setText("100");
        phone_space.setText("5");
        title_update_back.setOnClickListener(this);
        select_device_releate.setOnClickListener(this);
        title_bind_start.setOnClickListener(this);
        title_unbind_start.setOnClickListener(this);
        select_version_upgrade_releate.setOnClickListener(this);
        select_version_downgrade_releate.setOnClickListener(this);
        select_version_upgrade_releate1.setOnClickListener(this);
        select_version_downgrade_releate1.setOnClickListener(this);
        title_start_test.setOnClickListener(this);
        stop_test.setOnClickListener(this);
        test_fail_records.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_update_back:
                finish();
                break;
            case R.id.select_device_releate:
                if (Util.isFastClick()) {
                    if (isComputeUpdated)
                        gotoSelectDevice();
                    else
                        Toast.makeText(UpdateActivity.this, "正在升级", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.title_bind_start:
                if (Util.isFastClick())
                    startBindorUnbind(true);
                break;
            case R.id.title_unbind_start:
                if (Util.isFastClick())
                    startBindorUnbind(false);
                break;
            case R.id.select_version_upgrade_releate:
                popupWindowAdapter.notifyDataSetChanged();
                if (Build.VERSION.SDK_INT == 24) {
                    int[] a = new int[2];
                    view.getLocationInWindow(a);
                    listPopupWindow.setHeight(getResources().getDisplayMetrics().heightPixels - a[1] - view.getHeight());
                }
                Log.d("yj", "fileDir--------" + fileDir);
                listPopupWindow.show();
                break;
            case R.id.select_version_downgrade_releate:
                popupWindowAdapter.notifyDataSetChanged();
                if (Build.VERSION.SDK_INT == 24) {
                    int[] a = new int[2];
                    view.getLocationInWindow(a);
                    dlistPopupWindow.setHeight(getResources().getDisplayMetrics().heightPixels - a[1] - view.getHeight());
                }
                dlistPopupWindow.show();
                break;
            case R.id.select_version_upgrade_releate1:
                popupWindowAdapter.notifyDataSetChanged();
                if (Build.VERSION.SDK_INT == 24) {
                    int[] a = new int[2];
                    view.getLocationInWindow(a);
                    listPopupWindow1.setHeight(getResources().getDisplayMetrics().heightPixels - a[1] - view.getHeight());
                }
                Log.d("yj", "fileDir--------" + fileDir);
                listPopupWindow1.show();
                break;
            case R.id.select_version_downgrade_releate1:
                popupWindowAdapter.notifyDataSetChanged();
                if (Build.VERSION.SDK_INT == 24) {
                    int[] a = new int[2];
                    view.getLocationInWindow(a);
                    dlistPopupWindow1.setHeight(getResources().getDisplayMetrics().heightPixels - a[1] - view.getHeight());
                }
                dlistPopupWindow1.show();
                break;
            case R.id.title_start_test:
                if (Util.isFastClick())
                    clickUpdate();
                break;
            case R.id.test_fail_records:
                if (watchDevice.isLogin()) {
                    ScanBlueTooth.writeDeviceLog(watchDevice, UpdateActivity.this, currentday);
                    Toast.makeText(UpdateActivity.this, "导入设备日志成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(UpdateActivity.this, "请确保设备绑定状态", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.title_test_stop:
                if (Util.isFastClick())
                    stopTest();
                break;
        }
    }


    private void gotoSelectDevice() {
        Intent in = new Intent();
        in.setClass(UpdateActivity.this, SelectDeviceActivity.class);
        startActivityForResult(in, REQUEST_OK);
        CommonValue.isUpdateSecond = "secondisworked";
    }

    private void getAllname(String fileDir) {
        ArrayList<String> ss = Util.getFileName(fileDir, ".bin");
        for (String s : ss) {
            String versionname = s.substring(0, s.lastIndexOf("."));
            filenames.add(versionname);
        }

    }

    private void startBindorUnbind(boolean bind) {
        if (isComputeUpdated) {
            if (TextUtils.isEmpty(phone_select_device.getText().toString().trim()) || phone_select_device.getText().toString().trim().equals("--")) {
                Toast.makeText(UpdateActivity.this, getResources().getString(R.string.connect_warning_select_device), Toast.LENGTH_LONG).show();
                return;
            }
            watchDevice.setMac(phone_select_device.getText().toString().trim());
            if (bind) {
                if (watchDevice.isLogin()) {
                    isBind = true;
                    Toast.makeText(UpdateActivity.this, "设备已绑定", Toast.LENGTH_LONG).show();
                    return;
                }
                test_bind_unbind_result.setText("绑定设备中……");
                ScanBlueTooth.startScan(UpdateActivity.this);
            } else {
                if (watchDevice.isLogin()) {
                    test_bind_unbind_result.setText("绑定解绑中……");
                    ScanBlueTooth.endBind(watchDevice);
                } else {
                    Toast.makeText(UpdateActivity.this, "当前是解绑状态，请检查设备是否处于未绑定状态", Toast.LENGTH_LONG).show();
                }

            }

        } else {
            Toast.makeText(UpdateActivity.this, getResources().getString(R.string.send_auto_sending), Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BindEvent event) {
        Log.d("yj", "进入onevent--------");
        if (event.message.equals(CommonValue.BIND_SUCCESS)) {
            Log.d("yj", "onevent1--------");
            test_bind_unbind_result.setText("绑定设备成功");
            CommonShared.WriteToken(UpdateActivity.this, phone_select_device.getText().toString().trim(), watchDevice.getToken());
            isBind = true;
        } else if (event.message.equals(CommonValue.BIND_ERROR)) {
            Log.d("yj", "onevent2--------");
            wrong_logs = wrong_logs + "\n" + event.errorinfo;
            test_bind_unbind_result.setText("绑定设备失败");
            //ConnectionDialog.showNormalDialog(NotificationAutoTestActivity.this);
            watchDevice.setToken(CommonShared.ReadToken(UpdateActivity.this, phone_select_device.getText().toString().trim()));
            ScanBlueTooth.startLogin(watchDevice);
        }

        if (event.message.equals(CommonValue.UNBIND_SUCCESS)) {
            Log.d("yj", "onevent3--------");
            test_bind_unbind_result.setText("解绑设备成功");
            isBind = false;
        } else if (event.message.equals(CommonValue.UNBIND_ERROR)) {
            Log.d("yj", "onevent4--------");
            wrong_logs = wrong_logs + "\n" + event.errorinfo;
            test_bind_unbind_result.setText("解绑设备失败");
        }

        if (event.message.equals(CommonValue.UPDATE_PREGRESS)) {
            Log.d("yj", "onevent5--------");
            test_update_progress_selected.setText(Float.parseFloat(event.errorinfo) * 100 + "%");

        }

        if (event.message.equals(CommonValue.UPDATE_SUCCESS)) {
            if (!isDownGrade) {
                Log.d("yj", "onevent6--------");
                CommonValue.UPDATE_SUCCESS_COUNT++;
                timeupdateList.add(update_on_time);
                test_update_progress_selected.setText("升级成功");
                isDownGrade = true;
                uHandler.sendEmptyMessageDelayed(MSG_GET_UPDATE_LOGIN, 30 * 1000);
            } else {
                Log.d("yj", "onevent7--------");
                CommonValue.DOWNGRADE_SUCCESS_COUNT++;
                timedownlist.add(update_on_time);
                test_update_progress_selected.setText("降级成功");
                isDownGrade = false;
                long time;
                if (!isComputeUpdated) {
                    if (playspace > 10)
                        time = playspace * 1000;
                    else
                        time = 30 * 1000;
                    uHandler.sendEmptyMessageDelayed(MSG_GET_UPDATE_LOGIN, time);
                }
            }

        }

        if (event.message.equals(CommonValue.UPDATE_FAIL)) {
            Log.d("yj", "onevent8--------");
            wrong_logs = wrong_logs + "\n" + event.errorinfo;
            if (!isDownGrade) {
                CommonValue.UPDATE_FAIL_COUNT++;
            } else {
                CommonValue.DOWNGRADE_FAIL_COUNT++;
            }
            test_update_progress_selected.setText("升级失败");
            uHandler.sendEmptyMessageDelayed(MSG_GET_UPDATE_LOGIN, 30 * 1000);
//            uHandler.sendEmptyMessage(MSG_GET_UPDATE_LOGIN);

        }

        if (event.message.equals(CommonValue.LOGIN_SUCCESS)) {
            Log.d("yj", "isComputeUpdate----login--" + isComputeUpdated);
            if (!isComputeUpdated) {
                Log.d("yj", "isDownGrade---login---" + isDownGrade);
                if (!isDownGrade)
                    uHandler.sendEmptyMessage(MSG_GET_UPDATE_INFO);
                else {
                    Log.d("yj", "CommonValue.UPDATE_ALL_COUNT---login---" + CommonValue.UPDATE_ALL_COUNT);
                    Log.d("yj", "phone_count.getText().toString().trim()---login---" + test_all_time.getText());
                    uHandler.sendEmptyMessage(MSG_GET_UPDATE_INFO);
                }
            }
        } else if (event.message.equals(CommonValue.LOGIN_ERROR)) {
            uHandler.sendEmptyMessageDelayed(MSG_GET_UPDATE_LOGIN, 5000);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateEvent event) {
        if (event.message.equals(CommonValue.UPDATE_ON_UPDATE)) {
            int time = event.time;
            update_on_time = update_on_time + time;
            Log.d("yj", "update_on_time-----" + update_on_time);
        }
    }

    private void stopTest() {
        watchDevice.setMac(phone_select_device.getText().toString().trim());
        if (uHandler != null) {
            uHandler.removeCallbacksAndMessages(null);
        }
        playCount = 0;
        playspace = 0;
        isComputeUpdated = true;
        test_update_progress_selected.setText(getResources().getString(R.string.connect_phone_test_result));
//        sync_result.setText(getResources().getString(R.string.connect_phone_test_result));
        wrong_log_records.setText(wrong_logs);
        FileUtil.appendString(filePath, wrong_logs + " " + Util.getFormatTime(System.currentTimeMillis() / 1000, "yyyy-MM-dd HH:mm:ss"));
        if (BleScanner.getInstance().isScanning()) {
            BleScanner.getInstance().stopScan();
        }
        getUpdateCount(timeupdateList, update_max_time, update_min_time, update_average_time, update_success, update_failure, update_success_rate);
        getDownGradeCount(timedownlist, downgrade_max_time, downgrade_min_time, downgrade_average_time, downgrade_success, downgrade_failure, downgrade_success_rate);
    }

    private void getUpdateCount(List<Long> list, TextView max_time, TextView min_time, TextView average_time, TextView send_update_success_num, TextView send_update_fail_num, TextView send_update_success_rate) {
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
            max_time.setText(max + "毫秒");
            min_time.setText(min + "毫秒");
            average_time.setText(average + "毫秒");
        }
        send_update_success_num.setText(CommonValue.UPDATE_SUCCESS_COUNT + "次");
        send_update_fail_num.setText(CommonValue.UPDATE_FAIL_COUNT + "次");
        if (CommonValue.UPDATE_SUCCESS_COUNT == 0) {
            send_update_success_rate.setText("0%");
        } else {
            float ra = (CommonValue.UPDATE_SUCCESS_COUNT / Float.parseFloat(phone_count.getText().toString().trim())) * 100;
            String raa = Util.getTwoFloat(ra);
            send_update_success_rate.setText(raa + "%");
        }
    }

    private void getDownGradeCount(List<Long> list, TextView max_time, TextView min_time, TextView average_time, TextView send_downgrade_success_num, TextView send_downgrade_fail_num, TextView send_downgrade_success_rate) {
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
            max_time.setText(max + "毫秒");
            min_time.setText(min + "毫秒");
            average_time.setText(average + "毫秒");
        }
        send_downgrade_success_num.setText(CommonValue.DOWNGRADE_SUCCESS_COUNT + "次");
        send_downgrade_fail_num.setText(CommonValue.DOWNGRADE_FAIL_COUNT + "次");
        if (CommonValue.DOWNGRADE_SUCCESS_COUNT == 0) {
            send_downgrade_success_rate.setText("0%");
        } else {
            float ra = (CommonValue.DOWNGRADE_SUCCESS_COUNT / (Float.parseFloat(phone_count.getText().toString().trim()) - 1)) * 100;
            String raa = Util.getTwoFloat(ra);
            send_downgrade_success_rate.setText(raa + "%");
        }
    }

    private void clickUpdate() {
        if (isComputeUpdated) {
            if (TextUtils.isEmpty(phone_select_device.getText().toString().trim()) || phone_select_device.getText().toString().trim().equals("--")) {
                Toast.makeText(UpdateActivity.this, getResources().getString(R.string.connect_warning_select_device), Toast.LENGTH_LONG).show();
                return;
            }
            if (isBind) {
                CommonValue.UPDATE_ALL_COUNT = 0;
                CommonValue.UPDATE_SUCCESS_COUNT = 0;
                CommonValue.UPDATE_FAIL_COUNT = 0;
                CommonValue.DOWNGRADE_SUCCESS_COUNT = 0;
                CommonValue.DOWNGRADE_FAIL_COUNT = 0;
                timeupdateList.clear();
                timedownlist.clear();
                watchDevice.setMac(phone_select_device.getText().toString().trim());
                test_time.setText(GetTime.getCurrentTime_Today());
                test_model.setText(GetPhoneInfo.getSystemModel());
                test_mac.setText(phone_select_device.getText().toString().trim());
                test_distance.setText(phone_distance.getText().toString().trim() + "米");
                test_space.setText(phone_space.getText().toString().trim() + "秒");
                test_all_time.setText(phone_count.getText().toString().trim() + "次");
                test_current_count.setText("--");
                update_success.setText("--");
                update_failure.setText("--");
                update_success_rate.setText("--");
                update_max_time.setText("--");
                update_min_time.setText("--");
                update_average_time.setText("--");
                downgrade_success.setText("--");
                downgrade_failure.setText("--");
                downgrade_success_rate.setText("--");
                downgrade_max_time.setText("--");
                downgrade_min_time.setText("--");
                downgrade_average_time.setText("--");
                test_update_progress_selected.setText(getResources().getString(R.string.connect_phone_test_testing));
                playCount = Integer.valueOf(phone_count.getText().toString().trim());
                playspace = Integer.valueOf(phone_space.getText().toString().trim());
                Log.d("yj", "playCount--update---" + playCount + "--update--" + playspace);
                uHandler.sendEmptyMessage(MSG_GET_UPDATE_INFO);
            } else {
                Toast.makeText(UpdateActivity.this, "请先绑定设备", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(UpdateActivity.this, "升级进行中", Toast.LENGTH_LONG).show();
            return;
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


    private void getUpdatePopWindow() {
        listPopupWindow = new ListPopupWindow(this);

        listPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);//设置宽度

        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);//设置高度

        listPopupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.color_back_title)));//设置背景色

        listPopupWindow.setAdapter(popupWindowAdapter);

        listPopupWindow.setAnchorView(findViewById(R.id.select_version_upgrade_releate));

        listPopupWindow.setModal(false);//设置为true响应物理键listPopupWindow.setHorizontalOffset(100);//垂直间距listPopupWindow.setVerticalOffset(100);//水平间距findViewById(R.id.popup).setOnClickListener(newView.OnClickListener() {


        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {//item 点击事件

            @Override

            public void onItemClick(AdapterView parent, View view, int position, long id) {
                select_version_upgrade_num.setText(filenames.get(position));
                updatefilename = filenames.get(position);
                listPopupWindow.dismiss();

            }

        });

    }

    private void getUpdatePopWindow1() {
        listPopupWindow1 = new ListPopupWindow(this);

        listPopupWindow1.setWidth(ListPopupWindow.WRAP_CONTENT);//设置宽度

        listPopupWindow1.setHeight(ListPopupWindow.WRAP_CONTENT);//设置高度

        listPopupWindow1.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.color_back_title)));//设置背景色

        listPopupWindow1.setAdapter(popupWindowAdapter);

        listPopupWindow1.setAnchorView(findViewById(R.id.select_version_upgrade_releate));

        listPopupWindow1.setModal(false);//设置为true响应物理键listPopupWindow.setHorizontalOffset(100);//垂直间距listPopupWindow.setVerticalOffset(100);//水平间距findViewById(R.id.popup).setOnClickListener(newView.OnClickListener() {


        listPopupWindow1.setOnItemClickListener(new AdapterView.OnItemClickListener() {//item 点击事件

            @Override

            public void onItemClick(AdapterView parent, View view, int position, long id) {
                select_version_upgrade_num1.setText(filenames.get(position));
                updatefilename1 = filenames.get(position);
                listPopupWindow1.dismiss();

            }

        });

    }

    private void getDowngradePopWindow() {
        dlistPopupWindow = new ListPopupWindow(this);

        dlistPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);//设置宽度

        dlistPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);//设置高度

        dlistPopupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.color_back_title)));//设置背景色

        dlistPopupWindow.setAdapter(popupWindowAdapter);

        dlistPopupWindow.setAnchorView(findViewById(R.id.select_version_downgrade_releate));

        dlistPopupWindow.setModal(false);//设置为true响应物理键listPopupWindow.setHorizontalOffset(100);//垂直间距listPopupWindow.setVerticalOffset(100);//水平间距findViewById(R.id.popup).setOnClickListener(newView.OnClickListener() {


        dlistPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {//item 点击事件

            @Override

            public void onItemClick(AdapterView parent, View view, int position, long id) {
                select_version_downgrade_num.setText(filenames.get(position));
                downgradefilename = filenames.get(position);
                dlistPopupWindow.dismiss();
            }

        });
    }

    private void getDowngradePopWindow1() {
        dlistPopupWindow1 = new ListPopupWindow(this);

        dlistPopupWindow1.setWidth(ListPopupWindow.WRAP_CONTENT);//设置宽度

        dlistPopupWindow1.setHeight(ListPopupWindow.WRAP_CONTENT);//设置高度

        dlistPopupWindow1.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.color_back_title)));//设置背景色

        dlistPopupWindow1.setAdapter(popupWindowAdapter);

        dlistPopupWindow1.setAnchorView(findViewById(R.id.select_version_downgrade_releate));

        dlistPopupWindow1.setModal(false);//设置为true响应物理键listPopupWindow.setHorizontalOffset(100);//垂直间距listPopupWindow.setVerticalOffset(100);//水平间距findViewById(R.id.popup).setOnClickListener(newView.OnClickListener() {


        dlistPopupWindow1.setOnItemClickListener(new AdapterView.OnItemClickListener() {//item 点击事件

            @Override

            public void onItemClick(AdapterView parent, View view, int position, long id) {
                select_version_downgrade_num1.setText(filenames.get(position));
                downgradefilename1 = filenames.get(position);
                dlistPopupWindow1.dismiss();
            }

        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonValue.isUpdateSecond = "ideal";
        EventBus.getDefault().unregister(UpdateActivity.this);
        BleScanner.getInstance().stopScan();
        BleScanner.getInstance().removeDeviceScanCallback(new BleScanner.OnDeviceScanCallback() {
            @Override
            public void onFind(ScannedDevice scannedDevice) {
                Log.d("yj", "remove--update--scannedDevice--");
            }
        });
        if (uHandler != null) {
            uHandler.removeCallbacksAndMessages(null);
        }
    }


}