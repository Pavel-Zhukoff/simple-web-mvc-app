package ru.pavel_zhukoff;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private Connection connection;
    private static Database instance = null;

    private Database() {
        try {
            Class.forName(Config.getProperty("database.driver"));
            this.connection = DriverManager.getConnection(
                    Config.getProperty("database.url"),
                    Config.getProperty("database.username"),
                    Config.getProperty("database.password")
            );
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static Database getInstance() {
        if (Database.instance == null) {
            Database.instance = new Database();
        }
        return Database.instance;
    }

    public Connection getConnection() {
        return this.connection;
    }
}
