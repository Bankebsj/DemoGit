/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import javax.swing.JFrame;
import javax.swing.*;

/**   
 *
 * @author 5410
 */
public class Form extends JFrame{
       public Form() {
        setTitle("Quản lý cửa hàng truyện tranh");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Quản lý");

        JMenuItem nhanVienItem = new JMenuItem("Nhân viên");
        nhanVienItem.addActionListener(e -> setPanel(new Dao.NhanVienForm()));

        menu.add(nhanVienItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Mặc định
        setPanel(new JPanel()); // hoặc welcome panel
    
}
        private void setPanel(JPanel panel) {
        getContentPane().removeAll();
        getContentPane().add(panel);
        revalidate();
        repaint();
    }
         public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Form().setVisible(true));
    }
}

