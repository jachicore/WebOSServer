package com.ambimmort.webos.aos.resource;

import com.ambimmort.webos.commons.servlets.annotation.Project;
import com.ambimmort.webos.commons.servlets.annotation.WebServlet;
import com.ambimmort.webos.old.plugins.filesystem.elements.IFileSystem;
import com.ambimmort.webos.old.plugins.filesystem.elements.IPath;
import com.ambimmort.webos.old.plugins.filesystem.impl.MountManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;

@WebServlet(url = "/aas/resource/getAudioStream")
@Project(name = "WebOS", author = "Ambimmort")
public class GetAudioStream extends HttpServlet {

    private static final long serialVersionUID = 6098986937312850119L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String file = new String(Base64.decodeBase64(request.getParameter("file")), "utf-8");

        if ((file == null) || (file.isEmpty())) {
            return;
        }

        if (file.startsWith("aos://")) {
            if (file.contains("\\")) {
                file = file.replace('\\', '/');
            }
            String p = file.substring(file.indexOf("aos://") + "aos://".length(), file.length());

            IFileSystem fileSystem = MountManager.getInstance().getFileSystem(p);

            IPath path = fileSystem.mapWith(p);

            String filename = path.getName();
            InputStream inStream = fileSystem.getInputStream(p);
            String agent = request.getHeader("USER-AGENT");
            filename = processFileName(filename, agent);
            response.setCharacterEncoding("utf-8");
            response.setContentType("audio/mpeg");
            response.addHeader("Content-Disposition", " attachment; filename=" + filename + "");

            response.addHeader("Content-Length ", "" + path.getSize());

            byte[] b = new byte[100];
            try {
                int len = -1;
                while ((len = inStream.read(b)) > 0) {

                    response.getOutputStream().write(b, 0, len);
                }
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
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
 * Qualified Name:     com.ambimmort.aos.resource.GetAudioStream
 * JD-Core Version:    0.5.3
 */
