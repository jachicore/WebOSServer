/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.webos.dmcClient.filesystem.utils;

import com.ambimmort.webos.dmcClient.filesystem.utils.PropertiesUtil;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author SH
 */
public class FSConfig {

    private static FSConfig instance = null;
    private String MOUNTTYPE = "";

    /**
     * @return the MOUNTTYPE
     */
    public String getMOUNTTYPE() {
        return MOUNTTYPE;
    }
    private Set<String> sysfiles = null;
    private PropertiesUtil pro = null;
    private FSConfig() {
        pro = PropertiesUtil.getInstance();
        sysfiles = new HashSet<String>();
        sysfiles.add("/system/desktops");
        sysfiles.add("/system/desktops/default");
        sysfiles.add("/system/dock");
        readConfig();
    }

    private void readConfig(){
        pro.load("config/app.conf");
        this.MOUNTTYPE = "/"+pro.getValue("datatype") + "/";
    }
    public static synchronized FSConfig getInstance() {
        if (instance == null) {
            instance = new FSConfig();
        }
        return instance;
    }

    /**
     * @return the sysfiles
     */
    public Set<String> getSysfiles() {
        return sysfiles;
    }

}
