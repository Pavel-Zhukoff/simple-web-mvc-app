package ru.pavel_zhukoff;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final Properties APPLICATION_CONFIG = new Properties();
    private static final InputStream CONFIG_STREAM = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("config.properties");

    public static void loadConfig() {
        try {
            APPLICATION_CONFIG.load(CONFIG_STREAM);
        } catch (IOException e) {
            System.out.println("FAILED TO LOAD CONFIG");
            e.printStackTrace();
        }
    }

    public static String getProperty(String name) {
        return APPLICATION_CONFIG.getProperty(name, null);
    }
}
