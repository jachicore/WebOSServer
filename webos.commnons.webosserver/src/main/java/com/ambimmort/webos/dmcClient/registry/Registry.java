package com.ambimmort.webos.dmcClient.registry;

import com.ambimmort.webos.commons.dmc.DMCObject;

public class Registry extends DMCObject {

    private RegistryContext context = null;

    public Registry() {
        this.context = RegistryContext.getInstance();
    }

    public void addRegistry(String key, String value) {
        this.context.addRegistry(key, value);
    }

    public String getValue(String key) {
        return this.context.getValue(key);
    }

    public boolean updateValue(String key, String value) {
        return this.context.updateValue(key, value);
    }

    public boolean delete(String key) {
        return this.context.delete(key);
    }

}

