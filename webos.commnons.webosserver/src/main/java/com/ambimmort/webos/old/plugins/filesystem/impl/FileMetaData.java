 package com.ambimmort.webos.old.plugins.filesystem.impl;
 
 public class FileMetaData
 {
   public static int READ = 4;
 
   public static int WRITE = 2;
 
   public static int EXECUTE = 1;
   private long uid;
   private int permission;
   private boolean isDir;
 
   public FileMetaData()
   {
     this.uid = 0L;
 
     this.permission = 493;
 
     this.isDir = false; }
 
   public void setPermision(int permission) {
     this.permission = permission;
   }
 
   public int getPermission() {
     return this.permission;
   }
 
   public int getPermission(PermissionRole role)
   {
     if (role.equals(PermissionRole.USER))
       return ((this.permission & 0x1C0) >> 6);
     if (role.equals(PermissionRole.GROUP))
       return ((this.permission & 0x38) >> 3);
     if (role.equals(PermissionRole.EVERYONE)) {
       return (this.permission & 0x7);
     }
     return 0;
   }
 
   private int getOwnerId(long uuid)
   {
     return (int)(uuid >> 32);
   }
 
   private int getGroupId(long uuid) {
     return (int)(uuid & 0xFFFFFFFF);
   }
 
   public void setPermission(PermissionRole role, int permission) {
     if (role.equals(PermissionRole.USER))
       this.permission = (this.permission & 0x3F | permission << 6);
     else if (role.equals(PermissionRole.GROUP))
       this.permission = (this.permission & 0x1C7 | permission << 3);
     else if (role.equals(PermissionRole.EVERYONE))
       this.permission = (this.permission & 0x1F8 | permission);
   }
 
   public boolean canWrite(int uid)
   {
     return false;
   }
 
   public boolean canRead(int uid)
   {
     return false;
   }
 
   public boolean canExecute(int uid)
   {
     return false;
   }
 
   public String toString()
   {
     return ((this.isDir) ? "d" : "-");
   }
 
   public static void main(String[] args) {
     FileMetaData fd = new FileMetaData();
     fd.setPermision(466);
   }
 }

/* Location:           E:\company\webosInstall\2013.11.9-1\cc\applications\nisp.webos\nisp.webos.server\program\lib\nisp.webos.server-1.0.1-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.nisp.webos.plugins.filesystem.impl.FileMetaData
 * JD-Core Version:    0.5.3
 */