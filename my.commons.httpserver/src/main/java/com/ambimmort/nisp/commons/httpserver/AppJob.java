/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.nisp.commons.httpserver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.ProxyServlet;

/**
 *
 * @author c1avie
 */
public class AppJob implements Runnable {

    private AppDB db = AppDB.getInstance();
    private Set<AppModel> apps = new HashSet<AppModel>();

    public AppJob() {
    }

    public void run() {
        //
        while (true) {
            Set<AppModel> temp = null;
            try {
                temp = db.getAllApp();
            } catch (Exception e) {
                Logger.getLogger(AppJob.class.getName()).log(Level.SEVERE, null, e);
                e.printStackTrace();
                try {
                    Thread.sleep(10*1000);
                    continue;
                } catch (InterruptedException ex) {
                    Logger.getLogger(AppJob.class.getName()).log(Level.SEVERE, null, ex);
                    ex.printStackTrace();
                }
            }
            
            if (!temp.equals(apps)) {
                System.out.println("New App install,proxy it!");
                Map<String, ServletHolder> servlets = new HashMap<String, ServletHolder>();
                apps = temp;
                for (AppModel app : apps) {
                    if (app.getIsInnerApp().equals("0")) {
                        String profile = app.getProfile();
                        String appid = app.getAppid();
                        JSONObject obj = JSONObject.fromObject(profile);
                        JSONArray arr = obj.getJSONArray("funcs");
                        for (Object func : arr) {
                            JSONObject funcobj = JSONObject.fromObject(func);
                            if (funcobj.getString("id").equals("1000")) {
                                String url = funcobj.getString("url");
                                //String url = "http://192.168.1.101:8080/getapp";
                                ServletHolder proxy = new ServletHolder(ProxyServlet.Transparent.class);
                                String key = "/" + appid;
                                HttpServerApp.getInstance().getContext().addServlet(proxy, key + "/*");
                                proxy.setEnabled(true);
                                proxy.setInitParameter("ProxyTo", url);
                                proxy.setInitParameter("Prefix", key);
                                proxy.setAsyncSupported(true);
                                servlets.put(key, proxy);
                                System.out.println("proxy "+ appid + "to:"+url);
                            }
                        }
                    }
                }
                HttpServerApp.getInstance().restart(servlets);
            }
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AppJob.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//        for (int i = 0; i < 1; i++) {
//            System.out.println("add servlet " + i);
//            ServletHolder proxy = new ServletHolder(ProxyServlet.Transparent.class);
//            String key = "/9b1e9349-221f-47a4-9ac5-0174b3a87645";
//            HttpServer.getInstance().getContext().addServlet(proxy, key + "/*");
//            proxy.setEnabled(true);
//            proxy.setInitParameter("ProxyTo", "http://192.168.1.101:8080/getapp");
//            proxy.setInitParameter("Prefix", key);
//            proxy.setAsyncSupported(true);
//            Map<String, ServletHolder> servlets = new HashMap<String, ServletHolder>();
//            servlets.put(key, proxy);
//            HttpServer.getInstance().restart(servlets);
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(AppJob.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

    }

    protected void finalize()
            throws Throwable {
        super.finalize();
        shutdown();
    }

    private void shutdown() {
        //this.bridge.close();
    }

}
