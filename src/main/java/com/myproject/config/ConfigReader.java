package com.myproject.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties prop = new Properties();

    static {
        try (InputStream is = new FileInputStream("src/test/resources/config.properties")) {
            prop.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Could not load config.properties file", e);
        }
    }

    public static String get(String key) {
        return prop.getProperty(key);
    }

    public static int getInt(String key, int defaultVal) {
        try {
            return Integer.parseInt(prop.getProperty(key));
        } catch (Exception e) {
            return defaultVal;
        }
    }
}
