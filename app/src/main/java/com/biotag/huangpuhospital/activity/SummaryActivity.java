package com.biotag.huangpuhospital.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.biotag.huangpuhospital.R;
import com.biotag.huangpuhospital.adapter.ItemInfoAdapter;
import com.biotag.huangpuhospital.javabean.SingleItemInfoBean;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

import io.objectbox.Box;
import io.objectbox.BoxStore;

public class SummaryActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_summary;
    private Button btn_back;
    private ItemInfoAdapter itemInfoAdapter;
    private XRecyclerView xrcv;
    private ArrayList<SingleItemInfoBean> allItems= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        initView();
        BoxStore boxStore =((HuangpuApplication) getApplication()).getBoxStore();
        Box<SingleItemInfoBean> boxitems = boxStore.boxFor(SingleItemInfoBean.class);

        allItems = (ArrayList<SingleItemInfoBean>) boxitems.getAll();
        int itemnum = allItems.size();
        String info = new StringBuilder().append("共有").append(String.valueOf(itemnum)).append("条数据").toString();
        tv_summary.setText(info);
        itemInfoAdapter = new ItemInfoAdapter(allItems,this);
        xrcv.setAdapter(itemInfoAdapter);

        //==============================


    }

    private void initView() {
        tv_summary = (TextView) findViewById(R.id.tv_summary);
        btn_back = (Button) findViewById(R.id.btn_back);
        xrcv = (XRecyclerView) findViewById(R.id.xrcv);
        xrcv.setLayoutManager(new LinearLayoutManager(this));
        btn_back.setOnClickListener(this);
        //
        String str1 = "Lxh";
        String str2 = "Gl";
        int s = str1.compareTo(str2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }
}
