package com.ambimmort.webos.dmcClient.filesystem.elements;

public class File {
    private String fatherpath;
    private String name;
    private String path;
    private String type;
    private String userid;
    private String profile;
    private long lastModifyTime;
    private String linkpath;
    private long size;
    private String suffix;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        if (name.lastIndexOf(".") == -1) {
            this.suffix="";
        }
        else{
            this.suffix = name.substring(name.lastIndexOf(".") + 1, name.length());
        }
        
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public String getFatherpath() {
        return this.fatherpath;
    }

    public void setFatherpath(String fatherpath) {
        this.fatherpath = fatherpath;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getProfile() {
        return this.profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public boolean isDir() {
        return getType().equals("dir");
    }

    public long getLastModifyTime() {
        return this.lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the linkpath
     */
    public String getLinkpath() {
        return linkpath;
    }

    /**
     * @param linkpath the linkpath to set
     */
    public void setLinkpath(String linkpath) {
        this.linkpath = linkpath;
    }

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(long size) {
        this.size = size;
    }
}

/* Location:           F:\nisp.webos.server-1.0-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.dmcClient.filesystem.File
 * JD-Core Version:    0.5.3
 */
