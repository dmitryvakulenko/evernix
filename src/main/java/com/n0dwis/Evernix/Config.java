package com.n0dwis.Evernix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private static Config instance = null;


    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }

        return instance;
    }


    private Properties config;

    private Config() {
        config = new Properties();
        try {
            loadConfigFile();
        } catch (IOException $e) {
            throw new RuntimeException("Error reading config file");
        }
    }

    private void loadConfigFile() throws IOException {
        File configFile = new File(makeConfigFileName());
        if (configFile.exists()) {
            config.load(new FileInputStream(configFile));
        } else {
            configFile.createNewFile();
        }
    }

    public void saveConfig() {
        File configFile = new File(makeConfigFileName());
        try {
            config.store(new FileOutputStream(configFile), "Evernix configuration");
        } catch (IOException $e) {
            throw new RuntimeException("Error storing config file.");
        }
    }

    public String get(String key) {
        return config.get(key).toString();
    }

    public void put(String key, String value) {
        config.put(key, value);
    }

    private String makeConfigFileName() {
        return System.getProperty("user.home") + "/.evernix";
    }
}
