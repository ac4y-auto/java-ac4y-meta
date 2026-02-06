package examples;

import ac4y.base.database.DBConnection;
import ac4y.base.Ac4yException;
import ac4y.base.ErrorHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Példa adatbázis kapcsolat használatára
 */
public class DatabaseExample {

    /**
     * Egyszerű adatbázis lekérdezés példa
     */
    public static void simpleQuery() {
        try {
            // Connection létrehozás properties fájlból
            DBConnection dbConnection = new DBConnection("myapp.properties");
            Connection conn = dbConnection.getConnection();

            // Query végrehajtása
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE active = ?");
            ps.setBoolean(1, true);
            ResultSet rs = ps.executeQuery();

            // Eredmény feldolgozása
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");

                System.out.println("User: " + id + " - " + name + " (" + email + ")");
            }

            // Cleanup
            rs.close();
            ps.close();
            conn.close();

        } catch (ClassNotFoundException | SQLException | IOException | Ac4yException e) {
            ErrorHandler.addStack(e);
        }
    }

    /**
     * DAO pattern példa
     */
    static class UserDAO {

        private DBConnection dbConnection;

        public UserDAO() throws Exception {
            this.dbConnection = new DBConnection("userservice.properties");
        }

        public List<User> findAll() throws SQLException {
            List<User> users = new ArrayList<>();
            Connection conn = dbConnection.getConnection();

            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM users");
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    users.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email")
                    ));
                }
            } finally {
                conn.close();
            }

            return users;
        }

        public void create(User user) throws SQLException {
            Connection conn = dbConnection.getConnection();

            try {
                conn.setAutoCommit(false);

                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (name, email) VALUES (?, ?)"
                );
                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail());
                ps.executeUpdate();

                conn.commit();
                ps.close();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Egyszerű User entitás
     */
    static class User {
        private int id;
        private String name;
        private String email;

        public User(int id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
    }

    public static void main(String[] args) {
        simpleQuery();
    }
}
