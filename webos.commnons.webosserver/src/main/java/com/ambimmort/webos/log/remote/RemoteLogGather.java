package com.ambimmort.webos.log.remote;

import com.ambimmort.webos.commons.servlets.annotation.Project;
import com.ambimmort.webos.commons.servlets.annotation.WebServlet;
import com.ambimmort.webos.dbclient.notification.NotifDB;
import com.ambimmort.webos.dbclient.notification.NotifModel;
import com.ambimmort.webos.dmcClient.app.AppCache;
import com.ambimmort.webos.dmcClient.app.AppModel;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(url = "/aas/servlets/writeWarnInfo")
@Project(name = "WebOS", author = "SH@Ambimmort")
public class RemoteLogGather extends HttpServlet {

    private NotifDB notifdb = NotifDB.getInstance();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //req.setCharacterEncoding("utf-8");
        String callback = req.getParameter("callbackparam");
        //String success = req.getParameter("success_jsonpCallback");
        resp.setContentType("application/json;charset=utf-8");
        try {
            String userName = req.getParameter("username");
            String appid = req.getParameter("appid");
            AppModel app = AppCache.getInstance().getById(appid);
            if (app == null) {
                resp.getWriter().write(callback + "({content:'fail'})");
                resp.getWriter().close();
                return;
            }
            String notiftype = req.getParameter("notiftype");
            if (userName == null || appid == null || notiftype == null) {
                resp.getWriter().println(callback + "({content:'fail'})");
                resp.getWriter().close();
                return;
            }
            NotifModel model = new NotifModel();
            model.setAppid(appid);
            String message = req.getParameter("message");
            System.out.println("####");
            System.out.println(message);
//            String gb2312 = new String(req.getParameter("message").getBytes("ISO-8859-1"), "GB2312");
//            System.out.println("gb2312:" + gb2312);
//            String utf = new String(req.getParameter("message").getBytes("ISO-8859-1"), "utf-8");
//            System.out.println("utf:" + utf);
//            //System.out.println(message);
//            String utf8 = java.net.URLDecoder.decode(req.getParameter("message"), "UTF-8");
//            System.out.println("utf-8:" + utf8);
//            String gb2 = java.net.URLDecoder.decode(req.getParameter("message"), "gb2312");
//            System.out.println("utf-8:" + gb2);
            model.setMessage(message);
            model.setNotiftype(notiftype);
            model.setId(UUID.randomUUID().toString());
            model.setTimestp(System.currentTimeMillis());
            model.setUsername(userName);
            model.setFlag("0");
            notifdb.addNotif(model);

            resp.getWriter().write(callback + "({content:'success'})");
            resp.getWriter().close();
        } catch (Exception e) {
            org.apache.log4j.Logger.getLogger(WarnInfo.class).debug(e.toString());
            resp.getWriter().write(callback + "({content:'fail'})");
            resp.getWriter().close();
        }
    }
}
