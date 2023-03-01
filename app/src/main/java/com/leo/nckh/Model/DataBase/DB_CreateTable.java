package com.leo.nckh.Model.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_CreateTable extends SQLiteOpenHelper {

    public static final String TB_MUONPHONG = "MuonPhong";

    public static final String TB_MUONPHONG_ID = "ID";
    public static final String TB_MUONPHONG_MAGIANGVIEN = "MaGiangVien";
    public static final String TB_MUONPHONG_NGAYMUON = "NgayMuon";
    public static final String TB_MUONPHONG_TIETHOC = "TietHoc";
    public static final String TB_MUONPHONG_MAPHONG = "MaPhong";
    public static final String TB_MUONPHONG_GHICHU = "GhiChu";
    public static final String TB_MUONPHONG_SYNC = "Sync";

    public static final String TB_PHONG = "Phong";

    public static final String TB_PHONG_MAPHONG = "MaPhong";
    public static final String TB_PHONG_TENPHONG = "TenPhong";
    public static final String TB_PHONG_SOMAY = "SoMay";
    public static final String TB_PHONG_TINHTRANG = "TinhTrang";
    public static final String TB_PHONG_GHICHU = "GhiChu";


    public DB_CreateTable(Context context) {
        super(context, "QuanLyMuonPhong", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String tb_MuonPhong = "CREATE TABLE " + TB_MUONPHONG + " ( " + TB_MUONPHONG_ID + " INTEGER primary key, "
                + TB_MUONPHONG_MAGIANGVIEN + " TEXT not null, " + TB_MUONPHONG_NGAYMUON + " TEXT, " + TB_MUONPHONG_TIETHOC + " TEXT, "
                + TB_MUONPHONG_MAPHONG + " TEXT, " + TB_MUONPHONG_GHICHU + " TEXT," + TB_MUONPHONG_SYNC + " INTEGER, unique (" + TB_MUONPHONG_ID + " , " + TB_MUONPHONG_MAGIANGVIEN + " ));";

        String tb_Phong = "CREATE TABLE " + TB_PHONG + " ( " + TB_PHONG_MAPHONG + " TEXT primary key, "
                + TB_PHONG_TENPHONG + " TEXT not null, " + TB_PHONG_SOMAY + " INTEGER, " + TB_PHONG_TINHTRANG + " INTEGER, "
                + TB_PHONG_GHICHU + " TEXT );";

        sqLiteDatabase.execSQL(tb_MuonPhong);
        sqLiteDatabase.execSQL(tb_Phong);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public SQLiteDatabase open() {
        return this.getWritableDatabase();
    }


}
