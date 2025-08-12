/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package UI;
import static DB_Connection.DBConnection.getConnection;
import java.util.ArrayList;
import Entity.TruyenTranh;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Image;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

// zxing
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

// Java AWT / Swing / util
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.SwingUtilities;


/**
 *
 * @author canbi
 */
public class BanHangForm extends javax.swing.JPanel {
     private Integer maNV;
     private int maKH_selected = -1; 
        private Webcam webcam;
        private WebcamPanel webcamPanel;
        private volatile boolean scannerRunning = false;

        private String lastScannedCode = "";
        private long lastScannedTime = 0L;
        private final long SCAN_INTERVAL_MS = 2000; 

    /**
     * Creates new form BanHangForm
     */
    public BanHangForm(Integer maNV) {
        this.maNV = maNV;
        initComponents();
        loadTruyen();
        
        setupWebcam();
startBarcodeScanner();

        
            txtkhachdua.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            tinhTienThua();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            tinhTienThua();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            tinhTienThua();
        }
    });
    }
    
    
private void setupWebcam() {
    webcam = Webcam.getDefault();
    webcam.setViewSize(new Dimension(320, 240)); // kích thước camera
     webcam.open(); // *** quan trọng ***

    webcamPanel = new WebcamPanel(webcam);
    webcamPanel.setFPSDisplayed(true);
    webcamPanel.setPreferredSize(new Dimension(320, 240));

    pnlCamera.setLayout(new BorderLayout());
    pnlCamera.add(webcamPanel, BorderLayout.CENTER);
    pnlCamera.revalidate();
}



