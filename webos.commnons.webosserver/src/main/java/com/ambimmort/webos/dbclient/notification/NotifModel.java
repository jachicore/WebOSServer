/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ambimmort.webos.dbclient.notification;

import java.security.Timestamp;

/**
 *
 * @author SH
 */
public class NotifModel {
    private String id;
    private String appid;
    private String username;
    private String message;
    private String notiftype;
    private long timestp;
    private String flag;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the appid
     */
    public String getAppid() {
        return appid;
    }

    /**
     * @param appid the appid to set
     */
    public void setAppid(String appid) {
        this.appid = appid;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the notiftype
     */
    public String getNotiftype() {
        return notiftype;
    }

    /**
     * @param notiftype the notiftype to set
     */
    public void setNotiftype(String notiftype) {
        this.notiftype = notiftype;
    }

    /**
     * @return the timestp
     */
    public long getTimestp() {
        return timestp;
    }

    /**
     * @param timestp the timestp to set
     */
    public void setTimestp(long timestp) {
        this.timestp = timestp;
    }

    /**
     * @return the flag
     */
    public String getFlag() {
        return flag;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(String flag) {
        this.flag = flag;
    }
    
    
}
