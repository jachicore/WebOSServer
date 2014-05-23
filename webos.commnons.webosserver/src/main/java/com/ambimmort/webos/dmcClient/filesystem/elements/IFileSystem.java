package com.ambimmort.webos.dmcClient.filesystem.elements;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public abstract interface IFileSystem
{
  public abstract String getRoot();

  public abstract void setRoot(String paramString);

  public abstract File mapWith(String paramString);

  public abstract void createFile(String paramString);

  public abstract void createFile(String paramString1, String paramString2);

  public abstract void create(String paramString1, String paramString2,String paramString3,String profile,String type);

  public abstract void createLink(String paramString1, String paramString2,String linkPath);
  
  public abstract void deleteRecur(String paramString);

  public abstract void deleteRecur(String paramString1, String paramString2);
  
  public void deleteAllByUser(String user);

  public abstract void mkdir(String paramString);
  
  public abstract void mkdir(String paramString,String user);

  public abstract void rm(String paramString);

  public abstract boolean rn(String paramString1, String paramString2);

  public abstract void cp(String paramString1, String paramString2);

  public abstract void move(String paramString1, String paramString2);

  public abstract List<File> ls(String paramString);

  public abstract List<File> lsDirs(String paramString);

  public abstract List<File> lsFiles(String paramString);

  public abstract String read(String paramString);

  public abstract void write(String paramString1, String paramString2);

  public abstract boolean exists(String paramString);

  public abstract boolean existsByFatherAndName(String paramString1, String paramString2);

  public abstract boolean isDir(String paramString);

  public abstract InputStream getInputStream(String paramString);

  public abstract OutputStream getOutputStream(String paramString);

  public abstract void setLastModified(String paramString, long paramLong);
}

/* Location:           F:\nisp.webos.server-1.0-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.dmcClient.filesystem.IFileSystem
 * JD-Core Version:    0.5.3
 */