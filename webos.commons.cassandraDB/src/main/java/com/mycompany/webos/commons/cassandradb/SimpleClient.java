/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.webos.commons.cassandradb;

import com.ambimmort.webos.commons.cassandradb.core.DBSessionPool;
import com.ambimmort.webos.commons.cassandradb.core.IDBClient;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.util.Iterator;

/**
 *
 * @author SH
 */
public class SimpleClient implements IDBClient<Row> {

    private Cluster cluster;
    private Session session;

    public void connect(String node) {
        try {
            cluster = Cluster.builder()
                    .addContactPoint(node).build();
            session = cluster.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void connect(String[] nodes) {
        try {
            cluster = Cluster.builder()
                    .addContactPoints(nodes).build();
            session = cluster.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void build(String ks) {
        session.execute("USE " + ks);
    }

    public Iterator excute(String sql) {
        Iterator rs = session.execute(sql).iterator();
        return rs;
    }

    public void close() {
        DBSessionPool.getInstance().returnClient(this);
    }

    public static void main(String[] args) {
        SimpleClient client = new SimpleClient();
        client.connect("59.68.29.73");
        client.build("webos");
        Iterator<Row> i = client.excute("INSERT INTO user(username,profile) VALUES('test','test');");

        while (i.hasNext()) {
            Row row = i.next();
            System.out.println(row.getString("username"));
        }
        client.close();
    }
}
