/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DB_Connection;

/**
 *
 * @author 5410
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
     public static Connection getConnection() {
        try {
            // Load driver SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Cấu hình URL kết nối
            String url = "jdbc:sqlserver://localhost:1433;databaseName=CuaHangTruyenTranh;encrypt=false";
            String user = "sa";       // tài khoản SQL Server
            String password = "123456"; // mật khẩu SQL Server

            // Kết nối
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
     public static void main(String[] args) {
        getConnection(); // Gọi thử kết nối
    }
}
