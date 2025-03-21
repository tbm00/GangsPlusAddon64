package dev.tbm00.spigot.gangsplusaddon64.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class MySQLConnection {
    private HikariDataSource dataSource;
    private JavaPlugin javaPlugin;

    public MySQLConnection(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        setupConnectionPool();
        initializeDatabase();
    }

    private void setupConnectionPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + javaPlugin.getConfig().getString("mysql.host") + 
                        ":" + javaPlugin.getConfig().getInt("mysql.port") + 
                        "/" + javaPlugin.getConfig().getString("mysql.database") +
                        "?useSSL=" + javaPlugin.getConfig().getBoolean("mysql.useSSL", false));
        config.setUsername(javaPlugin.getConfig().getString("mysql.username"));
        config.setPassword(javaPlugin.getConfig().getString("mysql.password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "100");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(javaPlugin.getConfig().getInt("mysql.hikari.maximumPoolSize"));
        config.setMinimumIdle(javaPlugin.getConfig().getInt("mysql.hikari.minimumPoolSize"));
        config.setIdleTimeout(javaPlugin.getConfig().getInt("mysql.hikari.idleTimeout")*1000);
        config.setConnectionTimeout(javaPlugin.getConfig().getInt("mysql.hikari.connectionTimeout")*1000);
        config.setMaxLifetime(javaPlugin.getConfig().getInt("mysql.hikari.maxLifetime")*1000);
        if (javaPlugin.getConfig().getBoolean("mysql.hikari.leakDetection.enabled"))
            config.setLeakDetectionThreshold(javaPlugin.getConfig().getInt("mysql.hikari.leakDetection.threshold")*1000);

        dataSource = new HikariDataSource(config);
        javaPlugin.getLogger().info("Initialized Hikari connection pool.");

        try (Connection connection = getConnection()) {
            if (connection.isValid(2))
                javaPlugin.getLogger().info("MySQL database connection is valid!");
        } catch (SQLException e) {
            javaPlugin.getLogger().severe("Failed to establish connection to MySQL database: " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closeConnection() {
        if (dataSource != null && !dataSource.isClosed())
            dataSource.close();
    }

    private void initializeDatabase() {
        String headTable = "CREATE TABLE IF NOT EXISTS gangsplusaddon64_heads (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "username VARCHAR(16), " +
                "skull_meta TEXT, " +
                "last_update LONG);";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(headTable);
        } catch (SQLException e) {
            javaPlugin.getLogger().severe("Error initializing database: " + e.getMessage());
        }
    }

    public void loadHeadMetaCache() {
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT uuid, skull_meta, last_update FROM gangsplusaddon64_heads")) {
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String serializedMeta = rs.getString("skull_meta");
                long lastUpdate = rs.getLong("last_update");
                SkullMeta meta = deserializeSkullMeta(serializedMeta);
                if (meta != null) {
                    Utils.headMetaCache.put(uuid, Pair.of(meta, lastUpdate));
                }
            }
            javaPlugin.getLogger().info("Loaded head meta cache with " + Utils.headMetaCache.size() + " entries.");
        } catch (SQLException e) {
            javaPlugin.getLogger().severe("Error loading head meta cache: " + e.getMessage());
        }
    }

    public void saveHeadMetaCache() {
        String query = "INSERT INTO gangsplusaddon64_heads (uuid, username, skull_meta, last_update) " +
                    "VALUES (?,?,?,?) " +
                    "ON DUPLICATE KEY UPDATE skull_meta = VALUES(skull_meta), last_update = VALUES(last_update);";
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
            for (Map.Entry<UUID, Pair<SkullMeta, Long>> entry : Utils.headMetaCache.entrySet()) {
                UUID uuid = entry.getKey();
                Pair<SkullMeta, Long> pair = entry.getValue();
                SkullMeta meta = pair.getLeft();
                long lastUpdate = pair.getRight();
                String serializedMeta = serializeSkullMeta(meta);
                String username = javaPlugin.getServer().getOfflinePlayer(uuid).getName();
                if (username==null) username = "null";
                
                ps.setString(1, uuid.toString());
                ps.setString(2, username);
                ps.setString(3, serializedMeta);
                ps.setLong(4, lastUpdate);
                ps.addBatch();
            }
            ps.executeBatch();
            javaPlugin.getLogger().info("Saved head meta cache with " + Utils.headMetaCache.size() + " entries.");
        } catch (SQLException e) {
            javaPlugin.getLogger().severe("Error saving head meta cache: " + e.getMessage());
        }
    }

    public static String serializeSkullMeta(SkullMeta meta) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("skullMeta", meta);
        return config.saveToString();
    }

    public static SkullMeta deserializeSkullMeta(String serialized) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(serialized);
            return (SkullMeta) config.get("skullMeta");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}