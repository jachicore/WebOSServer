package com.ambimmort.webos.commons.env;

import java.util.HashSet;
import java.util.Set;

public class ENV {

    public static final boolean isDebug = true;
    public static class NotifType{
        public static final String WARNINFO="warninfo";
        public static final String CREATEWIN="createwin";
    }
    
    public static Set<String> webosService = null;
    static{
        webosService = new HashSet<String>();
        webosService.add(NotifType.CREATEWIN);
    }
    public static class File {

        public static final String separator = "/";
    }

}
