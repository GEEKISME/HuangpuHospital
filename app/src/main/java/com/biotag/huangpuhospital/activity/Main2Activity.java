package com.biotag.huangpuhospital.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseBackgroundFormat;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.table.PageTableData;
import com.biotag.huangpuhospital.R;
import com.biotag.huangpuhospital.common.SharedPreferencesUtils;
import com.biotag.huangpuhospital.common.ThreadManager;
import com.biotag.huangpuhospital.javabean.AssetDetailBean;
import com.biotag.huangpuhospital.javabean.AssetSummaryBean;
import com.biotag.huangpuhospital.javabean.SingleItemInfoBean;
import com.biotag.huangpuhospital.javabean.SingleItemInfoBean_;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.Query;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    private MainTwoHandler mainTwoHandler;

    private long[] pattern = {100, 400, 100, 400};

//    private TextView text_count;

//    private XRecyclerView content_xrcv;

    private ProgressDialog progressDialog;
    private Map<String, Integer> epc2num = new ConcurrentHashMap<String, Integer>();
    private int readCount = 0;
//    private ContentListAdapter contentListAdapter;
    private MediaPlayer findEpcSound;
    private AudioManager audioManager;
//    private BluetoothAdapter mBluetoothAdapter;
//    private BluetoothDevice device;
//    private Timer timer = new Timer();
//    private BluetoothSocket btSocket1 = null;
//    private final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
//    private UUID uuid;
//    private Box<SingleItemInfoBean> itemsBox;
    private Button btn_scan;
    private Button btn_summaryasset;
    private Button btn_config;
    private Button btn_checkinstrument;
    private Button btn_reload;

    private ArrayList<Integer> checked_rows = new ArrayList<>();

    private TextView tv_currentdeviceinfo;
    private TextView tv_assetserialnum_value;
    private TextView tv_assetname_value;
    private TextView tv_datepurchase_value;
    private TextView tv_placestored_value;
    private MyService myService;

    private Box<SingleItemInfoBean> itemInfoBeanBox;
    //summary
    private SmartTable<AssetSummaryBean> table_sum;
    private PageTableData<AssetSummaryBean> tableDatasum;
    private ArrayList<AssetSummaryBean> sumList = new ArrayList<>();
    //detail
    private SmartTable<AssetDetailBean> table_detail;
    private PageTableData<AssetDetailBean> tableDatadetail;
    private ArrayList<AssetDetailBean> detailList = new ArrayList<>();
    //====================================================================
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            myService = ((MyService.MyBinder)iBinder).getMyService();
            myService.setAddress(address);
            myService.setEpcNumberInterface(new EpcNumberInterface() {
                @Override
                public void epcinfo(String color, String name) {
                    Bundle bundle = new Bundle();
                    bundle.putString("color",color);
                    bundle.putString("name",name);
                    Message message = Message.obtain();
                    message.what = 1;
                    message.setData(bundle);
                    mainTwoHandler.sendMessage(message);

                }
            });
            myService.startScanBluetooth();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    private String address  ;
    private String devicename;
    //==========================================
    private RelativeLayout titlebar_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        BoxStore boxStore =((HuangpuApplication) getApplication()).getBoxStore();
        itemInfoBeanBox = boxStore.boxFor(SingleItemInfoBean.class);

        initView();
        //=====================================================


//        uuid = UUID.fromString(SPP_UUID);
//        Intent intent = getIntent();
//        address = intent.getStringExtra("address");
//        devicename = intent.getStringExtra("devicename");
        address = SharedPreferencesUtils.getString(this,"deviceaddress","");
        devicename= SharedPreferencesUtils.getString(this,"devicename","");
        if(address.equals("")){
            Toast.makeText(Main2Activity.this, "没有选择蓝牙设备", Toast.LENGTH_SHORT).show();
        }
        String deviceinfo = new StringBuilder().append("名称:").append(devicename).toString();
        tv_currentdeviceinfo.setText(deviceinfo);
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        device = mBluetoothAdapter.getRemoteDevice(address);

        mainTwoHandler = new MainTwoHandler(this);
