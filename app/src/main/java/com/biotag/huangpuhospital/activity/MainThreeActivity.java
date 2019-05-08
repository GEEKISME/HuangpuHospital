package com.biotag.huangpuhospital.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.biotag.huangpuhospital.R;

public class MainThreeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_scan;
    private Button btn_check;
    private Button btn_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_three);
        initPermission();
        initView();
    }

    private void initPermission() {
        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //申请了两种权限：WRITE_EXTERNAL_STORAGE与 CAMERA 权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,}, 60);
            return;
        }
    }
    private void initView() {
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_check = (Button) findViewById(R.id.btn_check);
        btn_setting = (Button) findViewById(R.id.btn_setting);

        btn_scan.setOnClickListener(this);
        btn_check.setOnClickListener(this);
        btn_setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                Intent intentone = new Intent(this,Main2Activity.class);
                startActivity(intentone);
                break;
            case R.id.btn_check:
                Intent intenttwo = new Intent(this,CheckActivity.class);
                startActivity(intenttwo);
                break;
            case R.id.btn_setting:
                Intent intent = new Intent(this,SettingActivity.class);
                startActivity(intent);
                break;
        }
    }
}
