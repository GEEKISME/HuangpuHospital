package com.biotag.huangpuhospital.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseBackgroundFormat;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.PageTableData;
import com.bin.david.form.listener.OnColumnItemClickListener;
import com.bin.david.form.utils.DensityUtils;
import com.biotag.huangpuhospital.R;
import com.biotag.huangpuhospital.adapter.SpeciedPlaceAdapetr;
import com.biotag.huangpuhospital.adapter.SpecifiedPlaceAdapter;
import com.biotag.huangpuhospital.common.CommonMethod;
import com.biotag.huangpuhospital.common.SharedPreferencesUtils;
import com.biotag.huangpuhospital.common.ThreadManager;
import com.biotag.huangpuhospital.javabean.SingleItemInfoBean;
import com.biotag.huangpuhospital.javabean.SingleItemInfoBean_;
import com.biotag.huangpuhospital.javabean.StroedPlaceItemNumInfoBean;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;

public class CheckActivity extends AppCompatActivity implements View.OnClickListener {

    private View v_bg;
    private ImageView iv_back;
    private TextView activity_title;
    private Button btn_load;
    private TextView tv_suck;
    private Button btn_export;
    //===========================================================
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
    private CheckActivtyHandler handler;
    private RelativeLayout titlebar_scan;
    private int screenSize_Width = 0;
    private SpeciedPlaceAdapetr speciedPlaceAdapetr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);       //屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_check);
        handler = new CheckActivtyHandler(this);
        BoxStore boxStore =((HuangpuApplication) getApplication()).getBoxStore();
        itemInfoBeanBox = boxStore.boxFor(SingleItemInfoBean.class);
        initView();
        boolean areDataStored = SharedPreferencesUtils.getBoolean(CheckActivity.this,"areDataStored",false);
        if(areDataStored){
            inflateTable();
        }
    }

    private void initView() {

        titlebar_scan = findViewById(R.id.titlebar_scan);
        TextView activity_title = titlebar_scan.findViewById(R.id.activity_title);
        activity_title.setText("盘点");
        ImageView iv_back = titlebar_scan.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_load = (Button) findViewById(R.id.btn_load);
        tv_suck = (TextView) findViewById(R.id.tv_suck);
        smartTable = (SmartTable) findViewById(R.id.smarttablesum);
        btn_export = (Button) findViewById(R.id.btn_export);

        btn_load.setOnClickListener(this);
        btn_export.setOnClickListener(this);

        String filename = SharedPreferencesUtils.getString(this,"txtfilename","");
        if(!filename.equals("")){
            btn_load.setText(new StringBuilder().append("当前加载任务: ").append(filename).toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_load:
                initBox();
                break;
            case R.id.btn_export:
                exportInfoTxtFile();
                itemInfoBeanBox.removeAll();
                SharedPreferencesUtils.saveBoolean(CheckActivity.this,"areDataStored",false);
                SharedPreferencesUtils.saveString(this,"txtfilename","");
                break;
        }
    }

    private void exportInfoTxtFile() {
        String filename  = SharedPreferencesUtils.getString(CheckActivity.this,"txtfilename","");
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


    private void initBox() {
        boolean areDataStored = SharedPreferencesUtils.getBoolean(this,"areDataStored",false);
        if(areDataStored){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("");
            builder.setMessage("确定要重新读取资产文件吗？");//提示内容
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferencesUtils.saveBoolean(CheckActivity.this,"areDataStored",false);
                    itemInfoBeanBox.removeAll();
                    initObjectBox();
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
        }else {
            if(btn_load.getText().equals("加载任务")){
                itemInfoBeanBox.removeAll();
                initObjectBox();
            }
        }

    }

    public void initObjectBox() {
        ThreadManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                String textString = getTextString();
                if(!textString.equals("读取失败")){
                    itemInfoBeanBox.removeAll();
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
                    SharedPreferencesUtils.saveBoolean(CheckActivity.this,"areDataStored",true);
                    handler.sendEmptyMessage(1);
                }else {
                    handler.sendEmptyMessage(2);
                }
            }
        });
    }

    private void inflateTable() {
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

        screenSize_Width = CommonMethod.getScreenWidth(this);
        if(screenSize_Width==1440){
            FontStyle.setDefaultTextSize(DensityUtils.sp2px(this,12));
        }else if (screenSize_Width<1440){
            FontStyle.setDefaultTextSize(DensityUtils.sp2px(this,13));
        }
        final Column<String> order = new Column<String>("序号","id");
        final Column<String> placename = new Column<String>("存放地","placename");
        final Column<String> itemtotalnumber = new Column<String>("资产数","itemtotalnumber");
        final Column<String> itemcheckednumber = new Column<String>("盘点数","itemcheckednumber");
        final Column<String> itemuncheckednumber = new Column<String>("未盘数","itemuncheckednumber");
        tableData = new PageTableData<StroedPlaceItemNumInfoBean>("",placeinfoList,order,placename,itemtotalnumber,itemcheckednumber,itemuncheckednumber);
        smartTable.getConfig().setCountBackground(new BaseBackgroundFormat(getResources().getColor(R.color.lightpink))).setShowXSequence(false).setShowYSequence(false);
        smartTable.getConfig().setVerticalPadding(30);
        smartTable.getConfig().setHorizontalPadding(0);
        tableData.setPageSize(placenum);//这样就只有一页了
        smartTable.setTableData(tableData);

        placename.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
            AlertDialog ab = null ;
            @Override
            public void onClick(Column<String> column, String value, String s, int position) {
//                Toast.makeText(CheckActivity.this, "点击了"+value+"   ,点击了"+s+",   "+position, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckActivity.this);
                builder.setTitle(value);
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                };
                ArrayList<SingleItemInfoBean> totalitems = (ArrayList)itemInfoBeanBox.query().equal(SingleItemInfoBean_.placeStored,value).build().find();
                speciedPlaceAdapetr = new SpeciedPlaceAdapetr(totalitems,CheckActivity.this);
                speciedPlaceAdapetr.setCloseDialogInterface(new CloseDialogInterface() {
                    @Override
                    public void killDialog() {
                        ab.cancel();
                    }
                });
                builder.setAdapter(speciedPlaceAdapetr,listener);
                ab = builder.create();
                ab.show();
            }
        });



        lastpage = tableData.getTotalPage();
