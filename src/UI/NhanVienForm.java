/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package UI;

import static DB_Connection.DBConnection.getConnection;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.Statement;
import java.sql.Connection;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;

/**
 *
 * @author 5410
 */
public class NhanVienForm extends javax.swing.JPanel {

    /**
     * Creates new form Nhan_Vien  
     */
    public NhanVienForm() {
        initComponents();
           loadNhanVien();
           
    }

    public void loadNhanVien() {
        DefaultTableModel model = (DefaultTableModel) NhanVienTable.getModel();
        model.setRowCount(0); // clear table
        try (Connection con = DB_Connection.DBConnection.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM NhanVien")) {

           
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("MaNhanVien"),
                    rs.getString("TenNhanVien"),
                    rs.getString("ChucVu"),
                    rs.getString("SoDienThoai"),
                    rs.getString("TaiKhoan"),
                    rs.getString("MatKhau"),
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
private void themNhanVien() {
    String tenNV = txtTenNhanVien.getText().trim();
    String chucVu = rdoQuanLy.isSelected() ? "Quản lý" : "Nhân viên";
    String soDT = txtSoDienThoai.getText().trim();
    String taiKhoan = txtTaiKhoan.getText().trim();
    String matKhau = txtMatKhau.getText().trim();
    
    if (tenNV.isEmpty() || soDT.isEmpty() || taiKhoan.isEmpty() || matKhau.isEmpty()) {
    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
    return;
}
if (!soDT.matches("^0\\d{9}$")) {
    JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ! Phải có 10 số và bắt đầu bằng 0");
    return;
}
    
    try (Connection con = getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM NhanVien WHERE TaiKhoan = ?";
    PreparedStatement checkPs = con.prepareStatement(checkSql);
    checkPs.setString(1, taiKhoan);
    ResultSet rs = checkPs.executeQuery();
    if (rs.next() && rs.getInt(1) > 0) {
        JOptionPane.showMessageDialog(this, "Tài khoản đã tồn tại. Vui lòng chọn tên tài khoản khác.");
        return;
    }
        String sql = "INSERT INTO NhanVien (TenNhanVien, ChucVu, SoDienThoai, TaiKhoan, MatKhau) "
                   + "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, tenNV);
        ps.setString(2, chucVu);
        ps.setString(3, soDT);
        ps.setString(4, taiKhoan);
        ps.setString(5, matKhau);
        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
        loadNhanVien(); 
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi khi thêm nhân viên!");
    }
}
private void updateNhanVien() {
    // Lấy chỉ số dòng đang chọn
    int selectedRow = NhanVienTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần cập nhật!");
        return;
    }

    // Lấy dữ liệu từ form
    String maNV = txtMaNhanVien.getText().trim();
    String tenNV = txtTenNhanVien.getText().trim();
    String sdt = txtSoDienThoai.getText().trim();
    String taiKhoan = txtTaiKhoan.getText().trim();
    String matKhau = txtMatKhau.getText().trim();
    String chucVu = "";

    // ✅ Validate
    if (maNV.isEmpty() || tenNV.isEmpty() || sdt.isEmpty() || taiKhoan.isEmpty() || matKhau.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!");
        return;
    }

    if (!sdt.matches("0\\d{9}")) {
        JOptionPane.showMessageDialog(this, "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0!");
        return;
    }

    if (rdoQuanLy.isSelected()) {
        chucVu = "Quản lý";
    } else if (rdoNhanVien.isSelected()) {
        chucVu = "Nhân viên";
    } else {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn chức vụ!");
        return;
    }

    try (Connection con = DB_Connection.DBConnection.getConnection()) {
        String sql = "UPDATE NhanVien SET TenNhanVien=?, ChucVu=?, SoDienThoai=?, TaiKhoan=?, MatKhau=? WHERE MaNhanVien=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, tenNV);
        ps.setString(2, chucVu);
        ps.setString(3, sdt);
        ps.setString(4, taiKhoan);
        ps.setString(5, matKhau);
        ps.setInt(6, Integer.parseInt(maNV)); // vì MaNhanVien là int

        int rows = ps.executeUpdate();
        if (rows > 0) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadNhanVien(); // reload lại bảng
            resetForm(); // xóa form nếu cần
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên cần cập nhật!");
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật nhân viên!");
    }
}

private void resetForm() {
    txtTenNhanVien.setText("");
    txtSoDienThoai.setText("");
    txtTaiKhoan.setText("");
    txtMatKhau.setText("");
    timKiemText.setText("");
    buttonGroup1.clearSelection(); 
    loadNhanVien();
}
private void xoanhanvien(){
int selectedRow = NhanVienTable.getSelectedRow();
if (selectedRow == -1) {
    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!");
    return;
}

int maNV = Integer.parseInt(NhanVienTable.getValueAt(selectedRow, 0).toString());

int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
if (confirm != JOptionPane.YES_OPTION) return;

try (Connection con = getConnection()) {
    String sql = "DELETE FROM NhanVien WHERE MaNhanVien = ?";
    PreparedStatement ps = con.prepareStatement(sql);
    ps.setInt(1, maNV);
    ps.executeUpdate();

    JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
    loadNhanVien();
    resetForm();
} catch (Exception e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(this, "Lỗi khi xóa nhân viên!");
}
}
public void loadNhanVien(String keyword) {
    DefaultTableModel model = (DefaultTableModel) NhanVienTable.getModel();
    model.setRowCount(0);

    String sql = "SELECT * FROM NhanVien WHERE TenNhanVien LIKE ? OR SoDienThoai LIKE ? OR TaiKhoan LIKE ?";
    try (Connection con = DB_Connection.DBConnection.getConnection();
         PreparedStatement stmt = con.prepareStatement(sql)) {

        String keywordPattern = "%" + keyword + "%";
        stmt.setString(1, keywordPattern);
        stmt.setString(2, keywordPattern);
        stmt.setString(3, keywordPattern);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("MaNhanVien"),
                rs.getString("TenNhanVien"),
                rs.getString("ChucVu"),
                rs.getString("SoDienThoai"),
                rs.getString("TaiKhoan"),
                rs.getString("MatKhau"),
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        maNV = new javax.swing.JLabel();
        matKhau = new javax.swing.JLabel();
        taiKhoan = new javax.swing.JLabel();
        tenNV = new javax.swing.JLabel();
        sdt = new javax.swing.JLabel();
        txtMaNhanVien = new javax.swing.JTextField();
        txtTenNhanVien = new javax.swing.JTextField();
        txtSoDienThoai = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        chucVu = new javax.swing.JLabel();
        rdoQuanLy = new javax.swing.JRadioButton();
        rdoNhanVien = new javax.swing.JRadioButton();
        txtTaiKhoan = new javax.swing.JTextField();
        themNVbtn = new javax.swing.JButton();
        xoaNVbtn = new javax.swing.JButton();
        suaNVbtn = new javax.swing.JButton();
        timKiem = new javax.swing.JLabel();
        timKiemText = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        txtMatKhau = new javax.swing.JPasswordField();
        jScrollPane1 = new javax.swing.JScrollPane();
        NhanVienTable = new javax.swing.JTable();

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("QUẢN LÝ NHÂN VIÊN");

        maNV.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        maNV.setText("Mã nhân viên");

        matKhau.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        matKhau.setText("Mật khẩu");

        taiKhoan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        taiKhoan.setText("Tài khoản");

        tenNV.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tenNV.setText("Tên nhân viên");

        sdt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sdt.setText("Số điện thoại");

        txtMaNhanVien.setEditable(false);
        txtMaNhanVien.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtMaNhanVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaNhanVienActionPerformed(evt);
            }
        });

