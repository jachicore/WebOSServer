/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.webos.dbclient.notification;

import com.ambimmort.webos.log.remote.LogGatherCache;
import com.ambimmort.webos.commons.cassandradb.core.CassandraSimpleDBBridge;
import com.ambimmort.webos.commons.cassandradb.core.DBBridge;
import com.datastax.driver.core.Row;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author SH
 */
public class NotifDB {

    private final Logger log = Logger.getLogger(NotifDB.class);
    private static NotifDB instance = null;
    private DBBridge bridge = null;

    private NotifDB() {
        init();
    }

    private void init() {
        this.bridge = new CassandraSimpleDBBridge();
    }

    @Override
    protected void finalize()
            throws Throwable {
        super.finalize();
        shutdown();
    }

    public static synchronized NotifDB getInstance() {
        if (instance == null) {
            instance = new NotifDB();
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

    public List<NotifModel> getNotifByUserPath(String username, String appid, String type) {
        List<NotifModel> notifs = new ArrayList<NotifModel>();
        String cql = "SELECT * FROM notification WHERE username='%s' AND notiftype='%s' AND appid = '%s' ALLOW FILTERING;";
        cql = String.format(cql, username, type, appid);
        try {
            Iterator rows = this.bridge.excute(cql);
            while (rows.hasNext()) {
                NotifModel notif = new NotifModel();
                Row row = (Row) rows.next();
                notif.setAppid(row.getString("appid").toString());
                notif.setId(row.getString("id"));
                notif.setMessage(row.getString("message"));
                notif.setNotiftype(row.getString("notiftype"));
                notif.setTimestp(row.getDate("timestp").getTime());
                notif.setUsername(row.getString("username"));
                notif.setFlag(row.getString("flag"));
                notifs.add(notif);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return notifs;
    }

    public List<NotifModel> getNotifByUserAndType(String username,String type) {
        List<NotifModel> notifs = new ArrayList<NotifModel>();
        String cql = "SELECT * FROM notification WHERE username='%s' AND notiftype='%s' ALLOW FILTERING;";
        cql = String.format(cql, username, type);
        try {
            Iterator rows = this.bridge.excute(cql);
            while (rows.hasNext()) {
                NotifModel notif = new NotifModel();
                Row row = (Row) rows.next();
                notif.setAppid(row.getString("appid").toString());
                notif.setId(row.getString("id"));
                notif.setMessage(row.getString("message"));
                notif.setNotiftype(row.getString("notiftype"));
                notif.setTimestp(row.getDate("timestp").getTime());
                notif.setUsername(row.getString("username"));
                notif.setFlag(row.getString("flag"));
                notifs.add(notif);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return notifs;
    }

    public List<String> getNotifPath(String username, String type) {
        List<String> paths = new ArrayList<String>();
        String cql = "SELECT appid FROM notification WHERE username='%s' AND notiftype='%s' ALLOW FILTERING;";
        cql = String.format(cql, username, type);
        try {
            Iterator rows = this.bridge.excute(cql);
            while (rows.hasNext()) {
                Row row = (Row) rows.next();
                paths.add(row.getString("appid").toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return paths;
    }

    public String addNotif(NotifModel model) {
        LogGatherCache.getInstance().addUser_type_appids(model.getUsername(), model.getAppid(), model.getNotiftype());
        LogGatherCache.getInstance().addUser_app_type_notify(model.getUsername(), model.getAppid(), model.getNotiftype(), model);
        String cql = "INSERT INTO notification (id, username, appid, notiftype , message , timestp ,flag) VALUES ( '%s','%s','%s','%s','%s',%s,'%s');";
        cql = String.format(cql, model.getId(), model.getUsername(), model.getAppid(), model.getNotiftype(), model.getMessage(), model.getTimestp(),model.getFlag());
        try {
            this.bridge.excute(cql);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return "false";
        }
        return "true";
    }

    public String deleteNotifByPath(String username, String appid, String type) {
        try {
            List<NotifModel> models = LogGatherCache.getInstance().getUser_app_type_notify(username, appid, type);
            //List<NotifModel> models = getNotifByUserPath(username, appid, type);
            for (NotifModel model : models) {
                deleteNotifById(model.getId());
            }
            models.clear();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return "false";
        }
        return "true";
    }

    public void deleteNotifById(String id) throws Exception {
        String cql = "DELETE FROM notification WHERE id ='%s';";
        cql = String.format(cql, id);
        this.bridge.excute(cql);
    }
}
