package com.ambimmort.webos.aos.resource;

import com.ambimmort.webos.commons.servlets.annotation.Project;
import com.ambimmort.webos.commons.servlets.annotation.WebServlet;
import com.ambimmort.webos.dmcClient.filesystem.utils.FSConfig;
import com.ambimmort.webos.dmcClient.filesystem.utils.MD5Util;
import com.ambimmort.webos.old.plugins.filesystem.elements.IFileSystem;
import com.ambimmort.webos.old.plugins.filesystem.elements.IPath;
import com.ambimmort.webos.old.plugins.filesystem.impl.MountManager;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;

@WebServlet(url = "/aas/resource/getImageIcon")
@Project(name = "WebOS", author = "Ambimmort")
public class GetImageIcon extends HttpServlet {

    private static final long serialVersionUID = 6098986937312850119L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String userid = request.getSession().getAttribute("username").toString();
        String file = new String(Base64.decodeBase64(request.getParameter("file")), "utf-8");

        String width = request.getParameter("swidth");
        String height = request.getParameter("sheight");
        if ((width == null) || (height == null)) {
            width = "200";
            height = "125";
        }

        int w = Integer.parseInt(width);
        int h = Integer.parseInt(height);

        String abpath = "";
        if (file.startsWith("file://")) {
            abpath = file.substring(file.indexOf("file://") + "file://".length(), file.length());
        } else if (file.startsWith("aos://")) {
            if (file.contains("\\")) {
                file = file.replace('\\', '/');
            }
            String p = file.substring(file.indexOf("aos://") + "aos://".length(), file.length());
            String p2 = null;
            IPath path = null;
            IFileSystem fileSystem = null;
            if (!p.startsWith("/images")) {
                p2 = FSConfig.getInstance().getMOUNTTYPE() + userid + "/" + MD5Util.MD5(p);
                fileSystem = MountManager.getInstance().getFileSystem(p2);
                path = fileSystem.mapWith(p2);
            } else {
                fileSystem = MountManager.getInstance().getFileSystem(p);
                path = fileSystem.mapWith(p);
            }

//            int index = path.getName().lastIndexOf(".");
//            if (index < 0) {
//                return;
//            }
//            String type = path.getName().substring(index + 1);
            String type = p.lastIndexOf(".") == -1 ? "" : p.substring(p.lastIndexOf(".") + 1, p.length());
            response.setContentType("image/" + type);
            request.setCharacterEncoding("utf-8");
            OutputStream out = response.getOutputStream();
            InputStream is;
            if (p2 != null) {
                is = fileSystem.getInputStream(p2);
            } else {
                is = fileSystem.getInputStream(p);
            }
            BufferedImage bi = ImageIO.read(is);
            bi = scaleImage(bi, w, h, Color.WHITE);
            try {
                ImageIO.write(bi, type, out);
            } finally {
                is.close();
                out.close();
            }
        }
    }

    public BufferedImage scaleImage(BufferedImage img, int width, int height, Color background) {
        BufferedImage newImage = new BufferedImage(width, height, 1);

        Graphics2D g = newImage.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            g.setBackground(background);
            g.clearRect(0, 0, width, height);
            g.drawImage(img, 0, 0, width, height, null);
        } finally {
            g.dispose();
        }
        return newImage;
    }
}

/* Location:           E:\company\webosInstall\2013.11.9-1\cc\applications\nisp.webos\nisp.webos.server\program\lib\nisp.webos.server-1.0.1-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.aos.resource.GetImageIcon
 * JD-Core Version:    0.5.3
 */
