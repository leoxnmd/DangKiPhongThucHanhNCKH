package com.leo.nckh.Controller.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.VolleyError;
import com.leo.nckh.Controller.Activity.Activity_Intro;
import com.leo.nckh.Model.DTO.DTO_TaiKhoan;
import com.leo.nckh.Model.DataBase.DB_Phong;
import com.leo.nckh.Controller.Activity.Activity_Home;
import com.leo.nckh.Controller.Adapter.Adapter_Spinner_DsPhong;
import com.leo.nckh.Model.DTO.DTO_MuonPhong;
import com.leo.nckh.Model.DataBase.DB_MuonPhong;
import com.leo.nckh.Model.JsonVolley.InternetState;
import com.leo.nckh.Model.JsonVolley.Json_MuonPhong;
import com.leo.nckh.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Fragment_AddMuonPhong extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch swChonPhong, swHangTuan;
    NumberPicker numPic1, numPic2, numPic3;
    Spinner sp_chonPhong;
    TextView txtChonPhong, tv_NgayChon, btnluu, btnThoat, tv_NgayketThuc;
    LinearLayout layout_hangTuan, layout_Ngaykt;
    ImageButton ib_lich, ib_ngayketThuc;
    CheckBox cb_t2, cb_t3, cb_t4, cb_t5, cb_t6, cb_t7;

    Calendar calendar;
    SimpleDateFormat df;
    View viewlayout;

    Fragment_DatePicker datePickerFrag;
    DB_MuonPhong db_muonPhong;
    Json_MuonPhong json_muonPhong;

    String chonPhong = "";
    String soTiet_vao = "";
    String maGv = "";
    int batDau;
    String ngayChon;
    int sl_muonPhong_cu;
    int sl_muonPhong_moi;
    boolean check_numpic2 = false, check_numpic1 = false, check_numpic3 = false, check_chonNgay = false;


    String[] sBuoi = {"Sáng ", "Chiều ", "Cả ngày"};
    String[] sThu = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
    List<String> list_sBuoi = new ArrayList<>();

    List<DTO_MuonPhong> list_add_muonphong = new ArrayList<>();
    List<DTO_MuonPhong> list_add_muonphong_hangtuan = new ArrayList<>();

    List<String> list_ngay = new ArrayList<>();
    List<String> list_ngay_hangTuan = new ArrayList<>();

    List<String> list_phong_cache_chon = new ArrayList<>();
    List<String> list_phong_cache_hangTuan = new ArrayList<>();

    List<String> list_phong_t2 = new ArrayList<>();
    List<String> list_phong_t3 = new ArrayList<>();
    List<String> list_phong_t4 = new ArrayList<>();
    List<String> list_phong_t5 = new ArrayList<>();
    List<String> list_phong_t6 = new ArrayList<>();
    List<String> list_phong_t7 = new ArrayList<>();
    List<List<String>> list_phong_tuan = Arrays.asList(list_phong_t2, list_phong_t3, list_phong_t4, list_phong_t5, list_phong_t6, list_phong_t7);

    List<CheckBox> mChecks = new ArrayList<>();
    List<CheckBox> mSelectedChecks = new ArrayList<>();


    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewlayout = inflater.inflate(R.layout.fragment_addmuonphong, container, false);
        init(viewlayout);
        maGv = getMaGv();

        db_muonPhong = new DB_MuonPhong(requireActivity());

        calendar = Calendar.getInstance();
        df = new SimpleDateFormat("d-M-yyyy");
        tv_NgayketThuc.setText(chuyenThu_ngay(sThu[ktra_cbChon(false)]));
        list_ngay = ngayKeTiep();
        json_muonPhong = new Json_MuonPhong(getContext());

        sl_muonPhong_cu = db_muonPhong.soLuong();

        xuLynumberPicker();
        showSpinner();
        return viewlayout;
    }

    private String getMaGv() {
        DTO_TaiKhoan taiKhoan = ((Activity_Home) requireActivity()).getTaiKhoan();
        return taiKhoan.getMaGiangVien();
    }

    @SuppressLint("SetTextI18n")
    public void boquaNgayCN() {//bỏ qua ngày chủ nhật
        for (String str : list_ngay) {
            String ss = chuyenNgay_Thu(str).toString();
            if (ss.equals("SUNDAY")) {
                tv_NgayChon.setText("CN," + str);
                break;
            } else {
                tv_NgayChon.setText(tenThu_cb(vitriThu()) + ", " + ngayHomnay());
                break;
            }
        }
    }

    //lấy text của checkbox chọn
    public String tenThu_cb(int i) {
        return mChecks.get(i).getText().toString();
    }

    @Override
    public void onStart() {
        super.onStart();
        mChecks.get(vitriThu()).setChecked(true);
    }

    public int stt() {//lấy id mới nhất trong bảng
        if (db_muonPhong.layId().size() > 0) {
            return db_muonPhong.layId().get(db_muonPhong.layId().size() - 1);
        }
        return 0;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        json_muonPhong.kiemtraInternet();

        sl_muonPhong_moi = db_muonPhong.soLuong();
        int stt = stt();
        switch (view.getId()) {
            case R.id.ib_ngayKetThuc:
            case R.id.tv_ngayKetThuc://khi bấm vào textview hoặc icon lịch mở lịch chọn ngày
                datePickerFrag = new Fragment_DatePicker(tv_NgayketThuc, false);
                datePickerFrag.show(requireActivity().getSupportFragmentManager(), "datePicker");
                list_ngay_hangTuan.clear();
                for (CheckBox c : mChecks) {
                    if (c.isChecked() && swHangTuan.isChecked()) {
                        //kiểm tra xem có check bõ nào đc chọn không và chức năng hàng tuần có đc bật
                        lay_ngay_hangTuan();
                    }
                }
                break;

            case R.id.ib_chonLich_add:
            case R.id.tvNgaychon_add://khi bấm vào textview hoặc icon lịch mở lịch chọn ngày
                swChonPhong.setChecked(false);
                bo_checked(false);
                sp_chonPhong.setEnabled(true);
                check_chonNgay = true;

                sw_on_off(true);

                datePickerFrag = new Fragment_DatePicker(tv_NgayChon, true);
                datePickerFrag.show(requireActivity().getSupportFragmentManager(), "datePicker");
                break;

            case R.id.btnLuu_add:
                if (new InternetState().checkconnect(requireContext())) {//khi có internet
                    if (swHangTuan.isChecked()) {//nếu chọn hàng tuần
                        for (String ngay : list_ngay_hangTuan) {
                            if (check_chonNgay || ktra_cbChon(true) > 0) {
                                stt += 10;
                                if (!chonPhong.equals("") && swChonPhong.isChecked()) {//nếu chọn phòng
                                    themdl_listMuonPhong(list_add_muonphong_hangtuan, stt, chonPhong, ngay, soTiet_vao);//thêm vào sql
                                } else {//nếu không chọn phòng thì ngẫu nhiên(phòng đầu của ds)
                                    list_phong_cache_hangTuan.addAll(locPhong_tietNgay(ngay));//add ds những phòng còn trống
                                    if (list_phong_cache_hangTuan.size() > 0) {//nếu có phòng trống thì thêm
                                        themdl_listMuonPhong(list_add_muonphong_hangtuan, stt, list_phong_cache_hangTuan.get(0), ngay, soTiet_vao);
                                        list_phong_cache_hangTuan.clear();//làm sạch list

                                    } else {//không có phòng thì thông báo
                                        Toast.makeText(getActivity(), "Ngày " + ngay + " không còn phòng \n Các ngày đủ điều kiện vẫn được tiếp tục đăng kí", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "Vui lòng chọn ngày ở icon lịch hoặc\n  Chọn 1 trong 7 ngày trong tuần.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        insert_mysql(list_add_muonphong_hangtuan);//thêm dữ liệu lên mysql
                    } else {//không mượn hàng tuần
                         if (check_chonNgay) {//nếu chọn ngày thủ công
                            if (list_phong_cache_chon.size() > 0) {
                                if (chonPhong.equals("") || !swChonPhong.isChecked()) {
                                    themdl_listMuonPhong(list_add_muonphong, stt + 10, list_phong_cache_chon.get(0), datePickerFrag.getNgay(), soTiet_vao);
                                } else {
                                    themdl_listMuonPhong(list_add_muonphong, stt + 10, chonPhong, datePickerFrag.getNgay(), soTiet_vao);
                                }
                                insert_mysql(list_add_muonphong);
                            } else {
                                Toast.makeText(getActivity(), "Tiết " + soTiet_vao + ", ngày " + datePickerFrag.getNgay() + " không còn phòng nào!!", Toast.LENGTH_SHORT).show();
                            }
                        } else if (ktra_cbChon(true) > 0) {//nếu chọn checkbox
                            for (int i = 0; i < mChecks.size(); i++) {
                                if (mChecks.get(i).isChecked()) {//kiểm tra check bõ nào đc chọn và lấy vti của nó
                                    if (list_phong_tuan.get(i).size() > 0) {
                                        String ngay = chuyenThu_ngay(sThu[i]);//lấy ngày tương ứng với checkbox
                                        stt += 10;
                                        if (!chonPhong.equals("") || swChonPhong.isChecked()) {
                                            themdl_listMuonPhong(list_add_muonphong, stt, chonPhong, ngay, soTiet_vao);
                                        } else {
                                            themdl_listMuonPhong(list_add_muonphong, stt, list_phong_tuan.get(i).get(0), ngay, soTiet_vao);
                                        }
                                    } else if (list_phong_tuan.get(i).size() <= 0) {
                                        Toast.makeText(getActivity(), "Tiết " + soTiet_vao + ", ngày " + chuyenThu_ngay(sThu[i]) + " không còn phòng nào!!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            insert_mysql(list_add_muonphong);
                        } else {
                            Toast.makeText(getActivity(), "Vui lòng chọn ngày ở icon lịch hoặc\n  Chọn 1 trong 7 ngày trong tuần.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {//không có internet
                    thoat();
                    Toast.makeText(getActivity(), "Mất kết nối internet !!!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnThoat_add:
                json_muonPhong.test();
                thoat();
                break;
        }
    }


    @SuppressLint("SetTextI18n")
    public void insert_mysql(List<DTO_MuonPhong> list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_loading_dialog, null);
        LinearLayout layout = view.findViewById(R.id.layout_btn_dialog);
        layout.setVisibility(View.GONE);
        TextView tv = view.findViewById(R.id.tv_load);
        tv.setText("Đợi một chút thôi...");

        builder.setCancelable(false);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        json_muonPhong.xuly_dangKi(list, new Json_MuonPhong.interface_dkMuonPhong() {
            @Override
            public void response_getdata(String response) {
                try {
                    JSONObject status = new JSONObject(response);
                    if (status.getString("status").equals("200")) {
                        Toast.makeText(getActivity(), "Mượn thành công phòng ", Toast.LENGTH_SHORT).show();

                        json_muonPhong.getDLJson_muonPhong(response1 -> {
                            dialog.dismiss();
                            thoat();
                        });

                    } else if (status.getString("status").equals("error")) {
                        db_muonPhong.xoaDl_muonPhong(status.getInt("id"));
                        Toast.makeText(getActivity(), "Mượn không thành công không có phòng", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void error_getdata(VolleyError error) {
                for (DTO_MuonPhong muonPhong : list) {
                    db_muonPhong.xoaDl_muonPhong(muonPhong.getId());
                }
                dialog.dismiss();
            }
        });

    }

    public List<String> dsPhong_hangTuan() {//lọc ra ds phòng trống hàng tuần theo ngày đã chọn
        DB_Phong db_phong = new DB_Phong(requireActivity());
        List<String> list2 = new ArrayList<>();
        for (String ngay : list_ngay_hangTuan) {

            List<String> list = db_phong.layMaPhong();//lam mới ds
            list.removeAll(locPhong_tietNgay(ngay));//lấy ra phòng đã mượn
            list2.addAll(list);
        }
        List<String> list = db_phong.layMaPhong();//lam mới ds
        list.removeAll(list2);//xóa ds phòng đã mượn
        return list;
    }

    //hiển thị ra các thứ được chọn
    @SuppressLint("SetTextI18n")
    public void noiTen_cb() {
        String ten = "";
        int i = 0;
        Collections.sort(list_sBuoi);//sắp xếp theo thứ tự
        for (String str : list_sBuoi) {
            i++;
            ten += str;
            if (i != list_sBuoi.size()) {
                ten += ",";
            }
        }
        if (ktra_cbChon(true) > 1) {//nếu nhiều hơn 2 tên thì nối các thứ không show ngày
            tv_NgayChon.setText(ten);
        } else if (ktra_cbChon(true) < 1) { //nếu có 1 thì hiển thị ngày và thứ
            list_sBuoi.clear();
            boquaNgayCN();//kiểm tra xem hôm nay có phải chủ nhât không
        }
    }

    //lấy ds các ngày của 1 tuần liên tiếp
    public void lay_ngay_hangTuan() {
        list_ngay_hangTuan.clear();
        Calendar calendar_hangTuan = Calendar.getInstance();
        String sNgaykt = tv_NgayketThuc.getText().toString();
        String sNgayMuon = tv_NgayChon.getText().toString();
        Date batDau;
        try {
            if (sNgayMuon.length() > 10) {//nếu chọn checkbox
                String[] ngayBatDau = sNgayMuon.split(",");
                batDau = df.parse(ngayBatDau[1].trim());
            } else {//nếu chọn ngày bằng lịch
                batDau = df.parse(sNgayMuon);
            }
            Date ketThuc = df.parse(sNgaykt);
            assert batDau != null;
            calendar_hangTuan.setTime(batDau);
            while (calendar_hangTuan.getTime().before(ketThuc) || calendar_hangTuan.getTime().equals(ketThuc)) {
                //lấy ra hôm nay cho đến ngày kết thúc
                list_ngay_hangTuan.add(df.format(calendar_hangTuan.getTime()));
                calendar_hangTuan.add(Calendar.DATE, 7);//khoảng cách nhảy ngày là 7
            }
        } catch (ParseException ignored) {
        }

    }

    //khi mở chọn phòng hay hàng tuần chỉ cho phép chọn 1 checkbox thôi
    public void chiChon1Checkbox(CompoundButton compoundButton) {
        CheckBox c = (CheckBox) compoundButton;
        if (swHangTuan.isChecked() || swChonPhong.isChecked()) {//nếu mở 1 trong 2 chức năng thì
            bo_checked(false);//bỏ chọn tất cả
            if (mSelectedChecks.contains(c)) {//contains dùng để kiểm tra checkbox đc chọn
                mSelectedChecks.remove(c);//xóa nó đi
            } else {
                if (mSelectedChecks.size() < 1) {//nếu có nhỏ hơn một
                    mSelectedChecks.add(c);//thì chọn
                } else {
                    mSelectedChecks.remove(0);//bỏ chọn cái trc
                    mSelectedChecks.add(c);//chọn cái mới
                }
                for (CheckBox cb : mChecks) {
                    cb.setChecked(mSelectedChecks.contains(cb));
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void checkbox() {
        if (ktra_cbChon(true) == 1) {//nếu chỉ chọn 1 checkbox
            int position = ktra_cbChon(false);
            String sNgay = chuyenThu_ngay(sThu[position]);
            tv_NgayChon.setText(mChecks.get(position).getText() + ", " + sNgay);
            sw_on_off(list_phong_tuan.get(position).size() > 0);

        } else if (ktra_cbChon(true) > 1) {//nếu chọn nhiều hơn 1 checkbox
            sw_on_off(false);
        } else {
            sw_on_off(false);
        }

    }

    public void sw_on_off(boolean check) {
        if (check) {
            swChonPhong.setEnabled(true);
            swHangTuan.setEnabled(true);
        } else {
            swHangTuan.setEnabled(false);
            swHangTuan.setChecked(false);
            swChonPhong.setEnabled(false);
            swChonPhong.setChecked(false);
        }

    }

    //sự kiện lắng nghe thay đổi checkbox
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        chiChon1Checkbox(compoundButton);
        check_chonNgay = false;
        checkbox();
        switch (compoundButton.getId()) {
            case R.id.cb_t2_add:
                xuLy_checkbox(compoundButton.isChecked(), 0);
                break;
            case R.id.cb_t3_add:
                xuLy_checkbox(compoundButton.isChecked(), 1);
                break;
            case R.id.cb_t4_add:
                xuLy_checkbox(compoundButton.isChecked(), 2);
                break;
            case R.id.cb_t5_add:
                xuLy_checkbox(compoundButton.isChecked(), 3);
                break;
            case R.id.cb_t6_add:
                xuLy_checkbox(compoundButton.isChecked(), 4);
                break;
            case R.id.cb_t7_add:
                xuLy_checkbox(compoundButton.isChecked(), 5);
                break;
        }
        noiTen_cb();
    }

    //bỏ chọn checkbox
    private void bo_checked(boolean check) {
        for (CheckBox c : mChecks) {
            if (check) {//tất cả đc chọn
                c.setChecked(true);
            } else {//bỏ chọn tất cả
                c.setChecked(false);
            }
        }
        list_add_muonphong.clear();
    }

    //thêm dữ liệu vào sql và list
    public void themdl_listMuonPhong(@NonNull List<DTO_MuonPhong> list, int id, String maPhong, String ngayMuon, String tietHoc) {
        DTO_MuonPhong model_muonPhong = new DTO_MuonPhong();
        model_muonPhong.setId(id);
        model_muonPhong.setMaGiangVien(maGv);
        model_muonPhong.setMaPhong(maPhong);
        model_muonPhong.setNgayMuon(ngayMuon);
        model_muonPhong.setTietHoc(tietHoc);
        model_muonPhong.setGhiChu("");
        model_muonPhong.setSync(0);
        db_muonPhong.themDl_muonPhong(model_muonPhong);
        list.add(model_muonPhong);//list này để thêm dl vào mysql dưới dạng json
    }

    //method xử lý khi chọn checkbox
    @SuppressLint("SetTextI18n")
    public void xuLy_checkbox(boolean check, int position) {
        sl_muonPhong_moi = db_muonPhong.soLuong();
        list_ngay_hangTuan.clear();
        tv_NgayketThuc.setText(chuyenThu_ngay(sThu[position]));
        locphong_1tuan();
        if (check) {
            if (list_phong_tuan.get(position).size() <= 0) {
                swChonPhong.setEnabled(false);
            }
            sp_chonPhong.setEnabled(true);
            btnluu.setEnabled(list_phong_tuan.get(position).size() > 0);
            list_sBuoi.add(tenThu_cb(position));
            if (swHangTuan.isChecked()) {
                lay_ngay_hangTuan();
            }
            dl_phong_spinner(list_phong_tuan.get(position));//đổ dlieu vào spinner
        } else {
            list_sBuoi.remove(tenThu_cb(position));
            btnluu.setEnabled(true);
        }
    }

    public void locphong_1tuan() {//lọc phòng 1 trống trong 1 tuần

        if (sl_muonPhong_cu != sl_muonPhong_moi || check_numpic1 || check_numpic2 || check_numpic3) {
            for (int i = 0; i < sThu.length; i++) {
                String ngay = chuyenThu_ngay(sThu[i]);
                list_phong_tuan.get(i).clear();
                list_phong_tuan.get(i).addAll(db_muonPhong.locMuonPhong_theoNgay(ngay, soTiet_vao));//xóa dữ liệu phòng đã mượn trước đó đi và thêm vào phòng chưa mượn
            }
        }
    }

    //Lấy ngày hôm nay
    public String ngayHomnay() {
        calendar = Calendar.getInstance();
        return ngayChon = df.format(calendar.getTime());
    }

    //lấy số lượng checkbox được chọnvà vitri
    public int ktra_cbChon(boolean check) {
        int sl = 0;
        for (int i = 0; i < mChecks.size(); i++) {
            if (mChecks.get(i).isChecked() && check) {
                sl++;
            } else if (mChecks.get(i).isChecked() && !check) {
                sl = i;
                break;
            }
        }
        return sl;
    }

    //lọc phòng theo tiết
    public List<String> locPhong_tietNgay(String ngay) {
        return db_muonPhong.locMuonPhong_theoNgay(ngay, soTiet_vao);
    }


    //chuyển thứ sang ngày
    public String chuyenThu_ngay(String key) {
        for (String ngay : list_ngay) {
            if (key.equals(chuyenNgay_Thu(ngay).toString())) {
                return ngay;
            }
        }
        return "";
    }

    public void getPhong_ngayChon() {
        if (tv_NgayChon.getText().length() <= 10) {
            list_phong_cache_chon.clear();
            list_phong_cache_chon.addAll(locPhong_tietNgay(tv_NgayChon.getText().toString()));
            if (list_phong_cache_chon.size() > 0) {
                sw_on_off(true);
            } else {
                sw_on_off(false);
                Toast.makeText(getActivity(), "Tiết " + soTiet_vao + " không còn phòng nào!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //đổ dữ liệu vào spinner
    public void dl_phong_spinner(List<String> list) {
        if (list.size() > 0) {
            Set<String> set = new LinkedHashSet<>(list);//loại bỏ các chuỗi trùng
            list.clear();
            list.addAll(set);
            Collections.sort(list);//sắp xếp lại
            Adapter_Spinner_DsPhong adapter = new Adapter_Spinner_DsPhong(requireActivity(), list);
            sp_chonPhong.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            if (ktra_cbChon(true) == 1) {
                sw_on_off(false);
                Toast.makeText(getActivity(), "Tiết " + soTiet_vao + " không còn phòng nào!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //lấy danh sách ngày trong 1 tuần bắt đầu từ hôm nay
    @SuppressLint("SimpleDateFormat")
    public List<String> ngayKeTiep() {
        List<String> list_NgayTiep = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            String start = df.format(calendar.getTime());
            list_NgayTiep.add(start);
            calendar.add(Calendar.DATE, 1);
        }
        return list_NgayTiep;
    }

    //chuyển ngày về thứ
    public DayOfWeek chuyenNgay_Thu(String ngay) {
        LocalDate localDate = LocalDate.parse(ngay, DateTimeFormatter.ofPattern("d-M-yyyy"));
        return localDate.getDayOfWeek();
    }

    //method xử lý 3 numberpicker
    void xuLynumberPicker() {
        numPic3.setMaxValue(2);
        numPic3.setDisplayedValues(sBuoi);
        numPic3.setWrapSelectorWheel(true);//true lặp lại thành vòng tròn
        numPic3.setValue(0);

        setNumPic1(1, 5);
        setNumPic2(2, 5);
        if (!check_numpic3) {
            batDau = 1;
            getTiet_chon(batDau + 1);
        }
        //numpick3 chọn buổi
        numPic3.setOnValueChangedListener((numberPicker, i, i1) -> {
            check_numpic3 = true;
            if (i1 == 0) {//sáng
                setNumPic1(1, 5);
                setNumPic2(2, 5);
                if (!check_numpic1) {
                    batDau = 1;
                    getTiet_chon(batDau + 1);
                }
            } else if (i1 == 1) {//chiều
                setNumPic1(6, 11);
                setNumPic2(7, 11);
                if (!check_numpic1) {
                    batDau = 1;
                    getTiet_chon(batDau + 1);
                }
            } else {//cả ngày
                setNumPic1(1, 11);
                setNumPic2(2, 11);
                if (!check_numpic1) {
                    batDau = 1;
                    getTiet_chon(batDau + 1);
                }
            }
        });
    }
//xử lý numpick1 tiết bắt đầu

    public void setNumPic1(int bd, int kt) {
        numPic1.setMinValue(bd);
        numPic1.setMaxValue(kt - 1);
        numPic1.setValue(bd);
        numPic1.setWrapSelectorWheel(true);
        numPic1.setOnValueChangedListener((numberPicker, i, i1) -> {
            setNumPic2((i1 + 1), kt);
            check_numpic1 = true;
            batDau = numberPicker.getValue();
            if (!check_numpic2) {
                getTiet_chon(batDau + 1);
            }
        });
    }

    //xử lý numpick2 tiết kết thúc
    private void setNumPic2(int bd, int kt) {
        numPic2.setMinValue(bd);
        numPic2.setMaxValue(kt);
        numPic2.setValue(bd);
        numPic2.setWrapSelectorWheel(true);//true lặp lại thành vòng tròn
        numPic2.setOnValueChangedListener((numberPicker, i, i1) -> {
            check_numpic2 = true;
            int ketThuc = numberPicker.getValue();
            getTiet_chon(ketThuc);
        });
    }

    //lấy tiết chọn từ 2 numpicker
    public void getTiet_chon(int ketThuc) {
        bo_checked(false);
        soTiet_vao = "";
        batDau = numPic1.getValue();
        ketThuc = numPic2.getValue();
        for (int i = batDau; i <= ketThuc; i++) {
            soTiet_vao += i;
            if (i != ketThuc) {
                soTiet_vao += ",";
            }
        }
        locphong_1tuan();
        getPhong_ngayChon();
        sl_muonPhong_moi = db_muonPhong.soLuong();
        check_numpic1 = false;
        check_numpic2 = false;
        check_numpic3 = false;
        swChonPhong.setChecked(false);
    }

//    //so sánh tiết nhập chọn và tiết trong csdl
//    public boolean ssTiet(String str1, String str2) {
//        String[] parts1 = str1.split(",");
//        String[] parts2 = str2.split(",");
//        for (String tmp1 : parts1)
//            for (String tmp2 : parts2) {
//                if (tmp1.equals(tmp2)) {
//                    return true;
//                }
//            }
//        return false;
//    }

    @SuppressLint("DiscouragedPrivateApi")
    private void showSpinner() {
        swChonPhong.setOnCheckedChangeListener((compoundButton, b) -> {
            chonPhong = "";
            if (swChonPhong.isChecked()) {
                sp_chonPhong.setVisibility(View.VISIBLE);
                for (int i = 0; i < mChecks.size(); i++)
                    if (mChecks.get(i).isChecked()) {
                        dl_phong_spinner(list_phong_tuan.get(i));
                    }
                txtChonPhong.setVisibility(View.GONE);
                if (swHangTuan.isChecked()) {
                    dl_phong_spinner(dsPhong_hangTuan());
                } else if (check_chonNgay) {
                    dl_phong_spinner(list_phong_cache_chon);
                }
            } else {
                txtChonPhong.setVisibility(View.VISIBLE);
                sp_chonPhong.setVisibility(View.GONE);
            }
        });

        swHangTuan.setOnCheckedChangeListener((compoundButton, b) -> {
            if (swHangTuan.isChecked()) {
                layout_Ngaykt.setVisibility(View.VISIBLE);
                layout_hangTuan.setVisibility(View.GONE);
            } else {
                layout_hangTuan.setVisibility(View.VISIBLE);
                layout_Ngaykt.setVisibility(View.GONE);
                tv_NgayketThuc.setText(ngayHomnay());
                if (check_chonNgay) {
                    dl_phong_spinner(list_phong_cache_chon);
                }
            }
        });
    }

    //lấy vtri thứ trong mảng tương ứng với ngày hôm nay
    public int vitriThu() {
        for (int i = 0; i < sThu.length; i++) {
            if ((chuyenNgay_Thu(ngayHomnay()).toString()).equals(sThu[i])) {
                return i;
            }
        }
        return 0;
    }

    @SuppressLint({"NonConstantResourceId", "ClickableViewAccessibility"})
    public void init(View viewlayout) {
        numPic1 = viewlayout.findViewById(R.id.num1);
        numPic2 = viewlayout.findViewById(R.id.num2);
        numPic3 = viewlayout.findViewById(R.id.num3);//buổi

        cb_t2 = viewlayout.findViewById(R.id.cb_t2_add);
        cb_t3 = viewlayout.findViewById(R.id.cb_t3_add);
        cb_t4 = viewlayout.findViewById(R.id.cb_t4_add);
        cb_t5 = viewlayout.findViewById(R.id.cb_t5_add);
        cb_t6 = viewlayout.findViewById(R.id.cb_t6_add);
        cb_t7 = viewlayout.findViewById(R.id.cb_t7_add);

        sp_chonPhong = viewlayout.findViewById(R.id.sp_chonPhong);
        swChonPhong = viewlayout.findViewById(R.id.sw_chonPhong);
        swHangTuan = viewlayout.findViewById(R.id.sw_hangTuan);
        tv_NgayketThuc = viewlayout.findViewById(R.id.tv_ngayKetThuc);
        txtChonPhong = viewlayout.findViewById(R.id.txtChonPhong);
        tv_NgayChon = viewlayout.findViewById(R.id.tvNgaychon_add);
        layout_hangTuan = viewlayout.findViewById(R.id.layout_HangTuan);
        layout_Ngaykt = viewlayout.findViewById(R.id.layout_NgayketThuc);
        ib_lich = viewlayout.findViewById(R.id.ib_chonLich_add);
        ib_ngayketThuc = viewlayout.findViewById(R.id.ib_ngayKetThuc);
        btnluu = viewlayout.findViewById(R.id.btnLuu_add);
        btnThoat = viewlayout.findViewById(R.id.btnThoat_add);

        mChecks.add(cb_t2);
        mChecks.add(cb_t3);
        mChecks.add(cb_t4);
        mChecks.add(cb_t5);
        mChecks.add(cb_t6);
        mChecks.add(cb_t7);

        cb_t2.setOnCheckedChangeListener(this);
        cb_t3.setOnCheckedChangeListener(this);
        cb_t4.setOnCheckedChangeListener(this);
        cb_t5.setOnCheckedChangeListener(this);
        cb_t6.setOnCheckedChangeListener(this);
        cb_t7.setOnCheckedChangeListener(this);

        btnThoat.setOnClickListener(this);
        btnluu.setOnClickListener(this);
        tv_NgayketThuc.setOnClickListener(this);
        tv_NgayChon.setOnClickListener(this);
        ib_ngayketThuc.setOnClickListener(this);
        ib_lich.setOnClickListener(this);


        sp_chonPhong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                chonPhong = "";
                chonPhong = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        tv_NgayChon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                getPhong_ngayChon();
            }
        });
        tv_NgayketThuc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (swHangTuan.isChecked()) {
                    lay_ngay_hangTuan();
                    if (dsPhong_hangTuan().size() <= 0) {
                        Toast.makeText(getActivity(), "Không có phòng nào đủ điều kiện không thể chọn phòng", Toast.LENGTH_SHORT).show();
                        swChonPhong.setEnabled(false);
                        swChonPhong.setChecked(false);
                    } else {
                        dl_phong_spinner(dsPhong_hangTuan());
                    }
                }
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void thoat() {
        FragmentManager manager = requireActivity().getSupportFragmentManager();
        manager.beginTransaction().remove(this).commit();
        ((Activity_Home) requireActivity()).open_fragDsMuonPhong(true, manager);
    }
}
