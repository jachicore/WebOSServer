package com.ambimmort.webos.log.remote;

import com.ambimmort.webos.commons.servlets.annotation.Project;
import com.ambimmort.webos.commons.servlets.annotation.WebServlet;
import com.ambimmort.webos.dbclient.notification.NotifDB;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

@WebServlet(url = "/aas/servlets/clearlogs")
@Project(name = "WebOS", author = "SH@Ambimmort")
public class clearlogs extends HttpServlet {
    
    private final NotifDB db = NotifDB.getInstance();
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String path = req.getParameter("path");
            String username = path.split("_")[0];
            String appid = path.split("_")[1];
            db.deleteNotifByPath(username, appid, "warninfo");
            resp.getWriter().println("true");
            resp.getWriter().close();
        } catch (Throwable t) {
            Logger.getLogger(clearlogs.class).debug(t.toString());
            resp.getWriter().println("false");
            resp.getWriter().close();
        }
    }
}

/* Location:           E:\company\webosInstall\2013.11.9-1\cc\applications\nisp.webos\nisp.webos.server\program\lib\nisp.webos.server-1.0.1-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.log.remote.clearlogs
 * JD-Core Version:    0.5.3
 */
