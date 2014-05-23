package com.ambimmort.webos.old.plugins.filesystem.extend;


import com.ambimmort.webos.dmcClient.filesystem.utils.CodeUtils;
import com.ambimmort.webos.old.plugins.filesystem.elements.IPath;
import com.ambimmort.webos.old.plugins.filesystem.elements.Path;
import com.ambimmort.webos.old.plugins.filesystem.impl.MountManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

public class VFSFileSystem extends AbstractFileSystem {

    private String root = null;

    private FileSystemManager manager = null;
    //private FileSystemManager m = null;
    public VFSFileSystem() {
        try {
            
            this.manager = VFS.getManager();
          //  this.manager.(HDFSFileProvider.SCHEME, new HDFSFileProvider());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static FileSystemOptions createDefaultOptions()
            throws FileSystemException {
        FileSystemOptions opts = new FileSystemOptions();

        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");

        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);

        SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, Integer.valueOf(10000));
        return opts;
    }

    public String getRoot() {
        return this.root;
    }

    public void setRoot(String path) {
        this.root = path;
    }

    public IPath mapWith(String path) {
        if (!(getMountPoint().getMountPoint().equals("/"))) {
            path = path.replace(getMountPoint().getMountPoint(), "");
            Path f = new Path();
            try {
                FileObject fileObject = this.manager.resolveFile(this.root + path, createDefaultOptions());

                f.path = getMountPoint().getMountPoint() + path;
                f.fileSystem = this;
                if (path.contains("\\")) {
                    f.path = path.replace('\\', '/');
                }
                if (fileObject.exists()) {
                    f.lastModified = fileObject.getContent().getLastModifiedTime();

                    if (fileObject.getType() == FileType.FILE) {
                        f.size = fileObject.getContent().getSize();
                    } else {
                        f.size = 0L;
                    }

                    return f;
                }

                logger.debug(this.root + path);
            } catch (FileSystemException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileObject fileObject = this.manager.resolveFile(this.root + path, createDefaultOptions());

                Path f = new Path();

                f.path = path;
                f.fileSystem = this;
                if (path.contains("\\")) {
                    f.path = path.replace('\\', '/');
                }
                if (fileObject.exists()) {
                    f.lastModified = fileObject.getContent().getLastModifiedTime();

                    if (fileObject.getType() == FileType.FILE) {
                        f.size = fileObject.getContent().getSize();
                    } else {
                        f.size = 0L;
                    }
                    f.fileSystem = this;
                    return f;
                }
                logger.debug(this.root + path);
            } catch (FileSystemException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public List<IPath> ls(String path) {
        return ls(path, false);
    }

    public List<IPath> lsDirs(String path) {
        return lsDirs(path, false);
    }

    public List<IPath> lsFiles(String path) {
        return lsFiles(path, false);
    }

    public List<IPath> ls(String path, boolean showHiden) {
        ArrayList files = new ArrayList();
        List ps = MountManager.getInstance().getMountPointPath(path);
        files.addAll(ps);
        path = resolvePath(path);
        try {
            FileObject fileObject = this.manager.resolveFile(this.root + path, createDefaultOptions());

            if (fileObject.getType() != FileType.FOLDER) {
                return new ArrayList();
            }
            if (exists(path)) {
                for (FileObject fi : fileObject.getChildren()) {
                    IPath xf = mapWith(path + "/" + fi.getName().getBaseName());
                    if (MountManager.getInstance().mountPoints.containsKey(xf.getPath())) {
                        continue;
                    }

                    if (!(showHiden)) {
                        if ((fi.getName().getBaseName().length() != 0) && (fi.getName().getBaseName().charAt(0) == '.')) {
                            continue;
                        }
                        files.add(xf);
                    } else {
                        files.add(xf);
                    }
                }
                return files;
            }
            return files;
        } catch (FileSystemException e) {
            e.printStackTrace();
        }

        return files;
    }

    public List<IPath> lsDirs(String path, boolean showHiden) {
        ArrayList files = new ArrayList();
        List ps = MountManager.getInstance().getMountPointPath(path);
        files.addAll(ps);
        path = resolvePath(path);
        try {
            FileObject fileObject = this.manager.resolveFile(this.root + path, createDefaultOptions());

            if (exists(path)) {
                for (FileObject fi : fileObject.getChildren()) {
                    if (fi.getType() == FileType.FOLDER) {
                        IPath xf = mapWith(path + "/" + fi.getName().getBaseName());

                        if (MountManager.getInstance().mountPoints.containsKey(xf.getPath())) {
                            continue;
                        }

                        if (!(showHiden)) {
                            if ((fi.getName().getBaseName().length() != 0) && (fi.getName().getBaseName().charAt(0) == '.')) {
                                continue;
                            }
                            files.add(xf);
                        } else {
                            files.add(xf);
                        }
                    }
                }
                return files;
            }
            return files;
        } catch (FileSystemException e) {
            e.printStackTrace();
        }

        return files;
    }

    public List<IPath> lsFiles(String path, boolean showHiden) {
        ArrayList files = new ArrayList();
        List ps = MountManager.getInstance().getMountPointPath(path);
        files.addAll(ps);
        path = resolvePath(path);
        try {
            FileObject fileObject = this.manager.resolveFile(this.root + path, createDefaultOptions());

            if (exists(path)) {
                for (FileObject fi : fileObject.getChildren()) {
                    if (fi.getType() == FileType.FILE) {
                        IPath xf = mapWith(path + "/" + fi.getName().getBaseName());

                        if (MountManager.getInstance().mountPoints.containsKey(xf.getPath())) {
                            continue;
                        }

                        if (!(showHiden)) {
                            if ((fi.getName().getBaseName().length() != 0) && (fi.getName().getBaseName().charAt(0) == '.')) {
                                continue;
                            }
                            files.add(xf);
                        } else {
                            files.add(xf);
                        }
                    }
                }
                return files;
            }
            return files;
        } catch (FileSystemException e) {
            e.printStackTrace();
        }
        return files;
    }

    public List<IPath> lsAll(String path) {
        return ls(path, false);
    }

    public boolean exists(String path) {
        path = resolvePath(path);
        try {
            FileObject fileObject = this.manager.resolveFile(this.root + path, createDefaultOptions());

            return fileObject.exists();
        } catch (FileSystemException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isDir(String path) {
        path = resolvePath(path);
        try {
            FileObject fileObject = this.manager.resolveFile(this.root + path, createDefaultOptions());

            return (fileObject.getType() == FileType.FOLDER);
        } catch (FileSystemException e) {
        }
        return false;
    }

    public InputStream getInputStream(String path) {
        path = resolvePath(path);
        try {
            FileObject fileObject = this.manager.resolveFile(this.root + path, createDefaultOptions());

            return fileObject.getContent().getInputStream();
        } catch (FileSystemException e) {
        }
        return null;
    }

    public OutputStream getOutputStream(String path) {
        path = resolvePath(path);
        try {
            FileObject fileObject = this.manager.resolveFile(this.root + path, createDefaultOptions());

            return fileObject.getContent().getOutputStream();
        } catch (FileSystemException e) {
        }
        return null;
    }

    public void write(String path, String content) {
        path = resolvePath(path);
        try {
            FileObject fileObject = this.manager.resolveFile(this.root + path, createDefaultOptions());

            String code = new CodeUtils().getEncode(content);
            if (!(code.equals("unknown"))) {
                try {
                    content = new String(content.getBytes(code));
                } catch (UnsupportedEncodingException e) {
                    logger.error(e);
                }
            }

            if (!(fileObject.exists())) {
                try {
                    fileObject.createFile();
                } catch (IOException e1) {
                    logger.error(e1);
                }
            }
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(fileObject.getContent().getOutputStream());
                pw.print(new String(content.getBytes(), "utf-8"));
                pw.close();
            } catch (UnsupportedEncodingException e) {
                logger.error(e);
                pw.close();
            }
            super.write(path, content);
        } catch (FileSystemException e) {
            e.printStackTrace();
        }
    }

    public void createFile(String path) {
        path = resolvePath(path);

        if (exists(path)) {
            IPath p = mapWith(path);

            String name = p.getName();

            int dot = name.lastIndexOf(".");

            String pn = null;
            String suffix = null;

            if ((dot != 0) && (dot != name.length() - 1)) {
                suffix = name.substring(dot + 1, name.length());
                pn = name.substring(0, dot);
            } else if (dot == -1) {
                suffix = "";
                pn = name;
            } else {
                pn = name.substring(0, dot);
                suffix = "";
            }

            for (int i = 0; i < 2147483647; ++i) {
                if (exists(p.getParent().getPath() + "/" + pn + i + "." + suffix)) {
                    continue;
                }
                createFile(p.getParent().getPath() + "/" + pn + i + "." + suffix);

                break;
            }

            return;
        }
        try {
            FileObject fileObject = this.manager.resolveFile(this.root + path, createDefaultOptions());

            fileObject.createFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.createFile(path);
    }

    public boolean forceDelete(File f) {
        boolean result = false;
        int tryCount = 0;
        while ((!(result)) && (tryCount++ < 10)) {
            logger.debug("try to delete file " + f.getName() + " cnt:" + tryCount);

            System.gc();
            result = f.delete();
        }
        return result;
    }

    public boolean forceDelete(FileObject f) {
        boolean result = false;
        int tryCount = 0;
        while ((!(result)) && (tryCount++ < 10)) {
            logger.debug("try to delete file " + f.getName() + " cnt:" + tryCount);

            System.gc();
            try {
                result = f.delete();
            } catch (FileSystemException e) {
                result = false;
            }
        }
        return result;
    }

    public void deleteFile(String path) {
        path = resolvePath(path);
        try {
            FileObject file = this.manager.resolveFile(this.root + path, createDefaultOptions());

            if (file.exists()) {
                file.delete(Selectors.SELECT_ALL);
            }
            super.deleteFile(path);
        } catch (FileSystemException e) {
            e.printStackTrace();
        }
    }

    public void rm(String path) {
        deleteFile(path);
        super.rm(path);
    }

    public boolean rn(String fromPath, String toPath) {
        if (MountManager.getInstance().getFileSystem(fromPath) == MountManager.getInstance().getFileSystem(toPath)) {
            fromPath = resolvePath(fromPath);

            toPath = resolvePath(toPath);
        }
        try {
            FileObject fromFileObject = this.manager.resolveFile(this.root + fromPath, createDefaultOptions());

            FileObject toFileObject = this.manager.resolveFile(this.root + toPath, createDefaultOptions());

            if (fromFileObject.equals(toFileObject)) {
                return false;
            }

            boolean result = false;
            int tryCount = 0;
            while ((!(result)) && (tryCount++ < 10)) {
                logger.debug("try to rename file " + fromFileObject.getName() + " to " + toFileObject.getName() + " cnt:" + tryCount);

                System.gc();
                if (fromFileObject.canRenameTo(toFileObject)) {
                    fromFileObject.moveTo(toFileObject);
                    result = true;
                } else {
                    result = false;
                }
                ++tryCount;
            }
            label243:
            return true;
        } catch (FileSystemException e) {
            e.printStackTrace();
            //break label243:
 
       cp(fromPath, toPath);
            rm(fromPath);

            super.rn(fromPath, toPath);
        }
        return true;
    }

    public void mkdir(String path) {
        path = resolvePath(path);
        try {
            FileObject fromFileObject = this.manager.resolveFile(this.root + path, createDefaultOptions());

            if (!(fromFileObject.exists())) {
                fromFileObject.createFolder();
            } else {
                IPath p = mapWith(path);
                String name = p.getName();
                for (int i = 0; i < 2147483647; ++i) {
                    if (!(exists(p.getParent().getPath() + "/" + name + i))) {
                        mkdir(p.getParent().getPath() + "/" + name + i);
                        break;
                    }
                }
                return;
            }
            super.mkdir(path);
        } catch (FileSystemException e) {
            e.printStackTrace();
        }
    }

    public synchronized String read(String filePath) {
        filePath = resolvePath(filePath);
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            //System.out.println(this.root + filePath);
            FileObject fromFileObject = this.manager.resolveFile(this.root + filePath, createDefaultOptions());
            if (fromFileObject.getType() != FileType.FILE) {
                //System.out.println("return null");
                return null;
            }
            InputStream i = fromFileObject.getContent().getInputStream();
            CodeUtils codoutil =new CodeUtils();
            String code = codoutil.getEncode(i);
            if (!(code.equals("unknown"))) {
                br = new BufferedReader(new InputStreamReader(fromFileObject.getContent().getInputStream(), Charset.forName(code)));
            } else {
                br = new BufferedReader(new InputStreamReader(fromFileObject.getContent().getInputStream()));
            }
            String t = null;
            while ((t = br.readLine()) != null) {
                sb.append(t).append('\n');
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String s = null;
        s = sb.toString();
        super.read(filePath);
        return s;
    }

    public void createNewFile(String path, String suffix, String type) {
        int i = 0;
        boolean ok = false;
    }

    public void setLastModified(String file, long time) {
        if (!(isDir(file))) {
            file = resolvePath(file);
            try {
                FileObject fromFileObject = this.manager.resolveFile(this.root + file, createDefaultOptions());

                fromFileObject.getContent().setLastModifiedTime(time);
            } catch (FileSystemException e) {
                e.printStackTrace();
            }
        }
    }

    public void createFile(String paramString, String paramString2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
