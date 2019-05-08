package com.biotag.huangpuhospital.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biotag.huangpuhospital.R;
import com.biotag.huangpuhospital.javabean.SingleItemInfoBean;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

public class ItemInfoAdapter extends XRecyclerView.Adapter<ItemInfoAdapter.ItemInfoHolder> {

    private ArrayList<SingleItemInfoBean> allItems = new ArrayList<>();
    private Context context;
    private int hasbeenChecknum = 0;

    public ItemInfoAdapter(ArrayList<SingleItemInfoBean> allItems, Context context) {
        this.allItems = allItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInfoHolder holder = new ItemInfoHolder(LayoutInflater.from(context).inflate(R.layout.single_iteminfo_layout,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemInfoHolder holder, int position) {
        SingleItemInfoBean singleItemInfoBean = allItems.get(position);
        holder.tv_assetserialnum_value.setText(singleItemInfoBean.getAssetsSerialNum());
        holder.tv_assetname_value.setText(singleItemInfoBean.getAssetsName());
        holder.tv_datepurchase_value.setText(singleItemInfoBean.getDatePurchased());
        holder.tv_keeper_value.setText(singleItemInfoBean.getKeeper());
        holder.tv_placestored_value.setText(singleItemInfoBean.getPlaceStored());
        holder.tv_placestored_value.setSelected(true);
        int isCheck = singleItemInfoBean.getCheckResult();
        if(isCheck==2){
            holder.tv_ischeck_value.setText("未盘点");
            holder.cardview.setCardBackgroundColor(Color.parseColor("#90FF4081"));
        }else if(isCheck==1){
            hasbeenChecknum++;
            holder.tv_ischeck_value.setText("已盘点");
            holder.cardview.setCardBackgroundColor(Color.parseColor("#7FFFAA"));
        }
        if(position==allItems.size()-1){

        }
    }

    @Override
    public int getItemCount() {
        return allItems.size();
    }

    class ItemInfoHolder extends RecyclerView.ViewHolder {
        TextView tv_assetserialnum_value;
        TextView tv_assetname_value;
        TextView tv_datepurchase_value;
        TextView tv_placestored_value;
        TextView tv_keeper_value;
        TextView tv_ischeck_value;
        CardView cardview;
        public ItemInfoHolder(View itemView) {
            super(itemView);
            tv_assetserialnum_value = itemView.findViewById(R.id.tv_assetserialnum_value);
            tv_assetname_value = itemView.findViewById(R.id.tv_assetname_value);
            tv_datepurchase_value = itemView.findViewById(R.id.tv_datepurchase_value);
            tv_placestored_value = itemView.findViewById(R.id.tv_placestored_value);
            tv_keeper_value = itemView.findViewById(R.id.tv_keeper_value);
            tv_ischeck_value = itemView.findViewById(R.id.tv_ischeck_value);
            cardview = itemView.findViewById(R.id.cardview);
        }
    }
}
