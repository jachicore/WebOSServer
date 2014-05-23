/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ambimmort.webos.commons.cassandradb.core;

import com.mycompany.webos.commons.cassandradb.SimpleClient;
import java.util.Stack;

/**
 *
 * @author SH
 */
public class DBSessionPool {
    private static DBSessionPool instance = null;
    private Stack<SimpleClient> sessions = null;
    private int size = 20;
    
    public DBSessionPool(){
        sessions = new Stack<SimpleClient>();
        for(int i=0;i<size;i++){
            sessions.push(new SimpleClient());
        }
        
    }
    
    public static synchronized DBSessionPool getInstance(){
        if(instance ==null){
            instance = new DBSessionPool();
        }
        return instance;
    }
    
    public synchronized SimpleClient getClient(){
        if (this.sessions.isEmpty()) {
            SimpleClient client = new SimpleClient();
            return client;
        }
        return sessions.pop();
    }
    
    public synchronized void returnClient(SimpleClient client){
        this.sessions.push(client);
    }
}
