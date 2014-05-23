package com.ambimmort.webos.dmcClient.filesystem.concrete;

import com.ambimmort.webos.dmcClient.filesystem.utils.FSConfig;
import com.ambimmort.webos.dmcClient.filesystem.elements.File;
import com.ambimmort.webos.commons.dmc.DMCObject;
import com.ambimmort.webos.commons.dmc.IHttpSessionAware;
import com.ambimmort.webos.commons.dmc.ISessionable;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpSession;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public final class FileSystem extends DMCObject implements ISessionable, IHttpSessionAware {

    METAFileSystem fs = null;

    public FileSystem() {
        //fs = new METAFileSystem();
    }

    public synchronized String mapWith(String file) {
        File f = this.fs.mapWith(file);
        return JSONObject.fromObject(f).toString();
    }

    public synchronized String read(String path) {
        return fs.read(path);
    }

    public synchronized void write(String file, String content) {
        if (exists(file).equals("false")) {
            create(file);
        }
        fs.write(file, content);
    }

    public synchronized String ls(String file) {
        List files = this.fs.ls(file);
        return JSONArray.fromObject(files).toString();
    }

    public synchronized String lsDirs(String file) {
        List files = null;
        files = this.fs.lsDirs(file);
        return JSONArray.fromObject(files).toString();
    }

    public synchronized String lsFiles(String file) {
        List files = null;
        files = this.fs.lsFiles(file);
        return JSONArray.fromObject(files).toString();
    }

    public synchronized String ls4realNameApp(String file) {
        List files = null;
        files = this.fs.ls4realNameApp(file);
        return JSONArray.fromObject(files).toString();
    }

    public synchronized String exists(String file) {
        boolean flag = false;
        flag = this.fs.exists(file);
        return flag + "";
    }

    public boolean existsByFatherAndName(String fatherpath, String name) {
        return this.fs.existsByFatherAndName(fatherpath, name);
    }

    public synchronized void create(String file) {
        this.fs.createFile(file);
    }

    public synchronized void createLink(String folder, String name, String srcpath) {
        this.fs.createLink(folder, name, srcpath);
    }

    public synchronized void createType(String path, String name, String srcpath, String profile, String type) {
        this.fs.create(path, name, srcpath, profile, type);
    }

    public synchronized void rm(String file) {
        if (FSConfig.getInstance().getSysfiles().contains(file)) {
            return;
        }
        this.fs.deleteRecur(file);
    }

    public synchronized void mkdir(String file) {
        this.fs.mkdir(file);
    }

    public synchronized void mkdir4User(String file, String user) {
        this.fs.mkdir(file, user);
    }

    public synchronized void cp(String from, String to) {
        if (FSConfig.getInstance().getSysfiles().contains(from)) {
            return;
        }
        this.fs.cp(from, to);
    }

    public synchronized void rn(String from, String to) {
        if (FSConfig.getInstance().getSysfiles().contains(from)) {
            return;
        }
        this.fs.rn(from, to);
    }

    public synchronized void mv(String from, String to) {
        if (FSConfig.getInstance().getSysfiles().contains(from)) {
            return;
        }
        rn(from, to);
    }

    public synchronized void delete(String file) {
        if (FSConfig.getInstance().getSysfiles().contains(file)) {
            return;
        }
        this.fs.deleteRecur(file);
    }

    public synchronized void deleteAllByUser(String user) {
        this.fs.deleteAllByUser(user);
        //还未删除实际数据。
    }

    public synchronized void deleteByFahterAndName(String folder, String name) {
        if (FSConfig.getInstance().getSysfiles().contains(folder + "/" + name)) {
            return;
        }
        this.fs.deleteRecur(folder, name);
    }

    public String sessiontest() {
        return this.fs.getRoot();
    }

    public void injectSession(HttpSession session) {
        if (session != null) {
            if (this.fs != null) {
                this.fs.setSession(session);
                return;
            }
            this.fs = new METAFileSystem(session);
            Set<String> sysfiles = FSConfig.getInstance().getSysfiles();
            for (String file : sysfiles) {
                if (!exists(file).equals("true")) {
                    mkdir(file);
                }
            }
            //this.fs.setSession(session);
        }

    }

    public InputStream getInputStream(String paramString) {
        return this.fs.getInputStream(paramString);
    }

    public OutputStream getOutputStream(String paramString) {
        return this.fs.getOutputStream(paramString);
    }

    public void test() {
        this.fs = new METAFileSystem("admin");
    }

}
