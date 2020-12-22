package com.example.connectapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.connectapplication.constans.CommonValue;
import com.example.connectapplication.utils.ScanBlueTooth;
import com.ryeex.ble.connector.scan.BleScanner;
import com.ryeex.ble.connector.scan.ScannedDevice;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SelectDeviceActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView iv_back;

    private TextView iv_discovery;

    private RecyclerView listView;

    //private List<DeviceModel> deviceModelList = new ArrayList<>();

    private DeviceScanAdapter deviceAdapter;

    private List<ScannedDevice> scannedDeviceList = new ArrayList<>();
    private List<ScannedDevice> scannedShowDeviceList = new ArrayList<>();

    private final int MSG_REFRESH_UI = 1;

    private final int REQUEST_OK = 1;

    Handler mUiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_REFRESH_UI:
                    scannedShowDeviceList.clear();
                    scannedShowDeviceList.addAll(scannedDeviceList);
                    if (!scannedShowDeviceList.isEmpty()) {
                        Collections.sort(scannedShowDeviceList, new Comparator<ScannedDevice>() {
                            @Override
                            public int compare(ScannedDevice o1, ScannedDevice o2) {
                                return o2.getRssi() - o1.getRssi();
                            }
                        });
                        deviceAdapter.notifyDataSetChanged();
                    }
                    sendEmptyMessageDelayed(MSG_REFRESH_UI, 3000);
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_device);
        init();
        BleScanner.getInstance().addDeviceScanCallback(new BleScanner.OnDeviceScanCallback() {
            @Override
            public void onFind(ScannedDevice scannedDevice) {
                if (CommonValue.isSecond.equals("secondisworked")||CommonValue.isNotificationAutoSecond.equals("secondisworked")||CommonValue.isNotificationManualSecond.equals("secondisworked")
                    ||CommonValue.isSyncDataAutoSecond.equals("secondisworked")||CommonValue.isIsSyncDataManualSecond.equals("secondisworked")||CommonValue.isConnectedNOConfirmSsecond.equals("secondisworked")
                     ||CommonValue.isUpdateSecond.equals("secondisworked")||CommonValue.isLongBlueTooth.equals("secondisworked")) {
                    Iterator<ScannedDevice> iterator = scannedDeviceList.iterator();
                    while (iterator.hasNext()) {
                        ScannedDevice scannedDeviceTemp = iterator.next();
                        if (scannedDeviceTemp.getMac().equals(scannedDevice.getMac())) {
                            iterator.remove();
                            break;
                        }
                    }

                    if (!scannedDeviceList.contains(scannedDevice)) {
                        scannedDeviceList.add(scannedDevice);
                    }

                    if (scannedShowDeviceList.isEmpty()) {
                        mUiHandler.sendEmptyMessage(MSG_REFRESH_UI);
                    }
                }
            }
        });
    }

    private void init() {
        iv_back = findViewById(R.id.select_back);
        iv_discovery = findViewById(R.id.connect_discover);
        listView = findViewById(R.id.ble_listview);
        iv_back.setOnClickListener(this);
        iv_discovery.setOnClickListener(this);
        listView.setLayoutManager(new LinearLayoutManager(this));
        deviceAdapter = new DeviceScanAdapter(scannedShowDeviceList);
        listView.setAdapter(deviceAdapter);
        listView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.color_divider_bg))
                .sizeResId(R.dimen.divider)
                .build());
        deviceAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (position >= 0 && position < scannedShowDeviceList.size()) {
                    //bindDevice(scannedShowDeviceList.get(position));
                    if(CommonValue.isSecond.equals("secondisworked"))
                        CommonValue.isSecond = "worked";
                    if(CommonValue.isNotificationAutoSecond.equals("secondisworked"))
                        CommonValue.isNotificationAutoSecond = "worked";
                    if(CommonValue.isNotificationManualSecond.equals("secondisworked"))
                        CommonValue.isNotificationManualSecond = "worked";
                    if(CommonValue.isSyncDataAutoSecond.equals("secondisworked"))
                        CommonValue.isSyncDataAutoSecond = "worked";
                    if(CommonValue.isIsSyncDataManualSecond.equals("secondisworked"))
                        CommonValue.isIsSyncDataManualSecond = "worked";
                    if(CommonValue.isConnectedNOConfirmSsecond.equals("secondisworked"))
                        CommonValue.isConnectedNOConfirmSsecond = "worked";
                    if(CommonValue.isUpdateSecond.equals("secondisworked"))
                        CommonValue.isUpdateSecond = "worked";
                    if(CommonValue.isLongBlueTooth.equals("secondisworked"))
                        CommonValue.isLongBlueTooth = "worked";
                    BleScanner.getInstance().stopScan();
                    Intent in = new Intent();
                    in.setClass(SelectDeviceActivity.this, BlueToothConnectActivity.class);
                    in.putExtra("discover_mac", scannedShowDeviceList.get(position).getMac());
                    setResult(RESULT_OK, in);
                    finish();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_back:
                finish();
                break;
            case R.id.connect_discover:
                Toast.makeText(SelectDeviceActivity.this, getResources().getString(R.string.connect_select_device_begin_scan), Toast.LENGTH_LONG).show();
                ScanBlueTooth.startScan(SelectDeviceActivity.this);
                break;
        }
    }

    public class DeviceScanAdapter extends BaseQuickAdapter<ScannedDevice, BaseViewHolder> {


        DeviceScanAdapter(List<ScannedDevice> scannedDeviceList) {
            super(R.layout.select_device_item, scannedDeviceList);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, ScannedDevice deviceModel) {
            String mac = deviceModel.getMac();
            int info = deviceModel.getRssi();

            baseViewHolder.setText(R.id.device_mac, mac);
            baseViewHolder.setText(R.id.device_info, info + "");
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (mUiHandler != null) {
            mUiHandler.removeCallbacksAndMessages(null);
        }
    }
}
