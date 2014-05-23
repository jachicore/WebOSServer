package com.ambimmort.webos.aos.resource;

import com.ambimmort.webos.commons.servlets.annotation.Project;
import com.ambimmort.webos.commons.servlets.annotation.WebServlet;
import com.ambimmort.webos.dmcClient.filesystem.utils.FSConfig;
import com.ambimmort.webos.dmcClient.filesystem.concrete.FileSystem;
import com.ambimmort.webos.dmcClient.filesystem.utils.MD5Util;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet(url = "/aas/resource/upload")
@Project(name = "WebOS", author = "Ambimmort")
public class UploadFile extends HttpServlet {

    private static final long serialVersionUID = 6098986937312850119L;

    public UploadFile() {
        System.out.println("init");
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!(ServletFileUpload.isMultipartContent(request))) {
                return;
            }

            request.setCharacterEncoding("utf-8");
            FileItemFactory factory = new DiskFileItemFactory();

            ServletFileUpload upload = new ServletFileUpload(factory);

            List items = upload.parseRequest(request);
            File repositoryPath = new File("tmp");
            DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
            diskFileItemFactory.setRepository(repositoryPath);
            JSONArray array = new JSONArray();
            Iterator iter = items.iterator();
            iter.next();
            //FileItem item = null;
            FileItem itemform = (FileItem) iter.next();
            FileItem itemfile = (FileItem) iter.next();
            //int i = 0;
//            while(iter.hasNext()){
//                item = (FileItem)iter.next();
//                System.out.println(i);
//                System.out.println(new String(Base64.decodeBase64(item.getString("UTF-8").getBytes()), "utf-8"));
//                System.out.println(item.getName());
//                i++;
//            }
            String path = new String(Base64.decodeBase64(itemform.getString("UTF-8").getBytes()), "utf-8");
            System.out.println("path:" + path);

            path = path.substring(path.indexOf("aos://") + "aos://".length(), path.length());
            String file = path + "/" + itemfile.getName();
            System.out.println("file:" + file);
            FileSystem fileSystem = new FileSystem();
            fileSystem.injectSession(request.getSession());
            fileSystem.create(file);

            BufferedOutputStream os = new BufferedOutputStream(fileSystem.getOutputStream(file));
            byte[] buffer = new byte[1024];
            InputStream is = itemfile.getInputStream();
            int rst = -1;
            while ((rst = is.read(buffer)) != -1) {
                os.write(buffer, 0, rst);
            }

            os.close();
            is.close();
            array.add(file);
            itemfile.delete();
            response.setCharacterEncoding("UTF-8");
            //response.getWriter().print(array.toString());
        } catch (FileUploadException ex) {
            Logger.getLogger(UploadFile.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(UploadFile.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
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
 * Qualified Name:     com.ambimmort.aos.resource.UploadFile
 * JD-Core Version:    0.5.3
 */
