package com.leo.nckh.Model.JsonVolley;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.leo.nckh.Controller.Notification.NotificationBasic;
import com.leo.nckh.Model.DataBase.DB_MuonPhong;
import com.leo.nckh.Model.DTO.DTO_MuonPhong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Json_MuonPhong {
    Context context;
    RequestQueue queue;
    DB_MuonPhong db_muonPhong;
    SharedPreferences.Editor editor;
    List<Integer> list_id_sqlite;
    List<Integer> list_id_mysql;

    public Json_MuonPhong(Context context) {
        this.context = context;
        queue = Volley.newRequestQueue(context);
        db_muonPhong = new DB_MuonPhong(context);
    }

    public static String URL_MUONPHONG = URL_Json.URL_Json + "ds_lichmuonphong.php";
    public static String URL_INSERT = URL_Json.URL_Json + "them_lichmuonphong.php";
    public static String URL_MUONPHONG_RESULT_SYNC = URL_Json.URL_Json + "sync_muonphong_thanhcong.php?id=";
    public static String URL_MUONPHONG_SYNC = URL_Json.URL_Json + "sync_muonphong.php";

    public void getDL_muonPhong(interface_MuonPhong listener) {
        kiemtraInternet();
        new Json_TaiKhoan(context).getInfoAdmin();
        new Json_Phong(context).getDLJson_phong();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_MUONPHONG, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("status") == 200) {
                        JSONArray JArr_phong = response.getJSONArray("data");
                        db_muonPhong.xoaDl();
                        for (int i = 0; i < JArr_phong.length(); i++) {
                            JSONObject JObj_Muonphong = JArr_phong.getJSONObject(i);
                            themdlsqlite(JObj_Muonphong);
                        }
                    } else if (response.getInt("status") == 204) {
                        db_muonPhong.xoaDl();
                        Log.d("TAGleo", "Không có dữ liệu");
                    }
                    listener.response_getdata(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
            listener.error_getdata(error);
            String message = "";
            if (error instanceof NetworkError) {
                message = "Không thể kết nối Internet ...\n Vui lòng kiểm tra kết nối của bạn!";
            } else if (error instanceof ServerError) {
                message = "Không thể tìm thấy máy chủ.\n Vui lòng thử lại sau một thời gian!!";//lỗi 404
            } else if (error instanceof AuthFailureError) {
                message = "Không thể kết nối Internet ...\n Vui lòng kiểm tra kết nối của bạn!";
            } else if (error instanceof ParseError) {
                message = "Lỗi phân tích cú pháp!\n Vui lòng thử lại sau một thời gian!!";//sai tên bảng
            } else if (error instanceof TimeoutError) {//không có phản hồi
                message = "Tốc độ Internet quá chậm !\n Xin vui lòng kiểm tra kết nối Internet của bạn.";
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                2500,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

    public void getDLJson_muonPhong_acc(String maGv, int sl_sqlite) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_MUONPHONG, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                list_id_mysql = new ArrayList<>();
                try {
                    if (response.getInt("status") == 200) {
                        JSONArray JArr_phong = response.getJSONArray("data");
                        if (sl_sqlite <= 0) {//nếu sqlite không có dữ liệu
                            for (int i = 0; i < JArr_phong.length(); i++) {
                                JSONObject JObj_Muonphong = JArr_phong.getJSONObject(i);
                                themdlsqlite(JObj_Muonphong);
                            }
                        } else {//nếu có dữ liệu r
                            if (response.getInt("soluong") != sl_sqlite) {
                                //nếu số lượng sqlite và mtsql không bằng nhau
                                for (int i = 0; i < JArr_phong.length(); i++) {
                                    JSONObject JObj_Muonphong = JArr_phong.getJSONObject(i);
                                    if (JObj_Muonphong.getString("MaGiangVien").equals(maGv)) {
                                        int id = JObj_Muonphong.getInt("ID");
                                        if (db_muonPhong.check_id_magv(id, maGv) <= 0) {//kiểm tra id này đã có trong sqlite chưa
                                            themdlsqlite(JObj_Muonphong);
                                            list_id_mysql.add(id);//thêm vào list để xóa
                                        } else {//nếu sqlite tồn tại id mà my sql không có
                                            list_id_mysql.add(id);//thêm vào list để xóa
                                        }
                                    }
                                }
                                xoa(list_id_mysql, maGv);
                            }
                            sync_dlEdit(maGv);
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

    public void getDLJson_muonPhong(interface_MuonPhongEdit inter) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_MUONPHONG, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                list_id_mysql = new ArrayList<>();
                try {
                    if (response.getInt("status") == 200) {
                        JSONArray JArr_phong = response.getJSONArray("data");
                        if (db_muonPhong.soLuong() <= 0) {//nếu sqlite không có dữ liệu
                            for (int i = 0; i < JArr_phong.length(); i++) {
                                JSONObject JObj_Muonphong = JArr_phong.getJSONObject(i);
                                themdlsqlite(JObj_Muonphong);
                            }
                        } else {//nếu có dữ liệu r
                            if (response.getInt("soluong") != db_muonPhong.soLuong()) {
                                //nếu số lượng sqlite và mtsql không bằng nhau
                                for (int i = 0; i < JArr_phong.length(); i++) {
                                    JSONObject JObj_Muonphong = JArr_phong.getJSONObject(i);
                                    int id = JObj_Muonphong.getInt("ID");
                                    if (db_muonPhong.check_id(id) <= 0) {//kiểm tra id này đã có trong sqlite chưa
                                        themdlsqlite(JObj_Muonphong);
                                        list_id_mysql.add(id);//thêm vào list để xóa
                                    } else {//nếu sqlite tồn tại id mà my sql không có
                                        list_id_mysql.add(id);//thêm vào list để xóa
                                    }
                                }
                                xoa(list_id_mysql, "");
                            } else {
                                sync_dlEdit("");
                            }
                        }
                        inter.response_getdata(response);
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                2500,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

    public void xoa(List<Integer> list, String maGv) {
        list_id_sqlite = db_muonPhong.layId();
        list_id_sqlite.removeAll(list);
        if (list_id_sqlite.size() > 0)
            for (Integer id : list_id_sqlite) {
//                if (!maGv.equals("")) {
//                    DTO_MuonPhong muonPhong = db_muonPhong.getdl_id(id, maGv);
//                    if (muonPhong != null)
//                        if (muonPhong.getMaPhong() != null) {
//                            new NotificationBasic(context).thongBao(id, "Ngày " + muonPhong.getNgayMuon() + " đã huỷ lịch", "Ngày " + muonPhong.getNgayMuon() + ", tiết " + muonPhong.getTietHoc() + ", phòng " + muonPhong.getMaPhong() + " đã bị huỷ lịch !!");
//                        }
//                }
                db_muonPhong.xoaDl_muonPhong(id);
            }
    }

    public void themdlsqlite(JSONObject JObj_Muonphong) {
        DTO_MuonPhong mMuonPhong = new DTO_MuonPhong();
        try {
            mMuonPhong.setId(JObj_Muonphong.getInt("ID"));
            mMuonPhong.setMaGiangVien(JObj_Muonphong.getString("MaGiangVien"));
            mMuonPhong.setMaPhong(JObj_Muonphong.getString("MaPhong"));
            mMuonPhong.setNgayMuon(JObj_Muonphong.getString("NgayMuon"));
            mMuonPhong.setTietHoc(JObj_Muonphong.getString("TietHoc"));
            mMuonPhong.setGhiChu(JObj_Muonphong.getString("GhiChu"));
            mMuonPhong.setSync(JObj_Muonphong.getInt("Sync"));
            db_muonPhong.themDl_muonPhong(mMuonPhong);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void sync_dlEdit(String maGvien) {
        //mục đích kiểm tra dữ liệu có được admin thay đổi gì không
        //nếu có thì cập nhập lại dữ liệu sqlite
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_MUONPHONG_SYNC, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //NotificationBasic t = new NotificationBasic(context);
                    if (response.getInt("status") == 200) {
                        JSONArray JArr_phong = response.getJSONArray("data");
                        if (Integer.parseInt(response.getString("soluong")) > 0) {//nếu số cột sync >0 thì cập nhập lại dữ liệu sqlite lại
                            for (int i = 0; i < JArr_phong.length(); i++) {
                                JSONObject JObj_Muonphong = JArr_phong.getJSONObject(i);
                                int id = JObj_Muonphong.getInt("ID");
                                String maGv = JObj_Muonphong.getString("MaGiangVien");
                                String maPhong = JObj_Muonphong.getString("MaPhong");
                                String ngayMuon = JObj_Muonphong.getString("NgayMuon");
                                String tietHoc = JObj_Muonphong.getString("TietHoc");
                                String ghiChu = JObj_Muonphong.getString("GhiChu");
                                String sync = JObj_Muonphong.getString("Sync");
                                if (maGv.equals(maGvien)) {
                                    //     String[] str = dlThaydoi_notifi(db_muonPhong.getdl_id(id, maGv), ngayMuon, tietHoc, maPhong);
                                    //     t.thongBao(id, str[0], str[1]);
                                    boolean check = db_muonPhong.suaDl_muonPhong(id, maGv, tietHoc, ngayMuon, maPhong, "0", ghiChu);
                                    if (check) {
                                        sync_muonphong_resultPHP(String.valueOf(id));
                                    }
                                } else {
                                    db_muonPhong.suaDl_muonPhong(id, maGv, tietHoc, ngayMuon, maPhong, sync, ghiChu);
                                }
                            }
                            Log.d("TAGleo", " cập nhâp thanh công");
                        }
                    } else if (response.getString("status").equals("error")) {
                        Log.d("TAGleo", "có lỗi");
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


//    public String[] dlThaydoi_notifi(DTO_MuonPhong muonPhong, String ngay, String tiet, String phong) {
//        String[] str = new String[2];
//        if (!muonPhong.getMaPhong().equals(phong) && muonPhong.getNgayMuon().equals(ngay) && muonPhong.getTietHoc().equals(tiet)) {
//            str[1] = "Phòng " + muonPhong.getMaPhong() + " Được chuyển sang phòng " + phong;
//            str[0] = "Ngày " + ngay + " và Tiết " + tiet + " đã chuyển sang phòng khác";
//        } else if (muonPhong.getMaPhong().equals(phong) && !muonPhong.getNgayMuon().equals(ngay) && muonPhong.getTietHoc().equals(tiet)) {
//            str[1] = "Ngày " + muonPhong.getNgayMuon() + " Được chuyển sang ngày " + ngay;
//            str[0] = "Phòng " + phong + " và Tiết " + tiet + " đã chuyển sang ngày khác";
//        } else if (muonPhong.getMaPhong().equals(phong) && muonPhong.getNgayMuon().equals(ngay) && !muonPhong.getTietHoc().equals(tiet)) {
//            str[1] = "Tiết " + muonPhong.getNgayMuon() + " Được chuyển sang tiết " + tiet;
//            str[0] = "Ngày " + ngay + " và phòng " + phong + " đã chuyển sang tiết khác";
//        } else if (!muonPhong.getMaPhong().equals(phong) && muonPhong.getNgayMuon().equals(ngay) && !muonPhong.getTietHoc().equals(tiet)) {
//            str[1] = "Phòng " + muonPhong.getMaPhong() + " Được chuyển sang phòng " + phong + " và, tiết " + muonPhong.getTietHoc() + " Được chuyển sang tiết " + tiet;
//            str[0] = "Ngày " + muonPhong.getNgayMuon() + " Dữ liệu thay đổi";
//        } else if (!muonPhong.getMaPhong().equals(phong) && !muonPhong.getNgayMuon().equals(ngay) && muonPhong.getTietHoc().equals(tiet)) {
//            str[1] = "Phòng " + muonPhong.getMaPhong() + " Được chuyển sang phòng " + phong + " và, ngày " + muonPhong.getNgayMuon() + " Được chuyển sang ngày " + ngay;
//            str[0] = "Ngày " + muonPhong.getNgayMuon() + " Dữ liệu thay đổi";
//        } else if (muonPhong.getMaPhong().equals(phong) && !muonPhong.getNgayMuon().equals(ngay) && !muonPhong.getTietHoc().equals(tiet)) {
//            str[1] = "Tiết " + muonPhong.getTietHoc() + " Được chuyển sang tiết " + tiet + " và, ngày " + muonPhong.getNgayMuon() + " Được chuyển sang ngày " + ngay;
//            str[0] = "Ngày " + muonPhong.getNgayMuon() + " Dữ liệu thay đổi";
//        } else {
//            str[0] = "Ngày " + muonPhong.getNgayMuon() + " Dữ liệu thay đổi";
//            str[1] = "Dữ liệu đã thay đổi";
//        }
//
//        return str;
//    }

    public void sync_muonphong_resultPHP(String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MUONPHONG_RESULT_SYNC + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject status = new JSONObject(response);
                    if (status.getString("status").equals("error")) {
                        Log.d("TAGleo", "có lỗi");
                    } else if (status.getString("status").equals("200")) {
                        Log.d("TAGleo", "Thành công");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parms = new HashMap<String, String>();
                parms.put("id", id);
                return parms;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @SuppressLint("CommitPrefEdits")
    public void xuly_dangKi(List<DTO_MuonPhong> list, interface_dkMuonPhong listener) {
        Set<DTO_MuonPhong> set = new LinkedHashSet<>(list);
        list.clear();
        list.addAll(set);
        String gson_List = new Gson().toJson(list);

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL_INSERT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.response_getdata(response);
            }
        }, error -> {
            listener.error_getdata(error);
            String message = "";
            if (error instanceof NetworkError) {
                message = "Không thể kết nối Internet ...\n Vui lòng kiểm tra kết nối của bạn!";
            } else if (error instanceof ServerError) {
                message = "Không thể tìm thấy máy chủ.\n Vui lòng thử lại sau một thời gian!!";
            } else if (error instanceof AuthFailureError) {
                message = "Không thể kết nối Internet ...\n Vui lòng kiểm tra kết nối của bạn!";
            } else if (error instanceof ParseError) {
                message = "Lỗi phân tích cú pháp!\n Vui lòng thử lại sau một thời gian!!";
            } else if (error instanceof TimeoutError) {//không có phản hồi
                message = "Tốc độ Internet quá chậm !\n Xin vui lòng kiểm tra kết nối Internet của bạn.";
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("them_muonphong", gson_List);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }


    public void kiemtraInternet() {
        editor = context.getSharedPreferences("InternetState", Context.MODE_PRIVATE).edit();
        StringRequest request = new StringRequest(Request.Method.GET, "https://stackoverflow.com/", response -> {
            editor.putBoolean("state", true);
            editor.apply();
        }, error -> {
            editor.putBoolean("state", false);
            editor.apply();
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                1500,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);

    }

    public void test() {
        StringRequest request = new StringRequest(Request.Method.GET, "https://nmdfstack.000webhostapp.com/nckh/notify.php/", response -> {

        }, error -> {

        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                1500,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);

    }

    public interface interface_dkMuonPhong {
        void response_getdata(String response);

        void error_getdata(VolleyError error);
    }

    public interface interface_MuonPhong {
        void response_getdata(JSONObject response);

        void error_getdata(VolleyError error);
    }

    public interface interface_MuonPhongEdit {
        void response_getdata(JSONObject response);
    }
}
