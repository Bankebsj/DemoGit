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
        private JPanel contentPanel; 

    public Form() {
        
    setTitle("Quản lý cửa hàng truyện tranh");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());
    
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Quản lý");
    
    JMenuItem nhanVienItem = new JMenuItem("Nhân viên");
    nhanVienItem.addActionListener(e -> setPanel(new NhanVienForm()));
    
     JMenuItem hoadonItem = new JMenuItem("Hóa Đơn");
    hoadonItem.addActionListener(e -> setPanel(new HoaDonForm()));


    menu.add(nhanVienItem);
    menu.add(hoadonItem);
    menuBar.add(menu);
    setJMenuBar(menuBar);
    setPanel(new NhanVienForm()); 
    pack(); 
    setVisible(true);
    }

        private void setPanel(JPanel panel) {
    getContentPane().removeAll();
    getContentPane().add(panel, BorderLayout.CENTER);
    revalidate();
    repaint();
    pack(); 
    }
         public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Form().setVisible(true));
    }
}

