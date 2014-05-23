/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.webos.dmcClient.app;

import com.ambimmort.webos.dmcClient.users.UserDB;
import com.ambimmort.webos.commons.cassandradb.core.CassandraSimpleDBBridge;
import com.ambimmort.webos.commons.cassandradb.core.DBBridge;
import com.datastax.driver.core.Row;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

/**
 *
 * @author SH
 */
public class AppDB {

    private Logger log = Logger.getLogger(AppDB.class);
    private static AppDB instance = null;
    private UserDB userdb = null;
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

    public List<AppModel> getAllApp() {
        List<AppModel> apps = new ArrayList<AppModel>();
        String cql = "SELECT * FROM app";
        Iterator rows = this.bridge.excute(cql);
        while (rows.hasNext()) {
            AppModel app = new AppModel();
            Row row = (Row) rows.next();
            app.setAppid(row.getString("appid").toString());
            app.setName(row.getString("name"));
            app.setProfile(row.getString("profile"));
            app.setInstallUrl(row.getString("url"));
            app.setIsInnerApp(row.getString("isinnerapp"));

            try {
                JSONObject profile = JSONObject.fromObject(app.getProfile());
                app.setImage(profile.getString("image"));
                apps.add(app);
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
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
            app.setIsInnerApp(row.getString("isinnerapp"));

            try {
                JSONObject profile = JSONObject.fromObject(app.getProfile());
                app.setImage(profile.getString("image"));
                apps.add(app);
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
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
            app.setIsInnerApp(row.getString("isinnerapp"));

            try {
                JSONObject profile = JSONObject.fromObject(app.getProfile());
                app.setImage(profile.getString("image"));
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        return app;
    }

    public ExeModel getExeById(String appkey, String username, JSONObject sessionobj) {
        String ip = sessionobj.getString("ip");
        String cookie = sessionobj.getString("cookie");
        ExeModel exe = new ExeModel();
        try {
            if ((!AppCache.getInstance().getFirstLevelCache().containsKey(appkey))
                    && (!(AppCache.getInstance().getSecondLevelCache().containsKey(appkey)))) {
                String cql = "SELECT * FROM app WHERE appid = '%s';";
                cql = String.format(cql, appkey);

                Iterator rows = this.bridge.excute(cql);
                if (!rows.hasNext()) {
                    exe.setImage("images/icons/app-warning-icon.png");
                    exe.setLinkurl("");
                    exe.setName("Has uninstall");
                    exe.setWinsettings("{}");
                    return exe;
                }
                while (rows.hasNext()) {
                    Row row = (Row) rows.next();
                    AppModel temp = new AppModel();
                    temp.setName(row.getString("name"));
                    temp.setAppid(appkey);
                    temp.setProfile(row.getString("profile"));
                    JSONObject profile = JSONObject.fromObject(row.getString("profile"));
                    temp.setImage(profile.getString("image"));
                    temp.setInstallUrl(row.getString("url"));
                    temp.setIsInnerApp(row.getString("isinnerapp"));
                    AppCache.getInstance().getFirstLevelCache().put(appkey, temp);
                }
            }
            AppModel temp = AppCache.getInstance().getById(appkey);
            boolean isinner = temp.getIsInnerApp().equals("1");
            exe.setName(temp.getName());
            JSONObject profile = JSONObject.fromObject(temp.getProfile());
            exe.setImage(profile.getString("image"));
            exe.setWinsettings(profile.getString("winSettings"));
            if (isinner) {
                JSONArray arr = profile.getJSONArray("funcs");
                for (Object func : arr) {
                    JSONObject funcobj = JSONObject.fromObject(func);
                    if (funcobj.getString("id").equals("1000")) {
                        exe.setLinkurl(funcobj.getString("url"));
                    }
                }
            } else {
                exe.setLinkurl("/" + appkey + "/");
            }

            String roles_str = UserDB.getInstance().getUser(username).getRoles();
            Object[] roles = null;

            roles = JSONArray.fromObject(roles_str).toArray();

            StringBuilder ids = new StringBuilder();
            for (Object role : roles) {
                String cql = "SELECT permissions FROM group_app_map WHERE appid = '%s' AND groupname='%s';";
                cql = String.format(cql, appkey, role.toString());
                Iterator rows = this.bridge.excute(cql);
                if (rows.hasNext()) {
                    Row row = (Row) rows.next();
                    String permissions = row.getString("permissions");
                    try {
                        JSONArray perobj = JSONArray.fromObject(permissions);
                        for (int j = 0; j < perobj.size(); j++) {
                            JSONObject obj = perobj.getJSONObject(j);
                            ids.append(obj.get("id")).append("_");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        log.error(ex.getMessage());
                    }

                }
            }
            if (ids.toString().equals("")) {
                exe.setImage("");
                exe.setName("No permissions");
                exe.setLinkurl("");
                return exe;
            }
            String perm = "id=" + ids.toString();
            if (perm.lastIndexOf("_") != -1) {
                perm = perm.substring(0, perm.lastIndexOf("_"));
            }
            exe.setLinkurl(exe.getLinkurl() + "?" + perm + "&" + "username=" + username + "&"
                    + "appid=" + appkey + "&ip=" + ip + "&cookie=" + cookie);
        } catch (Exception e) {
            exe.setImage("");
            exe.setName("error");
            exe.setLinkurl("");
            e.printStackTrace();
            log.error(e.getMessage());
            return exe;
        }
        return exe;
    }

    public List<AppModel> getAppsByRole(String groupname) {
        List<AppModel> apps = new ArrayList<AppModel>();
        try {
            String cql = "SELECT * FROM group_app_map WHERE groupname = '%s';";
            cql = String.format(cql, groupname);

            Iterator rows = this.bridge.excute(cql);
            while (rows.hasNext()) {
                Row row = (Row) rows.next();
                AppModel app = this.getAppByid(row.getString("appid"));
                if (app == null) {
                    continue;
                }
                app.setAppid(row.getString("appid"));
                app.setProfile(row.getString("permissions"));
                apps.add(app);
//apps.addAll(getAppById(row.getString("appid")));
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return apps;

    }

    public boolean addApp(String appid, String name, String url, String profile, String type) {
        String cql = "INSERT INTO app (appid, name,url, profile,isinnerapp ) VALUES ( '%s','%s','%s','%s','%s');";
        cql = String.format(cql, appid, name, url, profile, type);
        try {
            Iterator rows = this.bridge.excute(cql);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean updateApp(String id, String name, String url, String profile) {
        String cql = "UPDATE app SET name = '%s',profile='%s',url='%s' WHERE appid='%s';";
        cql = String.format(cql, name, profile,url, id);
        try {
            this.bridge.excute(cql);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteApp(String id) {
        String cql = "DELETE FROM app WHERE appid='%s';";
        //DELETE FROM group_app_map WHERE groupname=' ' AND  appid=' ';
        cql = String.format(cql, id);
        try {
            this.bridge.excute(cql);
            cql = "SELECT groupname,appid FROM group_app_map WHERE appid='%s' ALLOW FILTERING;";
            cql = String.format(cql, id);
            Iterator rows = this.bridge.excute(cql);
            while (rows.hasNext()) {

                Row row = (Row) rows.next();
                String appid = row.getString("appid");
                String groupname = row.getString("groupname");
                cql = "DELETE FROM group_app_map WHERE groupname='%s' AND  appid='%s';";
                cql = String.format(cql, groupname, appid);
                this.bridge.excute(cql);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
