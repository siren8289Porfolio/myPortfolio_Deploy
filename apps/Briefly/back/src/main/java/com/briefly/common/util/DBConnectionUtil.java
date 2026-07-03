package com.briefly.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnectionUtil {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DBConnectionUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties not found");
            }
            PROPS.load(in);
            Class.forName(PROPS.getProperty("db.driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBConnectionUtil() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                resolve("db.url", "DB_URL"),
                resolve("db.username", "DB_USERNAME"),
                resolve("db.password", "DB_PASSWORD")
        );
    }

    private static String resolve(String propertyKey, String envKey) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        return PROPS.getProperty(propertyKey);
    }
}
