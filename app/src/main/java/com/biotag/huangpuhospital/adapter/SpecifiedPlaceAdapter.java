package com.biotag.huangpuhospital.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.biotag.huangpuhospital.R;
import com.biotag.huangpuhospital.javabean.SingleItemInfoBean;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

public class SpecifiedPlaceAdapter extends XRecyclerView.Adapter<SpecifiedPlaceAdapter.SpcifiedHolder> {
    private ArrayList<SingleItemInfoBean> itemSpeciedList = new ArrayList<>();
    private Context context;

    public SpecifiedPlaceAdapter(ArrayList<SingleItemInfoBean> itemSpeciedList, Context context) {
        this.itemSpeciedList = itemSpeciedList;
        this.context = context;
    }

    @NonNull
    @Override
    public SpcifiedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SpcifiedHolder spcifiedHolder = new SpcifiedHolder(LayoutInflater.from(context).inflate(R.layout.specifiedplace_layout, parent, false));
        return spcifiedHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SpcifiedHolder holder, int position) {
        SingleItemInfoBean siib = itemSpeciedList.get(position);
        holder.tv_serial_value.setText(siib.getAssetsSerialNum());
        holder.tv_name_value.setText(siib.getAssetsName());
        holder.tv_date_value.setText(siib.getDatePurchased());
        holder.iv_check.setVisibility(View.GONE);
        int checkresult = siib.getCheckResult();
        if(checkresult==1){
            if(holder.iv_check.getVisibility()==View.GONE){
                holder.iv_check.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemSpeciedList.size();
    }

    class SpcifiedHolder extends RecyclerView.ViewHolder{
        TextView tv_serial_value;
        TextView tv_name_value;
        TextView tv_date_value;
        ImageView iv_check;
        public SpcifiedHolder(View itemView) {
            super(itemView);
            tv_serial_value = itemView.findViewById(R.id.tv_serial_value);
            tv_name_value = itemView.findViewById(R.id.tv_name_value);
            tv_date_value = itemView.findViewById(R.id.tv_date_value);
            iv_check = itemView.findViewById(R.id.iv_check);
        }
    }
}
