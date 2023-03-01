package com.leo.nckh.Controller.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leo.nckh.Controller.Activity.Activity_DangNhap;
import com.leo.nckh.Controller.Activity.Activity_DoiMatKhau;
import com.leo.nckh.Controller.Activity.Activity_Home;
import com.leo.nckh.Model.DTO.DTO_TaiKhoan;

import com.leo.nckh.R;

import java.lang.reflect.Type;
import java.util.List;


public class Fragment_User extends Fragment implements View.OnClickListener {

    View viewlayout;

    DTO_TaiKhoan taiKhoan;
    TextView txtTenTk, txtSDT, txtEmail;
    LinearLayout lnDangXuat, lnDoiPass, lnLienHe, lnAdmin, lnNgonNgu, lnInfo;
    FloatingActionButton btnMess;

    SharedPreferences sharedPreferences;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor_token;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewlayout = inflater.inflate(R.layout.fragment_user, container, false);

        anhxa();
        setToken();
        return viewlayout;
    }

    public List<DTO_TaiKhoan> getInfoAD() {

        Gson gson = new Gson();
        String json = sharedPref.getString("Admin", "");
        Type type = new TypeToken<List<DTO_TaiKhoan>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    private void anhxa() {
        taiKhoan = new DTO_TaiKhoan();
        Activity_Home activity = (Activity_Home) getActivity();
        assert activity != null;
        taiKhoan = activity.getTaiKhoan();

        txtTenTk = viewlayout.findViewById(R.id.taikhoan_tenTK);
        txtSDT = viewlayout.findViewById(R.id.taikhoan_SDT);
        txtEmail = viewlayout.findViewById(R.id.taikhoan_Email);
        lnDangXuat = viewlayout.findViewById(R.id.layout_dangxuat);
        lnDoiPass = viewlayout.findViewById(R.id.lnDoipass);
        lnLienHe = viewlayout.findViewById(R.id.layout_lienhe_user);
        lnAdmin = viewlayout.findViewById(R.id.layout_infoAdmin_user);
        lnNgonNgu = viewlayout.findViewById(R.id.layout_ngonNgu_user);
        lnInfo = viewlayout.findViewById(R.id.layout_info_user);
        btnMess=viewlayout.findViewById(R.id.user_message);

        sharedPreferences = requireActivity().getSharedPreferences("trangthai", Context.MODE_PRIVATE);
        sharedPref = requireActivity().getSharedPreferences("dsPhong", Context.MODE_PRIVATE);
        editor_token = requireActivity().getSharedPreferences("getToken", Context.MODE_PRIVATE).edit();

        txtTenTk.setText(taiKhoan.getHoTen());
        txtSDT.setText(taiKhoan.getSDT());
        txtEmail.setText(taiKhoan.getEmail());

        lnAdmin.setOnClickListener(this);
        lnDangXuat.setOnClickListener(this);
        lnLienHe.setOnClickListener(this);
        lnNgonNgu.setOnClickListener(this);
        lnDoiPass.setOnClickListener(this);
        lnInfo.setOnClickListener(this);
        btnMess.setOnClickListener(this);
    }
    public void setToken() {
//        FirebaseMessaging.getInstance().deleteToken().addOnSuccessListener(unused -> {
//        });
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {
                editor_token.putString("token", token).apply();
                Log.d("TAGleo", "onSuccess1: " + token);
            }
        });
    }
    @SuppressLint({"InflateParams", "SetTextI18n"})
    public void call() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_loading_dialog, null);
        ProgressBar pb = view.findViewById(R.id.pro_load);
        Button btnOk = view.findViewById(R.id.btnOk_load);
        Button btnThoat = view.findViewById(R.id.btnThoat_load);
        TextView tv = view.findViewById(R.id.tv_load);
        tv.setTextSize(18);
        pb.setVisibility(View.GONE);
        if (getInfoAD().size() > 0) {
            tv.setText("Liên hệ : " + getInfoAD().get(0).getHoTen());
            btnOk.setText("Call");
            btnThoat.setText("Email");
            builder.setView(view);
            AlertDialog dialog = builder.create();
            btnOk.setOnClickListener(view1 -> {
                dialog.dismiss();
                Uri number = Uri.parse("tel:" + getInfoAD().get(0).getSDT());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            });
            btnThoat.setOnClickListener(view12 -> {
                Intent mailIntent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:?subject=" + "" + "&body=" + " " + "&to=" + getInfoAD().get(0).getEmail());
                mailIntent.setData(data);
                startActivity(Intent.createChooser(mailIntent, "Liên hệ quản lý " + getInfoAD().get(0).getHoTen() + " ..."));
            });
            dialog.show();
        } else {
            Toast.makeText(requireContext(), "Quản lý đang bận ...", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    public void call_FStack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_loading_dialog, null);
        ProgressBar pb = view.findViewById(R.id.pro_load);
        Button btnOk = view.findViewById(R.id.btnOk_load);
        Button btnThoat = view.findViewById(R.id.btnThoat_load);
        TextView tv = view.findViewById(R.id.tv_load);
        tv.setTextSize(18);
        pb.setVisibility(View.GONE);

        tv.setText("Liên hệ FStack");
        btnOk.setText("Gửi Email");
        btnThoat.setText("Huỷ");
        builder.setCancelable(false);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        btnOk.setOnClickListener(view1 -> {
            dialog.dismiss();
            Intent mailIntent = new Intent(Intent.ACTION_VIEW);
            Uri data = Uri.parse("mailto:?subject=" + "Gửi admin" + "&body=" + " " + "&to=" + "fstack.utt@gmail.com");
            mailIntent.setData(data);
            startActivity(Intent.createChooser(mailIntent, "Liên hệ FStack ..."));
        });
        btnThoat.setOnClickListener(view12 -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lnDoipass:
                Intent iDoiPass = new Intent(requireContext().getApplicationContext(), Activity_DoiMatKhau.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("TaiKhoanDoiPass", taiKhoan);
                iDoiPass.putExtras(bundle);
                startActivity(iDoiPass);

                break;
            case R.id.layout_infoAdmin_user:
                call_FStack();
                break;
            case R.id.layout_lienhe_user:
                call();
                break;
            case R.id.layout_ngonNgu_user:
                Toast.makeText(requireContext(), "Chức năng này sắp có rồi...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layout_info_user:
                Toast.makeText(requireContext(), "Xin Chào " + taiKhoan.getHoTen(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.layout_dangxuat:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("key", 0);
                editor.apply();
                requireActivity().finish();
                startActivity(new Intent(requireActivity(), Activity_DangNhap.class));
                break;
            case R.id.user_message:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.me/100075713334044")));
                break;
        }
    }
}