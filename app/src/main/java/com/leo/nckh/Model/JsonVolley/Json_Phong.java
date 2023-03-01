package com.leo.nckh.Model.JsonVolley;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.leo.nckh.Model.DataBase.DB_Phong;
import com.leo.nckh.Model.DTO.DTO_Phong;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Json_Phong {
    Context context;
    DB_Phong db_phong;
    public static String URL_PHONG = URL_Json.URL_Json + "phong.php";

    public Json_Phong(Context context) {
        this.context = context;
        db_phong = new DB_Phong(context);
    }

    public void getDLJson_phong() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_PHONG, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    db_phong.xoaDl();
                    if (response.getInt("status") == 200) {
                        JSONArray JArr_phong = response.getJSONArray("data");
                        for (int i = 0; i < JArr_phong.length(); i++) {
                            JSONObject JObj_phong = JArr_phong.getJSONObject(i);
                            DTO_Phong mPhong = new DTO_Phong();
                            mPhong.setMaPhong(JObj_phong.getString("MaPhong"));
                            mPhong.setTenPhong(JObj_phong.getString("TenPhong"));
                            mPhong.setSoMay(JObj_phong.getString("SoMay"));
                            mPhong.setTinhTrang(JObj_phong.getString("TinhTrang"));
                            mPhong.setGhiChu(JObj_phong.getString("GhiChu"));
                            db_phong.themDl_phong(mPhong);
                        }
                    } else if (response.getInt("status") == 204) {
                        Log.d("TAGleo", "Không có dữ liệu");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);
    }

//    public void xoa(List<String> list) {
//        List<String> list_phong = db_phong.layMaPhong();
//        list_phong.removeAll(list);
//        for (String p : list_phong) {
//            db_phong.xoaDl_phong(p);
//        }
//
//    }

//    public void syncDLJson_phong() {
//        //mục đích kiểm tra dữ liệu có được admin thay đổi gì không
//        //nếu có thì cập nhập lại dữ liệu sqlite
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_PHONG_SYNC, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    if (Integer.parseInt(response.getString("soluong")) > 0) {//nếu số cột sync >0 thì cập nhập lại dữ liệu sqlite lại
//                        JSONArray JArr_phong = response.getJSONArray("phong");
//                        for (int i = 0; i < JArr_phong.length(); i++) {
//                            JSONObject JObj_phong = JArr_phong.getJSONObject(i);
//                            String ma = (JObj_phong.getString("MaPhong"));
//                            int soMay = Integer.parseInt(JObj_phong.getString("SoMay"));
//                            int tinhTrang = Integer.parseInt(JObj_phong.getString("TinhTrang"));
//                            db_phong.suaDl_phong(ma, soMay, tinhTrang);
//                          //  sync_phong_resultPHP(ma);//sửa lại mã sync khi cập nhập lại sqlite thành công
//                        }
//                        Log.d("TAGleo", " cập nhâp thanh công");
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, error -> {
//        });
//        RequestQueue queue = Volley.newRequestQueue(context);
//        queue.add(jsonObjectRequest);
//    }


//    public void sync_phong_resultPHP(String maPhong) {
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PHONG_RESULT_SYNC + maPhong, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//            }
//        }, error -> {
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> parms = new HashMap<String, String>();
//                parms.put("maPhong", maPhong);
//                return parms;
//            }
//        };
//        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        requestQueue.add(stringRequest);
//    }


}
