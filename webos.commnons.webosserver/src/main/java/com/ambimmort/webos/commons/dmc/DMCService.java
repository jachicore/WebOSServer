package com.ambimmort.webos.commons.dmc;

import com.ambimmort.webos.commons.servlets.annotation.Project;
import com.ambimmort.webos.commons.servlets.annotation.WebServlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;

@WebServlet(url = "/DMC/DMCService")
@Project(name = "WebOS", author = "Ambimmort")
public class DMCService extends HttpServlet {

    private static final long serialVersionUID = 1L;
    Logger logger;

    public DMCService() {
        this.logger = Logger.getLogger(DMCService.class);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            //request.getSession().setMaxInactiveInterval(60*20);
            String ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            //System.out.println("ip:"+ip);
            request.getSession().setAttribute("ip", ip);
            request.setCharacterEncoding("utf-8");
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("utf-8");
            Cookie[] cookies = request.getCookies();
            if (null != cookies) {
                for (Cookie cookie : cookies) {
                    request.getSession().setAttribute("cookie", cookie.getValue());
                }
            }
            String name = request.getParameter("name");
            String methodName = request.getParameter("methodName");

            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()), 1024);
            String tmp = null;
            StringBuilder sb = new StringBuilder();
            while ((tmp = br.readLine()) != null) {
                sb.append(tmp).append("\n");
            }
            br.close();
            Object rst = DMCInterpretor.getInstance().doDMCService(name, methodName, sb.toString(), request.getSession());
            PrintWriter out = response.getWriter();
            out.print(rst);
            out.close();
        } catch (Throwable t) {
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}

/* Location:           F:\nisp.webos.server-1.0-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.webos.commons.dmc.DMCService
 * JD-Core Version:    0.5.3
 */
