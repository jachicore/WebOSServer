/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ambimmort.nisp.commons.httpserver;

/**
 *
 * @author SH
 */
public class AppModel {
    private String appid;
    private String name;
    private String image;
    private String profile;
    private String installUrl;
    private String isInnerApp;

    /**
     * @return the appid
     */
    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AppModel other = (AppModel) obj;
        return !((this.appid == null) ? (other.appid != null) : !this.appid.equals(other.appid));
    }

    @Override
    public int hashCode(){
        return appid.hashCode();
    }
    
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return the profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * @return the installUrl
     */
    public String getInstallUrl() {
        return installUrl;
    }

    /**
     * @param installUrl the installUrl to set
     */
    public void setInstallUrl(String installUrl) {
        this.installUrl = installUrl;
    }

    /**
     * @return the isInnerApp
     */
    public String getIsInnerApp() {
        return isInnerApp;
    }

    /**
     * @param isInnerApp the isInnerApp to set
     */
    public void setIsInnerApp(String isInnerApp) {
        this.isInnerApp = isInnerApp;
    }
    
}
