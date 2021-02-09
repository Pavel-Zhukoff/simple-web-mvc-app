package ru.pavel_zhukoff;

import java.sql.Connection;

public class Model {
    private Connection connection = Database.getInstance().getConnection();
    public Connection getConnection() {
        return this.connection;
    }
}
