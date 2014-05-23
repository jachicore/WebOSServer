package com.ambimmort.webos.log.local;


import com.ambimmort.webos.commons.servlets.annotation.Project;
import com.ambimmort.webos.commons.servlets.annotation.WebServlet;
import com.ambimmort.webos.dmcClient.filesystem.utils.PropertiesUtil;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;

@WebServlet(url = "/aas/writelog")
@Project(name = "WebOS", author = "SH@Ambimmort")
public class WriteLogServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private PropertiesUtil pro;
    private SimpleDateFormat dateformat;
    private SimpleDateFormat sdf;

    public WriteLogServlet() {
        this.pro = PropertiesUtil.getInstance();
        pro.load("config/app.conf");

        this.dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        this.sdf = new SimpleDateFormat("yyyyMMdd");
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String operator = req.getParameter("user");
        String content = req.getParameter("content");
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        String time = this.dateformat.format(d);
        String date = this.sdf.format(d);
        if ((operator != null) && (content != null)) {
            String message = time + "," + operator + "," + content;
            String fileName = this.pro.getValue("writeLogPath") + date;
            File file = new File(fileName);
            if (!(file.exists())) {
                File parent = file.getParentFile();
                if ((parent != null) && (!(parent.exists()))) {
                    parent.mkdirs();
                }
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileUtils.write(file, message + "\n", "UTF-8", true);
        }
        resp.getWriter().println("null");
        resp.getWriter().close();
    }
}
