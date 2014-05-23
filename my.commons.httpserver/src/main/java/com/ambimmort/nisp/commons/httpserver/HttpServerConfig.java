package com.ambimmort.nisp.commons.httpserver;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class HttpServerConfig {

    private static HttpServerConfig instance = null;
    private int port = 8080;
    private List<String> resources = null;
    private Map<String, String> proxies = null;
    private Logger logger = Logger.getLogger(HttpServerConfig.class);

    private HttpServerConfig() {
        reload();
    }

    public void reload() {
        PropertyConfigurator.configure("config/log4j.conf");
        try {
            this.port = Integer.parseInt(FileUtils.readFileToString(new File("config/http.port")).trim());
        } catch (IOException ex) {
            System.exit(1);
        }
        //File f;
        for (File f: new File("config").listFiles()) {
            if (!(f.getAbsolutePath().endsWith(".resource"))) {
                continue;
            }
            try {
                this.resources = FileUtils.readLines(f);
            } catch (IOException ex) {
                this.logger.warn(ex);
            }
        }
        this.proxies = new HashMap();
        for (File f: new File("config").listFiles()) {
            if (!(f.getAbsolutePath().endsWith(".proxy"))) {
                continue;
            }
            try {
                List<String> ps = FileUtils.readLines(f);
                for (String p : ps) {
                    if (p.trim().equals("")) {
                        continue;
                    }
                    String[] px = p.split("->");
                    this.proxies.put(px[0], px[1]);
                }
            } catch (IOException ps) {
               // this.logger.warn(ex);
            }
        }
        System.out.println(this.proxies);
        this.logger.info(this.proxies);
    }

    public static HttpServerConfig getInstance() {
        if (instance == null) {
            instance = new HttpServerConfig();
        }
        return instance;
    }

    public int getPort() {
        return this.port;
    }

    public Map<String, String> getProxies() {
        return this.proxies;
    }

    public List<String> getResources() {
        return this.resources;
    }

    public static void main(String[] args) {
        String[] d = "a->b".split("->");
        System.out.println(d[0] + "  ->   " + d[1]);
    }
}

/* Location:           C:\Users\c1avie\Desktop\SH\nisp.commons.httpserver\lib\nisp.commons.httpservice-1.0-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.nisp.commons.httpservice.HttpServerConfig
 * JD-Core Version:    0.5.3
 */
