package com.leo.nckh.Controller.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.leo.nckh.Controller.Activity.Activity_Home;
import com.leo.nckh.Controller.Adapter.Adapter_Tab;
import com.leo.nckh.Model.DTO.DTO_MuonPhong;
import com.leo.nckh.Model.DataBase.DB_MuonPhong;
import com.leo.nckh.Model.JsonVolley.InternetState;
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
import java.util.Objects;

public class Fragment_Tab_DsMuonPhong extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View viewlayout;
    RecyclerView recyclerView;
    List<DTO_MuonPhong> list;

    String ma = "";
    Adapter_Tab adapter;
    Fragment_DsMuonPhong.DataViewModel dataViewModel;
    SwipeRefreshLayout refreshLayout;

    Json_MuonPhong json_muonPhong;
    DB_MuonPhong db_muonPhong;
    SharedPreferences prefs;
    int slcu;
    int slmoi;
    int sl_sync;

    @SuppressLint("NotifyDataSetChanged")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewlayout = inflater.inflate(R.layout.fragment_tab_dsmuonphong, container, false);
        recyclerView = viewlayout.findViewById(R.id.reyclerview_dsMuonphong);
        refreshLayout = viewlayout.findViewById(R.id.refreshList_dsMuonphong);
        refreshLayout.setColorSchemeResources(R.color.background);

        ma = getMaGv();
        json_muonPhong = new Json_MuonPhong(getActivity());
        db_muonPhong = new DB_MuonPhong(getActivity());

        lammoi();

        dataViewModel = new ViewModelProvider(requireActivity()).get(Fragment_DsMuonPhong.DataViewModel.class);
        dataViewModel.getQuery().observe(getViewLifecycleOwner(), s -> {
            if (s != null) {
                adapter.getFilter().filter(s);
            }
        });
        refreshLayout.setOnRefreshListener(this);
        slcu = db_muonPhong.soLuong();

        return viewlayout;
    }

    private String getMaGv() {
        return ((Activity_Home) requireActivity()).getmaGv();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        json_muonPhong.kiemtraInternet();
        lammoi();
       // ktra_dl_tb(slmoi, sl_sync);
        super.onResume();
    }

    public void ktra_dl_tb(int sl_sqlite, int sl_sync) {
        prefs = requireActivity().getSharedPreferences("InternetState_intro", Context.MODE_PRIVATE);
        Handler handler = new Handler();
        final int delay = 3500; // 1000 milliseconds == 1 second

        if (slcu < sl_sqlite || sl_sync > 0) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    boolean check = prefs.getBoolean("state", false);
                    if (check) {
                        json_muonPhong.getDLJson_muonPhong_acc(ma, sl_sqlite);
                    }
                    handler.postDelayed(this, delay);
                }
            }, delay);
            lammoi();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public List<DTO_MuonPhong> check_ds_ngay() {
        List<DTO_MuonPhong> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy");
        Calendar calendar = Calendar.getInstance();
        try {
            Date hienTai = sdf.parse(sdf.format(calendar.getTime()));
            for (DTO_MuonPhong muonPhong : new DB_MuonPhong(getContext()).locMuonPhong_theoAcc(ma)) {
                Date dNgay = sdf.parse(muonPhong.getNgayMuon());
                assert hienTai != null;
                if (hienTai.before(dNgay) || hienTai.equals(dNgay)) {
                    list.add(muonPhong);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        list.sort((muonPhong1, muonPhong2) -> {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat format = new SimpleDateFormat("d-M-yyyy");
            try {
                Date date1 = format.parse(muonPhong1.getNgayMuon());
                Date date2 = format.parse(muonPhong2.getNgayMuon());
                assert date1 != null;
                return date1.compareTo(date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });
        return list;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void lammoi() {
        list = check_ds_ngay();
        adapter = new Adapter_Tab(requireContext(), list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        slmoi = db_muonPhong.soLuong();
        sl_sync = db_muonPhong.soLuongSync_ma(ma);
    }


    @Override
    public void onRefresh() {
        json_muonPhong.getDL_muonPhong(new Json_MuonPhong.interface_MuonPhong() {
            @Override
            public void response_getdata(JSONObject response) {
                try {
                    if (response.getInt("status") == 200) {
                        lammoi();
                        refreshLayout.setRefreshing(false);
                    } else if (response.getInt("status") == 204) {
                        lammoi();
                        refreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "Không có dữ liệu mới !!!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error_getdata(VolleyError error) {
                refreshLayout.setRefreshing(false);
            }
        });

    }

}