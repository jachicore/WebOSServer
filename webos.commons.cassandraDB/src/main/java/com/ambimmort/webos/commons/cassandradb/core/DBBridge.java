/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.webos.commons.cassandradb.core;

import com.datastax.driver.core.Row;
import java.util.Iterator;

/**
 *
 * @author SH
 */
public abstract class DBBridge {

    IDBClient session = null;

    @Override
    protected void finalize() throws Throwable {

        super.finalize();
        close();
    }

    public synchronized Iterator<Row> excute(String query) {
        return session.excute(query);
    }

    public void build(String ks) {
        session.build(ks);
    }

    public void connect(String node) {
        session.connect(node);
    }

    public void close() {
        session.close();
        session = null;
    }
}
