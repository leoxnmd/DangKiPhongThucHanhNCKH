package com.leo.nckh.Model.DTO;

import java.io.Serializable;

public class DTO_TaiKhoan extends DTO_GiangVien implements Serializable {
    private String passWord;
    private int quyenDN;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public int getQuyenDN() {
        return quyenDN;
    }

    public void setQuyenDN(int quyenDN) {
        this.quyenDN = quyenDN;
    }
}
