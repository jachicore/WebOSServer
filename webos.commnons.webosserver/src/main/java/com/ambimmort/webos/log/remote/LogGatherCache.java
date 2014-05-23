/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.webos.log.remote;

import com.ambimmort.webos.dbclient.notification.NotifDB;
import com.ambimmort.webos.dbclient.notification.NotifModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author SH
 */
public class LogGatherCache {

    private static LogGatherCache instance = null;

    /**
     * @param aInstance the instance to set
     */
    public static void setInstance(LogGatherCache aInstance) {
        instance = aInstance;
    }
    private final HashMap<String, List<NotifModel>> user_app_type_notif;

    private final NotifDB db = NotifDB.getInstance();

    private final HashMap<String, Set<String>> user_type_appids;

    private LogGatherCache() {
        user_app_type_notif = new HashMap<String, List<NotifModel>>();
        user_type_appids = new HashMap<String, Set<String>>();
    }

    public synchronized static LogGatherCache getInstance() {
        if (instance == null) {
            instance = new LogGatherCache();
        }
        return instance;
    }

    /**
     * @return the warninfoGather
     */
    public List<NotifModel> getUser_app_type_notify(String username, String appid, String type) {
        String key = username + "_" + appid + "_" + type;
        if (getUser_app_type_notif().containsKey(key)) {
            List<NotifModel> notifs = getUser_app_type_notif().get(key);
            return notifs;
        } else {
            List<NotifModel> temp = db.getNotifByUserPath(username, appid, type);
            getUser_app_type_notif().put(key, temp);
            return temp;
        }
    }

    public void addUser_app_type_notify(String username, String appid, String type, NotifModel model) {
        String key = username + "_" + appid + "_" + type;
        if (getUser_app_type_notif().containsKey(key)) {
            getUser_app_type_notif().get(key).add(model);
        }
    }

    /**
     * @param username
     * @return the user_appids
     */
    public Set<String> getUser_appids(String username, String type) {
        String key = username + "_" + type;
        if (user_type_appids.containsKey(key)) {
            return user_type_appids.get(key);
        } else {
            List<String> temp = db.getNotifPath(username, type);

            Set<String> set = new HashSet<String>();
            for (String appid : temp) {
                set.add(appid);
            }
            user_type_appids.put(key, set);
            return set;
        }
    }

    public void addUser_type_appids(String username, String appid, String type) {
        String key = username + "_" + type;
        if (user_type_appids.containsKey(key)) {
            user_type_appids.get(key).add(appid);
        }
    }

    /**
     * @return the user_app_type_notif
     */
    public HashMap<String, List<NotifModel>> getUser_app_type_notif() {
        return user_app_type_notif;
    }

}
