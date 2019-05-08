package com.biotag.huangpuhospital.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biotag.huangpuhospital.R;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

public class DeviceListAdapter extends XRecyclerView.Adapter<DeviceListAdapter.DeviceHolder> {


    private ArrayList<String> deviceList = new ArrayList<>();
    private Context context;


    private ListItemClickListener listItemClickListener;
    public void setListItemClickListener(ListItemClickListener listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    public DeviceListAdapter(ArrayList<String> deviceArraylist, Context context) {
        this.deviceList = deviceArraylist;
        this.context = context;
    }



    @Override
    public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DeviceHolder holder_temp = new DeviceHolder(LayoutInflater.from(context).inflate(R.layout.device_list_item,parent,false));
        return holder_temp;
    }

    @Override
    public void onBindViewHolder(DeviceHolder holder, final int position) {
        final String devicename = deviceList.get(position);
        holder.txt_device_mac.setText(devicename);

        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listItemClickListener!=null){
                    listItemClickListener.clickListener(position);
                }
            }
        });
        if(position == 0){
            holder.alinesix.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    class DeviceHolder extends RecyclerView.ViewHolder{
        TextView txt_device_mac;
        ImageView  ic_connect_status;
        TextView txt_connect_status;
        RelativeLayout rl;
        View alinesix;

        public DeviceHolder(View itemView) {
            super(itemView);
            txt_device_mac = (TextView)itemView.findViewById(R.id.txt_device_mac);
            ic_connect_status = (ImageView)itemView.findViewById(R.id.ic_connect_status);
            txt_connect_status = (TextView)itemView.findViewById(R.id.txt_connect_status);
            rl = (RelativeLayout)itemView.findViewById(R.id.rl);
            alinesix = itemView.findViewById(R.id.alinesix);
        }
    }

    public interface ListItemClickListener{
        void clickListener(int position);
    }
}
