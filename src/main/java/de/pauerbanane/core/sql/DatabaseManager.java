package de.pauerbanane.core.sql;

import de.pauerbanane.core.BananaCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.xml.crypto.Data;
import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;

public class DatabaseManager {

    private BananaCore plugin;

    private Connection connection;
    private Statement statement;

    private Connection proxyConnection;
    private Statement proxyStatement;

    public enum DATABASE {
        DEFAULT, PROXY;
    }

    private static DatabaseManager instance;

    private String host,
                   database,
                   username,
                   password;
    private int    port;

    private String proxyHost,
                   proxyDatabase,
                   proxyUsername,
                   proxyPassword;
    private int    proxyPort;

    public DatabaseManager(BananaCore plugin) {
        this.plugin = plugin;
        instance = this;
        loadConfig();

        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    openConnection();
                    statement = connection.createStatement();
                    openProxyConnection();
                    proxyStatement = proxyConnection.createStatement();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };

        bukkitRunnable.runTaskAsynchronously(plugin);

    }

    public int getVotes(UUID uuid) {

        if (proxyConnection == null)
            return 0;

        try {
            int amount = 0;
            PreparedStatement pr = prepareProxy("SELECT * FROM vote_log WHERE player = ?");

            pr.setString(1, uuid.toString());
            ResultSet rs = pr.executeQuery();
            while (rs.next())
                amount++;

            rs.close();
            return amount;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getVotesToday() {
        if (proxyConnection == null)
            return 0;

        try {
            int amount = 0;
            PreparedStatement pr = prepareProxy("SELECT * FROM vote_log WHERE date = ?");

            pr.setDate(1, Date.valueOf(LocalDate.now()));
            ResultSet rs = pr.executeQuery();
            while (rs.next())
                amount++;

            rs.close();
            return amount;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public PreparedStatement prepareProxy(String query) {
        try {
            return proxyConnection.prepareStatement(query);
        } catch (Exception e) {
            e.printStackTrace();

            try {
                proxyConnection.close();
                openProxyConnection();
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }

            try {
                PreparedStatement pr =  proxyConnection.prepareStatement(query);
                plugin.getLogger().info("§aVerbindung konnte wieder hergestellt werden...");
                return pr;
            } catch (SQLException e1) {
                return null;
            }
        }
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        if (!config.isSet("MySQL")) {
            config.set("MySQL.username", "minecraft");
            config.set("MySQL.password", "tst491");
            config.set("MySQL.database", "survival");
            config.set("MySQL.host", "localhost");
            config.set("MySQL.port", 3306);

            plugin.saveConfig();
        }

        if (!config.isSet("MySQL_Proxy")) {
            config.set("MySQL_Proxy.username", "minecraft");
            config.set("MySQL_Proxy.password", "tst491");
            config.set("MySQL_Proxy.database", "proxy");
            config.set("MySQL_Proxy.host", "localhost");
            config.set("MySQL_Proxy.port", 3306);

            plugin.saveConfig();
        }

        ConfigurationSection section = config.getConfigurationSection("MySQL");
        username = section.getString("username");
        password = section.getString("password");
        database = section.getString("database");
        host = section.getString("host");
        port = section.getInt("port");

        section = config.getConfigurationSection("MySQL_Proxy");
        proxyUsername = section.getString("username");
        proxyPassword = section.getString("password");
        proxyDatabase = section.getString("database");
        proxyHost = section.getString("host");
        proxyPort = section.getInt("port");
    }

    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);
        }
    }

    public void openProxyConnection() throws SQLException, ClassNotFoundException {
        if (proxyConnection != null && !proxyConnection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (proxyConnection != null && !proxyConnection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            proxyConnection = DriverManager.getConnection("jdbc:mysql://" + this.proxyHost+ ":" + this.proxyPort + "/" + this.proxyDatabase, this.proxyUsername, this.proxyPassword);
        }
    }

    public static DatabaseManager getInstance() {
        return instance;
    }
}
