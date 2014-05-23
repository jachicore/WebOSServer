package com.ambimmort.webos.old.plugins.filesystem.impl;


import com.ambimmort.webos.commons.servlets.annotation.Boot;
import com.ambimmort.webos.commons.dmc.DMCObject;
import com.ambimmort.webos.old.plugins.filesystem.elements.File;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class OldFileSystem extends DMCObject {

    FSServiceImpl fs = null;

    public OldFileSystem() {
        this.fs = new FSServiceImpl();
    }

    public synchronized String mapWith(String file) {
        File f = new File();
        f = this.fs.mapWith(file);
        return JSONObject.fromObject(f).toString();
    }

    public synchronized String read(String file) {
        String content = "";
        content = this.fs.read(file);
        return content;
    }

    public synchronized void write(String file, String content) {
        this.fs.write(content, file);
    }

    public synchronized String ls(String file) {
        List files = null;
        files = this.fs.listAll(file);
        return JSONArray.fromObject(files).toString();
    }

    public synchronized String lsDirs(String file) {
        List files = null;
        files = this.fs.listDirs(file);
        return JSONArray.fromObject(files).toString();
    }

    public synchronized String lsFiles(String file) {
        List files = null;
        files = this.fs.listFiles(file);
        return JSONArray.fromObject(files).toString();
    }

    public synchronized String exists(String file) {
        boolean flag = false;
        flag = this.fs.exists(file);
        return flag + "";
    }

    public synchronized void create(String file) {
        this.fs.createFile(file);
    }

    public synchronized void rm(String file) {
        this.fs.deleteFile(file);
    }

    public synchronized void mkdir(String file) {
        this.fs.mkdir(file);
    }

    public synchronized void cp(String from, String to) {
        this.fs.cp(from, to);
    }

    public synchronized void rn(String from, String to) {
        this.fs.rn(from, to);
    }

    public synchronized void mv(String from, String to) {
        this.fs.move(from, to);
    }

    public synchronized void delete(String file) {
        this.fs.deleteFile(file);
    }
}

/* Location:           E:\company\webosInstall\2013.11.9-1\cc\applications\nisp.webos\nisp.webos.server\program\lib\nisp.webos.server-1.0.1-SNAPSHOT.jar
 * Qualified Name:     aasos.js.client.FileSystem
 * JD-Core Version:    0.5.3
 */
