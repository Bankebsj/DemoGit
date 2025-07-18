/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DB_Connection;
import java.sql.Connection;
/**
 *
 * @author 5410
 */
public class TestConnection {
     public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            System.out.println(" Kết nối thành công!");
        } else {
            System.out.println(" Kết nối thất bại!");
        }
    }
}
