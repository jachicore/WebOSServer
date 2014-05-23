package com.ambimmort.webos.dmcClient.filesystem.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    private Properties pro = new Properties();

    private static PropertiesUtil instance = null;

    private PropertiesUtil() {
        
    }

    public void load(String config) {
        try {
            InputStream is = new FileInputStream(new File(config));
            pro.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PropertiesUtil getInstance() {
        if (instance == null) {
            instance = new PropertiesUtil();
        }
        return instance;
    }


    public String getValue(String key) {
        return pro.getProperty(key);
    }

    public int getIntValue(String key) {
        return Integer.parseInt(pro.getProperty(key));
    }
}
