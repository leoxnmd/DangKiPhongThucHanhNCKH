package com.leo.nckh.Controller.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leo.nckh.Controller.Fragment.Fragment_AddMuonPhong;
import com.leo.nckh.Controller.Fragment.Fragment_DsMuonPhong;
import com.leo.nckh.Model.DTO.DTO_TaiKhoan;
import com.leo.nckh.Model.DataBase.DB_MuonPhong;
import com.leo.nckh.Model.JsonVolley.InternetState;
import com.leo.nckh.Controller.Fragment.Fragment_User;
import com.leo.nckh.Model.JsonVolley.Json_MuonPhong;
import com.leo.nckh.Model.JsonVolley.Json_Phong;
import com.leo.nckh.Model.JsonVolley.Json_TaiKhoan;
import com.leo.nckh.R;

import org.json.JSONObject;

public class Activity_Home extends AppCompatActivity {
    FloatingActionButton btnAdd;
    FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;

    BroadcastReceiver broadcastReceiver;
    SharedPreferences prefs;

    boolean flag = true;
    boolean check_openAdd = false;
    boolean check;

    InternetState internetState;
    DTO_TaiKhoan taiKhoan;
    Json_MuonPhong json_muonPhong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnAdd = findViewById(R.id.add_home);
        bottomNavigationView = findViewById(R.id.naviview);

        fragmentManager = getSupportFragmentManager();

        open_fragDsMuonPhong(true, fragmentManager);
        prefs = getSharedPreferences("InternetState_intro", MODE_PRIVATE);

        internetState = new InternetState();
        broadcastReceiver = internetState;
        json_muonPhong = new Json_MuonPhong(this);

        login();
        createNotificationChannel();
        init();
    }

    public void login() {
        check = prefs.getBoolean("state", false);
        if (check) {
            Bundle bundle = getIntent().getExtras();
            taiKhoan = (DTO_TaiKhoan) bundle.getSerializable("TaiKhoanLG");
            if (taiKhoan == null) {
                Toast.makeText(Activity_Home.this, "Hệ thống đang bảo trì", Toast.LENGTH_SHORT).show();
            }
        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onStart() {
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForDrawables"})
    public void init() {
        btnAdd.setOnClickListener(view -> {
            new Json_Phong(this).getDLJson_phong();
            check = prefs.getBoolean("state", false);
            if (internetState.checkconnect(this)) {
                if (!check) {
                    startActivity(new Intent(Activity_Home.this, Activity_DangNhap.class));
                    finish();
                } else {
                    if (flag) {//mở
                        flag = false;
                        open_fragDsMuonPhong(false, fragmentManager);
                        btnAdd.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_home));
                        open_fragAddMuonPhong(true);
                        loading(this);
                    } else {
                        open_fragAddMuonPhong(false);
                        open_fragDsMuonPhong(true, fragmentManager);
                        btnAdd.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_add));
                        flag = true;
                    }
                }

            } else {
                open_fragAddMuonPhong(false);
                open_fragDsMuonPhong(true, fragmentManager);
                btnAdd.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_add));
                flag = true;
                Toast.makeText(this, "Vui lòng kết nối Internet và đăng nhập\n Để sử dụng chức năng này", Toast.LENGTH_SHORT).show();
            }
        });
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.dsMuonPhong:
                    open_fragDsMuonPhong(true, fragmentManager);
                    return true;
                case R.id.user:
                    if (internetState.checkconnect(this)) {
                        if (!check) {
                            startActivity(new Intent(Activity_Home.this, Activity_DangNhap.class));
                            finish();
                        } else {
                            new Json_TaiKhoan(this).getInfoAdmin();
                            open_fragUser(true);
                            return true;
                        }
                    } else {
                        Toast.makeText(this, "Vui lòng kết nối Internet và đăng nhập\n Để sử dụng chức năng này", Toast.LENGTH_SHORT).show();
                        return false;
                    }
            }
            return false;
        });

    }
    @SuppressLint({"InflateParams", "SetTextI18n"})
    public void loading(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_loading_dialog, null);
        LinearLayout layout = view.findViewById(R.id.layout_btn_dialog);
        layout.setVisibility(View.GONE);
        TextView tv = view.findViewById(R.id.tv_load);
        tv.setText("Đợi một chút thôi...");

        builder.setCancelable(false);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
            json_muonPhong.getDLJson_muonPhong(response -> {
                dialog.dismiss();
            });
    }
    public void open_fragDsMuonPhong(boolean check, FragmentManager fragmentManager) {
        FragmentTransaction tragDsMuonPhong = fragmentManager.beginTransaction();
        Fragment_DsMuonPhong fragment_dsMuonPhong = new Fragment_DsMuonPhong();
        tragDsMuonPhong.replace(R.id.layout_content, fragment_dsMuonPhong);//thêm fragment vào layout content
        if (check) {
            tragDsMuonPhong.commit();
            home_add(false);
        } else {
            tragDsMuonPhong.remove(fragment_dsMuonPhong).commit();
            home_add(true);
        }
    }

    public void open_fragAddMuonPhong(boolean check) {
        FragmentTransaction tragMuonPhong = fragmentManager.beginTransaction();
        Fragment_AddMuonPhong fragment_muonPhong = new Fragment_AddMuonPhong();
        tragMuonPhong.replace(R.id.layout_content, fragment_muonPhong);//thêm fragment vào layout content
        if (check) {
            check_openAdd = true;
            tragMuonPhong.commit();
        } else {
            tragMuonPhong.remove(fragment_muonPhong).commit();
            check_openAdd = false;
        }
    }

    public void open_fragUser(boolean check) {
        FragmentTransaction tragUser = fragmentManager.beginTransaction();
        Fragment_User fragment_user = new Fragment_User();
        tragUser.replace(R.id.layout_content, fragment_user);//thêm fragment vào layout content
        if (check) {
            tragUser.commit();
            home_add(false);
        } else {
            tragUser.remove(fragment_user).commit();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void home_add(boolean check) {
        if (check) {
            btnAdd.setImageDrawable(getDrawable(R.drawable.ic_home));
        } else {
            btnAdd.setImageDrawable(getDrawable(R.drawable.ic_add));
            flag = true;
        }
    }

    public DTO_TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public String getmaGv() {
        if (check) {
            return taiKhoan.getMaGiangVien();
        } else {
            return prefs.getString("maGv", "");
        }
    }
    private static final String CHANNEL_ID = "101";

    @SuppressLint("ObsoleteSdkInt")
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "firebaseNotifChannel";
            String description = "Receve Firebase notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}