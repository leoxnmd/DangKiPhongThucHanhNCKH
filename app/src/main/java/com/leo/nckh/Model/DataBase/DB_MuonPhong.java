package com.leo.nckh.Model.DataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.leo.nckh.Model.DTO.DTO_MuonPhong;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DB_MuonPhong {

    private SQLiteDatabase database;
    DB_CreateTable createTable;

    public DB_MuonPhong(Context context) {
        createTable = new DB_CreateTable(context);
    }


    public void themDl_muonPhong(DTO_MuonPhong mMuonPhong) {
        database = createTable.open();
        ContentValues values = new ContentValues();
        values.put(DB_CreateTable.TB_MUONPHONG_ID, mMuonPhong.getId());
        values.put(DB_CreateTable.TB_MUONPHONG_MAGIANGVIEN, mMuonPhong.getMaGiangVien());
        values.put(DB_CreateTable.TB_MUONPHONG_TIETHOC, mMuonPhong.getTietHoc());
        values.put(DB_CreateTable.TB_MUONPHONG_NGAYMUON, mMuonPhong.getNgayMuon());
        values.put(DB_CreateTable.TB_MUONPHONG_MAPHONG, mMuonPhong.getMaPhong());
        values.put(DB_CreateTable.TB_MUONPHONG_SYNC, mMuonPhong.getSync());
        values.put(DB_CreateTable.TB_MUONPHONG_GHICHU, mMuonPhong.getGhiChu());
        database.insert(DB_CreateTable.TB_MUONPHONG, null, values);

        database.close();
    }

    public boolean suaDl_muonPhong(int id, String ma, String tietHoc, String ngayMuon, String maPhong, String sync, String ghiChu) {
        database = createTable.open();
        ContentValues values = new ContentValues();
        values.put(DB_CreateTable.TB_MUONPHONG_TIETHOC, tietHoc);
        values.put(DB_CreateTable.TB_MUONPHONG_NGAYMUON, ngayMuon);
        values.put(DB_CreateTable.TB_MUONPHONG_MAPHONG, maPhong);
        values.put(DB_CreateTable.TB_MUONPHONG_SYNC, sync);
        values.put(DB_CreateTable.TB_MUONPHONG_GHICHU, ghiChu);
        long check = database.update(DB_CreateTable.TB_MUONPHONG, values, DB_CreateTable.TB_MUONPHONG_MAGIANGVIEN + " = '" + ma + "' and " + DB_CreateTable.TB_MUONPHONG_ID + " = '" + id + "'", null);

        database.close();
        return check != 0;
    }

    public void xoaDl_muonPhong(int id) {
        database = createTable.open();
        database.delete(DB_CreateTable.TB_MUONPHONG, DB_CreateTable.TB_MUONPHONG_ID + " = '" + id + "'", null);
        database.close();
    }

    public void xoaDl() {
        database = createTable.open();
        database.delete(DB_CreateTable.TB_MUONPHONG, null, null);
        database.close();
    }

    public int soLuong() {
        database = createTable.open();
        String truyVan = "Select * from " + DB_CreateTable.TB_MUONPHONG;
        Cursor cursor = database.rawQuery(truyVan, null);
        int count = cursor.getCount();
        cursor.close();
        database.close();
        return count;
    }

    public int check_id(int id) {
        database = createTable.open();
        String truyVan = "Select * from " + DB_CreateTable.TB_MUONPHONG + " where " + DB_CreateTable.TB_MUONPHONG_ID + " = '" + id + "'";
        Cursor cursor = database.rawQuery(truyVan, null);
        int count = cursor.getCount();
        cursor.close();
        database.close();
        return count;
    }
    public int check_id_magv(int id,String ma) {
        database = createTable.open();
        String truyVan = "Select * from " + DB_CreateTable.TB_MUONPHONG + " where " + DB_CreateTable.TB_MUONPHONG_MAGIANGVIEN + " = '" + ma + "' and " + DB_CreateTable.TB_MUONPHONG_ID + " = '" + id + "'";
        Cursor cursor = database.rawQuery(truyVan, null);
        int count = cursor.getCount();
        cursor.close();
        database.close();
        return count;
    }
    @SuppressLint({"Recycle", "Range"})
    public DTO_MuonPhong getdl_id(int id, String ma) {
        database = createTable.open();
        DTO_MuonPhong mMuonPhong = new DTO_MuonPhong();
        String truyVan = "Select * from " + DB_CreateTable.TB_MUONPHONG + " where " + DB_CreateTable.TB_MUONPHONG_MAGIANGVIEN + " = '" + ma + "' and " + DB_CreateTable.TB_MUONPHONG_ID + " = '" + id + "'";
        Cursor cursor = database.rawQuery(truyVan, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            mMuonPhong.setMaPhong(cursor.getString(cursor.getColumnIndex(DB_CreateTable.TB_MUONPHONG_MAPHONG)));
            mMuonPhong.setNgayMuon(cursor.getString(cursor.getColumnIndex(DB_CreateTable.TB_MUONPHONG_NGAYMUON)));
            mMuonPhong.setTietHoc(cursor.getString(cursor.getColumnIndex(DB_CreateTable.TB_MUONPHONG_TIETHOC)));
            cursor.moveToNext();
        }
        database.close();
        return mMuonPhong;
    }

    @SuppressLint({"Recycle", "Range"})
    public List<Integer> layId() {
        database = createTable.open();
        List<Integer> listid = new ArrayList<>();
        String truyVan = "Select * from " + DB_CreateTable.TB_MUONPHONG;
        Cursor cursor = database.rawQuery(truyVan, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            listid.add(cursor.getInt(cursor.getColumnIndex(DB_CreateTable.TB_MUONPHONG_ID)));
            cursor.moveToNext();
        }
        database.close();
        return listid;
    }

    @SuppressLint({"Recycle", "Range"})
    public List<String> locMuonPhong_theoNgay(String ngay, String tiet) {
        database = createTable.open();
        List<String> list = new ArrayList<>();
        String[] str_tiet = tiet.split(",");
        String truyVan = getsql_locPhong(ngay, str_tiet);
        Cursor cursor = database.rawQuery(truyVan, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(cursor.getColumnIndex(DB_CreateTable.TB_MUONPHONG_MAPHONG)));
            cursor.moveToNext();
        }
        database.close();
        return list;
    }

    public String getsql_locPhong(String ngay, String[] tiet) {
        String truyVan = "Select * from " + DB_CreateTable.TB_PHONG + " p where " + DB_CreateTable.TB_PHONG_TINHTRANG + " = '0' and not exists ( select " + DB_CreateTable.TB_MUONPHONG_MAPHONG + " from " + DB_CreateTable.TB_MUONPHONG + " mp "
                + " where p." + DB_CreateTable.TB_PHONG_MAPHONG + " = mp." + DB_CreateTable.TB_MUONPHONG_MAPHONG + " and " +
                DB_CreateTable.TB_MUONPHONG_NGAYMUON + " = '" + ngay + "' and (";
        for (String str : tiet) {
            if (!str.equals(tiet[tiet.length - 1])) {
                truyVan += DB_CreateTable.TB_MUONPHONG_TIETHOC + " like '%" + str + "%' or ";
            } else {
                truyVan += DB_CreateTable.TB_MUONPHONG_TIETHOC + " like '%" + str + "%'))";
            }
        }
        return truyVan;
    }

    @SuppressLint({"Recycle", "Range"})
    public List<DTO_MuonPhong> locMuonPhong_theoAcc(String ma) {
        database = createTable.open();
        List<DTO_MuonPhong> list = new ArrayList<>();
        String truyVan = "Select * from " + DB_CreateTable.TB_MUONPHONG + " where " + DB_CreateTable.TB_MUONPHONG_MAGIANGVIEN + " = '" + ma + "' ";
        Cursor cursor = database.rawQuery(truyVan, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DTO_MuonPhong mMuonPhong = new DTO_MuonPhong();
            mMuonPhong.setMaPhong(cursor.getString(cursor.getColumnIndex(DB_CreateTable.TB_MUONPHONG_MAPHONG)));
            mMuonPhong.setNgayMuon(cursor.getString(cursor.getColumnIndex(DB_CreateTable.TB_MUONPHONG_NGAYMUON)));
            mMuonPhong.setTietHoc(cursor.getString(cursor.getColumnIndex(DB_CreateTable.TB_MUONPHONG_TIETHOC)));
            mMuonPhong.setId(cursor.getInt(cursor.getColumnIndex(DB_CreateTable.TB_MUONPHONG_ID)));
            list.add(mMuonPhong);
            cursor.moveToNext();
        }
        database.close();
        return list;
    }

    @SuppressLint({"Recycle", "Range"})
    public List<DTO_MuonPhong> dsMuonPhongSync() {
        database = createTable.open();
        List<DTO_MuonPhong> list = new ArrayList<>();
        String truyVan = "Select * from " + DB_CreateTable.TB_MUONPHONG + " where " + DB_CreateTable.TB_MUONPHONG_SYNC + " = '1'";
        Cursor cursor = database.rawQuery(truyVan, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DTO_MuonPhong mMuonPhong = new DTO_MuonPhong();
            mMuonPhong.setNgayMuon(cursor.getString(cursor.getColumnIndex(DB_CreateTable.TB_MUONPHONG_NGAYMUON)));
            mMuonPhong.setMaGiangVien(cursor.getString(cursor.getColumnIndex(DB_CreateTable.TB_MUONPHONG_MAGIANGVIEN)));
            mMuonPhong.setId(cursor.getInt(cursor.getColumnIndex(DB_CreateTable.TB_MUONPHONG_ID)));
            list.add(mMuonPhong);
            cursor.moveToNext();
        }
        database.close();
        return list;
    }
    public int soLuongSync_ma(String ma) {
        database = createTable.open();
        String truyVan = "Select * from " + DB_CreateTable.TB_MUONPHONG + " where " + DB_CreateTable.TB_MUONPHONG_SYNC + " = '1' and "+ DB_CreateTable.TB_MUONPHONG_MAGIANGVIEN + " = '" + ma + "' ";
        Cursor cursor = database.rawQuery(truyVan, null);
        int count = cursor.getCount();
        cursor.close();
        database.close();
        return count;
    }

}


