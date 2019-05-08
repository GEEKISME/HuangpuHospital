package com.biotag.huangpuhospital.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biotag.huangpuhospital.R;
import com.biotag.huangpuhospital.activity.CloseDialogInterface;
import com.biotag.huangpuhospital.javabean.SingleItemInfoBean;

import java.util.ArrayList;

public class SpeciedPlaceAdapetr extends BaseAdapter {
    private ArrayList<SingleItemInfoBean>  totalitems = new ArrayList<>();
    private Context context;
    private CloseDialogInterface closeDialogInterface;

    public SpeciedPlaceAdapetr(ArrayList<SingleItemInfoBean> totalitems, Context context) {
        this.totalitems = totalitems;
        this.context = context;
    }

    public void setCloseDialogInterface(CloseDialogInterface closeDialogInterface) {
        this.closeDialogInterface = closeDialogInterface;
    }

    @Override
    public int getCount() {
        return totalitems.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SingleItemInfoBean siib = totalitems.get(position);
        View view = LayoutInflater.from(context).inflate(R.layout.asset_layout, parent, false);
        LinearLayout ll_wrapall = view.findViewById(R.id.ll_wrapall);
        TextView tv_assetserialnum = view.findViewById(R.id.tv_assetserialnum);
        TextView tv_assetname = view.findViewById(R.id.tv_assetname);
        TextView tv_checkstate = view.findViewById(R.id.tv_checkstate);
        tv_assetserialnum.setText(siib.getAssetsSerialNum());
        tv_assetname.setText(siib.getAssetsName());
        int checkresult = siib.getCheckResult();
        if(checkresult==1){
            tv_checkstate.setText("已盘点");
            ll_wrapall.setBackgroundResource(R.color.green);
        }else {
            tv_checkstate.setText("未盘点");
            ll_wrapall.setBackgroundResource(R.color.white);
        }
        return view;

    }
}
