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

@WebServlet(url = "/aas/servlets/warnInfo")
@Project(name = "WebOS", author = "SH@Ambimmort")
public class WarnInfo extends HttpServlet {

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
            Set<String> appids = LogGatherCache.getInstance().getUser_appids(userName, ENV.NotifType.WARNINFO);
            //Map<String>
            int count = appids.size();
            Object[] apparr = appids.toArray();
            for (int i = 0; i < count; ++i) {
                String appid = apparr[i].toString();
                AppModel model = AppCache.getInstance().getById(appid);
                if (model != null) {
                    JSONObject o = new JSONObject();
                    o.put("name", model.getName());
                    o.put("path", userName + "_" + appid);
                    List<NotifModel> notifs = LogGatherCache.getInstance().getUser_app_type_notify(userName, appid, ENV.NotifType.WARNINFO);
                    int size = 0;
                    for(NotifModel notif:notifs){
                        if (notif.getFlag().equals("0")) {
                            size++;
                        }
                    }
                    o.put("count", size);
                    items.add(o);
                }
            }

            rst.put("items", items);
            resp.setCharacterEncoding("utf-8");
            resp.getWriter().write(rst.toString());
            //IOUtils.write(rst.toString(), resp.getOutputStream(), "utf-8");
            //rst.clear();
            resp.getWriter().flush();
            resp.getWriter().close();
            //IOUtils.closeQuietly(resp.getOutputStream());
        } catch (Throwable e) {
            Logger.getLogger(WarnInfo.class).debug(e.toString());
            IOUtils.closeQuietly(resp.getOutputStream());
        }
    }
}
