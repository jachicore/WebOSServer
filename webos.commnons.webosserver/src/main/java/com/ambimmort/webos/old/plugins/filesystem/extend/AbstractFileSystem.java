package com.ambimmort.webos.old.plugins.filesystem.extend;

import com.ambimmort.webos.old.plugins.filesystem.elements.IFileSystem;
import com.ambimmort.webos.old.plugins.filesystem.elements.FileSystemChangeEvent;
import com.ambimmort.webos.old.plugins.filesystem.elements.IPath;
import com.ambimmort.webos.old.plugins.filesystem.elements.MountPoint;
import com.ambimmort.webos.old.plugins.filesystem.impl.IFileSystemListener;
import com.ambimmort.webos.old.plugins.filesystem.impl.MountManager;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public abstract class AbstractFileSystem
        implements IFileSystem {

    protected static Logger logger = Logger.getLogger(AbstractFileSystem.class);
    private List<IFileSystemListener> fileSystemListeners;
    private MountPoint mountPoint;

    public AbstractFileSystem() {
        this.fileSystemListeners = new ArrayList();

        this.mountPoint = null;
    }

    public MountPoint getMountPoint() {
        return this.mountPoint;
    }

    public void setMountPoint(MountPoint mountPoint) {
        this.mountPoint = mountPoint;
    }

    private void fireFileSystemChange(FileSystemChangeEvent arg) {
        for (IFileSystemListener l : this.fileSystemListeners) {
            l.onFileSystemChange(arg);
        }
    }

    public String resolvePath(String path) {
        if (!(getMountPoint().getMountPoint().equals("/"))) {
            path = path.replace(getMountPoint().getMountPoint(), "");
        } else if (path.equals("/")) {
            path = "";
        }

        return path;
    }

    public void createFile(String path) {
        FileSystemChangeEvent event = new FileSystemChangeEvent();
        event.setType(1);
        event.setArg(path);
        fireFileSystemChange(event);
    }

    public void deleteFile(String path) {
        FileSystemChangeEvent event = new FileSystemChangeEvent();
        event.setType(2);
        event.setArg(path);
        fireFileSystemChange(event);
    }

    public void mkdir(String path) {
        FileSystemChangeEvent event = new FileSystemChangeEvent();
        event.setType(5);
        event.setArg(path);
        fireFileSystemChange(event);
    }

    public void rm(String path) {
        FileSystemChangeEvent event = new FileSystemChangeEvent();
        if (mapWith(path).isDir()) {
            event.setType(6);
        } else {
            event.setType(2);
        }
        event.setArg(path);
        fireFileSystemChange(event);
    }

    public String read(String path) {
        FileSystemChangeEvent event = new FileSystemChangeEvent();
        event.setType(3);
        event.setArg(path);
        fireFileSystemChange(event);
        return null;
    }

    public void write(String path, String content) {
        FileSystemChangeEvent event = new FileSystemChangeEvent();
        event.setType(4);
        event.setArg(path);
        fireFileSystemChange(event);
    }

    public void cp(String fromPath, String toPath) {
        IFileSystem dstFileSystem = MountManager.getInstance().getFileSystem(toPath);

        IPath srcFile = dstFileSystem.mapWith(fromPath);

        if (!(dstFileSystem.exists(toPath))) {
            if (srcFile.isDir()) {
                dstFileSystem.mkdir(toPath);
            } else {
                dstFileSystem.createFile(toPath);
            }

        }

        if ((!(isDir(fromPath))) && (!(dstFileSystem.isDir(toPath)))) {
            if (!(exists(fromPath))) {
                return;
            }

            copy(getInputStream(fromPath), dstFileSystem.getOutputStream(toPath));
        } else if ((!(isDir(fromPath))) && (dstFileSystem.isDir(toPath))) {
            cp(fromPath, toPath + "/" + srcFile.getName());
        } else {
            if ((!(isDir(fromPath))) || (!(dstFileSystem.isDir(toPath)))) {
                return;
            }
            dstFileSystem.mkdir(toPath + "/" + srcFile.getName());

            for (IPath f : lsAll(fromPath)) {
                cp(fromPath + "/" + f.getName(), toPath + "/" + srcFile.getName());
            }
        }
    }

    public void move(String fromPath, String toPath) {
        IFileSystem dstFileSystem = MountManager.getInstance().getFileSystem(toPath);
        IPath file = mapWith(fromPath);
        IPath file1 = dstFileSystem.mapWith(toPath);
        if ((isDir(fromPath)) && (dstFileSystem.isDir(toPath))) {
            String tf = file1.getPath() + "/" + file.getName();

            if (!(dstFileSystem.exists(tf))) {
                dstFileSystem.mkdir(tf);
            }
            for (IPath f : lsFiles(fromPath)) {
                move(fromPath + "/" + f.getName(), toPath + "/" + file.getName());
            }

            deleteFile(fromPath);
        } else if ((!(isDir(fromPath))) && (!(dstFileSystem.isDir(toPath)))) {
            cp(fromPath, toPath);
            deleteFile(fromPath);
        } else if ((!(isDir(fromPath))) && (dstFileSystem.isDir(toPath))) {
            rn(fromPath, toPath + "/" + file.getName());
        }
        deleteFile(fromPath);
    }

    public boolean rn(String fromPath, String toPath) {
        FileSystemChangeEvent event = new FileSystemChangeEvent();
        event.setType(7);
        event.setArg(fromPath + ":" + toPath);
        fireFileSystemChange(event);
        return true;
    }

    public static void copy(InputStream is, OutputStream os) {
        BufferedInputStream bis = new BufferedInputStream(is);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(os);
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            bos.flush();
            bos.close();
            bis.close();
        } catch (Exception e) {
            logger.error(e);
        } finally {
            try {
                is.close();
                os.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}

/* Location:           E:\company\webosInstall\2013.11.9-1\cc\applications\nisp.webos\nisp.webos.server\program\lib\nisp.webos.server-1.0.1-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.nisp.webos.plugins.filesystem.extend.AbstractFileSystem
 * JD-Core Version:    0.5.3
 */
