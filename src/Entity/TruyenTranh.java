/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

import java.math.BigDecimal;

/**
 *
 * @author K.J
 */
public class TruyenTranh {
    private String maTruyen;
    private String tenTruyen;
    private int maTacGia;
    private int maTheLoai;
    private BigDecimal giaBan;
    private int soLuong;
    private String moTa;
    private int maNcc;

    public TruyenTranh() {
    }        

    public TruyenTranh(String maTruyen, String tenTruyen, int maTacGia, int maTheLoai, BigDecimal giaBan, int soLuong, String moTa, int maNcc) {
        this.maTruyen = maTruyen;
        this.tenTruyen = tenTruyen;
        this.maTacGia = maTacGia;
        this.maTheLoai = maTheLoai;
        this.giaBan = giaBan;
        this.soLuong = soLuong;
        this.moTa = moTa;
        this.maNcc = maNcc;
    }

    public String getMaTruyen() {
        return maTruyen;
    }

    public void setMaTruyen(String maTruyen) {
        this.maTruyen = maTruyen;
    }

    public String getTenTruyen() {
        return tenTruyen;
    }

    public void setTenTruyen(String tenTruyen) {
        this.tenTruyen = tenTruyen;
    }

    public int getMaTacGia() {
        return maTacGia;
    }

    public void setMaTacGia(int maTacGia) {
        this.maTacGia = maTacGia;
    }

    public int getMaTheLoai() {
        return maTheLoai;
    }

    public void setMaTheLoai(int maTheLoai) {
        this.maTheLoai = maTheLoai;
    }

    public BigDecimal getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(BigDecimal giaBan) {
        this.giaBan = giaBan;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public int getMaNcc() {
        return maNcc;
    }

    public void setMaNcc(int maNcc) {
        this.maNcc = maNcc;
    }
    
    
    
    
}
