package com.biotag.huangpuhospital.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biotag.huangpuhospital.R;
import com.biotag.huangpuhospital.adapter.DeviceListAdapter;
import com.biotag.huangpuhospital.common.Log;
import com.biotag.huangpuhospital.common.SharedPreferencesUtils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.kyleduo.switchbutton.SwitchButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {
    private String address  ;
    private String devicename;
    private RelativeLayout titlebar;
    private TextView tv_tipinfo;
    private SwitchButton switchbutton;
    private BluetoothAdapter mBluetoothAdapter;
    private XRecyclerView xrcv;
    private DeviceListAdapter deviceListAdapter;
    public ArrayList<String> deviceArraylist = new ArrayList<>();
    //==========service 相关

    //=================================================================
    private MainHander mainHander;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String devicename = device.getName();
                    if(devicename!=null&&devicename.trim().length()>0&&!hashMap.containsKey(devicename)){
                        //hashmap 确保蓝牙设备只被发现一次
                        deviceArraylist.add(devicename);
                        hashMap.put(devicename,device.getAddress());
                        deviceListAdapter.notifyDataSetChanged();
                    }

                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    progressDialog.setMessage("Scaning....");
                    progressDialog.show();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    progressDialog.setMessage("Scanning over....");
                    progressDialog.dismiss();
                    break;
            }
        }
    };
    private HashMap<String, String> hashMap = new HashMap<String, String>();
    private ProgressDialog progressDialog;
    private LinearLayout ll_bindedlist;
    private TextView txt_device_mac;

    //=================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initview();
        //==============================================
        //==============================================
        deviceListAdapter = new DeviceListAdapter(deviceArraylist,this);
        deviceListAdapter.setListItemClickListener(new DeviceListAdapter.ListItemClickListener() {
            @Override
            public void clickListener(int position) {
                if(deviceArraylist.size()>0){
                    devicename = deviceArraylist.get(position);
                    address = hashMap.get(devicename);
                    // 存储下连接的地址
                    SharedPreferencesUtils.saveString(SettingActivity.this,"deviceaddress",address);
                    SharedPreferencesUtils.saveString(SettingActivity.this,"devicename",devicename);
                    Toast.makeText(SettingActivity.this, "蓝牙地址已经存储~", Toast.LENGTH_SHORT).show();
                    android.util.Log.i("tms","add is:"+address);
                }
            }
        });
        xrcv.setAdapter(deviceListAdapter);
        mainHander = new MainHander(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(receiver, filter);
    }

    private void initPermission() {
        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //申请了两种权限：WRITE_EXTERNAL_STORAGE与 CAMERA 权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,}, 60);
            return;
        }
    }

    private void initview() {
        ll_bindedlist = findViewById(R.id.ll_bindedlist);
        txt_device_mac = findViewById(R.id.txt_device_mac);
        String devicename = SharedPreferencesUtils.getString(this,"devicename","");
        if(devicename.equals("")){
            ll_bindedlist.setVisibility(View.INVISIBLE);
        }else {
            ll_bindedlist.setVisibility(View.VISIBLE);
            txt_device_mac.setText(devicename);
        }
        ll_bindedlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingActivity.this, "蓝牙地址已经存储，可以扫描标签了~", Toast.LENGTH_SHORT).show();
            }
        });

        xrcv = findViewById(R.id.xrcv);
        xrcv.setLayoutManager(new LinearLayoutManager(this));
        titlebar = findViewById(R.id.titlebar);
        ImageView iv_back = titlebar.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView activity_title = titlebar.findViewById(R.id.activity_title);
        activity_title.setText("设置");
        tv_tipinfo = findViewById(R.id.tv_tipinfo);
        switchbutton = findViewById(R.id.switchbutton);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            switchbutton.setChecked(true);
        }else if(!mBluetoothAdapter.isEnabled()){
            switchbutton.setChecked(false);
        }
        switchbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischeck) {
                if(ischeck){
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 0);//打开“设置”中的蓝牙界面
                }
                if(!ischeck){
                    Intent intent =  new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivity(intent);
                }
            }
        });

        final Button btn_search  = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if(mBluetoothAdapter==null){
                    bluetoothnotsupport();
                    switchbutton.setChecked(false);
                    return;
                }
                if(!mBluetoothAdapter.isEnabled()){
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 0);//打开“设置”中的蓝牙界面
                    return;
                }
                String texttemp = btn_search.getText().toString();
                if(texttemp.equals("查找设备")){
                    if(mBluetoothAdapter!=null){
                        mBluetoothAdapter.startDiscovery();
                        btn_search.setText("停止搜索");
                    }
                }else if(texttemp.equals("停止搜索")){
                    if(mBluetoothAdapter!=null){
                        mBluetoothAdapter.cancelDiscovery();
                        btn_search.setText("查找设备");
                    }
                }

            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if(mBluetoothAdapter!=null){
                    mBluetoothAdapter.cancelDiscovery();
                }
            }
        });

    }

    private void bluetoothnotsupport() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Warning")
                .setMessage("BlueTooth not supported on this device !")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        Log.i("tms","onresume");
        if(mBluetoothAdapter.isEnabled()){
            switchbutton.setChecked(true);
        }else if(!mBluetoothAdapter.isEnabled()){
            switchbutton.setChecked(false);
        }
        super.onResume();
    }

    //=============================================================
    static class MainHander extends Handler {
        WeakReference<SettingActivity> settingActivityWeakReference;

        public MainHander(SettingActivity settingActivity) {
            settingActivityWeakReference = new WeakReference<>(settingActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SettingActivity settingActivity = settingActivityWeakReference.get();
            if (settingActivity != null) {
                switch (msg.what) {

                }
            }
        }
    }
}
