package com.ambimmort.webos.old.plugins.filesystem.elements;

import java.util.List;

public abstract interface IMountManager
{
  public abstract IFileSystem getFileSystem(String paramString);

  public abstract MountPoint getMountPoint(String paramString);

  public abstract List<IPath> getMountPointPath(String paramString);
}

/* Location:           E:\company\webosInstall\2013.11.9-1\cc\applications\nisp.webos\nisp.webos.server\program\lib\nisp.webos.server-1.0.1-SNAPSHOT.jar
 * Qualified Name:     aasos.js.client.IMountManager
 * JD-Core Version:    0.5.3
 */