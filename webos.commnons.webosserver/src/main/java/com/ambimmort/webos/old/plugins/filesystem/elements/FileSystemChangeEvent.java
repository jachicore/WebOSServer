package com.ambimmort.webos.old.plugins.filesystem.elements;

public class FileSystemChangeEvent {

    public static final int FILE_CREATE = 1;
    public static final int FILE_DELETE = 2;
    public static final int FILE_READ = 3;
    public static final int FILE_WRITE = 4;
    public static final int DIR_MAKE = 5;
    public static final int DIR_REMOVE = 6;
    public static final int FILE_RENAME = 7;
    private int type;
    private Object arg;

    public FileSystemChangeEvent() {
        this.type = 0;

        this.arg = null;
    }

    public void setArg(Object arg) {
        this.arg = arg;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getArg() {
        return this.arg;
    }

    public int getType() {
        return this.type;
    }
}

/* Location:           E:\company\webosInstall\2013.11.9-1\cc\applications\nisp.webos\nisp.webos.server\program\lib\nisp.webos.server-1.0.1-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.nisp.webos.plugins.filesystem.elements.FileSystemChangeEvent
 * JD-Core Version:    0.5.3
 */
