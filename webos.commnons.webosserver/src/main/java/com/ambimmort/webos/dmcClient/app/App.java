/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.webos.dmcClient.app;

import com.ambimmort.webos.dmcClient.filesystem.concrete.FileSystem;
import com.ambimmort.webos.commons.dmc.DMCObject;
import com.ambimmort.webos.commons.dmc.IHttpSessionAware;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpSession;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 * @author SH
 */
public class App extends DMCObject implements IHttpSessionAware {

    //private FileSystem fs = null;
    private final AppDB db = AppDB.getInstance();

    public App() {
        if (this.getHttpSession() != null) {
//            fs = new FileSystem();
//            fs.injectSession(this.getHttpSession());
        }

    }

//    public App(HttpSession session) {
////        fs = new FileSystem();
////        fs.injectSession(session);
//    }
    public void test() {
//        fs = new FileSystem();
//        fs.test();
    }

    public String getExeById(String appkey, String username) {
        String ip = this.getHttpSession().getAttribute("ip").toString();
        String cookie = this.getHttpSession().getAttribute("cookie").toString();
        JSONObject sessionObj = new JSONObject();
        sessionObj.put("ip", ip);
        sessionObj.put("cookie", cookie);
        ExeModel exe = db.getExeById(appkey, username, sessionObj);
        return JSONObject.fromObject(exe).toString();
    }

    public String getAppsByRole(String role) {
        List<AppModel> apps = db.getAppsByRole(role);
        return JSONArray.fromObject(apps).toString();
    }

    public String getAppsByRoles(String roles) {
        Object[] arr = JSONArray.fromObject(roles).toArray();
        Set<AppModel> apps = new HashSet<AppModel>();
        for (Object obj : arr) {
            List<AppModel> applist = db.getAppsByRole(obj.toString());
            apps.addAll(applist);
        }
        return JSONArray.fromObject(apps).toString();
    }

    public String getAppById(String id) {
        if (AppCache.getInstance().getFirstLevelCache().containsKey(id)) {
            return JSONArray.fromObject(AppCache.getInstance().getFirstLevelCache().get(id)).toString();
        }
        if (AppCache.getInstance().getSecondLevelCache().containsKey(id)) {
            return JSONArray.fromObject(AppCache.getInstance().getSecondLevelCache().get(id)).toString();
        }
        List<AppModel> apps = db.getAppById(id);
        if (apps.size() > 0) {
            AppCache.getInstance().getFirstLevelCache().put(id, apps.get(0));
        }
        return JSONArray.fromObject(apps).toString();
    }

    public String getAllApp() {
        List<AppModel> apps = null;
        if (AppCache.getInstance().getSecondLevelCache().isEmpty()) {
            apps = db.getAllApp();
            for (AppModel m : apps) {
                AppCache.getInstance().getSecondLevelCache().put(m.getAppid(), m);
            }
            return JSONArray.fromObject(apps).toString();
        }
        apps = new ArrayList<AppModel>();
        for (Map.Entry entry : AppCache.getInstance().getSecondLevelCache().entrySet()) {
            AppModel val = (AppModel) entry.getValue();
            apps.add(val);
        }
        return JSONArray.fromObject(apps).toString();
    }

    public String addApp(String name, String url, String profile) {
        String appid = UUID.randomUUID().toString();
        AppModel app = new AppModel();
        app.setAppid(appid);
        JSONObject pro = JSONObject.fromObject(profile);
        app.setImage(pro.getString("image"));
        app.setInstallUrl(url);
        app.setName(name);
        app.setProfile(profile);
        app.setIsInnerApp("0");
        AppCache.getInstance().getSecondLevelCache().put(appid, app);
        if (db.addApp(appid, name, url, profile, "0")) {
            return appid;
        }
        return null;

    }

    public String addInnerApp(String name, String url, String profile) {
        String appid = UUID.randomUUID().toString();
        AppModel app = new AppModel();
        app.setAppid(appid);
        JSONObject pro = JSONObject.fromObject(profile);
        app.setImage(pro.getString("image"));
        app.setInstallUrl(url);
        app.setName(name);
        app.setProfile(profile);
        app.setIsInnerApp("1");
        AppCache.getInstance().getSecondLevelCache().put(appid, app);
        if (db.addApp(appid, name, url, profile, "1")) {
            return appid;
        }
        return null;

    }

    public boolean updateApp(String id, String name, String url, String profile) {
        AppModel app = db.getAppById(id).get(0);
        app.setInstallUrl(url);
        app.setName(name);
        JSONObject pro = JSONObject.fromObject(profile);
        app.setImage(pro.getString("image"));
        app.setProfile(profile);
        AppCache.getInstance().getSecondLevelCache().remove(id);
        AppCache.getInstance().getSecondLevelCache().put(id, app);
        AppCache.getInstance().getFirstLevelCache().remove(id);
        AppCache.getInstance().getFirstLevelCache().put(id, app);
        return db.updateApp(id, name, url, profile);
    }

    public boolean deleteApp(String id) {
        AppCache.getInstance().getFirstLevelCache().remove(id);
        AppCache.getInstance().getSecondLevelCache().remove(id);
        return db.deleteApp(id);
    }

}
