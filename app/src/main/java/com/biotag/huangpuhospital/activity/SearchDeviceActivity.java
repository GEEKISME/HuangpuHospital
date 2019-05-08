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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;

import com.biotag.huangpuhospital.R;
import com.biotag.huangpuhospital.adapter.DeviceListAdapter;
import com.biotag.huangpuhospital.common.SharedPreferencesUtils;
import com.biotag.huangpuhospital.common.ThreadManager;
import com.biotag.huangpuhospital.javabean.SingleItemInfoBean;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import io.objectbox.Box;
import io.objectbox.BoxStore;

public class SearchDeviceActivity extends AppCompatActivity {


    private XRecyclerView xrcv;
    private Button btn_search;
    private BluetoothAdapter mBluetoothAdapter;
    public  ArrayList<String> deviceArraylist = new ArrayList<>();
    private DeviceListAdapter deviceListAdapter;
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
    private boolean hasRequetBt = false;
    private Box<SingleItemInfoBean> itemsBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchdevice);
        initPermission();
        BoxStore boxStore = ((HuangpuApplication)getApplication()).getBoxStore();
        itemsBox = boxStore.boxFor(SingleItemInfoBean.class);
        boolean areDataStored = SharedPreferencesUtils.getBoolean(this,"areDataStored",false);
        if(!areDataStored){
            initObjectBox();
        }
        initView();
        deviceListAdapter = new DeviceListAdapter(deviceArraylist,this);
        deviceListAdapter.setListItemClickListener(new DeviceListAdapter.ListItemClickListener() {
            @Override
            public void clickListener(int position) {
                if(deviceArraylist.size()>0){
                    Intent intent = new Intent(SearchDeviceActivity.this,Main2Activity.class);
                    String devicename = deviceArraylist.get(position);
                    String address = hashMap.get(devicename);
                    intent.putExtra("address",address);
                    intent.putExtra("devicename",devicename);
                    // 存储下连接的地址
                    SharedPreferencesUtils.saveString(SearchDeviceActivity.this,"deviceaddress",address);
                    SharedPreferencesUtils.saveString(SearchDeviceActivity.this,"devicename",devicename);
                    startActivity(intent);
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

    public void initObjectBox() {
        ThreadManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                String textString = getTextString();
                if(!textString.equals("读取失败")){
                    String[] items = textString.split("\n");
                    int size = items.length;
                    String singleitem;
                    String[] itemPart;
                    for (int i = 0; i <size ; i++) {
                        singleitem = items[i];
                        itemPart = singleitem.split("\\|");
                        SingleItemInfoBean singleItemInfoBean = new SingleItemInfoBean();
                        singleItemInfoBean.setInventoryId(Integer.parseInt(itemPart[0]));
                        singleItemInfoBean.setAssetId(Integer.parseInt(itemPart[1]));
                        singleItemInfoBean.setAssetsSerialNum(itemPart[2]);
                        singleItemInfoBean.setAssetsName(itemPart[3]);
                        singleItemInfoBean.setDatePurchased(itemPart[4]);
                        singleItemInfoBean.setDepartmentId(Integer.parseInt(itemPart[5]));
                        singleItemInfoBean.setDepartmentName(itemPart[6]);
                        singleItemInfoBean.setPlaceStored(itemPart[7]);
                        singleItemInfoBean.setKeeper(itemPart[8]);
                        singleItemInfoBean.setCheckResult(Integer.parseInt(itemPart[9]));
                        singleItemInfoBean.setCheckDate(itemPart[10]);
                        itemsBox.put(singleItemInfoBean);
                    }
                    SharedPreferencesUtils.saveBoolean(SearchDeviceActivity.this,"areDataStored",true);

                }
            }
        });
    }

    private String getTextString() {
        try {
            ///SheetdetailInfo_20181016_1.txt
            //从约定好的路径下读取资产文件
            File targetFile = new File(Environment.getExternalStorageDirectory() + "/HRP/");
            if(targetFile.isDirectory()){
                File[] filearray = targetFile.listFiles();
                int length = filearray.length;
                for (int i = 0; i < length; i++) {
                    File tempfile = filearray[i];
                    String filename = tempfile.getName();
                    if(filename.startsWith("Sheet")&&!filename.contains("Android")){
                        InputStream is = new FileInputStream(tempfile);
                        int size = is.available();
                        byte[] buffer = new byte[size];
                        is.read(buffer);
                        is.close();
                        String textString = new String(buffer,"gbk");
                        return textString;
                    }
                }
            }

//            if(targetFile.exists()){
//                InputStream is = new FileInputStream(targetFile);
//                int size = is.available();
//                byte[] buffer = new byte[size];
//                is.read(buffer);
//                is.close();
//                String textString = new String(buffer,"gbk");
//                return textString;
//            }


//            InputStream is = getAssets().open("SheetdetailInfo_20181016_1.txt");
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            String textString = new String(buffer,"gbk");
//            return textString;
        }catch(Exception e){
            e.printStackTrace();
        }
        return "读取失败";
    }


    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if(mBluetoothAdapter!=null){
            mBluetoothAdapter.cancelDiscovery();
        }
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
    private void initView() {
        xrcv = (XRecyclerView) findViewById(R.id.xrcv);
        xrcv.setLayoutManager(new LinearLayoutManager(this));
        btn_search = (Button) findViewById(R.id.btn_search);


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if(mBluetoothAdapter == null){
                    bluetoothnotsupport();
                    return;
                }
                if (!mBluetoothAdapter.isEnabled()&&!hasRequetBt ) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 0);//打开“设置”中的蓝牙界面
                    hasRequetBt = true;
                    return;
                }
                String text_temp = btn_search.getText().toString();
                if(text_temp.equals("搜索设备")){

                    if (mBluetoothAdapter!=null) {
                        mBluetoothAdapter.startDiscovery();
                        btn_search.setText("停止搜索");
                    }

                }else if(text_temp.equals("停止搜索")){
                    if(mBluetoothAdapter!=null){
                        mBluetoothAdapter.cancelDiscovery();
                        btn_search.setText("搜索设备");
                    }
                }

            }
        });

        progressDialog = new ProgressDialog(this);
        //progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                if (mBluetoothAdapter != null) {
                    mBluetoothAdapter.cancelDiscovery();
                }
            }
        });
        checkBluetooth();


    }

    private void checkBluetooth() {

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



    //============================================================


    static class MainHander extends Handler {
        WeakReference<SearchDeviceActivity> mainActivityWeakReference;

        public MainHander(SearchDeviceActivity searchDeviceActivity) {
            mainActivityWeakReference = new WeakReference<>(searchDeviceActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SearchDeviceActivity searchDeviceActivity = mainActivityWeakReference.get();
            if (searchDeviceActivity != null) {
                switch (msg.what) {

                }
            }
        }
    }


}
