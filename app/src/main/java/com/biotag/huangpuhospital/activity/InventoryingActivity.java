package com.biotag.huangpuhospital.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.biotag.huangpuhospital.R;
import com.biotag.huangpuhospital.adapter.ContentListAdapter;
import com.biotag.huangpuhospital.javabean.SingleItemInfoBean;
import com.biotag.huangpuhospital.javabean.SingleItemInfoBean_;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.Query;

public class InventoryingActivity extends AppCompatActivity implements View.OnClickListener {

//    private TextView tv_currentdeviceinfo;
    private Button btn_inveSummary;
    private TextView tv_assetserialnum;
    private TextView tv_assetserialnum_value;
    private TextView tv_assetname;
    private TextView tv_assetname_value;
    private TextView tv_datepurchase;
    private TextView tv_datepurchase_value;
    private TextView tv_placestored;
    private TextView tv_placestored_value;
    private CardView tv_info;
    private View view2;
    private InventoryingActivityHandler handler;
    private Box<SingleItemInfoBean> itemsBox;
    private ContentListAdapter adapter = null;
    private ArrayList<SingleItemInfoBean> itemSpeciedList = new ArrayList<>();
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService myService = ((MyService.MyBinder) iBinder).getMyService();
            myService.startScanBluetooth();
            myService.setEpcNumberInterface(new EpcNumberInterface() {
                @Override
                public void epcinfo(String color, String name) {
                    Bundle bundle = new Bundle();
                    bundle.putString("color", color);
                    bundle.putString("name", name);
                    Message message = Message.obtain();
                    message.what = 1;
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    private XRecyclerView xrcv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventorying);

        initView();
        Intent intent = getIntent();
        String address = intent.getStringExtra("address");
        String devicename = intent.getStringExtra("devicename");
        String deviceinfo = new StringBuilder().append("设备名称:").append(devicename).toString();
//        tv_currentdeviceinfo.setText(deviceinfo);
        handler = new InventoryingActivityHandler(this);

        Intent intent1 = new Intent(this, MyService.class);
        intent1.putExtra("address", address);
        bindService(intent1, sc, Context.BIND_AUTO_CREATE);

        BoxStore boxStore = ((HuangpuApplication) getApplication()).getBoxStore();
        itemsBox = boxStore.boxFor(SingleItemInfoBean.class);
        adapter = new ContentListAdapter(itemSpeciedList, this);
        xrcv.setAdapter(adapter);
    }

    private void initView() {
//        tv_currentdeviceinfo = (TextView) findViewById(R.id.tv_currentdeviceinfo);
        btn_inveSummary = (Button) findViewById(R.id.btn_inveSummary);
        tv_assetserialnum = (TextView) findViewById(R.id.tv_assetserialnum);
        tv_assetserialnum_value = (TextView) findViewById(R.id.tv_assetserialnum_value);
        tv_assetname = (TextView) findViewById(R.id.tv_assetname);
        tv_assetname_value = (TextView) findViewById(R.id.tv_assetname_value);
        tv_datepurchase = (TextView) findViewById(R.id.tv_datepurchase);
        tv_datepurchase_value = (TextView) findViewById(R.id.tv_datepurchase_value);
        tv_placestored = (TextView) findViewById(R.id.tv_placestored);
        tv_placestored_value = (TextView) findViewById(R.id.tv_placestored_value);
        tv_info = (CardView) findViewById(R.id.tv_info);
        view2 = (View) findViewById(R.id.view2);

        btn_inveSummary.setOnClickListener(this);
        xrcv = (XRecyclerView) findViewById(R.id.xrcv);
        xrcv.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_inveSummary:
                Intent intent1 = new Intent(this, InventoryActivity.class);
                startActivity(intent1);
                break;
        }
    }

    static class InventoryingActivityHandler extends Handler {
        WeakReference<InventoryingActivity> inventoryingActivityWeakReference;

        public InventoryingActivityHandler(InventoryingActivity inventoryingActivity) {
            inventoryingActivityWeakReference = new WeakReference<>(inventoryingActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            InventoryingActivity inventoryingActivity = inventoryingActivityWeakReference.get();
            if (inventoryingActivity != null) {
                switch (msg.what) {
                    case 1:
                        Bundle bundle = msg.getData();
                        String tempstr = bundle.getString("color");//tempstr  是 epc编号 01201809230004
                        String name = bundle.getString("name");//  name是     资产信息   联想笔记本|2018-07-23|信息科办公室
                        inventoryingActivity.tv_assetserialnum_value.setText(tempstr);
                        String[] namePart = name.split("\\|");
                        inventoryingActivity.tv_assetname_value.setText(namePart[0]);
                        inventoryingActivity.tv_datepurchase_value.setText(namePart[1]);
                        inventoryingActivity.tv_placestored_value.setText(namePart[2]);
                        inventoryingActivity.dbOperation(tempstr, name);
                        break;
                }
            }
        }
    }

    private void dbOperation(String tempstr, String name) {
        String[] namePart = name.split("\\|");//tempstr  是 epc编号 01201809230004
        String place = namePart[2];//name是     资产信息   联想笔记本|2018-07-23|信息科办公室
        Query assetIdQuery = itemsBox.query().equal(SingleItemInfoBean_.assetsSerialNum, tempstr).build();
        ArrayList<SingleItemInfoBean> assetIdQueryResultList = (ArrayList<SingleItemInfoBean>) assetIdQuery.find();
        int size = assetIdQueryResultList.size();
        if (size == 1) {// 找到条目后开始修改update
            //======================
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
            itemsBox.put(singleItemInfoBean);
            //=======================
            Query assetPlaceQuery_check = itemsBox.query().equal(SingleItemInfoBean_.placeStored, assetPlace).equal(SingleItemInfoBean_.checkResult,1).build();
            Query assetPlaceQuery_uncheck = itemsBox.query().equal(SingleItemInfoBean_.placeStored, assetPlace).equal(SingleItemInfoBean_.checkResult,2).build();
            ArrayList<SingleItemInfoBean> assetPlaceQueryResultList_check = (ArrayList<SingleItemInfoBean>) assetPlaceQuery_check.find();
            ArrayList<SingleItemInfoBean> assetPlaceQueryResultList_uncheck = (ArrayList<SingleItemInfoBean>) assetPlaceQuery_uncheck.find();
            itemSpeciedList.addAll(assetPlaceQueryResultList_check);
            itemSpeciedList.addAll(assetPlaceQueryResultList_uncheck);
            adapter.notifyDataSetChanged();
            int totalnum = itemSpeciedList.size();
            Toast.makeText(this, "当前位置总共" + totalnum + "件资产", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "数据库中没有找到该资产编号", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(sc);
        super.onDestroy();
    }
}
