package com.leo.nckh.Model.DataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.leo.nckh.Model.DTO.DTO_Phong;

import java.util.ArrayList;
import java.util.List;

public class DB_Phong {
    private SQLiteDatabase database;
    DB_CreateTable createTable;

    public DB_Phong(Context context) {
        createTable = new DB_CreateTable(context);
    }


    public void themDl_phong(DTO_Phong mPhong) {
        database = createTable.open();
        ContentValues values = new ContentValues();
        values.put(DB_CreateTable.TB_PHONG_MAPHONG, mPhong.getMaPhong());
        values.put(DB_CreateTable.TB_PHONG_TENPHONG, mPhong.getTenPhong());
        values.put(DB_CreateTable.TB_PHONG_SOMAY, mPhong.getSoMay());
        values.put(DB_CreateTable.TB_PHONG_TINHTRANG, mPhong.getTinhTrang());
        values.put(DB_CreateTable.TB_PHONG_GHICHU, mPhong.getGhiChu());
        database.insert(DB_CreateTable.TB_PHONG, null, values);
        database.close();
    }

    public void xoaDl() {
        database = createTable.open();
        database.delete(DB_CreateTable.TB_PHONG, null, null);
        database.close();
    }


//    public void suaDl_phong(String ma, int soMay, int tinhTrang) {
//        ContentValues values = new ContentValues();
//        values.put(DB_CreateTable.TB_PHONG_SOMAY, soMay);
//        values.put(DB_CreateTable.TB_PHONG_TINHTRANG, tinhTrang);
//        values.put(DB_CreateTable.TB_PHONG_GHICHU, 0);
//        database.update(DB_CreateTable.TB_PHONG, values, DB_CreateTable.TB_PHONG_MAPHONG + " = '" + ma + "' ", null);
//    }

//    public void xoaDl_phong(String ma) {
//        database.delete(DB_CreateTable.TB_PHONG, DB_CreateTable.TB_PHONG_MAPHONG + " = '" + ma + "' ", null);
//    }


//    public int soLuong() {
//        String truyVan = "Select * from " + DB_CreateTable.TB_PHONG;
//        Cursor cursor = database.rawQuery(truyVan, null);
//        int count = cursor.getCount();
//        cursor.close();
//        return count;
//    }

    @SuppressLint({"Recycle", "Range"})
    public String getSomay(String ma) {
        database = createTable.open();
        String truyVan = "Select * from " + DB_CreateTable.TB_PHONG + " where " + DB_CreateTable.TB_PHONG_MAPHONG + " = '" + ma + "' ";
        Cursor cursor = database.rawQuery(truyVan, null);
//        cursor.moveToFirst();
        if (cursor.moveToLast()) {
            return cursor.getString(cursor.getColumnIndex(DB_CreateTable.TB_PHONG_SOMAY));
        }
        cursor.close();
        database.close();
        return "";
    }

    @SuppressLint({"Recycle", "Range"})
    public List<String> layMaPhong() {
        database = createTable.open();
        List<String> listMaPhong = new ArrayList<>();
        String truyVan = "Select * from " + DB_CreateTable.TB_PHONG + " where " + DB_CreateTable.TB_PHONG_TINHTRANG + " = 0 ";
        Cursor cursor = database.rawQuery(truyVan, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            listMaPhong.add(cursor.getString(cursor.getColumnIndex(DB_CreateTable.TB_PHONG_MAPHONG)));
            cursor.moveToNext();
        }
        cursor.close();
        database.close();
        return listMaPhong;
    }
}
