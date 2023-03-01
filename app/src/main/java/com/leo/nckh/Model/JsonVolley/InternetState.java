package com.leo.nckh.Model.JsonVolley;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.leo.nckh.Model.DTO.DTO_TaiKhoan;
import com.leo.nckh.R;

public class InternetState extends BroadcastReceiver {
    SharedPreferences prefs;
    Json_MuonPhong json_muonPhong;

    @Override
    public void onReceive(Context context, Intent intent) {
        prefs = context.getSharedPreferences("InternetState_intro", MODE_PRIVATE);
        boolean check = prefs.getBoolean("state", false);
        if (checkconnect(context)) {
            Toast.makeText(context, "Đã kết nối Internet", Toast.LENGTH_SHORT).show();
        } else {
            if (check) {
                loading(context, intent);
            }
        }
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    public void loading(Context context, Intent intent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_loading_dialog, null);
        Button btnOk = view.findViewById(R.id.btnOk_load);
        Button btnThoat = view.findViewById(R.id.btnThoat_load);
        TextView tv = view.findViewById(R.id.tv_load);
        tv.setText("Mất kết nối Intenet!!!\n Vui lòng kết nối lại Internet");
        btnOk.setText("thử lại");
        btnThoat.setText("offline");
        builder.setCancelable(false);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        btnOk.setOnClickListener(view1 -> {
            dialog.dismiss();
            onReceive(context, intent);
        });
        btnThoat.setOnClickListener(view12 -> dialog.dismiss());
        dialog.show();
    }

    public boolean checkconnect(Context context) {
        json_muonPhong = new Json_MuonPhong(context);
        json_muonPhong.kiemtraInternet();
        prefs = context.getSharedPreferences("InternetState", MODE_PRIVATE);
        boolean check = prefs.getBoolean("state", false);

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected())
            return check;
        return false;
    }


}