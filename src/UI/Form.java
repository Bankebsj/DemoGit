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

    // Menu
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Quản lý");
    JMenuItem nhanVienItem = new JMenuItem("Nhân viên");
    nhanVienItem.addActionListener(e -> setPanel(new NhanVienForm()));

    menu.add(nhanVienItem);
    menuBar.add(menu);
    setJMenuBar(menuBar);

    // Default Panel
    setPanel(new NhanVienForm());  // Load giao diện nhân viên luôn

    pack();  // tự căn chỉnh kích thước theo nội dung
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