//        Toast.makeText(this, "page size is "+lastpage, Toast.LENGTH_SHORT).show();
        TextView tv_suck = findViewById(R.id.tv_suck);
        int totaluncheckeditemnum = size-totalcheckeditemnum;
        String text_summary = new StringBuilder().append("合计场所数目：")
                .append(String.valueOf(placenum)).append(" ,资产总数：")
                .append(String.valueOf(size)).append(" ,已盘点的资产数目是:")
                .append(String.valueOf(totalcheckeditemnum)).append(" ,未盘点的资产数目是：")
                .append(String.valueOf(totaluncheckeditemnum)).toString();
        tv_suck.setText(text_summary);


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
                        SharedPreferencesUtils.saveString(this,"txtfilename",filename);
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

    static class CheckActivtyHandler extends Handler{
        WeakReference<CheckActivity> checkActivityWeakReference;
        public CheckActivtyHandler(CheckActivity checkActivity){
            checkActivityWeakReference = new WeakReference<>(checkActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            CheckActivity checkActivity = checkActivityWeakReference.get();
            if(checkActivity!=null){
                switch (msg.what){
                    case 1:
                        Toast.makeText(checkActivity, "数据库初始化完毕~", Toast.LENGTH_SHORT).show();
                        String filename = SharedPreferencesUtils.getString(checkActivity,"txtfilename","");

                        checkActivity.btn_load.setText(new StringBuilder().append("当前加载任务: ").append(filename).toString());
                        checkActivity.inflateTable();
                        break;
                    case 2:
                        Toast.makeText(checkActivity, "读取数据失败~", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }
}
