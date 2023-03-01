package com.leo.nckh.Controller.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.leo.nckh.Model.DataBase.DB_Phong;
import com.leo.nckh.R;

import java.util.List;

public class Adapter_Spinner_DsPhong extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<String> list;
    viewHolder vholder;
    DB_Phong db_phong;

    public Adapter_Spinner_DsPhong(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        db_phong = new DB_Phong(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    public static class viewHolder {
        TextView phong;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.custom_spinner_maphong, viewGroup, false);

            vholder = new viewHolder();
            vholder.phong = view.findViewById(R.id.tv_phong_customsp);
            view.setTag(vholder);
        } else {
            vholder = (viewHolder) view.getTag();
        }
        String str = list.get(i);

        String toaNha = str.substring(0, 2);
        String maPhong = str.substring(2, str.length());
        vholder.phong.setText(toaNha + " - " + maPhong + " ( " + db_phong.getSomay(str) + " máy )");
        return view;

    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {

        if (view == null) {
            view = inflater.inflate(R.layout.custom_spinner_maphong, parent, false);

            vholder = new viewHolder();
            vholder.phong = view.findViewById(R.id.tv_phong_customsp);
            view.setTag(vholder);
        } else {
            vholder = (viewHolder) view.getTag();
        }
        String str = list.get(position);
        vholder.phong.setText(str);
        return view;
    }
}
