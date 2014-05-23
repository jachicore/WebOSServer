package com.ambimmort.webos.commons.dmc;

import javax.servlet.http.HttpSession;

public class SessionJS
        implements IHttpSessionAware {

    HttpSession session;

    public SessionJS() {
        this.session = null;
    }

    public void bind(HttpSession session) {
        this.session = session;
        //this.session.setMaxInactiveInterval(20*60);
    }

    public void setAttribute(String key, String value) {
        this.session.setAttribute(key, value);
    }

    public String getAttribute(String key) {
        if (this.session.getAttribute(key) != null) {
            return ((String) this.session.getAttribute(key));
        }
        return null;
    }
}

