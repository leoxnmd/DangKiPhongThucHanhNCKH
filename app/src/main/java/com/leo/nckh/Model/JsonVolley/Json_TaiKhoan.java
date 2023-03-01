package com.leo.nckh.Model.JsonVolley;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.leo.nckh.Controller.Activity.Activity_DangNhap;
import com.leo.nckh.Controller.Activity.Activity_Home;
import com.leo.nckh.Model.DTO.DTO_TaiKhoan;
import com.leo.nckh.Model.DataBase.DB_MuonPhong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Json_TaiKhoan {
    Context context;
    RequestQueue queue;
    SharedPreferences sharedPref;


    public static String URL_TAIKHOAN = URL_Json.URL_Json + "Login.php";
    public static String URL_DOIPASS = URL_Json.URL_Json + "DoiMatKhau.php";
    public static String URl_QUENMK = URL_Json.URL_Json + "SendMailVerifyForgotPassword.php";
    public static String URl_INFOAD = URL_Json.URL_Json + "infoQuanLy.php";

    public Json_TaiKhoan(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences("dsPhong", Context.MODE_PRIVATE);
        queue = Volley.newRequestQueue(context);
    }

    public void getData(DTO_TaiKhoan tk, interface_taiKhoan listener) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_TAIKHOAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("StatusCode");
                    if (status == 200) {
                        DTO_TaiKhoan taiKhoan = new DTO_TaiKhoan();
                        JSONArray arrTK = jsonObject.getJSONArray("Data");
                        JSONObject jsonObject1 = arrTK.getJSONObject(0);

                        taiKhoan.setMaGiangVien(jsonObject1.getString("MaGV"));
                        taiKhoan.setPassWord(jsonObject1.getString("PassWord"));
                        taiKhoan.setQuyenDN(jsonObject1.getInt("Quyen"));

                        taiKhoan.setHoTen(jsonObject1.getString("TenGV"));
                        taiKhoan.setEmail(jsonObject1.getString("Email"));
                        taiKhoan.setSDT(jsonObject1.getString("SDT"));
                        taiKhoan.setQuyenDN(jsonObject1.getInt("Quyen"));
                        listener.response_getdata(taiKhoan);
                    } else {
                        Toast.makeText(context, jsonObject.getInt("StatusCode") + jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                        listener.response_error(response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                error -> Toast.makeText(context, "Lỗi mạng " + error, Toast.LENGTH_SHORT).show()) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<>();
                param.put("taikhoan", tk.getMaGiangVien());
                param.put("pass", tk.getPassWord());
                param.put("token", tk.getToken());
                return param;
            }
        };
        queue.add(stringRequest);

    }

    public void setData(DTO_TaiKhoan taiKhoan, interface_dmk listener) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DOIPASS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("StatusCode");
                    if (status == 200) {
                        Toast.makeText(context, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, jsonObject.getInt("StatusCode") + jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                    }
                    listener.response_getdata(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("Loi", e + "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, "Lỗi mạng " , Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("pass", taiKhoan.getPassWord());
                param.put("idtk", taiKhoan.getMaGiangVien() + "");
                return param;
            }
        };
        queue.add(stringRequest);
    }

    public void json_quenMatKhau(String maGv, interface_quenmk listener) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URl_QUENMK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    listener.response_getdata(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> Toast.makeText(context, "Lỗi mạng " , Toast.LENGTH_SHORT).show()
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<>();
                param.put("taikhoan", maGv);
                return param;
            }
        };
        queue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void getInfoAdmin() {
        List<DTO_TaiKhoan> list = new ArrayList<>();

        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.GET, URl_INFOAD, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("status") == 200) {
                        JSONArray JArr_Muonphong = response.getJSONArray("data");
                        JSONObject Objadmin = JArr_Muonphong.getJSONObject(0);
                        DTO_TaiKhoan taiKhoan = new DTO_TaiKhoan();
                        taiKhoan.setHoTen(Objadmin.getString("HoTen"));
                        taiKhoan.setEmail(Objadmin.getString("Email"));
                        taiKhoan.setSDT(Objadmin.getString("SDT"));
                        taiKhoan.setMaGiangVien(Objadmin.getString("MaGiangVien"));
                        list.add(taiKhoan);

                        SharedPreferences.Editor editor = sharedPref.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(list);
                        editor.putString("Admin", json);
                        editor.apply();

                    } else if (response.getInt("status") == 204) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(list);
                        editor.putString("Admin", json);
                        editor.apply();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {}
        );
        queue.add(jsonObject);

    }

    public interface interface_taiKhoan {
        void response_getdata(DTO_TaiKhoan taiKhoan);

        void response_error(String response);

    }

    public interface interface_quenmk {
        void response_getdata(String taiKhoan) throws JSONException;

    }

    public interface interface_dmk {
        void response_getdata(String response) throws JSONException;

    }
}
