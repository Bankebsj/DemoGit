/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

/**
 *
 * @author canbi
 */
public class KhachHang {
     private int maKhachHang; // Mã khách hàng
    private String tenKhachHang; // Tên khách hàng
    private String soDienThoai; // Số điện thoại
    private String diaChi; // Địa chỉ
    private int diemTichLuy; // Điểm tích lũy

    // Constructor mặc định
    public KhachHang() {
    }

    // Constructor đầy đủ
    public KhachHang(int maKhachHang, String tenKhachHang, String soDienThoai, String diaChi, int diemTichLuy) {
        this.maKhachHang = maKhachHang;
        this.tenKhachHang = tenKhachHang;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.diemTichLuy = diemTichLuy;
    }

    // Getter và Setter
    public int getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(int maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }

    
    public Object[] toDataRow() {
        return new Object[]{
            this.getMaKhachHang(), // Cột 1: Mã khách hàng
            this.getTenKhachHang(), // Cột 2: Tên khách hàng
            this.getSoDienThoai(), // Cột 3: Số điện thoại
            this.getDiaChi(), // Cột 4: Địa chỉ
            this.getDiemTichLuy() // Cột 5: Điểm tích lũy
        };
    }

    
    @Override
    public String toString() {
        return "KhachHang{" +
                "maKhachHang='" + maKhachHang + '\'' +
                ", tenKhachHang='" + tenKhachHang + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", diemTichLuy=" + diemTichLuy +
                '}';
    }

    public KhachHang(String tenKhachHang, String soDienThoai, String diaChi, int diemTichLuy) {
        this.tenKhachHang = tenKhachHang;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.diemTichLuy = diemTichLuy;
    }
}
