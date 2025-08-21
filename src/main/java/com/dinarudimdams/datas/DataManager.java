package com.dinarudimdams.datas;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.*;

public class DataManager {

    private final Map<String, AuthData> authCache = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Connection connection;

    public DataManager(String dbPath) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        createTable();
    }

    private void createTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS player_data (
                    uuid TEXT PRIMARY KEY,
                    password VARCHAR(255)
                )
            """);
        }
    }

    public AuthData getAuthData(String uuid) {
        if (authCache.containsKey(uuid)) return authCache.get(uuid);

        try (PreparedStatement stmt = connection.prepareStatement("SELECT password FROM auth_data WHERE uuid = ?")) {
            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();

            var data = rs.next() ? new AuthData(uuid, rs.getString("password")) : new AuthData(uuid, "error");

            authCache.put(uuid, data);
            return data;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new AuthData(uuid, "error");
        }
    }

    public void setAuthData(String uuid, AuthData data) {
        authCache.put(uuid, data);
    }

    public void flushAsync() {
        executor.submit(() -> {
            for (AuthData data : authCache.values()) {
                try (PreparedStatement stmt = connection.prepareStatement("""
                        INSERT INTO auth_data (uuid, password) VALUES (?, ?)
                        ON CONFLICT(uuid) DO UPDATE SET password=excluded.password
                """)) {
                    stmt.setString(1, data.getUuid());
                    stmt.setString(2, data.getPassword());
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        });
    }

    public void shutdown() {
        flushAsync();
        executor.shutdown();
        try {
            var ignored = executor.awaitTermination(5, TimeUnit.SECONDS);
            connection.close();
        } catch (InterruptedException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}