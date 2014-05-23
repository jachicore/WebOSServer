 package com.ambimmort.webos.old.plugins.filesystem.elements;
 
 public class Path
   implements IPath
 {
   public String path;
   public String owner;
   public String permission;
   public long lastModified;
   public long size;
   public IFileSystem fileSystem;
 
   public Path()
   {
     this.path = "";
 
     this.owner = "";
 
     this.permission = "";
 
     this.lastModified = 0L;
 
     this.size = 0L;
 
     this.fileSystem = null;
   }
 
   public void setPath(String path) {
     this.path = path;
   }
 
   public String getPath()
   {
     return this.path;
   }
 
   public String getName()
   {
     if (this.path.equals("/")) {
       return "/";
     }
     return this.path.substring(this.path.lastIndexOf("/") + 1, this.path.length());
   }
 
   public String getOwner()
   {
     return this.owner;
   }
 
   public String getPermission()
   {
     return this.permission;
   }
 
   public IPath getParent()
   {
     return getFileSystem().mapWith(this.path.substring(0, this.path.lastIndexOf("/")));
   }
 
   public boolean isDir()
   {
     return getFileSystem().isDir(this.path);
   }
 
   public long getSize()
   {
     return this.size;
   }
 
   public long getLastModified()
   {
     return this.lastModified;
   }
 
   public IFileSystem getFileSystem()
   {
     return this.fileSystem;
   }
 }

/* Location:           E:\company\webosInstall\2013.11.9-1\cc\applications\nisp.webos\nisp.webos.server\program\lib\nisp.webos.server-1.0.1-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.nisp.webos.plugins.filesystem.elements.Path
 * JD-Core Version:    0.5.3
 */