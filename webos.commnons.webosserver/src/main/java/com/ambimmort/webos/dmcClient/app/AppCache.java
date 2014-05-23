/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.webos.dmcClient.app;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author SH
 */
public class AppCache {

    private static AppCache instance = null;
    private Map<String, AppModel> firstLevelCache = new HashMap<String, AppModel>();
    private Map<String, AppModel> secondLevelCache = new HashMap<String, AppModel>();

    private AppCache() {

    }

    public static synchronized AppCache getInstance() {
        if (instance == null) {
            instance = new AppCache();
        }
        return instance;
    }

    /**
     * @return the firstLevelCache
     */
    public Map<String, AppModel> getFirstLevelCache() {
        return firstLevelCache;
    }

    /**
     * @return the secondLevelCache
     */
    public Map<String, AppModel> getSecondLevelCache() {
        return secondLevelCache;
    }

    public AppModel getById(String appid) {
        if ((!getSecondLevelCache().containsKey(appid)
                && (!this.getFirstLevelCache().containsKey(appid)))) {
            new App().getAllApp();
        }
        AppModel model = null;
        if (getFirstLevelCache().containsKey(appid)) {
            model = getFirstLevelCache().get(appid);
        } else if (getSecondLevelCache().containsKey(appid)){
            model = getSecondLevelCache().get(appid);
            getFirstLevelCache().put(appid, model);
        }
        return model;
    }

}