private void startBarcodeScanner() {
    scannerRunning = true;
    Thread scannerThread = new Thread(() -> {
        MultiFormatReader reader = new MultiFormatReader();
        Map<DecodeHintType,Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, Arrays.asList(
            BarcodeFormat.CODE_128, BarcodeFormat.EAN_13, BarcodeFormat.EAN_8, BarcodeFormat.QR_CODE
        ));

        while (scannerRunning) {
            try {
                if (webcam != null && webcam.isOpen()) {
                    BufferedImage image = webcam.getImage();
                    if (image != null) {
                        LuminanceSource source = new BufferedImageLuminanceSource(image);
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                        try {
                            Result result = reader.decode(bitmap, hints);
                            if (result != null) {
                                String code = result.getText();
                                long now = System.currentTimeMillis();
                                if (!code.equals(lastScannedCode) || now - lastScannedTime > SCAN_INTERVAL_MS) {
                                    lastScannedCode = code;
                                    lastScannedTime = now;
                                    SwingUtilities.invokeLater(() -> addProductToCartByCode(code));
                                }
                            }
                        } catch (NotFoundException nf) {
                        }
                    }
                }
                Thread.sleep(120);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }, "Barcode-Scanner-Thread");
    scannerThread.setDaemon(true);
    scannerThread.start();
}


private void addProductToCartByCode(String maTruyen) {
    new Thread(() -> {
        try (Connection con = DB_Connection.DBConnection.getConnection()) {
            String sql = "SELECT MaTruyen, TenTruyen, GiaBan, SoLuong FROM TruyenTranh WHERE MaTruyen = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maTruyen);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm: " + maTruyen)
                        );
                        return;
                    }

                    String ma = rs.getString("MaTruyen");
                    String ten = rs.getString("TenTruyen");
                    double gia = rs.getDouble("GiaBan");
                    int soLuongTon = rs.getInt("SoLuong");

                    SwingUtilities.invokeLater(() -> {
                        DefaultTableModel gioHangModel = (DefaultTableModel) gioHangTable.getModel();
                        boolean found = false;
                        for (int i = 0; i < gioHangModel.getRowCount(); i++) {
                            Object cellMa = gioHangModel.getValueAt(i, 0);
                            if (cellMa != null && cellMa.toString().equals(ma)) {
                                int curQty = (int) gioHangModel.getValueAt(i, 2);
                                int newQty = curQty + 1;
                                gioHangModel.setValueAt(newQty, i, 2);
                                gioHangModel.setValueAt(gia * newQty, i, 3);
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            gioHangModel.addRow(new Object[]{ma, ten, 1, gia});
                        }

                        DefaultTableModel truyenModel = (DefaultTableModel) truyenTranhTable.getModel();
                        for (int r = 0; r < truyenModel.getRowCount(); r++) {
                            Object m = truyenModel.getValueAt(r, 0);
                            if (m != null && m.toString().equals(ma)) {
                                Object stockObj = truyenModel.getValueAt(r, 5);
                                int curStock = 0;
                                try { curStock = Integer.parseInt(stockObj.toString()); } catch (Exception ignore) {}
                                truyenModel.setValueAt(Math.max(0, curStock - 1), r, 5);
                                break;
                            }
                        }

                        tinhTongTienHang();
                    });

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, "Lỗi thêm sản phẩm: " + ex.getMessage())
            );
        }
    }).start();
}

    
    
    private void loadTruyen() {
    DefaultTableModel model = (DefaultTableModel) truyenTranhTable.getModel();
    model.setRowCount(0);
    try (Connection con = DB_Connection.DBConnection.getConnection()) {
        String sql = """
                 SELECT t.MaTruyen, t.TenTruyen, tg.TenTacGia, tl.TenTheLoai,
                        t.GiaBan, t.SoLuong, t.MoTa, ncc.TenNCC
                 FROM TruyenTranh t
                 JOIN TacGia tg ON t.MaTacGia = tg.MaTacGia
                 JOIN TheLoai tl ON t.MaTheLoai = tl.MaTheLoai
                 LEFT JOIN NhaCungCap ncc ON t.MaNCC = ncc.MaNCC
                 """;
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("MaTruyen"),
                rs.getString("TenTruyen"),
                rs.getString("TenTacGia"),
                rs.getString("TenTheLoai"),
                rs.getBigDecimal("GiaBan"),
                rs.getInt("SoLuong"),
                rs.getString("MoTa"),
                rs.getString("TenNCC")
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    
    
    private void tinhTongTienHang() {
    DefaultTableModel gioHangModel = (DefaultTableModel) gioHangTable.getModel();
    double tongTien = 0;
    for (int i = 0; i < gioHangModel.getRowCount(); i++) {
        tongTien += Double.parseDouble(gioHangModel.getValueAt(i, 3).toString());
    }

        txtTong.setText(String.format("%.0f", tongTien));
        capNhatSoTienCanTra();
}

    
    private void themVaoGioHang() {
    int selectedRow = truyenTranhTable.getSelectedRow();
    
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để thêm vào giỏ hàng.");
        return;
    }

    DefaultTableModel truyenTranhModel = (DefaultTableModel) truyenTranhTable.getModel();
    
    Object soLuongTonKhoObj = truyenTranhModel.getValueAt(selectedRow, 5);
    int soLuongTonKho = 0;
    if (soLuongTonKhoObj instanceof Integer) {
        soLuongTonKho = (int) soLuongTonKhoObj;
    } else {
        try {
            soLuongTonKho = Integer.parseInt(soLuongTonKhoObj.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi định dạng số lượng tồn kho.");
            return;
        }
    }

    if (soLuongTonKho <= 0) {
        JOptionPane.showMessageDialog(this, "Sản phẩm đã hết hàng!");
        return;
    }

    String maTruyen = truyenTranhTable.getValueAt(selectedRow, 0).toString();
    String tenTruyen = truyenTranhTable.getValueAt(selectedRow, 1).toString();
    int soLuongThem = 1; 
    Object giaBanGocObj = truyenTranhTable.getValueAt(selectedRow, 4); 

    DefaultTableModel gioHangModel = (DefaultTableModel) gioHangTable.getModel();
    boolean found = false;
    double giaBan = 0.0;
    
    if (giaBanGocObj instanceof BigDecimal) {
        giaBan = ((BigDecimal) giaBanGocObj).doubleValue();
    } else {
        try {
            giaBan = Double.parseDouble(giaBanGocObj.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi định dạng giá bán.");
            return;
        }
    }
    
    for (int i = 0; i < gioHangModel.getRowCount(); i++) {
        Object maTruyenTrongGio = gioHangModel.getValueAt(i, 0);

        if (maTruyenTrongGio != null && maTruyenTrongGio.equals(maTruyen)) {
            int currentSoLuongGioHang = (int) gioHangModel.getValueAt(i, 2);
            int newSoLuongGioHang = currentSoLuongGioHang + soLuongThem;
            gioHangModel.setValueAt(newSoLuongGioHang, i, 2); 
            
            double tongTienMoi = giaBan * newSoLuongGioHang;
            gioHangModel.setValueAt(tongTienMoi, i, 3);
            
            found = true;
            break;
        }
    }
    
    if (!found) {
        gioHangModel.addRow(new Object[]{maTruyen, tenTruyen, soLuongThem, giaBan});
    }

    int soLuongMoi = soLuongTonKho - 1;
    truyenTranhModel.setValueAt(soLuongMoi, selectedRow, 5);
}
    
    private void capNhatSoTienCanTra() {
    try {
        double tongTienHang = Double.parseDouble(txtTong.getText().trim());
        double giamGia = txtgiamgia.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtgiamgia.getText().trim());

        double khachCanTra = tongTienHang - giamGia;
        if (khachCanTra < 0) khachCanTra = 0; 

        txtcantra.setText(String.valueOf(khachCanTra));
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi tính số tiền cần trả.");
    }
}

    
    
    private void xoaKhoiGioHang() {
    int selectedGioHangRow = gioHangTable.getSelectedRow();
    
    if (selectedGioHangRow == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm trong giỏ hàng để xóa.");
        return;
    }

    DefaultTableModel gioHangModel = (DefaultTableModel) gioHangTable.getModel();
    
    String maTruyenBiXoa = gioHangModel.getValueAt(selectedGioHangRow, 0).toString();
    int soLuongBiXoa = (int) gioHangModel.getValueAt(selectedGioHangRow, 2);

    gioHangModel.removeRow(selectedGioHangRow);

    DefaultTableModel truyenTranhModel = (DefaultTableModel) truyenTranhTable.getModel();
    
    for (int i = 0; i < truyenTranhModel.getRowCount(); i++) {
        String maTruyenTonKho = truyenTranhModel.getValueAt(i, 0).toString();
        
       
        if (maTruyenTonKho.equals(maTruyenBiXoa)) {
            int currentSoLuongTonKho = (int) truyenTranhModel.getValueAt(i, 5);
            int newSoLuongTonKho = currentSoLuongTonKho + soLuongBiXoa;
            
            truyenTranhModel.setValueAt(newSoLuongTonKho, i, 5);
            break;
        }
    }
}
    public void resetKH(){
        txtKH.setText("");
        txtDiem.setText("");
        txtSDT.setText("");
        txtgiamgia.setText("");
    }
    
    private void tinhTienThua() {
    String canTraStr = txtcantra.getText().trim().replace(",", "");
    String khachDuaStr = txtkhachdua.getText().trim().replace(",", "");

    if (canTraStr.isEmpty() || khachDuaStr.isEmpty()) {
        txttienthua.setText("");
        return;
    }

    try {
        double khachCanTra = Double.parseDouble(canTraStr);
        double khachDua = Double.parseDouble(khachDuaStr);
        double tienThoi = khachDua - khachCanTra;
        txttienthua.setText(String.format("%.0f", tienThoi));
    } catch (NumberFormatException e) {
        txttienthua.setText("");
    }
}
   
    public void timkiem(String keyword) {
    DefaultTableModel model = (DefaultTableModel) truyenTranhTable.getModel();
    model.setRowCount(0);

    String sql = """
                 SELECT t.MaTruyen, t.TenTruyen, tg.TenTacGia, tl.TenTheLoai,
                        t.GiaBan, t.SoLuong, t.MoTa, ncc.TenNCC
                 FROM TruyenTranh t
                 JOIN TacGia tg ON t.MaTacGia = tg.MaTacGia
                 JOIN TheLoai tl ON t.MaTheLoai = tl.MaTheLoai
                 LEFT JOIN NhaCungCap ncc ON t.MaNCC = ncc.MaNCC
                 WHERE t.TenTruyen LIKE ? OR tg.TenTacGia LIKE ? OR tl.TenTheLoai LIKE ?
                 """;
    try (Connection con = DB_Connection.DBConnection.getConnection();
         PreparedStatement stmt = con.prepareStatement(sql)) {

        String keywordPattern = "%" + keyword + "%";
        stmt.setString(1, keywordPattern);
        stmt.setString(2, keywordPattern);
        stmt.setString(3, keywordPattern);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("MaTruyen"),
                rs.getString("TenTruyen"),
                rs.getString("TenTacGia"),
                rs.getString("TenTheLoai"),
                rs.getBigDecimal("GiaBan"),
                rs.getInt("SoLuong"),
                rs.getString("MoTa"),
                rs.getString("TenNCC")
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    
  private void thanhToan() {
    if (maKH_selected == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng tìm và chọn khách hàng trước khi thanh toán!");
        return;
    }

    try (Connection con = DB_Connection.DBConnection.getConnection()) {
        con.setAutoCommit(false); 

        double tongTien = Double.parseDouble(txtcantra.getText().trim());

        String sqlHoaDon = "INSERT INTO HoaDon (MaKhachHang, MaNhanVien, NgayLap, TongTien) VALUES (?, ?, GETDATE(), ?)";
        PreparedStatement psHoaDon = con.prepareStatement(sqlHoaDon, Statement.RETURN_GENERATED_KEYS);
        psHoaDon.setInt(1, maKH_selected); 
        psHoaDon.setInt(2, maNV); 
        psHoaDon.setDouble(3, tongTien);
        psHoaDon.executeUpdate();

        ResultSet rsKeys = psHoaDon.getGeneratedKeys();
        int maHoaDon = 0;
        if (rsKeys.next()) {
            maHoaDon = rsKeys.getInt(1);
        }

        String sqlChiTiet = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaTruyen, SoLuong, DonGia) VALUES (?, ?, ?, ?)";
        PreparedStatement psChiTiet = con.prepareStatement(sqlChiTiet);

        String sqlUpdateSL = "UPDATE TruyenTranh SET SoLuong = SoLuong - ? WHERE MaTruyen = ?";
        PreparedStatement psUpdateSL = con.prepareStatement(sqlUpdateSL);

        DefaultTableModel gioHangModel = (DefaultTableModel) gioHangTable.getModel();
        for (int i = 0; i < gioHangModel.getRowCount(); i++) {
            String maTruyen = gioHangModel.getValueAt(i, 0).toString();
            int soLuong = (int) gioHangModel.getValueAt(i, 2);
            double donGia = Double.parseDouble(gioHangModel.getValueAt(i, 3).toString());

            psChiTiet.setInt(1, maHoaDon);
            psChiTiet.setString(2, maTruyen);
            psChiTiet.setInt(3, soLuong);
            psChiTiet.setDouble(4, donGia);
            psChiTiet.addBatch();

            psUpdateSL.setInt(1, soLuong);
            psUpdateSL.setString(2, maTruyen);
            psUpdateSL.addBatch();
        }

        psChiTiet.executeBatch();
        psUpdateSL.executeBatch();

        if (!txtgiamgia.getText().trim().isEmpty()) {
              int soTienGiam = Integer.parseInt(txtgiamgia.getText().trim());
              int soDiemGiam = soTienGiam / 1000;
            String sqlCapNhatDiem = "UPDATE KhachHang SET DiemTichLuy = DiemTichLuy - ? WHERE MaKhachHang = ?";
            PreparedStatement psDiem = con.prepareStatement(sqlCapNhatDiem);
            psDiem.setInt(1,soDiemGiam);
            psDiem.setInt(2, maKH_selected);
            psDiem.executeUpdate();
        } else {
            String sqlCapNhatDiem = "UPDATE KhachHang SET DiemTichLuy = DiemTichLuy + 1 WHERE MaKhachHang = ?";
            PreparedStatement psDiem = con.prepareStatement(sqlCapNhatDiem);
            psDiem.setInt(1, maKH_selected);
            psDiem.executeUpdate();
        }

        con.commit();      
        JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi khi thanh toán: " + e.getMessage());
    }
}

  private void resetForm() {
    DefaultTableModel gioHangModel = (DefaultTableModel) gioHangTable.getModel();
    gioHangModel.setRowCount(0);

    resetKH();

    txtTong.setText("");
    txtcantra.setText("");
    txtkhachdua.setText("");
    txttienthua.setText("");
    txtgiamgia.setText("");

    maKH_selected = -1;

    loadTruyen();
}


   public static void main(String[] args) {
        getConnection(); 
    }
   
   

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        gioHangTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        btnXoaGioHang = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtSDT = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtKH = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtDiem = new javax.swing.JTextField();
        btnTim = new javax.swing.JButton();
        btnSudung = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txtTong = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtgiamgia = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        cboHinhThuc = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        txtcantra = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtkhachdua = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txttienthua = new javax.swing.JTextField();
        btnThanhToan = new javax.swing.JButton();
        btnclear = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        truyenTranhTable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        timkiemfields = new javax.swing.JTextField();
        timkiemBTN = new javax.swing.JButton();
        pnlCamera = new javax.swing.JPanel();

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 51, 51));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Bán Hàng ");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        gioHangTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã Truyện", "Tên Truyện", "Số Lượng", "Giá Bán"
            }
        ));
        jScrollPane1.setViewportView(gioHangTable);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Giỏ Hàng");

        btnXoaGioHang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXoaGioHang.setText("Xóa");
        btnXoaGioHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaGioHangActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(btnXoaGioHang)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnXoaGioHang)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jSeparator1.setForeground(new java.awt.Color(0, 0, 0));

        jSeparator2.setForeground(new java.awt.Color(0, 0, 0));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Tạo Hóa Đơn");

        jLabel5.setText("SĐT :");

        txtSDT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSDTActionPerformed(evt);
            }
        });

        jLabel6.setText("Tên KH :");

        jLabel7.setText("Điểm tích lũy  :");

        btnTim.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTim.setText("Tìm");
        btnTim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimActionPerformed(evt);
            }
        });

        btnSudung.setText("Sử Dụng");
        btnSudung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSudungActionPerformed(evt);
            }
        });

        jButton5.setText("Xóa");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel8.setText("Tổng tiền hàng :");

        txtTong.setEditable(false);

        jLabel9.setText("Giảm giá : ");

        txtgiamgia.setEditable(false);

        jLabel10.setText("Hình thức thanh toán:");

        cboHinhThuc.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tiền Mặt", "Chuyển Khoản" }));

        jLabel11.setText("Khách cần trả : ");

        txtcantra.setEditable(false);

        jLabel12.setText("Khách đưa :");

        txtkhachdua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtkhachduaActionPerformed(evt);
            }
        });

        jLabel13.setText("Tiền Trả Lại : ");

        txttienthua.setEditable(false);
        txttienthua.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txttienthuaKeyReleased(evt);
            }
        });

        btnThanhToan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThanhToan.setText("Thanh Toán");
        btnThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThanhToanActionPerformed(evt);
            }
        });

        btnclear.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnclear.setText("Hủy");
        btnclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnclearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDiem, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnSudung))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtKH))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addComponent(btnTim)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(100, 100, 100))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton5)
                                .addGap(59, 59, 59))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTong, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtgiamgia, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboHinhThuc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel12))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtcantra, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                                    .addComponent(txtkhachdua)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(54, 54, 54)
                                .addComponent(txttienthua)))
                        .addContainerGap())))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(btnThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnclear, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTim))
                .addGap(22, 22, 22)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtKH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtDiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSudung))
                .addGap(24, 24, 24)
                .addComponent(jButton5)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtTong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtgiamgia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(cboHinhThuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtcantra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtkhachdua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txttienthua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(59, 59, 59)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnclear, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33))
        );

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        truyenTranhTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã Truyện", "Tên Truyện", "Tên tác giả ", "Tên thể loại", "giá bán ", "Số lượng", "Mô tả ", "Tên NCC"
            }
        ));
        truyenTranhTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                truyenTranhTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(truyenTranhTable);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Danh Sách Sản Phẩm");

        timkiemBTN.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        timkiemBTN.setText("Tìm Kiếm");
        timkiemBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timkiemBTNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel3)
                .addGap(252, 252, 252)
                .addComponent(timkiemfields, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(timkiemBTN)
                .addContainerGap(46, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(timkiemfields, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timkiemBTN))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlCamera.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(255, 204, 255), null, null));

        javax.swing.GroupLayout pnlCameraLayout = new javax.swing.GroupLayout(pnlCamera);
        pnlCamera.setLayout(pnlCameraLayout);
        pnlCameraLayout.setHorizontalGroup(
            pnlCameraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlCameraLayout.setVerticalGroup(
            pnlCameraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 188, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlCamera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnlCamera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnclearActionPerformed
        // TODO add your handling code here:
        resetForm();
    }//GEN-LAST:event_btnclearActionPerformed

    private void timkiemBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timkiemBTNActionPerformed
        // TODO add your handling code here:
                    String keyword = timkiemfields.getText().trim();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập từ khóa tìm kiếm (tên truyen , tác giả hoặc thể loại)!");
            return;
        }
          timkiem(keyword);

    }//GEN-LAST:event_timkiemBTNActionPerformed

    private void truyenTranhTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_truyenTranhTableMouseClicked
        // TODO add your handling code here:
        themVaoGioHang();
        tinhTongTienHang();
    
    }//GEN-LAST:event_truyenTranhTableMouseClicked

    private void btnXoaGioHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaGioHangActionPerformed
        // TODO add your handling code here:
        xoaKhoiGioHang();
        tinhTongTienHang();
    }//GEN-LAST:event_btnXoaGioHangActionPerformed

    private void txtSDTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSDTActionPerformed
         // TODO add your handling code here:
    }//GEN-LAST:event_txtSDTActionPerformed

    private void btnTimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimActionPerformed
        // TODO add your handling code here:
 String sdt = txtSDT.getText().trim();

    if (sdt.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại.");
        return;
    }

    try (Connection con = DB_Connection.DBConnection.getConnection()) {
        String sql = "SELECT MaKhachHang, TenKhachHang, DiemTichLuy FROM KhachHang WHERE SoDienThoai = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, sdt);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            maKH_selected = rs.getInt("MaKhachHang"); 
            txtKH.setText(rs.getString("TenKhachHang")); 
            txtDiem.setText(String.valueOf(rs.getInt("DiemTichLuy")));
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng.");
            txtKH.setText("");
            txtDiem.setText("");
            maKH_selected = -1; 
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi khi tìm khách hàng.");
    }
    }//GEN-LAST:event_btnTimActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        resetKH();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void btnSudungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSudungActionPerformed
        // TODO add your handling code here:
        try {
        int diemHienCo = Integer.parseInt(txtDiem.getText().trim());

        if (diemHienCo < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điểm hợp lệ (> 0)");
            return;
        }
    
        int giamGia = diemHienCo * 1000;
        txtgiamgia.setText(String.valueOf(giamGia));

        capNhatSoTienCanTra();

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Số điểm không hợp lệ!");
    }
    }//GEN-LAST:event_btnSudungActionPerformed

    private void txtkhachduaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtkhachduaActionPerformed
        // TODO add your handling code here:
  
    }//GEN-LAST:event_txtkhachduaActionPerformed

    private void txttienthuaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txttienthuaKeyReleased
  
    }//GEN-LAST:event_txttienthuaKeyReleased

    private void btnThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThanhToanActionPerformed
        // TODO add your handling code here:
        thanhToan();
        resetForm();
    }//GEN-LAST:event_btnThanhToanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSudung;
    private javax.swing.JButton btnThanhToan;
    private javax.swing.JButton btnTim;
    private javax.swing.JButton btnXoaGioHang;
    private javax.swing.JButton btnclear;
    private javax.swing.JComboBox<String> cboHinhThuc;
    private javax.swing.JTable gioHangTable;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel pnlCamera;
    private javax.swing.JButton timkiemBTN;
    private javax.swing.JTextField timkiemfields;
    private javax.swing.JTable truyenTranhTable;
    private javax.swing.JTextField txtDiem;
    private javax.swing.JTextField txtKH;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtTong;
    private javax.swing.JTextField txtcantra;
    private javax.swing.JTextField txtgiamgia;
    private javax.swing.JTextField txtkhachdua;
    private javax.swing.JTextField txttienthua;
    // End of variables declaration//GEN-END:variables
}
