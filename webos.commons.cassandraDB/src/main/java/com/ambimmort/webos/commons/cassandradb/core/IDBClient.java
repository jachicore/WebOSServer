/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.webos.commons.cassandradb.core;

import java.util.Iterator;

/**
 *
 * @author SH
 * @param <T>
 */
public interface IDBClient<T> {

    public void connect(String node);
    
    public void connect(String[] nodes);

    public void build(String ks);

    public Iterator<T> excute(String sql);

    public void close();
}
