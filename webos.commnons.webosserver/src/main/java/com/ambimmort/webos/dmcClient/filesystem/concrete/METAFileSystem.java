package com.ambimmort.webos.dmcClient.filesystem.concrete;

import com.ambimmort.webos.dmcClient.filesystem.utils.FSConfig;
import com.ambimmort.webos.commons.env.ENV;
import com.ambimmort.webos.dmcClient.app.AppDB;
import com.ambimmort.webos.dmcClient.filesystem.elements.File;
import com.ambimmort.webos.dmcClient.filesystem.elements.FileSystemDB;
import com.ambimmort.webos.dmcClient.filesystem.elements.IFileSystem;
import com.ambimmort.webos.dmcClient.filesystem.utils.MD5Util;
import com.ambimmort.webos.old.plugins.filesystem.impl.FSServiceImpl;
import com.datastax.driver.core.Row;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;

public class METAFileSystem
        implements IFileSystem {

    FSServiceImpl mountfs = null;
    private String root;
    private HttpSession session;
    private String userid;
    private String fileProfile;
    String time = "yyyy-MM-dd HH:mm:ss";
    SimpleDateFormat sd = new SimpleDateFormat(this.time);

    public METAFileSystem(HttpSession obj) {
        this.root = "";
        mountfs = new FSServiceImpl();
        this.fileProfile = "{}";
        this.session = obj;
        if (session != null) {
            this.userid = obj.getAttribute("username").toString();
        } else {
            this.userid = "null";
        }

    }

    public METAFileSystem(String userid) {
        this.root = "";
        mountfs = new FSServiceImpl();
        this.fileProfile = "{}";
        this.userid = userid;
    }

    public String getRoot() {
        return sessiontest();
    }

    public void setRoot(String path) {
        this.root = path;
    }

    public String sessiontest() {
        return this.userid;
    }

    public File mapWith(String path) {
        String cql = "SELECT * FROM file WHERE userid='%s' AND path='%s';";
        cql = String.format(cql, new Object[]{this.userid, path});
        List<File> files = FileSystemDB.getInstance().getFilesByQuery(cql);
        for (File file : files) {
            return file;
        }
        return null;
    }

    public List<File> ls(String path) {
        String cql = "SELECT * FROM file WHERE userid='%s' AND fatherpath='%s';";
        cql = String.format(cql, new Object[]{this.userid, path});
        List files = FileSystemDB.getInstance().getFilesByQuery(cql);
        return files;
    }

    public List<File> lsDirs(String dirPath) {
        String cql = "SELECT * FROM file WHERE userid='%s' AND fatherpath='%s' AND type='dir';";
        cql = String.format(cql, new Object[]{this.userid, dirPath});
        List files = FileSystemDB.getInstance().getFilesByQuery(cql);
        return files;
    }

    public List<File> lsFiles(String dirPath) {
        String cql = "SELECT * FROM file WHERE userid='%s' AND fatherpath='%s' AND type='file';";
        cql = String.format(cql, new Object[]{this.userid, dirPath});
        List files = FileSystemDB.getInstance().getFilesByQuery(cql);
        return files;
    }

    private String getNameNoSuffix(String path) {
        String name = path.substring(path.lastIndexOf("/") + 1, path.length());
        name = name.lastIndexOf(".") == -1 ? name : name.substring(0, name.lastIndexOf("."));
        return name;
    }

    public List<File> ls4realNameApp(String path) {
        List<File> files = new ArrayList<File>();
        for (File file : this.ls(path)) {
            String suffix = getSuffix(file.getName());
            if (suffix.equals("exe")) {
                file.setName(AppDB.getInstance().getAppById(getNameNoSuffix(file.getName())).get(0).getName());
            }
            files.add(file);
        }
        return files;
    }

    public boolean exists(String path) {
        return exists(path, this.userid);
    }

    private boolean exists(String path, String user) {
        String cql = "SELECT * FROM file WHERE userid='%s' AND path='%s';";
        cql = String.format(cql, new Object[]{user, path});
        List files = FileSystemDB.getInstance().getFilesByQuery(cql);
        return (!(files.isEmpty()));
    }

    public boolean existsByFatherAndName(String fatherpath, String name) {
        String cql = "SELECT * FROM file WHERE userid='%s' AND fatherpath='%s' AND filename='%s';";
        cql = String.format(cql, new Object[]{this.userid, fatherpath, name});
        List files = FileSystemDB.getInstance().getFilesByQuery(cql);
        return (!(files.isEmpty()));
    }

    public boolean isDir(String path) {
        String name = path.substring(path.lastIndexOf("/") + 1, path.length());
        return name.indexOf(".") == -1;
    }

    private void rename(String srcpath, String toPath) {
        String name = this.getName(toPath);
        String father = this.getFather(toPath);
        String cql = "SELECT * FROM file WHERE path='%s' AND userid='%s';";
        cql = String.format(cql, new Object[]{srcpath, this.userid});
        //System.out.println(cql);
        Iterator rows = FileSystemDB.getInstance().excute(cql);
        File file = null;

        while (rows.hasNext()) {
            file = new File();
            Row row = (Row) rows.next();
            file.setSize(Long.parseLong(row.getString("size")));
            file.setLastModifyTime(Long.parseLong(row.getString("lastmodifytime")));
            file.setLinkpath(row.getString("linkpath"));
            file.setFatherpath(father);
            file.setName(name);
            file.setPath(toPath);
            file.setProfile(row.getString("profile"));
            file.setType(row.getString("type"));
            file.setUserid(row.getString("userid"));
        }
        if (file != null) {
            create4rename(file);
        }

    }

    private boolean isDir(String folder, String name) {
        String cql = "SELECT type FROM file WHERE userid='%s' AND fatherpath='%s' AND filename='%s';";
        cql = String.format(cql, new Object[]{this.userid, folder, name});
        Iterator rows = FileSystemDB.getInstance().excute(cql);
        while (rows.hasNext()) {
            Row row = (Row) rows.next();
            return row.getString("type").equals("dir");
        }
        return false;
    }

    public boolean isDir(File file) {
        return file.getType().equals("dir");
    }

    public synchronized void write(String file, String content) {
        long size = content.getBytes().length;
        setSize(file, size);
        setLastModified(file, System.currentTimeMillis());
        file = FSConfig.getInstance().getMOUNTTYPE() + userid + "/" + MD5Util.MD5(file);
        this.mountfs.write(content, file);
    }

    public void deleteRecur(String path) {
        String fatherpath = getFather(path);
        String name = path.substring(path.lastIndexOf("/") + 1, path.length());
        deleteRecur(fatherpath, name);
    }

    public void deleteRecur(String fatherpath, String name) {
        if (isDir(fatherpath, name)) {
            List<File> files = ls(fatherpath + ENV.File.separator + name);

            for (File file : files) {
                deleteRecur(file.getFatherpath(), file.getName());
            }
        }
        deleteNoRecur(fatherpath, name);
        String suffix = getSuffix(name);
        if (needPersistence(suffix)) {
            String file = FSConfig.getInstance().getMOUNTTYPE() + userid + "/" + MD5Util.MD5(fatherpath + "/" + name);
            this.mountfs.deleteFile(file);
        }

    }

    private boolean needPersistence(String suffix) {
        if (suffix.equals("") || suffix.equals("exe") || suffix.equals("url") || suffix.equals("lin")) {
            return false;
        }
        return true;
    }

    private void deleteNoRecur(String fatherpath, String name) {
        String cql = "DELETE FROM file WHERE userid='%s' AND fatherpath='%s' AND filename='%s';";
        cql = String.format(cql, new Object[]{this.userid, fatherpath, name});
        //System.out.println(cql);
        try {
            FileSystemDB.getInstance().excute(cql);
        } catch (Exception ex) {
            Logger.getLogger(METAFileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void rm(String path) {
        deleteRecur(path);
    }

    public boolean rn(String fromPath, String toPath) {

        String fatherpath = getFather(fromPath);
        String name = fromPath.substring(fromPath.lastIndexOf("/") + 1, fromPath.length());
        //String toFather = getFather(toPath);
        if (isDir(fatherpath, name)) {
            deleteNoRecur(fatherpath, name);
            createFile(toPath);
            List<File> files = ls(fatherpath + ENV.File.separator + name);

            for (File file : files) {
                rn(fromPath + "/" + file.getName(), toPath + "/" + file.getName());
            }
            return true;
        }
        String suffix = getSuffix(name);
        rename(fromPath, toPath);
        deleteNoRecur(fatherpath, name);
        if (needPersistence(suffix)) {
            fromPath = FSConfig.getInstance().getMOUNTTYPE() + userid + "/" + MD5Util.MD5(fromPath);
            toPath = FSConfig.getInstance().getMOUNTTYPE() + userid + "/" + MD5Util.MD5(toPath);
            this.mountfs.rn(fromPath, toPath);
        }

//        try {
//            String fatherpath = getFather(fromPath);
//            String name = fromPath.substring(fromPath.lastIndexOf("/") + 1, fromPath.length());
//            deleteNoRecur(fatherpath, name);
//            createFile(toPath);
//            fromPath = FSConfig.getInstance().getMOUNTTYPE()+ userid + "/" + MD5Util.MD5(fromPath);
//            toPath = FSConfig.getInstance().getMOUNTTYPE() + userid + "/" + MD5Util.MD5(toPath);
//            this.mountfs.rn(fromPath, toPath);
//            //reHash(fromPath, toPath);
//        } catch (Exception e) {
//            return false;
//        }
        return true;
    }

    public void mkdir(String path) {
        mkdir(path, this.userid);
    }

    public String read(String path) {
        System.out.println("read:" + path);
        String content = "";
        path = FSConfig.getInstance().getMOUNTTYPE() + userid + "/" + MD5Util.MD5(path);
        content = this.mountfs.read(path);
        return content;
    }

    public void setLastModified(String path, long time) {
        String cql = "SELECT fatherpath,filename FROM file WHERE path='%s' AND userid='%s';";

        cql = String.format(cql, new Object[]{path, this.userid});
        try {
            Iterator i = FileSystemDB.getInstance().excute(cql);
            while (i.hasNext()) {
                Row row = (Row) i.next();
                String fatherpath = row.getString("fatherpath");
                String filename = row.getString("filename");
                cql = "UPDATE file SET lastmodifytime = '%s' WHERE fatherpath='%s' AND filename='%s' AND userid='%s';";
                cql = String.format(cql, time, fatherpath, filename, this.userid);
                FileSystemDB.getInstance().excute(cql);
            }
        } catch (Exception ex) {
            Logger.getLogger(METAFileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setSize(String path, long size) {
        String cql = "SELECT fatherpath,filename FROM file WHERE path='%s' AND userid='%s';";

        cql = String.format(cql, new Object[]{path, this.userid});
        try {
            Iterator i = FileSystemDB.getInstance().excute(cql);
            while (i.hasNext()) {
                Row row = (Row) i.next();
                String fatherpath = row.getString("fatherpath");
                String filename = row.getString("filename");
                cql = "UPDATE file SET size = '%s' WHERE fatherpath='%s' AND filename='%s' AND userid='%s';";
                cql = String.format(cql, size, fatherpath, filename, this.userid);
                FileSystemDB.getInstance().excute(cql);
            }
        } catch (Exception ex) {
            Logger.getLogger(METAFileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String getSuffix(String path) {
        String suffix = path.lastIndexOf(".") == -1 ? "" : path.substring(path.lastIndexOf(".") + 1, path.length());
        return suffix;
    }

    public void createFile(String path) {
        String fatherpath = getFather(path);
        if (!(exists(fatherpath))) {
            mkdir(fatherpath);
        }
        String name = path.substring(path.lastIndexOf("/") + 1, path.length());
        createFile(fatherpath, name);
        String suffix = getSuffix(path);
        if (this.needPersistence(suffix)) {
            path = FSConfig.getInstance().getMOUNTTYPE() + userid + "/" + MD5Util.MD5(path);
            this.mountfs.createFile(path);
        }

    }

    public void createFile(String fatherpath, String name) {
        String path = fatherpath + "/" + name;
        path = path.replaceAll("//", "/");
        String type = isDir(name) ? "dir" : "file";
        String cql = "INSERT INTO file(fatherpath,filename,path,userid,profile,type,size,lastmodifytime) VALUES('%s','%s','%s','%s','%s','%s','%s','%s');";
        Long time = System.currentTimeMillis();
        cql = String.format(cql, new Object[]{fatherpath, name, path, this.userid, this.fileProfile, type, 0, time.toString()});
        //System.out.println(cql);
        try {
            FileSystemDB.getInstance().excute(cql);
        } catch (Exception ex) {
            Logger.getLogger(METAFileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void create4rename(File file) {
        String cql = "INSERT INTO file(fatherpath,filename,path,linkpath,userid,profile,type,size,lastmodifytime) VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s');";
        Long time = System.currentTimeMillis();
        cql = String.format(cql, new Object[]{file.getFatherpath(), file.getName(), file.getPath(), file.getLinkpath(), file.getUserid(), file.getProfile(), file.getType(), file.getSize(), file.getLastModifyTime()});
        System.out.println(cql);
        try {
            FileSystemDB.getInstance().excute(cql);
        } catch (Exception ex) {
            Logger.getLogger(METAFileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void create(String fatherpath, String name, String path, String profile, String type) {
        String cql = "INSERT INTO file(fatherpath,filename,path,userid,profile,type,size,lastmodifytime) VALUES('%s','%s','%s','%s','%s','%s','%s','%s');";
        Long time = System.currentTimeMillis();
        cql = String.format(cql, new Object[]{fatherpath, name, path, this.userid, this.fileProfile, type, 0, time.toString()});
        try {
            FileSystemDB.getInstance().excute(cql);
        } catch (Exception ex) {
            Logger.getLogger(METAFileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createLink(String paramString1, String paramString2, String linkPath) {
        String cql = "INSERT INTO file(fatherpath,filename,path,linkpath,userid,profile,type,lastmodifytime,size) VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s');";
        Long time = System.currentTimeMillis();
        cql = String.format(cql, new Object[]{paramString1, paramString2, paramString1 + ENV.File.separator + paramString2,
            linkPath, this.userid, "{}", "file", time.toString(), "0"});
        try {
            FileSystemDB.getInstance().excute(cql);
        } catch (Exception ex) {
            Logger.getLogger(METAFileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getFather(String path) {
        String fatherpath = path.substring(0, path.lastIndexOf("/"));
        if (fatherpath.isEmpty()) {
            fatherpath = "/";
        }
        return fatherpath;
    }

    private String getName(String path) {
        String name = path.substring(path.lastIndexOf("/") + 1, path.length());
        return name;
    }
    
    private boolean needLink(String suffix){
        return suffix.equals("url")||suffix.equals("lin");
    }
    
    public void cp(String paramString1, String paramString2) {

        if (isDir(paramString1)) {
            String dirpath = paramString2;
            mkdir(dirpath);
            List<File> files = ls(paramString1);

            for (File file : files) {
                cp(file.getPath(), dirpath + ENV.File.separator + file.getName());
            }
        } else {
            if (this.exists(paramString2)) {
                this.deleteRecur(paramString2);
            }
            String suffix = this.getSuffix(paramString1);
            if (needLink(suffix)) {
                
                File f =this.mapWith(paramString1);
                this.createLink(this.getFather(paramString2), this.getName(paramString2), f.getLinkpath());
            }else{
                createFile(paramString2);
            }
            

            //this.mountfs.createFile(paramString2);
            if (needPersistence(suffix)) {
                paramString2 = FSConfig.getInstance().getMOUNTTYPE() + userid + "/" + MD5Util.MD5(paramString2);
                paramString1 = FSConfig.getInstance().getMOUNTTYPE() + userid + "/" + MD5Util.MD5(paramString1);
                this.mountfs.cp(paramString1, paramString2);
            }

        }
    }

    public void move(String paramString1, String paramString2) {
        cp(paramString1, paramString2);
        deleteRecur(paramString1);
    }

    public InputStream getInputStream(String path) {
        path = FSConfig.getInstance().getMOUNTTYPE() + userid + "/" + MD5Util.MD5(path);
        return this.mountfs.getInputStream(path);
    }

    public OutputStream getOutputStream(String path) {
        path = FSConfig.getInstance().getMOUNTTYPE() + userid + "/" + MD5Util.MD5(path);
        return this.mountfs.getOutputStream(path);
    }

    public void mkdir(String path, String user) {
        String fatherpath = getFather(path);
        if ((!(exists(fatherpath, user))) && (((!(fatherpath.equals("/"))) || (!(path.equals("/")))))) {
            mkdir(fatherpath, user);
        }
        String name = path.substring(path.lastIndexOf("/") + 1, path.length());
        if (name.isEmpty()) {
            name = "/";
        }
        Long time = System.currentTimeMillis();
        String cql = "INSERT INTO file(fatherpath,filename,path,userid,profile,type,lastmodifytime,size) VALUES('%s','%s','%s','%s','%s','%s','%s','%s');";
        cql = String.format(cql, new Object[]{fatherpath, name, path, user, this.fileProfile, "dir", time.toString(), "0"});

        try {
            FileSystemDB.getInstance().excute(cql);
        } catch (Exception ex) {
            Logger.getLogger(METAFileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteAllByUser(String user) {
        String cql = "SELECT path FROM file WHERE userid='%s';";

        cql = String.format(cql, new Object[]{user});
        try {
            Iterator i = FileSystemDB.getInstance().excute(cql);
            while (i.hasNext()) {
                Row row = (Row) i.next();
                String file = row.getString("path");
                File f = mapWith(file);
                if (this.needPersistence(f.getSuffix())) {
                    file = FSConfig.getInstance().getMOUNTTYPE() + userid + "/" + MD5Util.MD5(file);
                    this.mountfs.deleteFile(file);
                }

            }
        } catch (Exception ex) {
            Logger.getLogger(METAFileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        cql = "DELETE FROM file WHERE userid='%s';";
        cql = String.format(cql, user);
        try {
            FileSystemDB.getInstance().excute(cql);
        } catch (Exception ex) {
            Logger.getLogger(METAFileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the session
     */
    public HttpSession getSession() {
        return session;
    }

    /**
     * @param session the session to set
     */
    public void setSession(HttpSession session) {
        this.session = session;
    }
}

/* Location:           F:\nisp.webos.server-1.0-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.dmcClient.filesystem.concrete.METAFileSystem
 * JD-Core Version:    0.5.3
 */
