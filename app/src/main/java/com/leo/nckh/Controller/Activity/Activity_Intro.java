package com.leo.nckh.Controller.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.leo.nckh.Model.DTO.DTO_MuonPhong;
import com.leo.nckh.Model.DataBase.DB_MuonPhong;
import com.leo.nckh.Model.JsonVolley.Json_MuonPhong;
import com.leo.nckh.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Activity_Intro extends AppCompatActivity {
    TextView tvVersion;
    Json_MuonPhong json_muonPhong;
    DB_MuonPhong db_muonPhong;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editor_internet;
    SharedPreferences.Editor editor_token;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        editor = getSharedPreferences("InternetState_intro", Context.MODE_PRIVATE).edit();
        editor_internet = getSharedPreferences("InternetState", Context.MODE_PRIVATE).edit();
        editor_token = getSharedPreferences("getToken", Context.MODE_PRIVATE).edit();
        setToken();

        db_muonPhong = new DB_MuonPhong(this);
        json_muonPhong = new Json_MuonPhong(this);
        json_muonPhong.getDL_muonPhong(new Json_MuonPhong.interface_MuonPhong() {
            @Override
            public void response_getdata(JSONObject response) {
                try {
                    editor_internet.putBoolean("state", true);
                    editor_internet.apply();

                    if (response.getInt("status") == 200) {
                        int sl_mysql = Integer.parseInt(response.getString("soluong"));
                        int sl_sqlite = db_muonPhong.soLuong();
                        if (sl_mysql == sl_sqlite) {
                            startActivity(new Intent(Activity_Intro.this, Activity_DangNhap.class));
                            finish();
                        }
                    } else if (response.getInt("status") == 204) {
                        startActivity(new Intent(Activity_Intro.this, Activity_DangNhap.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error_getdata(VolleyError error) {
                editor.putBoolean("state", false);
                editor.apply();
                editor_internet.putBoolean("state", false);
                editor_internet.apply();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(Activity_Intro.this, Activity_Home.class));
                        finish();
                    }
                }, 1000);
                Toast.makeText(Activity_Intro.this, "Mất kết nối internet !!!", Toast.LENGTH_SHORT).show();
            }
        });

        result_sync();
        tvVersion = findViewById(R.id.tvVersion);
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            tvVersion.setText(getString(R.string.version_app) + packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }

    public void setToken() {
        FirebaseMessaging.getInstance().deleteToken().addOnSuccessListener(unused -> {
        });
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {
                editor_token.putString("token", token).apply();
                Log.d("TAGleo", "onSuccess1: " + token);
            }
        });
    }

    private void result_sync() {
        if (check_ds_ngay().size() > 0)
            for (DTO_MuonPhong muonPhong : check_ds_ngay()) {
                json_muonPhong.sync_muonphong_resultPHP(String.valueOf(muonPhong.getId()));
            }
    }

    @SuppressLint("SimpleDateFormat")
    public List<DTO_MuonPhong> check_ds_ngay() {
        List<DTO_MuonPhong> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy");
        Calendar calendar = Calendar.getInstance();
        List<DTO_MuonPhong> list_sync = db_muonPhong.dsMuonPhongSync();
        try {
            if (list_sync.size() > 0) {
                Date hienTai = sdf.parse(sdf.format(calendar.getTime()));
                for (DTO_MuonPhong muonPhong : list_sync) {
                    Date dNgay = sdf.parse(muonPhong.getNgayMuon());
                    assert hienTai != null;
                    if (hienTai.after(dNgay)) {
                        list.add(muonPhong);
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

}