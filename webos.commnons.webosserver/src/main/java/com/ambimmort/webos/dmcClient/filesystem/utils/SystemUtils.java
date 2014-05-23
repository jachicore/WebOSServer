/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.webos.dmcClient.filesystem.utils;

/**
 *
 * @author c1avie
 */
public class SystemUtils {
    private static SystemUtils instance = null;
    private SystemUtils(){
        
    }
    
    public synchronized static SystemUtils getInstance(){
        if (instance == null) {
            instance = new SystemUtils();
        }
        return instance;
    }
}
