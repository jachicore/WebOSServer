package com.ambimmort.webos.dmcClient.registry;

import com.ambimmort.webos.commons.cassandradb.core.CassandraSimpleDBBridge;
import com.ambimmort.webos.commons.cassandradb.core.DBBridge;
import com.datastax.driver.core.Row;
import java.util.Iterator;

public class RegistryDB {

    private static RegistryDB instance = null;
    private DBBridge bridge = null;

    private RegistryDB() {
        init();
    }

    protected void finalize()
            throws Throwable {
        super.finalize();
        shutdown();
    }

    public static synchronized RegistryDB getInstance() {
        if (instance == null) {
            instance = new RegistryDB();
        }
        return instance;
    }

    private void init() {
        this.bridge = new CassandraSimpleDBBridge();
    }

    public boolean excute(String cql) {
        boolean rst = true;
        try {
            this.bridge.excute(cql);
        } catch (Exception e) {
            rst = false;
        }
        return rst;
    }

    public String getValueByCQL(String cql) {
        Iterator rows = this.bridge.excute(cql);
        if (rows.hasNext()) {
            Row row = (Row) rows.next();
            return row.getString("value");
        }
        return null;
    }

    public boolean add(String key, String value) {
        if (key.isEmpty()) {
            return false;
        }
        String cql = "INSERT INTO registry(key,value) VALUES('%s','%s');";
        boolean result = true;
        cql = String.format(cql, new Object[]{key, value});
        try {
            this.bridge.excute(cql);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public boolean update(String key, String value) {
        String cql = "UPDATE registry SET value = '%s' WHERE key = '%s';";
        boolean result = true;
        cql = String.format(cql, new Object[]{value, key});
        try {
            this.bridge.excute(cql);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public boolean delete(String key) {
        String cql = "DELETE FROM registry WHERE key = '%s';";
        boolean result = true;
        cql = String.format(cql, new Object[]{key});
        try {
            this.bridge.excute(cql);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public void shutdown() {
        this.bridge.close();
    }
}
