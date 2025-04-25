package org.example;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDao implements Dao<Object> {
    private Connection conn;
    private final String dbName = "calendar_app";
    private final String user = "postgres";
    private final String password = "admin";
    private final String baseUrl = "jdbc:postgresql://localhost:5432/";

    public DatabaseDao() {
        connect();
        createDatabaseIfNotExists();
        connectToCalendarDb();
        createTablesIfNotExist();
    }

    @Override
    public Connection connect() {
        try {
            conn = DriverManager.getConnection(baseUrl+dbName, user, password);
            createDatabaseIfNotExists();
        } catch (SQLException e) {
            throw new RuntimeException("Connection failed: " + e.getMessage(), e);
        }
        return conn;
    }

    private void connectToCalendarDb() {
        try {
            conn = DriverManager.getConnection(baseUrl + dbName, user, password);
            createDatabaseIfNotExists();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to calendar_app DB: " + e.getMessage(), e);
        }
    }


    private void createDatabaseIfNotExists() {
        try (Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'");
            if (!rs.next()) {
                stmt.executeUpdate("CREATE DATABASE " + dbName);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database creation error: " + e.getMessage(), e);
        }
    }

    private void createTablesIfNotExist() {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS login (
                    id_login SERIAL PRIMARY KEY,
                    username TEXT UNIQUE NOT NULL,
                    passwd TEXT NOT NULL
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS day (
                    id SERIAL PRIMARY KEY,
                    id_login INT REFERENCES login(id_login),
                    data DATE NOT NULL
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS tasks (
                      id_task SERIAL PRIMARY KEY,
                      id_day INT REFERENCES day(id),
                      task TEXT NOT NULL,
                      status TEXT NOT NULL  
                  );
                  
            """);
        } catch (SQLException e) {
            throw new RuntimeException("Table creation error: " + e.getMessage(), e);
        }
    }

    public void createUser(String username, String plainPassword) {

        String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO calendar_app.login (username, passwd) VALUES (?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, hashed);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("User creation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Object write(Object obj) {
        // Możesz dodać rozróżnienie np. instanceof Task lub Day
        return null;
    }

    @Override
    public List<Object> read() {
        return null;
    }

    @Override
    public void close() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
    public int login(String username, String plainPassword) {
        String query = "SELECT id_login, passwd FROM login WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            System.out.println(stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashed = rs.getString("passwd");
                if (BCrypt.checkpw(plainPassword, hashed)) {
                    return rs.getInt("id_login");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
        return -1;
    }
    public boolean register(String username, String password) {
        String checkUserSql = "SELECT COUNT(*) FROM login WHERE username = ?";
        String insertUserSql = "INSERT INTO login (username, passwd) VALUES (?, ?)";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkUserSql)) {

            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Użytkownik już istnieje
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            try (PreparedStatement insertStmt = conn.prepareStatement(insertUserSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, hashedPassword);
                insertStmt.executeUpdate();
                return true; // Rejestracja udana
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void ensureDayExists(int userId, String date) {
        String checkQuery = "SELECT id FROM day WHERE id_login = ? AND data = ?";
        String insertQuery = "INSERT INTO day (id_login, data) VALUES (?, ?)";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, userId);
            checkStmt.setDate(2, Date.valueOf(date));
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setDate(2, Date.valueOf(date));
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error ensuring day exists: " + e.getMessage(), e);
        }
    }

    public void addTask(int userId, String taskDescription, String date, String status) {
        ensureDayExists(userId, date);

        String query = "INSERT INTO tasks (id_day, task, status) " +
                "VALUES ((SELECT id FROM day WHERE id_login = ? AND data = ?), ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setString(3, taskDescription);
            stmt.setString(4, status);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding task: " + e.getMessage(), e);
        }
    }
    public List<Task> getTasksForDateAndColumn(String date, int userId, String status) {
        List<Task> tasks = new ArrayList<>();
        String query = """
        SELECT t.task FROM tasks t
        JOIN day d ON t.id_day = d.id
        WHERE d.data = ? AND d.id_login = ? AND t.status = ?
    """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(date));
            stmt.setInt(2, userId);
            stmt.setString(3, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tasks.add(new Task(rs.getString("task")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
    public void updateTaskStatus(String taskDescription, String date, int userId, String newStatus) {
        String query = """
        UPDATE tasks
        SET status = ?
        WHERE task = ?
          AND id_day = (
              SELECT id FROM day WHERE id_login = ? AND data = ?
          )
    """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setString(2, taskDescription);
            stmt.setInt(3, userId);
            stmt.setDate(4, Date.valueOf(date));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating task status: " + e.getMessage(), e);
        }
    }

}
