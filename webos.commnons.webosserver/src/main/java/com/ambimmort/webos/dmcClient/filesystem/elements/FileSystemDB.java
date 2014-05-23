package com.ambimmort.webos.dmcClient.filesystem.elements;

import com.ambimmort.webos.commons.cassandradb.core.CassandraSimpleDBBridge;
import com.ambimmort.webos.commons.cassandradb.core.DBBridge;
import com.ambimmort.webos.dmcClient.app.AppDB;
import com.datastax.driver.core.Row;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FileSystemDB {

    private static FileSystemDB instance = null;
    private Set<File> files;
    private DBBridge bridge = null;

    private FileSystemDB() {
        this.files = new HashSet();
        init();
    }

    protected void finalize()
            throws Throwable {
        super.finalize();
        shutdown();
    }

    public static synchronized FileSystemDB getInstance() {
        if (instance == null) {
            instance = new FileSystemDB();
        }
        return instance;
    }

    private void init() {
        this.bridge = new CassandraSimpleDBBridge();
    }

    public Iterator excute(String cql) {
        try {
            return this.bridge.excute(cql);
        } catch (Exception e) {
        }
        return null;
    }

    public ArrayList<File> getFilesByQuery(String cql) {
        ArrayList files = new ArrayList();
        Iterator rows = this.bridge.excute(cql);
        while (rows.hasNext()) {
            Row row = (Row) rows.next();
            File file = new File();
            file.setFatherpath(row.getString("fatherpath"));
            String name = row.getString("filename");
            file.setName(name);
            if (row.getString("lastmodifytime") == null) {
                file.setLastModifyTime(System.currentTimeMillis());
            } else {
                file.setLastModifyTime(Long.parseLong(row.getString("lastmodifytime")));
            }

            file.setPath(row.getString("path"));
            file.setProfile(row.getString("profile"));
            if (row.getString("size") == null) {
                file.setSize(0);
            } else {
                file.setSize(Long.parseLong(row.getString("size")));
            }

            file.setType(row.getString("type"));
            file.setUserid(row.getString("userid"));
            file.setLinkpath(row.getString("linkpath"));
            files.add(file);
        }
        return files;
    }

    public String addFile(File file) {
        if ((file.getFatherpath().isEmpty()) || (file.getName().isEmpty()) || (file.getUserid().isEmpty()) || (file.getType().isEmpty()) || (file.getProfile().isEmpty())) {
            return "{\"rst\":false}";
        }
        String cql = "INSERT INTO file(fatherpath,filename,profile,type,userid,size,lastmodifytime) VALUES('%s','%s','%s','%s','%s','%s','%s');";
        String result = "{\"rst\":true}";
        String fatherpath = file.getFatherpath();
        String filename = file.getName();
        String profile = file.getProfile();
        String type = file.getType();
        String userid = file.getUserid();
        long lastmodifytime = file.getLastModifyTime();
        long size = file.getSize();
        cql = String.format(cql, new Object[]{fatherpath, filename, profile, type, userid, size, lastmodifytime});
        try {
            this.bridge.excute(cql);
        } catch (Exception e) {
            result = "{\"rst\":false}";
        }
        return result;
    }

    public String deleteFile(File file) {
        if ((file.getFatherpath().isEmpty()) || (file.getName().isEmpty()) || (file.getUserid().isEmpty())) {
            return "{\"rst\":false}";
        }
        String cql = "DELETE FROM file WHERE fatherpath='%s' AND filename='%s' AND userid = '%s';";
        String result = "{\"rst\":true}";
        String fatherpath = file.getFatherpath();
        String filename = file.getName();
        String userid = file.getUserid();
        cql = String.format(cql, new Object[]{fatherpath, filename, userid});
        try {
            this.bridge.excute(cql);
        } catch (Exception e) {
            result = "{\"rst\":false}";
        }
        return result;
    }

    public void shutdown() {
        this.bridge.close();
    }
}

/* Location:           F:\nisp.webos.server-1.0-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.dmcClient.filesystem.FileSystemDB
 * JD-Core Version:    0.5.3
 */
