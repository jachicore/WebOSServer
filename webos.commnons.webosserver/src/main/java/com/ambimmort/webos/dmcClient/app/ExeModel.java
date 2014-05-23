/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ambimmort.webos.dmcClient.app;

/**
 *
 * @author SH
 */
public class ExeModel {
    private String name;
    private String image;
    private String linkurl;
    private String winsettings="{}";

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
     * @return the linkurl
     */
    public String getLinkurl() {
        return linkurl;
    }

    /**
     * @param linkurl the linkurl to set
     */
    public void setLinkurl(String linkurl) {
        this.linkurl = linkurl;
    }

    /**
     * @return the winsettings
     */
    public String getWinsettings() {
        return winsettings;
    }

    /**
     * @param winsettings the winsettings to set
     */
    public void setWinsettings(String winsettings) {
        this.winsettings = winsettings;
    }
    
    
}
