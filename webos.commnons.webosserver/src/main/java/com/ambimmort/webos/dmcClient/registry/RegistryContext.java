package com.ambimmort.webos.dmcClient.registry;

import java.util.HashMap;

public class RegistryContext {

    private static RegistryContext instance = null;
    private HashMap<String, String> registry = null;
    private RegistryDB db = null;
    private int size = 100;

    private RegistryContext() {
        this.registry = new HashMap();
        this.db = RegistryDB.getInstance();
    }

    public static synchronized RegistryContext getInstance() {
        if (instance == null) {
            instance = new RegistryContext();
        }
        return instance;
    }

    public void addRegistry(String key, String value) {
        if (this.registry.size() >= this.size) {
            this.registry.clear();
        }
        this.registry.put(key, value);
        this.db.add(key, value);
    }

    public String getValue(String key) {
        if (this.registry.containsKey(key)) {
            return ((String) this.registry.get(key));
        }
        String cql = "SELECT * FROM registry WHERE key='%s';";
        cql = String.format(cql, key);
        String value = this.db.getValueByCQL(cql);
        this.registry.put(key, value);
        if (value.isEmpty()||value ==null) {
            value = "{}";
        }
        return value;
    }

    public boolean updateValue(String key, String value) {
        this.registry.put(key, value);
        return this.db.update(key, value);
    }

    public boolean delete(String key) {
        this.registry.remove(key);
        return this.db.delete(key);
    }
}

