package com.leo.nckh.Controller.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.leo.nckh.Model.JsonVolley.Json_TaiKhoan;

import com.leo.nckh.R;

import org.json.JSONObject;


public class Activity_QuenMatKhau extends AppCompatActivity {

    EditText edtMaGV;
    Button btXacNhan;
    ImageButton ibBack;

    LinearLayout lnLayoutLoad;
    Json_TaiKhoan json_taiKhoan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quenmatkhau);
        anhxa();
        json_taiKhoan = new Json_TaiKhoan(this);
        btXacNhan.setOnClickListener(v -> {
            loadDl();
        });
    }

    private void anhxa() {
        edtMaGV = findViewById(R.id.quenmatKhau_magv);
        btXacNhan = findViewById(R.id.btQuenMatKhau);
        lnLayoutLoad = findViewById(R.id.quenmatkhau_load);
        ibBack = findViewById(R.id.back_quenmatkhau);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void loadDl() {
        String magv = edtMaGV.getText().toString();
        if (!magv.equals("")) {
            lnLayoutLoad.setVisibility(View.VISIBLE);
            json_taiKhoan.json_quenMatKhau(magv, taiKhoan -> {
                JSONObject jsonObject = new JSONObject(taiKhoan);
                int status = jsonObject.getInt("StatusCode");
                if (status == 200) {
                    startActivity(new Intent(Activity_QuenMatKhau.this, Activity_DangNhap.class));
                    finish();
                    Toast.makeText(Activity_QuenMatKhau.this, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Activity_QuenMatKhau.this, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }
                lnLayoutLoad.setVisibility(View.GONE);
            });
        } else {
            Toast.makeText(Activity_QuenMatKhau.this, "Hãy nhập mã giảng viên...", Toast.LENGTH_SHORT).show();
        }

    }
}