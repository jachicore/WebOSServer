package com.ambimmort.webos.log.remote;

import com.ambimmort.webos.commons.env.ENV;
import com.ambimmort.webos.commons.servlets.annotation.Project;
import com.ambimmort.webos.commons.servlets.annotation.WebServlet;
import com.ambimmort.webos.dbclient.notification.NotifDB;
import com.ambimmort.webos.dbclient.notification.NotifModel;
import com.ambimmort.webos.dmcClient.app.App;
import com.ambimmort.webos.dmcClient.app.AppCache;
import com.ambimmort.webos.dmcClient.app.AppModel;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

@WebServlet(url = "/aas/servlets/explorerService")
@Project(name = "WebOS", author = "SH@Ambimmort")
public class ExplorerService extends HttpServlet {

    private final NotifDB db = NotifDB.getInstance();
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String userName = req.getParameter("username");
            if (userName == null) {
                resp.getWriter().print("{\"r:\":false,\"items\":[]}");
                return;
            }
            JSONObject rst = new JSONObject();
            rst.put("r", Boolean.valueOf(true));
            JSONArray items = new JSONArray();
            //Map<String>
            for (String service : ENV.webosService) {
                List<NotifModel> files = NotifDB.getInstance().getNotifByUserAndType(userName,service);
                for (NotifModel file : files) {
                    JSONObject o = JSONObject.fromObject(file.getMessage());
                    items.add(o);
                    NotifDB.getInstance().deleteNotifById(file.getId());
                }
                rst.put(service, items);
            }

            resp.setCharacterEncoding("utf-8");
            resp.getWriter().write(rst.toString());
            //IOUtils.write(rst.toString(), resp.getOutputStream(), "utf-8");
            //rst.clear();
            resp.getWriter().flush();
            resp.getWriter().close();
            //IOUtils.closeQuietly(resp.getOutputStream());
        } catch (Throwable e) {
            Logger.getLogger(ExplorerService.class).debug(e.toString());
            IOUtils.closeQuietly(resp.getOutputStream());
        }
    }
}
