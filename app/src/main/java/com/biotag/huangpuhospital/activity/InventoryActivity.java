package com.biotag.huangpuhospital.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseBackgroundFormat;
import com.bin.david.form.data.table.PageTableData;
import com.bin.david.form.listener.OnColumnItemClickListener;
import com.biotag.huangpuhospital.R;
import com.biotag.huangpuhospital.adapter.SpecifiedPlaceAdapter;
import com.biotag.huangpuhospital.common.SharedPreferencesUtils;
import com.biotag.huangpuhospital.javabean.SingleItemInfoBean;
import com.biotag.huangpuhospital.javabean.SingleItemInfoBean_;
import com.biotag.huangpuhospital.javabean.StroedPlaceItemNumInfoBean;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;


public class  InventoryActivity extends AppCompatActivity {

    private ArrayList<SingleItemInfoBean> allItems= new ArrayList<>();
    private ArrayList<String> storedPlaceList = new ArrayList<>();
    private ArrayList<StroedPlaceItemNumInfoBean> placeinfoList = new ArrayList<>();
    private int storedPlaceNum = 0;
    private SmartTable<StroedPlaceItemNumInfoBean> smartTable;
    private PageTableData<StroedPlaceItemNumInfoBean> tableData;
    private int lastpage;
    private int totalcheckeditemnum = 0;//用来记录所有的被盘点的资产
    private XRecyclerView xrcv_specifiedplace;
    private SpecifiedPlaceAdapter contentListAdapter;
    private ArrayList<SingleItemInfoBean> itemSpeciedPlaceList = new ArrayList<>();
    private Box<SingleItemInfoBean> itemInfoBeanBox;
    private TextView tv_speciedplace;
    private int total = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);       //屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_inventory);
        tv_speciedplace = findViewById(R.id.tv_speciedplace);
        smartTable = findViewById(R.id.smarttable);
        BoxStore boxStore =((HuangpuApplication) getApplication()).getBoxStore();
        itemInfoBeanBox = boxStore.boxFor(SingleItemInfoBean.class);
        allItems = (ArrayList<SingleItemInfoBean>) itemInfoBeanBox.getAll();
        int size = allItems.size();
        for (int i = 0; i < size; i++) {
            SingleItemInfoBean siib = allItems.get(i);
            String place = siib.getPlaceStored();
            //  storedPlaceList 所有的存储地点
            if(!storedPlaceList.contains(place)){
                storedPlaceList.add(place);
            }
        }

        final int placenum = storedPlaceList.size();
        for (int i = 0; i < placenum; i++) {
            String placename = storedPlaceList.get(i);
            StroedPlaceItemNumInfoBean spb = new StroedPlaceItemNumInfoBean();
            spb.setId(String.valueOf(i+1));
            spb.setPlacename(placename);
            List<SingleItemInfoBean> totalitems = itemInfoBeanBox.query().equal(SingleItemInfoBean_.placeStored,placename).build().find();
            total = totalitems.size();// 该地点的物资总数
            List<SingleItemInfoBean> checkeditems = itemInfoBeanBox.query().equal(SingleItemInfoBean_.placeStored,placename).equal(SingleItemInfoBean_.checkResult,1).build().find();
            int checkedpart = checkeditems.size();//该地点已盘点物资总数
            totalcheckeditemnum += checkedpart; //
            spb.setItemtotalnumber(String.valueOf(total));
            spb.setItemcheckednumber(String.valueOf(checkedpart));
            int uncheckpart = total-checkedpart;//该地点未盘点物资总数
            spb.setItemuncheckednumber(String.valueOf(uncheckpart));
            placeinfoList.add(spb);
        }

        /*     StroedPlaceItemNumInfoBean的各个字段
         *     private String id;
         *     private String placename;
         *     private String itemtotalnumber;
         *     private String itemcheckednumber;
         *     private String itemuncheckednumber;
         */


        final Column<String> order = new Column<String>("序号","id");
        final Column<String> placename = new Column<String>("存放地","placename");
        final Column<String> itemtotalnumber = new Column<String>("资产数","itemtotalnumber");
        final Column<String> itemcheckednumber = new Column<String>("盘点数","itemcheckednumber");
        final Column<String> itemuncheckednumber = new Column<String>("未盘数","itemuncheckednumber");
        tableData = new PageTableData<StroedPlaceItemNumInfoBean>("",placeinfoList,order,placename,itemtotalnumber,itemcheckednumber,itemuncheckednumber);
        smartTable.getConfig().setCountBackground(new BaseBackgroundFormat(getResources().getColor(R.color.lightpink))).setShowXSequence(false).setShowYSequence(false);
        tableData.setPageSize(placenum);//这样就只有一页了
        smartTable.setTableData(tableData);
        //==================================================================
        placename.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
            @Override
            public void onClick(Column<String> column, String value, String s, int position) {
                Toast.makeText(InventoryActivity.this, "点击了"+value+"   ,点击了"+s+",   "+position, Toast.LENGTH_SHORT).show();
                tv_speciedplace.setText(value);
                itemSpeciedPlaceList.clear();
                List<SingleItemInfoBean> totalitems = itemInfoBeanBox.query().equal(SingleItemInfoBean_.placeStored,value).build().find();
                itemSpeciedPlaceList.addAll(totalitems);
                contentListAdapter.notifyDataSetChanged();
            }
        });
        //==================================================================
        lastpage = tableData.getTotalPage();
        Toast.makeText(this, "page size is "+lastpage, Toast.LENGTH_SHORT).show();
        TextView tv_summary = findViewById(R.id.tv_summary);
        int totaluncheckeditemnum = size-totalcheckeditemnum;
        String text_summary = new StringBuilder().append("合计场所数目：").append(String.valueOf(placenum)).append(",资产总数：").append(String.valueOf(size)).append(",已盘点的资产数目是:").append(String.valueOf(totalcheckeditemnum)).append(",未盘点的资产数目是：").append(String.valueOf(totaluncheckeditemnum)).toString();
        tv_summary.setText(text_summary);
        //========================================================================
        xrcv_specifiedplace = findViewById(R.id.xrcv_specifiedplace);
        xrcv_specifiedplace.setLayoutManager(new LinearLayoutManager(this));
        contentListAdapter = new SpecifiedPlaceAdapter(itemSpeciedPlaceList,this);
        xrcv_specifiedplace.setAdapter(contentListAdapter);
        //========================================================================
        Button btn_export = findViewById(R.id.btn_export);
        btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(total == totalcheckeditemnum){
                    Toast.makeText(InventoryActivity.this, "开始数据导出....", Toast.LENGTH_SHORT).show();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(InventoryActivity.this);
                    builder.setTitle("");
                    builder.setMessage("仍有部分资产尚未盘点，确定要导出文件吗？");//提示内容
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            exportInfoTxtFile();
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
                }
            }
        });
    }

    private void exportInfoTxtFile() {
        String filename  = SharedPreferencesUtils.getString(InventoryActivity.this,"txtfilename","");
        if(!filename.equals("")){
//            String[] filenametwopart = filename.split(".");
            int dotposition = filename.lastIndexOf(".");
            filename = filename.substring(0,dotposition);
            filename = new StringBuilder().append(filename).append("_Android").append(".txt").toString();
            File targetFile = new File(Environment.getExternalStorageDirectory()+"/HRP/"+filename);
            if(!targetFile.exists()){
                try {
                    targetFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 开始拼接字符串
            int size = allItems.size();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i <size ; i++) {
                SingleItemInfoBean siib = allItems.get(i);
                sb.append(String.valueOf(siib.getInventoryId())).append("|").append(String.valueOf(siib.getAssetId()))
                        .append("|").append(siib.getAssetsSerialNum()).append("|").append(siib.getAssetsName())
                        .append("|").append(siib.getDatePurchased()).append("|").append(siib.getDepartmentId())
                        .append("|").append(siib.getDepartmentName()).append("|").append(siib.getPlaceStored())
                        .append("|").append(siib.getKeeper()).append("|").append(siib.getCheckResult()).append("|")
                        .append(siib.getCheckDate()).append("\n");
            }
            String temptext = sb.toString();
            try {
                PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(targetFile)));
                printWriter.println(temptext);
                printWriter.close();
                Toast.makeText(this, "文件导出完成~", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //=========================================
}
