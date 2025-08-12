/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Barcode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import javax.imageio.ImageIO;
/**
 *
 * @author canbi
 */
public class BarcodeUtils {
 public static BufferedImage createCode128Barcode(String data, int width, int height) throws WriterException {
        Code128Writer writer = new Code128Writer();
        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.CODE_128, width, height);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
    
    public static List<String> layDanhSachMaSP() {
        List<String> ds = new ArrayList<>();
        String sql = "SELECT MaTruyen FROM TruyenTranh";
        try (Connection con = DB_Connection.DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(rs.getString("MaTruyen"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    public static void taoVaLuuBarcodeNhieuSP(List<String> danhSachMaSP) {
        try {
            File dir = new File("H:/barcodes");
            if (!dir.exists()) {
                dir.mkdirs(); // tạo thư mục nếu chưa có
            }
            for (String maSP : danhSachMaSP) {
                try {
                    BufferedImage barcode = createCode128Barcode(maSP, 300, 100);
                    File outputfile = new File(dir, maSP + ".png");
                    ImageIO.write(barcode, "png", outputfile);
                    System.out.println("Đã tạo barcode cho: " + maSP);
                } catch (WriterException e) {
                    e.printStackTrace();
                    System.out.println("Lỗi tạo barcode cho mã: " + maSP);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        List<String> danhSachMaSP = layDanhSachMaSP();
        taoVaLuuBarcodeNhieuSP(danhSachMaSP);
    }
    
}
