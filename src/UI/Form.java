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

        // Khởi tạo contentPanel mặc định
        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
    
    }

        private void setPanel(JPanel panel) {
        getContentPane().remove(contentPanel);
        contentPanel = panel;
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
         public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Form().setVisible(true));
    }
}

