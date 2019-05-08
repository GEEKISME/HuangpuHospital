package com.biotag.huangpuhospital.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biotag.huangpuhospital.R;
import com.biotag.huangpuhospital.javabean.SingleItemInfoBean;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

public class ContentListAdapter extends XRecyclerView.Adapter<ContentListAdapter.ContentHolder> {
    private ArrayList<SingleItemInfoBean> itemSpeciedList = new ArrayList<>();
    private Context context;


    public ContentListAdapter(ArrayList<SingleItemInfoBean> itemSpeciedList, Context context) {
        this.itemSpeciedList = itemSpeciedList;
        this.context = context;
    }

    @NonNull
    @Override
    public ContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ContentHolder holder = new ContentHolder(LayoutInflater.from(context).inflate(R.layout.inventory_item_list,
                parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ContentHolder holder, int position) {
        SingleItemInfoBean siib = itemSpeciedList.get(position);
        holder.tv_assetserialnum_value.setText(siib.getAssetsSerialNum());
        holder.tv_assetname_value.setText(siib.getAssetsName());
        holder.tv_datepurchase_value.setText(siib.getDatePurchased());
        holder.tv_placestored_value.setText(siib.getPlaceStored());
        holder.tv_keeper_value.setText(siib.getKeeper());
        int checkresult = siib.getCheckResult();
        if(checkresult==1){
            if(holder.tv_ischeck_value.getVisibility()==View.GONE){
                holder.tv_ischeck_value.setVisibility(View.VISIBLE);
            }
            if(holder.tv_ischeck.getVisibility()==View.GONE){
                holder.tv_ischeck.setVisibility(View.VISIBLE);
            }

            holder.tv_ischeck_value.setText(siib.getCheckDate());
            holder.cardView.setBackgroundColor(Color.GREEN);
        }else {
            holder.tv_ischeck_value.setVisibility(View.GONE);
            holder.tv_ischeck.setVisibility(View.GONE);
            holder.cardView.setBackgroundColor(Color.LTGRAY);
        }
    }

    @Override
    public int getItemCount() {
        return itemSpeciedList.size();
    }

    class ContentHolder extends XRecyclerView.ViewHolder {
         TextView tv_assetserialnum_value;
         TextView tv_assetname_value;
         TextView tv_datepurchase_value;
         TextView tv_placestored_value;
         TextView tv_keeper_value;
         TextView tv_ischeck_value;
         TextView tv_ischeck;

         CardView cardView;
        public ContentHolder(View itemView) {
            super(itemView);
            tv_assetserialnum_value = itemView.findViewById(R.id.tv_assetserialnum_value);
            tv_assetname_value = itemView.findViewById(R.id.tv_assetname_value);
            tv_datepurchase_value = itemView.findViewById(R.id.tv_datepurchase_value);
            tv_placestored_value = itemView.findViewById(R.id.tv_placestored_value);
            tv_keeper_value = itemView.findViewById(R.id.tv_keeper_value);
            tv_ischeck_value = itemView.findViewById(R.id.tv_ischeck_value);
            tv_ischeck = itemView.findViewById(R.id.tv_ischeck);
            cardView = itemView.findViewById(R.id.tv_info);
        }
    }
}
