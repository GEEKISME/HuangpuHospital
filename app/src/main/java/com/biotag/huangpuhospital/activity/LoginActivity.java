package com.biotag.huangpuhospital.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.biotag.huangpuhospital.R;
import com.biotag.huangpuhospital.common.SharedPreferencesUtils;

import java.text.SimpleDateFormat;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_worknum;

    private EditText et_password;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        et_worknum = findViewById(R.id.et_worknum);
        String worknum = SharedPreferencesUtils.getString(this,"worknum","");
        if (!worknum.equals("")) {
            et_worknum.setText(worknum);
        }
        et_password = findViewById(R.id.et_password);

        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:


                String worknum = et_worknum.getText().toString();

                String pwd = et_password.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String date = sdf.format(System.currentTimeMillis());
                if(worknum!=null&&worknum.equals("9999")&&pwd!=null&&pwd.equals(date)){
                    String address = SharedPreferencesUtils.getString(this,"deviceaddress","");
                    String devicename = SharedPreferencesUtils.getString(this,"devicename","");
                    Intent intent = null;
//                    if(address.equals("")){
//                        intent = new Intent(this,SearchDeviceActivity.class);
//                        startActivity(intent);
//                    }else {
//                        intent = new Intent(this,Main2Activity.class);
//                        intent.putExtra("address",address);
//                        intent.putExtra("devicename",devicename);
//                        startActivity(intent);
//                    }
//                    finish();
                    //===========================================
                    if(address.equals("")){
                        intent = new Intent(this,MainThreeActivity.class);
                        startActivity(intent);
                    }else {
                        intent = new Intent(this,MainThreeActivity.class);
                        intent.putExtra("address",address);
                        intent.putExtra("devicename",devicename);
                        startActivity(intent);
                    }
                    SharedPreferencesUtils.saveString(this,"worknum",worknum);
                    finish();
                }else {
                    et_worknum.setText("");
                    et_password.setText("");
                    Toast.makeText(this, "登录失败~", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
