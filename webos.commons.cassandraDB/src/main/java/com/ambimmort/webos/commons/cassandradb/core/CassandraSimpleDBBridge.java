/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.webos.commons.cassandradb.core;

/**
 *
 * @author SH
 */
public final class CassandraSimpleDBBridge extends DBBridge {
    private PropertiesUtil pro;

    public CassandraSimpleDBBridge() {
        this(DBSessionPool.getInstance().getClient());
    }

    public CassandraSimpleDBBridge(IDBClient session) {
        pro = PropertiesUtil.getInstance();
        this.session = session;
        init();
//        try {
//            new ClassLoaderTest();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    void init() {
        pro.load("config/app.conf");
        String db = pro.getValue("cassandradb");
        String[] nodes = db.split(",");
        session.connect(nodes);
        session.build(pro.getValue("columnFamily"));
 
    }
}
