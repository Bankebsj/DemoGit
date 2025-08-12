/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import javax.swing.*;
import java.awt.*;

/**   
 *
 * @author 5410
 */
public class Form extends JFrame{

  private Integer maNhanVien;
    private String tenNhanVien;

    public Form(Integer maNV, String tenNV) {
        this.maNhanVien = maNV;
        this.tenNhanVien = tenNV;

        setTitle("Quản lý cửa hàng truyện tranh - Nhân viên: " + tenNV);
        setSize(1118, 738);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();

        JMenuItem nhanVienItem = new JMenuItem("Nhân viên");
        nhanVienItem.addActionListener(e -> setPanel(new NhanVienForm()));

        JMenuItem hoadonItem = new JMenuItem("Hóa Đơn");
        hoadonItem.addActionListener(e -> setPanel(new HoaDonForm()));

        JMenuItem banhangItem = new JMenuItem("Bán Hàng");
        banhangItem.addActionListener(e -> setPanel(new BanHangForm(maNV))); 

        menuBar.add(nhanVienItem);
        menuBar.add(hoadonItem);
        menuBar.add(banhangItem);

        setJMenuBar(menuBar);

        JButton btnDangXuat = new JButton("Đăng xuất");
        btnDangXuat.addActionListener(e -> {
            new FormLogin().setVisible(true);
            dispose();
        });
        menuBar.add(Box.createHorizontalGlue()); 
        menuBar.add(btnDangXuat);

        setPanel(new NhanVienForm());
        setVisible(true);
    }

   
    private void setPanel(JPanel panel) {
        getContentPane().removeAll();
        getContentPane().add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

  
}

