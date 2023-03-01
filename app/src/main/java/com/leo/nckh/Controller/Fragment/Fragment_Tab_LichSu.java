package com.leo.nckh.Controller.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leo.nckh.Controller.Activity.Activity_Home;
import com.leo.nckh.Controller.Adapter.Adapter_Tab;
import com.leo.nckh.Model.DTO.DTO_MuonPhong;
import com.leo.nckh.Model.DataBase.DB_MuonPhong;
import com.leo.nckh.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Fragment_Tab_LichSu extends Fragment {

    View viewlayout;
    RecyclerView recyclerView;
    List<DTO_MuonPhong> list;
    String ma = "";
    Adapter_Tab adapter;
    Fragment_DsMuonPhong.DataViewModel dataViewModel;

    @SuppressLint("NotifyDataSetChanged")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewlayout = inflater.inflate(R.layout.fragment_tab_lichsu, container, false);
        recyclerView = viewlayout.findViewById(R.id.reyclerview_lichsu);

        ma = getMaGv();
        list = check_ds_ngay();
        setHasOptionsMenu(true);

        adapter = new Adapter_Tab(requireContext(), list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        dataViewModel = new ViewModelProvider(requireActivity()).get(Fragment_DsMuonPhong.DataViewModel.class);
        dataViewModel.getQuery().observe(getViewLifecycleOwner(), s -> {
            if (s != null) {
                adapter.getFilter().filter(s);
            }
        });
        return viewlayout;
    }

    private String getMaGv() {
        return ((Activity_Home) requireActivity()).getmaGv();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        list = check_ds_ngay();
        adapter = new Adapter_Tab(requireContext(), list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    public List<DTO_MuonPhong> check_ds_ngay() {
        List<DTO_MuonPhong> list = new ArrayList<>();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy");
        Calendar calendar = Calendar.getInstance();
        try {
            Date hienTai = sdf.parse(sdf.format(calendar.getTime()));
            for (DTO_MuonPhong muonPhong : new DB_MuonPhong(getContext()).locMuonPhong_theoAcc(ma)) {
                Date dNgay = sdf.parse(muonPhong.getNgayMuon());
                assert hienTai != null;
                if (hienTai.after(dNgay)) {
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

}