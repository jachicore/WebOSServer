package com.ambimmort.webos.aos.resource;

import com.ambimmort.webos.commons.servlets.annotation.Project;
import com.ambimmort.webos.commons.servlets.annotation.WebServlet;
import com.ambimmort.webos.dmcClient.filesystem.concrete.FileSystem;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

@WebServlet(url = "/aas/resource/getFile")
@Project(name = "WebOS", author = "Ambimmort")
public class GetFile extends HttpServlet {

    private static final long serialVersionUID = 6098986937312850119L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String ff = request.getParameter("file");
        String file = URLDecoder.decode(new String(Base64.decodeBase64(ff.getBytes())), "utf-8");

        if ((file == null) || (file.isEmpty())) {
            return;
        }

        if (file.startsWith("aos://")) {
            if (file.contains("\\")) {
                file = file.replace('\\', '/');
            }
            String p = file.substring(file.indexOf("aos://") + "aos://".length(), file.length());
            FileSystem fileSystem = new FileSystem();
            fileSystem.injectSession(request.getSession());

            //IPath path = fileSystem.mapWith(p);

            if (p == null) {
                return;
            }
            String agent = request.getHeader("USER-AGENT");
            String filename = p.lastIndexOf("/")==-1?p:p.substring(p.lastIndexOf("/")+1, p.length());
            filename = processFileName(filename, agent);
            String encode = request.getParameter("encode");
            if (encode != null) {
                response.setCharacterEncoding(encode);
            } else {
                response.setCharacterEncoding("utf-8");
            }

            response.setContentType("application/x-msdownload");
            response.addHeader("Content-Disposition", " attachment; filename=" + filename + "");
            InputStream inStream;
            if (encode != null) {
                inStream = fileSystem.getInputStream(p);
                List<String> contents = IOUtils.readLines(inStream, "utf-8");
                for (String s : contents) {
                    response.getWriter().println(s);
                }
                inStream.close();
            } else {
                inStream = fileSystem.getInputStream(p);
                byte[] b = new byte[100];
                try {
                    int len;
                    while ((len = inStream.read(b)) > 0) {

                        response.getOutputStream().write(b, 0, len);
                    }
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String processFileName(String fileName, String agent)
            throws IOException {
        String codedfilename = null;
        if ((null != agent) && (-1 != agent.indexOf("MSIE"))) {
            String prefix = (fileName.lastIndexOf(".") != -1) ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;

            String extension = (fileName.lastIndexOf(".") != -1) ? fileName.substring(fileName.lastIndexOf(".")) : "";

            String name = URLEncoder.encode(fileName, "UTF8");
            if (name.lastIndexOf("%0A") != -1) {
                name = name.substring(0, name.length() - 3);
            }
            int limit = 150 - extension.length();
            if (name.length() > limit) {
                name = URLEncoder.encode(prefix.substring(0, Math.min(prefix.length(), limit / 9)), "UTF-8");

                if (name.lastIndexOf("%0A") != -1) {
                    name = name.substring(0, name.length() - 3);
                }

            }

            codedfilename = name;
        } else if ((null != agent) && (-1 != agent.indexOf("Mozilla"))) {
            codedfilename = "=?UTF-8?B?" + new String(Base64.encodeBase64(fileName.getBytes("UTF-8"))) + "?=";
        } else {
            codedfilename = fileName;
        }
        return codedfilename;
    }
}

/* Location:           E:\company\webosInstall\2013.11.9-1\cc\applications\nisp.webos\nisp.webos.server\program\lib\nisp.webos.server-1.0.1-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.aos.resource.GetFile
 * JD-Core Version:    0.5.3
 */
