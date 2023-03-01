package com.leo.nckh.Controller.Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Fragment_DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    int iNam, iThang, iNgay;
    SimpleDateFormat sdf;

    String ngay, ngayvao;
    TextView edNgay;
    boolean check;

    @SuppressLint("SimpleDateFormat")
    public Fragment_DatePicker(TextView edNgay, boolean check) {
        this.edNgay = edNgay;
        this.check = check;
        sdf = new SimpleDateFormat("d-M-yyyy");

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (check) {
            Calendar calendar = Calendar.getInstance();
            iNam = calendar.get(Calendar.YEAR);
            iThang = calendar.get(Calendar.MONTH);
            iNgay = calendar.get(Calendar.DAY_OF_MONTH);
            ngayvao = sdf.format(calendar.getTime());
        } else {
            ngayvao = edNgay.getText().toString();
            String[] parts = ngayvao.split("-");
            iNgay = Integer.parseInt(parts[0]);
            iThang = Integer.parseInt(parts[1]) - 1;
            iNam = Integer.parseInt(parts[2]);
        }

        return new DatePickerDialog(getActivity(), this, iNam, iThang, iNgay);
    }

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        datePicker.setMinDate(2020);
        String sNgay;
        if (i1 + 1 < 10) {
            sNgay = i2 + "-" + (i1 + 1) + "-" + i;
        } else {
            sNgay = i2 + "-" + (i1 + 1) + "-" + i;
        }
        setNgay(sNgay);
        try {
            Date ngayChon = sdf.parse(sNgay);
            Date homnay = sdf.parse(ngayvao);
            assert ngayChon != null;
            if (ngayChon.after(homnay) || ngayChon.equals(homnay)) {
                edNgay.setText(sNgay);
            } else {
                Toast.makeText(getActivity(), "Vui lòng chọn đúng ngày", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


}