//        BoxStore boxStore = ((HuangpuApplication) getApplication()).getBoxStore();
//        itemsBox = boxStore.boxFor(SingleItemInfoBean.class);

        Intent intent1 = new Intent(this,MyService.class);
        intent1.putExtra("address",address);
        intent1.putExtra("devicename",devicename);
        bindService(intent1,sc,Context.BIND_AUTO_CREATE);

    }

    private void initView() {

//        text_count = (TextView) findViewById(R.id.text_count);
//        content_xrcv = (XRecyclerView) findViewById(R.id.content_xrcv);

        //
        titlebar_scan = findViewById(R.id.titlebar_scan);
        TextView activity_title = titlebar_scan.findViewById(R.id.activity_title);
        activity_title.setText("扫描");
        ImageView iv_back = titlebar_scan.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //===========================
        table_sum = findViewById(R.id.table_sum);
        table_detail = findViewById(R.id.table_detail);
        progressDialog = new ProgressDialog(this);
        //progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);

//        contentListAdapter = new ContentListAdapter(epc2num, this);
//        content_xrcv.setAdapter(contentListAdapter);

        //
        findEpcSound = new MediaPlayer();
        // mediaPlayer01.start();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(this);
        btn_summaryasset = (Button) findViewById(R.id.btn_check);
        btn_summaryasset.setOnClickListener(this);
        tv_currentdeviceinfo = (TextView) findViewById(R.id.tv_currentdeviceinfo);
        btn_config = findViewById(R.id.btn_config);
        btn_config.setOnClickListener(this);
        btn_checkinstrument = findViewById(R.id.btn_setting);
        btn_checkinstrument.setOnClickListener(this);
        btn_reload = findViewById(R.id.btn_reload);
        btn_reload.setOnClickListener(this);
        tv_assetserialnum_value = findViewById(R.id.tv_assetserialnum_value);
        tv_assetname_value = findViewById(R.id.tv_assetname_value);
        tv_datepurchase_value = findViewById(R.id.tv_datepurchase_value);
        tv_placestored_value = findViewById(R.id.tv_placestored_value);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan://搜寻蓝牙设备并尝试连接设备
                myService.startScanBluetooth();
                break;
            case R.id.btn_check:
                Intent intent = new Intent(this, SummaryActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_config:
                SharedPreferencesUtils.saveString(this,"deviceaddress","");
                SharedPreferencesUtils.saveString(this,"devicename","");
                startActivity(new Intent(this,SearchDeviceActivity.class));
                break;
            case R.id.btn_setting:
                Intent intent1 = new Intent(this,InventoryingActivity.class);
                intent1.putExtra("address",address);
                intent1.putExtra("devicename",devicename);
                startActivity(intent1);
                break;
            case R.id.btn_reload:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("");
                builder.setMessage("确定要重新读取资产文件吗？");//提示内容
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refreshDatabase();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
    }

    private void refreshDatabase() {
        BoxStore boxStore =((HuangpuApplication) getApplication()).getBoxStore();
        itemInfoBeanBox = boxStore.boxFor(SingleItemInfoBean.class);
        itemInfoBeanBox.removeAll();
        SharedPreferencesUtils.saveBoolean(this,"areDataStored",false);
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
                        itemInfoBeanBox.put(singleItemInfoBean);
                    }
                    SharedPreferencesUtils.saveBoolean(Main2Activity.this,"areDataStored",true);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Main2Activity.this, "数据库更新完成~", Toast.LENGTH_SHORT).show();
                        }
                    });
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
                        SharedPreferencesUtils.saveString(Main2Activity.this,"txtfilename",filename);
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
        }catch(Exception e){
            e.printStackTrace();
        }
        return "读取失败";
    }
    private void refreshTableData(String assetSerial,String placeStored){
        List<SingleItemInfoBean> alllist = itemInfoBeanBox.getAll();

        Query assetIdQuery = itemInfoBeanBox.query().equal(SingleItemInfoBean_.assetsSerialNum,assetSerial).build();
        ArrayList<SingleItemInfoBean> assetIdQueryResultList = (ArrayList<SingleItemInfoBean>) assetIdQuery.find();
        int size = assetIdQueryResultList.size();
        if(size == 1){
            SingleItemInfoBean singleItemInfoBean = assetIdQueryResultList.get(0);
            String assetPlace = singleItemInfoBean.getPlaceStored();
            int checkresult = singleItemInfoBean.getCheckResult();
            if (checkresult == 1) {
                Toast.makeText(this, "该资产已经盘点过了！", Toast.LENGTH_SHORT).show();
            }
            long currentmill = System.currentTimeMillis();
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
            String ymdhms = sdf2.format(currentmill);
            singleItemInfoBean.setCheckDate(ymdhms);
            singleItemInfoBean.setCheckResult(1);
            itemInfoBeanBox.put(singleItemInfoBean);
            //===================================
            Query assetPlaceQuery_check = itemInfoBeanBox.query().equal(SingleItemInfoBean_.placeStored, assetPlace).equal(SingleItemInfoBean_.checkResult,1).build();
            Query assetPlaceQuery_uncheck = itemInfoBeanBox.query().equal(SingleItemInfoBean_.placeStored, assetPlace).equal(SingleItemInfoBean_.checkResult,2).build();
            ArrayList<SingleItemInfoBean> assetPlaceQueryResultList_check = (ArrayList<SingleItemInfoBean>) assetPlaceQuery_check.find();
            ArrayList<SingleItemInfoBean> assetPlaceQueryResultList_uncheck = (ArrayList<SingleItemInfoBean>) assetPlaceQuery_uncheck.find();
            //===================================================================
            for (SingleItemInfoBean siib:assetPlaceQueryResultList_check) {
                AssetDetailBean adb = new AssetDetailBean();
                adb.setAssetName(siib.getAssetsName());
                adb.setAssetSerialNum(siib.getAssetsSerialNum());
                adb.setCheckOrUncheck("已盘点");
                detailList.add(adb);
            }
            for (SingleItemInfoBean siibs:assetPlaceQueryResultList_uncheck) {
                AssetDetailBean adb = new AssetDetailBean();
                adb.setAssetName(siibs.getAssetsName());
                adb.setAssetSerialNum(siibs.getAssetsSerialNum());
                adb.setCheckOrUncheck("未盘点");
                detailList.add(adb);
            }
            //===================================================================
            int checkedsize = assetPlaceQueryResultList_check.size();
            int uncheckedsize = assetPlaceQueryResultList_uncheck.size();
            int total = checkedsize+uncheckedsize;
            AssetSummaryBean asb  = new AssetSummaryBean();
            asb.setPlaceStored(assetPlace);
            asb.setTotal(total);
            asb.setCheckedNum(checkedsize);
            asb.setUncheckedNum(uncheckedsize);
            sumList.add(asb);
            table_sum.getConfig().setCountBackground(new BaseBackgroundFormat(getResources().getColor(R.color.green)))
                    .setShowXSequence(false).setShowYSequence(false);
            table_sum.getConfig().setVerticalPadding(40);
            table_sum.getConfig().setHorizontalPadding(40);
            final Column<String> placeStored_column = new Column<String>("存放地", "placeStored");
            final Column<String> total_column = new Column<String>("资产数", "total");
            final Column<String> checkedNum_column = new Column<String>("盘点数", "checkedNum");
            final Column<String> uncheckedNum_column = new Column<String>("未盘数目", "uncheckedNum");
            tableDatasum = new PageTableData<AssetSummaryBean>("",sumList,placeStored_column,total_column,checkedNum_column,uncheckedNum_column);
            table_sum.setTableData(tableDatasum);
            //===================
            table_detail.getConfig().setCountBackground(new BaseBackgroundFormat(getResources().getColor(R.color.green)))
                    .setShowXSequence(false).setShowYSequence(false);
            table_detail.getConfig().setVerticalPadding(40);
            table_detail.getConfig().setHorizontalPadding(40);
            final Column<String> assetSerialNum_column = new Column<String>("资产编号", "assetSerialNum");
            final Column<String> assetName_column = new Column<String>("资产名称", "assetName");
            final Column<String> checkOrUncheck = new Column<String>("盘点状态", "checkOrUncheck");

            tableDatadetail = new PageTableData<AssetDetailBean>("",detailList,assetSerialNum_column,assetName_column,checkOrUncheck);
            table_detail.setTableData(tableDatadetail);
            table_detail.getConfig().setContentCellBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {
                @Override
                public int getBackGroundColor(CellInfo cellInfo) {
                    if(cellInfo.data.equals(new String("已盘点"))){
                        checked_rows.add(cellInfo.row);
                        return ContextCompat.getColor(Main2Activity.this,R.color.green);
                    }else if(cellInfo.data.equals("未盘点")){
                        return ContextCompat.getColor(Main2Activity.this,R.color.red);
                    }
                    return 0;
                }
            });

            table_detail.getConfig().setContentCellBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {
                @Override
                public int getBackGroundColor(CellInfo cellInfo) {
                    if(checked_rows.contains(cellInfo.row)){
                        return ContextCompat.getColor(Main2Activity.this,R.color.lightpink);
                    }else {
                        return ContextCompat.getColor(Main2Activity.this,R.color.green);
                    }
                }
            });
        }
    }





    static class MainTwoHandler extends Handler {
        WeakReference<Main2Activity> main2ActivityWeakReference;

        public MainTwoHandler(Main2Activity main2Activity) {
            main2ActivityWeakReference = new WeakReference<>(main2Activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Main2Activity main2Activity = main2ActivityWeakReference.get();
            if (main2Activity != null) {
                switch (msg.what) {
                    case 1:
                        Bundle bundle = msg.getData();
                        String tempstr = bundle.getString("color");//tempstr  是 epc编号 01201809230004
                        String name = bundle.getString("name");//  name是     资产信息   联想笔记本|2018-07-23|信息科办公室
                        main2Activity.tv_assetserialnum_value.setText(tempstr);
                        String[] namePart = name.split("\\|");

                        if (namePart.length==3) {
                            main2Activity.tv_assetname_value.setText(namePart[0]);
                            main2Activity.tv_datepurchase_value.setText(namePart[1]);
                            main2Activity.tv_placestored_value.setText(namePart[2]);
                            main2Activity.refreshTableData(tempstr,namePart[2]);
                        }else {
                            main2Activity.tv_assetserialnum_value.setText("");
                            main2Activity.tv_assetname_value.setText("");
                            main2Activity.tv_datepurchase_value.setText("");
                            main2Activity.tv_placestored_value.setText("");
                            Toast.makeText(main2Activity, "标签读取失败，请再试一次吧~", Toast.LENGTH_SHORT).show();
                        }

//                        main2Activity.dbOperation(tempstr, name);
                        break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (btSocket1 != null) {
//            try {
//                btSocket1.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        unbindService(sc);
    }






//    class MyTask extends TimerTask {
//
//        @Override
//        public void run() {
//            Bundle bundle = new Bundle();
//            Message msg = Message.obtain();
//            if (mBluetoothAdapter == null) {
//                return;
//            }
//            try {
//                btSocket1 = device.createRfcommSocketToServiceRecord(uuid);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                return;
//            }
//            try {
//                btSocket1.connect();
//            } catch (IOException e) {
//                e.printStackTrace();
//                msg.what = 2;
//                mainTwoHandler.sendMessage(msg);
//            }
//
//            while (true) {
//                if (btSocket1.getRemoteDevice() == null) {
//                    return;
//                }
//
//
//                try {
//                    byte[] temp = new byte[250];// 最后一个编号是249
//                    btSocket1.getOutputStream();
//                    InputStream inStream = btSocket1.getInputStream();
//                    int count = inStream.available();
//                    if (count > 0) {
//                        int byteread = inStream.read(temp);
//                        byte[] epcnobyte = new byte[16];
//                        byte[] namebyte = new byte[213];
//                        System.arraycopy(temp, 12, epcnobyte, 0, 16);
//                        System.arraycopy(temp, 37, namebyte, 0, 213);
//
//                        String str1 = CommonMethod.bytesToHexString(epcnobyte);
//                        String str2 = CommonMethod.bytesToHexString(namebyte);
//                        str1 = str1.split("0000")[0];
//                        str2 = CommonMethod.hex2straboutinfo(str2);
//
//                        bundle.putString("color", str1);
//                        bundle.putString("name", str2);
//
//                        Log.i("tms", str1);
//                        Log.i("tms", str2);
//
//                        msg = Message.obtain();
//                        msg.what = 1;
//                        msg.setData(bundle);
//                        mainTwoHandler.sendMessage(msg);
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//        }
//
//
//
//
//    }


}
