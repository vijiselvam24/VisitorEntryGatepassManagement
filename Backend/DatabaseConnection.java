import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public static Connection getConnection() {

        try {
            String url = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true";
            String username = "root";
            String password = "viji@selvam24";

            Connection con = DriverManager.getConnection(
                url,
                username,
                password
            );

            System.out.println("Database Connected");

            return con;

        } catch (Exception e) {

            System.out.println("Database Connection Failed");
            e.printStackTrace();

            return null;
        }
    }

    public static void main(String[] args) {

        getConnection();

    }
}