package com.ea.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    private static Properties loadProperties() {
        Properties configuration = new Properties();
        InputStream inputStream = PropertiesLoader.class
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

    public static String getStringProperty(String key) {
        return loadProperties().getProperty(key);
    }

    public static Integer getIntegerProperty(String key) {
        return Integer.parseInt(loadProperties().getProperty(key));
    }

}
