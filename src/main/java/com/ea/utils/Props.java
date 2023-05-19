package com.ea.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Properties loader
 */
public class Props {

    private static Properties loadProperties() {
        Properties configuration = new Properties();
        InputStream inputStream = Props.class
                .getClassLoader()
                .getResourceAsStream("application.properties");
        try {
            configuration.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return configuration;
    }

    public static String getString(String key) {
        return loadProperties().getProperty(key);
    }

    public static Integer getInt(String key) {
        return Integer.parseInt(loadProperties().getProperty(key));
    }

    public static boolean isActive(String key) {
        return Boolean.parseBoolean(loadProperties().getProperty(key));
    }

}