        txtTenNhanVien.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtTenNhanVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenNhanVienActionPerformed(evt);
            }
        });

        txtSoDienThoai.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtSoDienThoai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSoDienThoaiActionPerformed(evt);
            }
        });

        chucVu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        chucVu.setText("Chức vụ");

        buttonGroup1.add(rdoQuanLy);
        rdoQuanLy.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        rdoQuanLy.setText("Quản lý");

        buttonGroup1.add(rdoNhanVien);
        rdoNhanVien.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        rdoNhanVien.setText("Nhân viên");
        rdoNhanVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoNhanVienActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chucVu, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdoNhanVien)
                    .addComponent(rdoQuanLy))
                .addContainerGap(137, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chucVu)
                    .addComponent(rdoQuanLy))
                .addGap(18, 18, 18)
                .addComponent(rdoNhanVien)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        txtTaiKhoan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtTaiKhoan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTaiKhoanActionPerformed(evt);
            }
        });

        themNVbtn.setText("ADD");
        themNVbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                themNVbtnActionPerformed(evt);
            }
        });

        xoaNVbtn.setText("DELETE");
        xoaNVbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xoaNVbtnActionPerformed(evt);
            }
        });

        suaNVbtn.setText("UPDATE");
        suaNVbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                suaNVbtnActionPerformed(evt);
            }
        });

        timKiem.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        timKiem.setText("Tìm kiếm");

        timKiemText.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        timKiemText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timKiemTextActionPerformed(evt);
            }
        });

        jButton1.setText("Search");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("RESET");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(maNV)
                                .addGap(32, 32, 32)
                                .addComponent(txtMaNhanVien))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(sdt, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(txtSoDienThoai)))
                        .addGap(75, 75, 75)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tenNV, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(taiKhoan, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(matKhau, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(timKiem)
                        .addGap(18, 18, 18)
                        .addComponent(timKiemText, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(suaNVbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(themNVbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(xoaNVbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(13, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTenNhanVien)
                            .addComponent(txtTaiKhoan)
                            .addComponent(txtMatKhau))
                        .addGap(41, 41, 41))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maNV)
                    .addComponent(tenNV)
                    .addComponent(txtMaNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTenNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sdt)
                    .addComponent(txtSoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(taiKhoan)
                    .addComponent(txtTaiKhoan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(matKhau)
                            .addComponent(txtMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(46, 46, 46)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(themNVbtn)
                            .addComponent(xoaNVbtn))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(timKiem)
                        .addComponent(timKiemText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(suaNVbtn)
                        .addComponent(jButton2)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        NhanVienTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã Nhân Viên", "Tên nhân viên", "Chức vụ", "Số điện thoại", "Tài khoản", "Mật khẩu"
            }
        ));
        NhanVienTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NhanVienTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(NhanVienTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 805, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(46, 46, 46))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(323, 323, 323))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtMaNhanVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaNhanVienActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaNhanVienActionPerformed

    private void txtTenNhanVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenNhanVienActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenNhanVienActionPerformed

    private void txtSoDienThoaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSoDienThoaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSoDienThoaiActionPerformed

    private void rdoNhanVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoNhanVienActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdoNhanVienActionPerformed

    private void txtTaiKhoanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaiKhoanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTaiKhoanActionPerformed

    private void themNVbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_themNVbtnActionPerformed
        // TODO add your handling code here:
        themNhanVien();
    }//GEN-LAST:event_themNVbtnActionPerformed

    private void xoaNVbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xoaNVbtnActionPerformed
xoanhanvien();        // TODO add your handling code here:
    }//GEN-LAST:event_xoaNVbtnActionPerformed

    private void suaNVbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_suaNVbtnActionPerformed
        // TODO add your handling code here:
        updateNhanVien();
    }//GEN-LAST:event_suaNVbtnActionPerformed

    private void timKiemTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timKiemTextActionPerformed


    }//GEN-LAST:event_timKiemTextActionPerformed

    private void NhanVienTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_NhanVienTableMouseClicked
        // TODO add your handling code here:
      int selectedRow = NhanVienTable.getSelectedRow();
    if (selectedRow != -1) {
        txtMaNhanVien.setText(NhanVienTable.getValueAt(selectedRow, 0).toString());
        txtTenNhanVien.setText(NhanVienTable.getValueAt(selectedRow, 1).toString());
        
      
        String chucVu = NhanVienTable.getValueAt(selectedRow, 2).toString();
        if (chucVu.equalsIgnoreCase("Quản lý")) {
            rdoQuanLy.setSelected(true);
        } else {
            rdoNhanVien.setSelected(true);
        }

        txtSoDienThoai.setText(NhanVienTable.getValueAt(selectedRow, 3).toString());
        txtTaiKhoan.setText(NhanVienTable.getValueAt(selectedRow, 4).toString());
        txtMatKhau.setText(NhanVienTable.getValueAt(selectedRow, 5).toString());
        
    }

    }//GEN-LAST:event_NhanVienTableMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
     resetForm();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

                 String keyword = timKiemText.getText().trim();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập từ khóa tìm kiếm (tên, số điện thoại hoặc tài khoản)!");
            return;
        }
          loadNhanVien(keyword);

    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable NhanVienTable;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel chucVu;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel maNV;
    private javax.swing.JLabel matKhau;
    private javax.swing.JRadioButton rdoNhanVien;
    private javax.swing.JRadioButton rdoQuanLy;
    private javax.swing.JLabel sdt;
    private javax.swing.JButton suaNVbtn;
    private javax.swing.JLabel taiKhoan;
    private javax.swing.JLabel tenNV;
    private javax.swing.JButton themNVbtn;
    private javax.swing.JLabel timKiem;
    private javax.swing.JTextField timKiemText;
    private javax.swing.JTextField txtMaNhanVien;
    private javax.swing.JPasswordField txtMatKhau;
    private javax.swing.JTextField txtSoDienThoai;
    private javax.swing.JTextField txtTaiKhoan;
    private javax.swing.JTextField txtTenNhanVien;
    private javax.swing.JButton xoaNVbtn;
    // End of variables declaration//GEN-END:variables
}
