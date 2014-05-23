package com.ambimmort.webos.old.plugins.filesystem.elements;

public abstract interface IPath
{
  public abstract void setPath(String paramString);

  public abstract String getPath();

  public abstract String getName();

  public abstract String getOwner();

  public abstract String getPermission();

  public abstract IPath getParent();

  public abstract boolean isDir();

  public abstract long getSize();

  public abstract long getLastModified();

  public abstract IFileSystem getFileSystem();
}

/* Location:           E:\company\webosInstall\2013.11.9-1\cc\applications\nisp.webos\nisp.webos.server\program\lib\nisp.webos.server-1.0.1-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.nisp.webos.plugins.filesystem.elements.IPath
 * JD-Core Version:    0.5.3
 */