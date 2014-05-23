/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.nisp.commons.httpserver;

import com.ambimmort.webos.commons.cassandradb.core.CassandraSimpleDBBridge;
import com.ambimmort.webos.commons.cassandradb.core.DBBridge;
import com.datastax.driver.core.Row;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author SH
 */
public class AppDB {

    private Logger log = Logger.getLogger(AppDB.class);
    private static AppDB instance = null;
    private DBBridge bridge = null;

    private AppDB() {
        init();
    }

    private void init() {
        this.bridge = new CassandraSimpleDBBridge();
    }

    protected void finalize()
            throws Throwable {
        super.finalize();
        shutdown();
    }

    public static synchronized AppDB getInstance() {
        if (instance == null) {
            instance = new AppDB();
        }
        return instance;
    }

    public void shutdown() {
        this.bridge.close();
    }

    public boolean excute(String cql) {
        boolean rst = true;
        try {
            this.bridge.excute(cql);
        } catch (Exception e) {
            log.error(e.getMessage());
            rst = false;
        }
        return rst;
    }

    public Set<AppModel> getAllApp() {
        Set<AppModel> apps = new HashSet<AppModel>();
        String cql = "SELECT * FROM app";
        Iterator rows = this.bridge.excute(cql);
        while (rows.hasNext()) {
            AppModel app = new AppModel();
            Row row = (Row) rows.next();
            app.setAppid(row.getString("appid").toString());
            app.setName(row.getString("name"));
            app.setProfile(row.getString("profile"));
            app.setInstallUrl(row.getString("url"));
            app.setIsInnerApp(row.getString("isInnerApp"));
            apps.add(app);
        }
        return apps;
    }

    public List<AppModel> getAppById(String id) {
        String cql = "SELECT * FROM app WHERE appid = '%s';";
        cql = String.format(cql, id);
        List<AppModel> apps = new ArrayList<AppModel>();
        Iterator rows = this.bridge.excute(cql);
        while (rows.hasNext()) {
            AppModel app = new AppModel();
            Row row = (Row) rows.next();
            app.setAppid(row.getString("appid"));
            app.setName(row.getString("name"));
            app.setInstallUrl(row.getString("url"));
            app.setProfile(row.getString("profile"));
            app.setIsInnerApp(row.getString("isInnerApp"));
            apps.add(app);

        }
        return apps;
    }

    private AppModel getAppByid(String id) {
        String cql = "SELECT * FROM app WHERE appid = '%s';";
        cql = String.format(cql, id);
        Iterator rows = this.bridge.excute(cql);
        AppModel app = new AppModel();
        while (rows.hasNext()) {

            Row row = (Row) rows.next();
            app.setAppid(row.getString("appid"));
            app.setName(row.getString("name"));
            app.setInstallUrl(row.getString("url"));
            app.setProfile(row.getString("profile"));
            app.setIsInnerApp(row.getString("isInnerApp"));
        }
        return app;
    }

}
