package com.ambimmort.webos.aos.resource;

import com.ambimmort.webos.commons.servlets.annotation.Project;
import com.ambimmort.webos.commons.servlets.annotation.WebServlet;
import com.ambimmort.webos.dmcClient.filesystem.utils.CodeUtils;
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

@WebServlet(url = "/aas/resource/getImage")
@Project(name = "WebOS", author = "Ambimmort")
public class GetImage extends HttpServlet {

    private static final long serialVersionUID = 6098986937312850119L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    public BufferedImage scaleImage(BufferedImage img, int width, int height) {
        BufferedImage newImage = new BufferedImage(width, height, 1);

        Graphics2D g = newImage.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            //g.setBackground(background);
            g.clearRect(0, 0, width, height);
            g.drawImage(img, 0, 0, width, height, null);
        } finally {
            g.dispose();
        }
        return newImage;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String userid = request.getSession().getAttribute("username").toString();
        //String filebase64 = java.net.URLDecoder.decode(request.getParameter("file"), "utf-8");
        //System.out.println("base64:"+filebase64);
        String file = new String(Base64.decodeBase64(request.getParameter("file")), "utf-8");
        System.out.println("file:"+file);
        if (file.startsWith("aos://")) {
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
                System.out.println("p:" + p);
                System.out.println("p2:" + p2);
                path = fileSystem.mapWith(p2);
            } else {
                fileSystem = MountManager.getInstance().getFileSystem(p);
                path = fileSystem.mapWith(p);
            }

//            int index = path.getName().lastIndexOf(".");
//            if (index < 0) {
//                System.out.println("<0");
//                return;
//            }
            String type = p.lastIndexOf(".") == -1 ? "" : p.substring(p.lastIndexOf(".") + 1, p.length()).toLowerCase();
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
            if (p2 != null) {
                int newWidth = bi.getWidth();
                int newHeight = bi.getHeight();
                while (newWidth > 1500 || newHeight > 1500) {
                    newWidth = newWidth / 2;
                    newHeight = newHeight / 2;
                }
                System.out.println(newWidth);
                System.out.println(newHeight);

                bi = scaleImage(bi, newWidth, newHeight);
            }

            try {
                ImageIO.write(bi, type, out);
            } finally {
                is.close();
                out.close();
            }
        }
    }
}

/* Location:           E:\company\webosInstall\2013.11.9-1\cc\applications\nisp.webos\nisp.webos.server\program\lib\nisp.webos.server-1.0.1-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.aos.resource.GetImage
 * JD-Core Version:    0.5.3
 */
