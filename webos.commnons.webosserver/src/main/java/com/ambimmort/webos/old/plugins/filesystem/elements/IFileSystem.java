package com.ambimmort.webos.old.plugins.filesystem.elements;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public abstract interface IFileSystem
{
  public abstract String getRoot();

  public abstract void setRoot(String paramString);

  public abstract IPath mapWith(String paramString);

  public abstract void createFile(String paramString);

  public abstract void createNewFile(String paramString1, String paramString2, String paramString3);

  public abstract void deleteFile(String paramString);

  public abstract void mkdir(String paramString);

  public abstract void rm(String paramString);

  public abstract boolean rn(String paramString1, String paramString2);

  public abstract void cp(String paramString1, String paramString2);

  public abstract void move(String paramString1, String paramString2);

  public abstract List<IPath> ls(String paramString);

  public abstract List<IPath> lsDirs(String paramString);

  public abstract List<IPath> lsFiles(String paramString);

  public abstract List<IPath> ls(String paramString, boolean paramBoolean);

  public abstract List<IPath> lsDirs(String paramString, boolean paramBoolean);

  public abstract List<IPath> lsFiles(String paramString, boolean paramBoolean);

  public abstract List<IPath> lsAll(String paramString);

  public abstract String read(String paramString);

  public abstract void write(String paramString1, String paramString2);

  public abstract boolean exists(String paramString);

  public abstract boolean isDir(String paramString);

  public abstract InputStream getInputStream(String paramString);

  public abstract OutputStream getOutputStream(String paramString);

  public abstract MountPoint getMountPoint();

  public abstract void setLastModified(String paramString, long paramLong);
}

/* Location:           E:\company\webosInstall\2013.11.9-1\cc\applications\nisp.webos\nisp.webos.server\program\lib\nisp.webos.server-1.0.1-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.nisp.webos.plugins.filesystem.elements.IFileSystem
 * JD-Core Version:    0.5.3
 */