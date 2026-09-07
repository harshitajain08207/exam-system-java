import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthService {

    // Turns a plain password into a scrambled hash before storing it
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Creates a new user (Teacher or Student)
    public static boolean signup(String name, String email, String password, String role) {
        String hashedPassword = hashPassword(password);
        String sql = "INSERT INTO Users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.setString(4, role);

            stmt.executeUpdate();
            System.out.println("Signup successful!");
            return true;

        } catch (Exception e) {
            System.out.println("Signup failed!");
            e.printStackTrace();
            return false;
        }
    }

    // Checks if email + password match a user in the database
    public static boolean login(String email, String password) {
        String hashedPassword = hashPassword(password);
        String sql = "SELECT * FROM Users WHERE email = ? AND password_hash = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, hashedPassword);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Login successful! Welcome, " + rs.getString("name"));
                System.out.println("Role: " + rs.getString("role"));
                return true;
            } else {
                System.out.println("Invalid email or password.");
                return false;
            }

        } catch (Exception e) {
            System.out.println("Login failed!");
            e.printStackTrace();
            return false;
        }
    }

    // Quick test — run this file directly to try signup + login
    public static void main(String[] args) {
        signup("Harshita Jain", "harshita@test.com", "mypassword123", "TEACHER");
        login("harshita@test.com", "mypassword123");
    }
}
