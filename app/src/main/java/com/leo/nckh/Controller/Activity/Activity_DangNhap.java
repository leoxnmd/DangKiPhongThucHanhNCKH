package com.leo.nckh.Controller.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.leo.nckh.Model.DTO.DTO_TaiKhoan;
import com.leo.nckh.Model.JsonVolley.Json_TaiKhoan;
import com.leo.nckh.R;

public class Activity_DangNhap extends AppCompatActivity {
    Button btnLogin;
    TextView tvQuenMk;
    EditText edtTaiKhoan, edtMatKhau;
    LinearLayout layout_load;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferences_token;

    DTO_TaiKhoan tk;
    CheckBox chbLuuMatKhau;
    Json_TaiKhoan json_taiKhoan;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editor_token;
    SharedPreferences.Editor editor_internet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangnhap);
        anhxa();
        json_taiKhoan = new Json_TaiKhoan(this);
        json_taiKhoan.getInfoAdmin();

        login();
        quenMk();

        int checkLogin = sharedPreferences.getInt("key", 0);
        if (checkLogin == 1) {
            checkLogin();
        }
        editor = getSharedPreferences("InternetState_intro", Context.MODE_PRIVATE).edit();
        editor_internet = getSharedPreferences("InternetState", Context.MODE_PRIVATE).edit();
        editor_token = getSharedPreferences("getToken", Context.MODE_PRIVATE).edit();

        // setToken();
    }

    public void quenMk() {
        tvQuenMk.setOnClickListener(view -> startActivity(new Intent(Activity_DangNhap.this, Activity_QuenMatKhau.class)));
    }

    public String getToken() {
        Log.d("TAGleo", "onSuccess2: " + sharedPreferences_token.getString("token", ""));
        return sharedPreferences_token.getString("token", "");
    }

    private void login() {
        btnLogin.setOnClickListener(view -> {
            if (!edtTaiKhoan.getText().toString().equals("") || !edtMatKhau.getText().toString().equals("")) {
                layout_load.setVisibility(View.VISIBLE);

                tk = new DTO_TaiKhoan();
                tk.setMaGiangVien(edtTaiKhoan.getText().toString());
                tk.setPassWord(edtMatKhau.getText().toString());
                tk.setToken(getToken());

                json_taiKhoan.getData(tk, new Json_TaiKhoan.interface_taiKhoan() {
                    @Override
                    public void response_getdata(DTO_TaiKhoan taiKhoan) {
                        editor_internet.putBoolean("state", true);
                        editor_internet.apply();

                        if (chbLuuMatKhau.isChecked()) {
                            luuData(taiKhoan);
                            luuTrangThaiLogin(1);
                        } else {
                            luuData(taiKhoan);
                            luuTrangThaiLogin(0);
                        }
                        if (taiKhoan.getQuyenDN() == 1) {
                            editor.putBoolean("state", true);
                            editor.apply();

                            editor.putString("maGv", edtTaiKhoan.getText().toString());
                            editor.apply();

                            Intent intent = new Intent(Activity_DangNhap.this, Activity_Home.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("TaiKhoanLG", taiKhoan);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else if (taiKhoan.getQuyenDN() == 2) {
                            Toast.makeText(Activity_DangNhap.this, "Tài khoản của bạn tạm thời bị khoá", Toast.LENGTH_SHORT).show();
                        }
                        layout_load.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void response_error(String response) {
                        editor_internet.putBoolean("state", false);
                        editor_internet.apply();
                        layout_load.setVisibility(View.INVISIBLE);
                    }
                });
            } else {
                Toast.makeText(Activity_DangNhap.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                layout_load.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void checkLogin() {

        chbLuuMatKhau.setChecked(true);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("TaiKhoanTK", "");
        DTO_TaiKhoan taiKhoanSV = gson.fromJson(json, DTO_TaiKhoan.class);
        edtTaiKhoan.setText(taiKhoanSV.getMaGiangVien());
        edtMatKhau.setText(taiKhoanSV.getPassWord());
    }

    public void luuData(DTO_TaiKhoan taiKhoan) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(taiKhoan);
        prefsEditor.putString("TaiKhoanTK", json);
        prefsEditor.apply();
    }

    public void luuTrangThaiLogin(int ma) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("key", ma);
        editor.apply();
    }

    private void anhxa() {
        btnLogin = findViewById(R.id.btnLogin_DN);
        edtTaiKhoan = findViewById(R.id.edTaiKhoan_DN);
        edtMatKhau = findViewById(R.id.edMatKhau_DN);
        chbLuuMatKhau = findViewById(R.id.check_luumatkhau);
        tvQuenMk = findViewById(R.id.tvquenmatkhau_DN);
        layout_load = findViewById(R.id.load_DN);
        sharedPreferences = getSharedPreferences("trangthai", MODE_PRIVATE);
        sharedPreferences_token = getSharedPreferences("getToken", MODE_PRIVATE);

    }

}