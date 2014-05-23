package com.ambimmort.webos.log.remote;

import com.ambimmort.webos.commons.env.ENV;
import com.ambimmort.webos.commons.servlets.annotation.Project;
import com.ambimmort.webos.commons.servlets.annotation.WebServlet;
import com.ambimmort.webos.dbclient.notification.NotifDB;
import com.ambimmort.webos.dbclient.notification.NotifModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;

@WebServlet(url = "/aas/servlets/showlogs")
@Project(name = "WebOS", author = "SH@Ambimmort")
public class showlogs extends HttpServlet {

    private final NotifDB db = NotifDB.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setCharacterEncoding("utf-8");
            String path = req.getParameter("path");
            if (path == null) {
                return;
            }
            String username = path.split("_")[0];
            String appid = path.split("_")[1];
            JSONObject rst = new JSONObject();
            List<NotifModel> files = LogGatherCache.getInstance().getUser_app_type_notify(username, appid, ENV.NotifType.WARNINFO);
            for (NotifModel model : files) {
                String cql = "UPDATE notification SET flag = '1' WHERE id='" + model.getId() + "'";
                db.excute(cql);
                model.setFlag("1");
            }
            LogGatherCache.getInstance().getUser_app_type_notif().put(path+ "_" + ENV.NotifType.WARNINFO, files);
            int count = files.size();
            List late30m = new ArrayList();
            List others = new ArrayList();
            for (int i = 0; i < count; ++i) {
                NotifModel file = files.get(i);
                JSONObject record = new JSONObject();
                record.put("time", Long.valueOf(file.getTimestp()));
                record.put("content", file.getMessage());
                if (System.currentTimeMillis() - file.getTimestp() > 1800000L) {
                    others.add(record);
                } else {
                    late30m.add(record);
                }
            }
            Collections.sort(late30m, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return (int) (((JSONObject) o2).getLong("time") - ((JSONObject) o1).getLong("time"));
                }
            });
            Collections.sort(others, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return (int) (((JSONObject) o2).getLong("time") - ((JSONObject) o1).getLong("time"));
                }
            });
            JSONArray late30marr = new JSONArray();
            JSONArray othersarr = new JSONArray();
            Iterator i = late30m.iterator();
            JSONObject o;
            for (; i.hasNext();) {
                o = (JSONObject) i.next();
                late30marr.add(o);
            }
            i = others.iterator();
            for (; i.hasNext();) {
                o = (JSONObject) i.next();
                othersarr.add(o);
            }
            rst.put("last30m", late30marr);
            rst.put("others", othersarr);
            resp.setCharacterEncoding("utf-8");
            IOUtils.write(rst.toString(), resp.getOutputStream(), "utf-8");
            IOUtils.closeQuietly(resp.getOutputStream());
        } catch (Throwable t) {
            t.printStackTrace();
            IOUtils.closeQuietly(resp.getOutputStream());
        } finally {
        }
    }
}
