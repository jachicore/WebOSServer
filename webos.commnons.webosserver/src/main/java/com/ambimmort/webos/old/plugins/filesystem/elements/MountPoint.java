package com.ambimmort.webos.old.plugins.filesystem.elements;


import com.ambimmort.webos.old.plugins.filesystem.extend.VFSFileSystem;
import java.net.URI;

public class MountPoint {

    private URI protocol;
    private String mountPoint;
    private IFileSystem fileSystem;
    private String file;

    public MountPoint() {
        this.protocol = null;
        this.mountPoint = "";
        this.fileSystem = null;
        this.file = null;
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setFileSystem(IFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public IFileSystem getFileSystem() {
        return this.fileSystem;
    }

    public String getMountPoint() {
        return this.mountPoint;
    }

    public URI getProtocol() {
        return this.protocol;
    }

    public void setMountPoint(String path) {
        this.mountPoint = path;
    }

    public void setProtocol(URI protocol) {
        this.protocol = protocol;
        VFSFileSystem lfs = new VFSFileSystem();
        lfs.setRoot(protocol.toString());
        if (lfs.getRoot().equals("/")) {
            lfs.setRoot("");
        }
        this.fileSystem = lfs;
        lfs.setMountPoint(this);
    }

    public String getPath(String path) {
        if (this.mountPoint.equals(path)) {
            return "";
        }
        return path.replace(this.mountPoint, "");
    }

    public String toString() {
        return "mp:" + this.mountPoint + "\t" + "fs root:" + getFileSystem().getRoot();
    }
}

/* Location:           E:\company\webosInstall\2013.11.9-1\cc\applications\nisp.webos\nisp.webos.server\program\lib\nisp.webos.server-1.0.1-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.nisp.webos.plugins.filesystem.elements.MountPoint
 * JD-Core Version:    0.5.3
 */
