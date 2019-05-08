package com.biotag.huangpuhospital.activity;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.biotag.huangpuhospital.common.CommonMethod;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MyService extends Service {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice device;
    private Timer timer = new Timer();
    private BluetoothSocket btSocket1 = null;
    private final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private UUID uuid;

    public void setAddress(String address) {
        this.address = address;
    }

    public String address= "";

    public EpcNumberInterface getEpcNumberInterface() {
        return epcNumberInterface;
    }

    public void setEpcNumberInterface(EpcNumberInterface epcNumberInterface) {
        this.epcNumberInterface = epcNumberInterface;
    }

    private EpcNumberInterface epcNumberInterface;

    public MyService() {
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        address = intent.getStringExtra("address");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {

        super.onCreate();
    }




    //在解绑的服务的方法中将socket关闭
    @Override
    public boolean onUnbind(Intent intent) {
        if (btSocket1 != null) {
            try {
                btSocket1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new MyBinder();
    }

    //============================
    public class MyBinder extends Binder{
        MyService getMyService(){
            return MyService.this;
        }
    }

    public void startScanBluetooth(){
        if(!address.equals("")){
            uuid = UUID.fromString(SPP_UUID);
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            device = mBluetoothAdapter.getRemoteDevice(address);
            timer.schedule(new MyTask(),0L);
        }
    }

    //===========================================
    class MyTask extends TimerTask {

        @Override
        public void run() {

            if (mBluetoothAdapter == null) {
                return;
            }
            try {
                btSocket1 = device.createRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            try {
                btSocket1.connect();
            } catch (IOException e) {
                e.printStackTrace();

            }

            while (true) {
                if (btSocket1.getRemoteDevice() == null) {
                    return;
                }

                try {
                    byte[] temp = new byte[250];// 最后一个编号是249
                    btSocket1.getOutputStream();
                    InputStream inStream = btSocket1.getInputStream();
                    int count = inStream.available();
                    if (count > 0) {
                        int byteread = inStream.read(temp);
                        byte[] epcnobyte = new byte[17];
                        byte[] namebyte = new byte[213];
                        System.arraycopy(temp, 11, epcnobyte, 0, 17);
                        System.arraycopy(temp, 37, namebyte, 0, 213);

                        String str1 = CommonMethod.bytesToHexString(epcnobyte);
                        String str2 = CommonMethod.bytesToHexString(namebyte);
//                        str1 = str1.split("0000")[0];
                        str1 = CommonMethod.hex2straboutinfo(str1);
                        str2 = CommonMethod.hex2straboutinfo(str2);
                        if(epcNumberInterface!=null){
                            epcNumberInterface.epcinfo(str1,str2);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
