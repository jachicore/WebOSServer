 package com.ambimmort.webos.old.plugins.filesystem.impl;
 
import com.ambimmort.webos.old.plugins.filesystem.elements.File;
import com.ambimmort.webos.old.plugins.filesystem.elements.FileSystemChangeEvent;
import com.ambimmort.webos.old.plugins.filesystem.elements.IFileSystem;
import com.ambimmort.webos.old.plugins.filesystem.elements.IPath;
import com.ambimmort.webos.old.plugins.filesystem.elements.MountPoint;
 import java.io.BufferedInputStream;
 import java.io.BufferedOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.util.ArrayList;
 import java.util.List;
 import org.apache.log4j.Logger;
 
 public class FSServiceImpl
 {
   private String path;
   private List<IFileSystemListener> fileSystemListeners;
   private static Logger logger = Logger.getLogger(FSServiceImpl.class);
 
   public FSServiceImpl()
   {
     this.path = "/";
 
     this.fileSystemListeners = new ArrayList();
   }
 
   public File mkFile(IPath path)
   {
     File f = new File();
     f.path = path.getPath();
     f.name = path.getName();
     f.type = ((path.isDir()) ? "dir" : "file");
     f.lastModified = path.getLastModified();
     f.dataCreated = path.getLastModified();
     f.size = path.getSize();
     int in = path.getName().lastIndexOf(".");
     if (in < 0)
       f.suffix = "";
     else {
       f.suffix = path.getName().substring(in + 1);
     }
 
     return f;
   }
 
   public List<File> mkFiles(List<IPath> paths) {
     List files = new ArrayList();
     for (IPath p : paths) {
       files.add(mkFile(p));
     }
     return files;
   }
 
   private void fireFileSystemChange(FileSystemChangeEvent arg)
   {
     for (IFileSystemListener l : this.fileSystemListeners)
       l.onFileSystemChange(arg);
   }
 
   public File mapWith(String path)
   {
     return mkFile(MountManager.getInstance().getFileSystem(path).mapWith(path));
   }
 
   public void createFile(String filePath)
   {
     MountManager.getInstance().getFileSystem(filePath).createFile(filePath);
   }
 
   public void deleteFile(String filePath) {
     MountManager.getInstance().getFileSystem(filePath).deleteFile(filePath);
   }
 
   public boolean forceDelete(java.io.File f) {
     boolean result = false;
     int tryCount = 0;
     while ((!(result)) && (tryCount++ < 10)) {
       logger.debug("try to delete file " + f.getName() + " cnt:" + tryCount);
 
       System.gc();
       result = f.delete();
     }
     return result;
   }
 
   public void mkdir(String dirPath) {
     MountManager.getInstance().getFileSystem(dirPath).mkdir(dirPath);
   }
 
   public List<File> listFiles(String dirPath)
   {
     return mkFiles(MountManager.getInstance().getFileSystem(dirPath).ls(dirPath));
   }
 
   public List<File> listDirs(String dirPath)
   {
     return mkFiles(MountManager.getInstance().getFileSystem(dirPath).lsDirs(dirPath));
   }
 
   public List<File> listAll(String dirPath)
   {
     return mkFiles(MountManager.getInstance().getFileSystem(dirPath).lsAll(dirPath));
   }
 
   public boolean exists(String path)
   {
     return MountManager.getInstance().getFileSystem(path).exists(path);
   }
 
   public String write(String content, String filePath)
   {
     MountManager.getInstance().getFileSystem(filePath).write(filePath, content);
 
     return "";
   }
 
   public String read(String filePath)
   {
     return MountManager.getInstance().getFileSystem(filePath).read(filePath);
   }
 
   public void cp(String src, String dst)
   {
     MountManager.getInstance().getFileSystem(src).cp(src, dst);
   }
 
   public void move(String src, String dst)
   {
     MountManager.getInstance().getFileSystem(src).move(src, dst);
   }
 
   public void rn(String src, String dst)
   {
     MountManager.getInstance().getFileSystem(src).rn(src, dst);
   }
 
   public File getParent(String path)
   {
     return mkFile(MountManager.getInstance().getFileSystem(path).mapWith(path).getParent());
   }
 
   public static void copy(InputStream is, OutputStream os)
   {
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
     }
     catch (Exception e) {
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

    public InputStream getInputStream(String paramString) {
        return MountManager.getInstance().getFileSystem(paramString).getInputStream(paramString);
    }

    public OutputStream getOutputStream(String paramString) {
        return MountManager.getInstance().getFileSystem(paramString).getOutputStream(paramString);
    }
 }
