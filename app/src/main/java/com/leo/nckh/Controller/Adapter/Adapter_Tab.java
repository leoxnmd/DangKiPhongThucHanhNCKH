package com.leo.nckh.Controller.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leo.nckh.Model.DTO.DTO_MuonPhong;
import com.leo.nckh.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Adapter_Tab extends RecyclerView.Adapter<Adapter_Tab.ViewHolder> implements Filterable {

    Context context;
    LayoutInflater inflater;
    List<DTO_MuonPhong> list;
    List<DTO_MuonPhong> backup;

    public Adapter_Tab(Context context, List<DTO_MuonPhong> list) {
        this.context = context;
        this.list = list;
        backup = new ArrayList<>(list);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_tab, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DTO_MuonPhong muonPhong = list.get(position);

        muonPhong.setChon(checkNgay(muonPhong.getNgayMuon()));

        holder.tvPhong.setText(getphong(muonPhong.getMaPhong()));
        holder.tvNgay.setText(chuyenNgay_Thu(muonPhong.getNgayMuon()));
        holder.tvTiet.setText("Tiết " + muonPhong.getTietHoc());

        if (muonPhong.isChon()) {
            holder.tvPhong.setTextColor(context.getColor(R.color.background));
            holder.tvNgay.setTextColor(context.getColor(R.color.background));
            holder.tvTiet.setTextColor(context.getColor(R.color.background));
        } else {
            holder.tvPhong.setTextColor(context.getColor(R.color.gray));
            holder.tvNgay.setTextColor(context.getColor(R.color.gray));
            holder.tvTiet.setTextColor(context.getColor(R.color.gray));
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public String getphong(String str) {
        int index = str.indexOf( 'P' );

        String toaNha = str.substring(0,index);
        String maPhong = str.substring(index, str.length());
        return "Phòng " + toaNha + "," + maPhong;
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence keyword) {
                ArrayList<DTO_MuonPhong> filtereddata = new ArrayList<>();

                if (keyword.toString().isEmpty())
                    filtereddata.addAll(backup);
                else {
                    for (DTO_MuonPhong obj : backup) {
                        if ((chuyenNgay_Thu(obj.getNgayMuon())).toLowerCase().contains(keyword.toString().toLowerCase()) ||
                                ("Tiết " + obj.getTietHoc()).toLowerCase().contains(keyword.toString().toLowerCase()) ||
                                (getphong(obj.getMaPhong())).toLowerCase().contains(keyword.toString().toLowerCase()) ||
                                obj.getNgayMuon().toLowerCase().contains(keyword.toString().toLowerCase()) ||
                                obj.getTietHoc().toLowerCase().contains(keyword.toString().toLowerCase()) ||
                                obj.getMaPhong().toLowerCase().contains(keyword.toString().toLowerCase()))
                            filtereddata.add(obj);
                    }
                }
//            if (filtereddata.isEmpty()) {
//                //   Toast.makeText(context,"Không tìm thấy",Toast.LENGTH_SHORT).show();
//            }
                FilterResults results = new FilterResults();
                results.values = filtereddata;
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                list.clear();
                list.addAll((ArrayList<DTO_MuonPhong>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }


    public String chuyenNgay_Thu(String ngay) {
        LocalDate localDate = LocalDate.parse(ngay, DateTimeFormatter.ofPattern("d-M-yyyy"));
        String thu = localDate.getDayOfWeek().toString();
        switch (thu) {
            case "MONDAY":
                return "T2, " + ngay;
            case "TUESDAY":
                return "T3, " + ngay;
            case "WEDNESDAY":
                return "T4, " + ngay;
            case "THURSDAY":
                return "T5, " + ngay;
            case "FRIDAY":
                return "T6, " + ngay;
            case "SATURDAY":
                return "T7, " + ngay;
            case "SUNDAY":
                return "CN, " + ngay;
            default:
                return ngay;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public Boolean checkNgay(String ngay) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date hienTai = df.parse(df.format(calendar.getTime()));
            Date dNgay = df.parse(ngay);
            assert dNgay != null;
            if (dNgay.equals(hienTai)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPhong, tvTiet, tvNgay;
        RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPhong = itemView.findViewById(R.id.tv_phong_tab);
            tvTiet = itemView.findViewById(R.id.tv_tiet_tab);
            tvNgay = itemView.findViewById(R.id.tv_ngay_tab);
            layout=itemView.findViewById(R.id.layout_adapter);
        }
    }

}
