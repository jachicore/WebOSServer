package com.ambimmort.webos.old.plugins.filesystem.impl;

import com.ambimmort.webos.old.plugins.filesystem.elements.FileSystemChangeEvent;

public abstract interface IFileSystemListener
{
  public abstract void onFileSystemChange(FileSystemChangeEvent paramFileSystemChangeEvent);
}

/* Location:           E:\company\webosInstall\2013.11.9-1\cc\applications\nisp.webos\nisp.webos.server\program\lib\nisp.webos.server-1.0.1-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.nisp.webos.plugins.filesystem.impl.IFileSystemListener
 * JD-Core Version:    0.5.3
 */