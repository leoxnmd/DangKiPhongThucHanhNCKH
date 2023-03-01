package com.leo.nckh.Controller.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.leo.nckh.Model.DTO.DTO_TaiKhoan;


import com.leo.nckh.Model.JsonVolley.Json_TaiKhoan;
import com.leo.nckh.R;

import org.json.JSONException;
import org.json.JSONObject;


public class Activity_DoiMatKhau extends AppCompatActivity {

    ImageButton ibBack;
    EditText edtMatKhauCu, edtMatKhauMoi, edtXacNhanMatKhau;
    TextView txtHoTen, txtSDT, txtEmail;
    Button btXacNhan;
    DTO_TaiKhoan taiKhoan;
    Json_TaiKhoan json_taiKhoan;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doimatkhau);
        anhxa();

        json_taiKhoan = new Json_TaiKhoan(this);
        taiKhoan = new DTO_TaiKhoan();

        Bundle bundle = getIntent().getExtras();
        taiKhoan = (DTO_TaiKhoan) bundle.getSerializable("TaiKhoanDoiPass");
        setDataTk();
        xulybtn();
    }

    private void setDataTk() {
        txtEmail.setText(taiKhoan.getEmail());
        txtHoTen.setText(taiKhoan.getHoTen());
        txtSDT.setText(taiKhoan.getSDT());
    }

    private void xulybtn() {
        btXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtMatKhauCu.getText().toString().isEmpty() || edtMatKhauMoi.getText().toString().isEmpty() || edtXacNhanMatKhau.getText().toString().isEmpty()) {
                    Toast.makeText(Activity_DoiMatKhau.this, "Nhận đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    if (edtMatKhauMoi.getText().toString().equals(edtXacNhanMatKhau.getText().toString())) {
                        if (edtMatKhauCu.getText().toString().equals(taiKhoan.getPassWord())) {
                            DTO_TaiKhoan dto_taiKhoan = new DTO_TaiKhoan();
                            dto_taiKhoan.setMaGiangVien(taiKhoan.getMaGiangVien());
                            dto_taiKhoan.setPassWord(edtMatKhauMoi.getText().toString());
                            json_taiKhoan.setData(dto_taiKhoan, new Json_TaiKhoan.interface_dmk() {
                                @Override
                                public void response_getdata(String response) throws JSONException {
                                    JSONObject jsonObject = new JSONObject(response);
                                    int status = jsonObject.getInt("StatusCode");
                                    if (status == 200) {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putInt("key", 1);
                                        editor.apply();
                                        Toast.makeText(Activity_DoiMatKhau.this, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(Activity_DoiMatKhau.this, jsonObject.getInt("StatusCode") + jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(Activity_DoiMatKhau.this, "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Activity_DoiMatKhau.this, "Mật khẩu mới và xác nhận mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void anhxa() {
        edtMatKhauCu = findViewById(R.id.doipass_matkhaucu);
        edtMatKhauMoi = findViewById(R.id.doipass_matkhaumoi);
        edtXacNhanMatKhau = findViewById(R.id.doipass_passxacnhan);
        txtHoTen = findViewById(R.id.matkhau_tenTK);
        txtSDT = findViewById(R.id.matkhau_SDT);
        txtEmail = findViewById(R.id.matkhau_email);
        btXacNhan = findViewById(R.id.doipass_btxacnhan);
        ibBack = findViewById(R.id.btnBack_doimatkhau);
        sharedPreferences = getSharedPreferences("trangthai", Context.MODE_PRIVATE);

        ibBack.setOnClickListener(view -> {
            finish();
        });
    }

}