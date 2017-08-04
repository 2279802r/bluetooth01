package com.bluetooth01.bluetooth01;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by ranjiaqing on 17/7/19.
 */

public class SelectDevice extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_CODE = 0x1003;

    private BluetoothAdapter mBluetoothAdapter;
    private Button mScanBtn;
    private ListView mDevList;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> mArrayAdapter = new ArrayList<String>();
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 请求显示进度条
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.bt_list);

        initView();

        // 打开并查找蓝牙设备
        openAndFindBTDevice();

        // 用来接受设备查找到的广播和扫描完成的广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // 动态注册广播接收器
        // 用来接受扫描到的设备信息
        registerReceiver(mReceiver, filter);
    }

    private void initView(){
        mDevList = (ListView) findViewById(R.id.scanDevList);

        mDevList.setOnItemClickListener(this);

        mScanBtn = (Button) findViewById(R.id.scanDevBtn);
        mScanBtn.setOnClickListener(this);

        adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                mArrayAdapter);

        mDevList.setAdapter(adapter);
    }

    private void openAndFindBTDevice(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Your device is not support Bluetooth!");
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_CODE);
        }else{
            findBTDevice();
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 扫描到新的蓝牙设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 获得蓝牙设备对象
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //防止设备对象添加重复
                if(mDeviceList.contains(device)){
                    return;
                }
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                System.out.println(device.getName() + "\n" + device.getAddress());
                mDeviceList.add(device);
                adapter.notifyDataSetChanged();
            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                // 扫描完成，关闭显示进度条
                setProgressBarIndeterminateVisibility(false);
            }
        }
    };

    @Override
    public void onClick(View v) {
        if(!mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.startDiscovery();
            setProgressBarIndeterminateVisibility(true);
        }
    }

    private void findBTDevice(){
        // 用来保存已配对的蓝牙设备
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                // 将已配对的设备信心添加到ListView中
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mDeviceList.add(device);
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_CODE){
            if(resultCode ==  RESULT_OK){
                System.out.println("Device open successfully");
                findBTDevice();
            }else{
                System.out.println("Device open failed");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        String targetDev = mArrayAdapter.get(arg2);
        System.out.println(targetDev);
        Intent data = new Intent();
        data.putExtra("DEVICE", mDeviceList.get(arg2));
        // 当用户点击某项设备时，将该设备对象返回给调用着（MainActivity）
        setResult(RESULT_OK, data);
        this.finish();
    }
}
