package com.ambimmort.webos.commons.dmc;

import java.util.HashMap;
import javax.servlet.http.HttpSession;

public class SessionManager {

    private static SessionManager instance = null;
    private HashMap<String, HttpSession> sessions = new HashMap();

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void addSession(HttpSession session) {
        if (!(this.sessions.containsKey(session.getId()))) {
            this.sessions.put(session.getId(), session);
        }
    }

    public HttpSession getSession(String id) {
        return ((HttpSession) this.sessions.get(id));
    }
}

