package Dao;

import Entity.KhachHang;
import Sevice.DBconnect;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class KhachHangRepository {

    public ArrayList<KhachHang> getAll() {
        ArrayList<KhachHang> danhSach = new ArrayList<>();
        String sql = """
            SELECT  MaKhachHang, TenKhachHang, SoDienThoai, DiaChi, DiemTichLuy 
            FROM KhachHang
        """;

        try (Connection con = DBconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int maKhachHang = rs.getInt(1); // Cột 1: MaKhachHang
                String tenKhachHang = rs.getString(2); // Cột 2: TenKhachHang
                String soDienThoai = rs.getString(3); // Cột 3: SoDienThoai
                String diaChi = rs.getString(4); // Cột 4: DiaChi
                int diemTichLuy = rs.getInt(5); // Cột 5: DiemTichLuy

                // Tạo đối tượng KhachHang và thêm vào danh sách
                KhachHang kh = new KhachHang(maKhachHang, tenKhachHang, soDienThoai, diaChi, diemTichLuy);
                danhSach.add(kh);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy danh sách khách hàng", e);
        }
        return danhSach;
    }
    public int themKhachHang(KhachHang kh) {
    String sql = """
        INSERT INTO KhachHang (TenKhachHang, SoDienThoai, DiaChi, DiemTichLuy)
        VALUES (?, ?, ?, ?)
    """;

    try (Connection con = DBconnect.getConnection();
         PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setString(1, kh.getTenKhachHang());
        ps.setString(2, kh.getSoDienThoai());
        ps.setString(3, kh.getDiaChi());
        ps.setInt(4, kh.getDiemTichLuy());

        int rowsAffected = ps.executeUpdate();

        if (rowsAffected > 0) {
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Trả về MaKhachHang vừa tạo
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return 0;
}
    // Cập nhật thông tin khách hàng
    public boolean updateKhachHang(int maKhachHang, String tenKhachHang, String soDienThoai, String diaChi, int diemTichLuy) {
        String sql = """
            UPDATE KhachHang 
            SET TenKhachHang = ?, SoDienThoai = ?, DiaChi = ?, DiemTichLuy = ? 
            WHERE MaKhachHang = ?
        """;

        try (Connection con = DBconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tenKhachHang);
            ps.setString(2, soDienThoai);
            ps.setString(3, diaChi);
            ps.setInt(4, diemTichLuy);
            ps.setInt(5, maKhachHang);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}