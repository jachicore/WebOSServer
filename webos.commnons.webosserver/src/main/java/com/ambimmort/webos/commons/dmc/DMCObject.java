package com.ambimmort.webos.commons.dmc;

import javax.servlet.http.HttpSession;

public abstract class DMCObject
        implements IHttpSessionAware {

    private HttpSession session;

    public DMCObject() {
        this.session = null;
    }

    public void bind(HttpSession session) {
        
        this.session = session;
    }

    public HttpSession getHttpSession() {
        return this.session;
    }
}

/* Location:           F:\nisp.webos.server-1.0-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.webos.commons.dmc.DMCObject
 * JD-Core Version:    0.5.3
 */
