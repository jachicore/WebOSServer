package com.ambimmort.webos.old.plugins.filesystem.impl;



import com.ambimmort.webos.old.plugins.filesystem.elements.File;
import com.ambimmort.webos.old.plugins.filesystem.elements.IFileSystem;
import com.ambimmort.webos.old.plugins.filesystem.elements.IMountManager;
import com.ambimmort.webos.old.plugins.filesystem.elements.IPath;
import com.ambimmort.webos.old.plugins.filesystem.elements.MountPoint;
import com.ambimmort.webos.old.plugins.filesystem.elements.Path;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MountManager
        implements IMountManager {

    private static MountManager instance = null;
    public HashMap<String, MountPoint> mountPoints = new HashMap();
    private HashMap<String, Long> mountPointFiles = new HashMap();
    private Timer timer = new Timer();

    private MountManager() {
        System.out.println("load mount point");
        initDefaultMountPointConfig();
        /*this.timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                MountManager.this.checkExtensibleMountPoint();
            }
        }, 1000L, 5000L);*/
    }

    private void initDefaultMountPointConfig() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new java.io.File("config/MountPoint.xml"));
            NodeList nl = doc.getElementsByTagName("mount");
            for (int i = 0; i < nl.getLength(); ++i) {
                Element ele = (Element) nl.item(i);
                String mp = ele.getAttribute("mountPoint");
                String value = ele.getTextContent();
                MountPoint mountPoint = new MountPoint();
                mountPoint.setFile("config/MountPoint.xml");
                mountPoint.setMountPoint(mp);
                mountPoint.setProtocol(URI.create(value.replace(" ", "%20")));
                this.mountPoints.put(mp, mountPoint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void checkExtensibleMountPoint() {
        FSServiceImpl fs;
        List<File> files;
        try {
            fs = new FSServiceImpl();
            if (fs.exists("/etc/mountpoints")) {
                files = fs.listFiles("/etc/mountpoints");
                for (File f : files) {
                    if (this.mountPointFiles.containsKey(f.getPath())) {
                        if (f.getLastModified() > ((Long) this.mountPointFiles.get(f.getPath())).longValue()) {
                            setupMountPoint(f, fs);
                        }
                    } else {
                        setupMountPoint(f, fs);
                    }
                }

                for (String k : this.mountPointFiles.keySet()) {
                    boolean exist = false;
                    for (File f : files) {
                        if (f.getPath().equals(k)) {
                            exist = true;
                            break;
                        }
                    }
                    if (!(exist)) {
                        uninstallMountPoint(fs.mapWith(k), fs);
                    }
                }
            }
        } catch (Throwable t) {
        }
    }

    private void setupMountPoint(File f, FSServiceImpl fs) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(fs.read(f.getPath()).getBytes()));
            NodeList nl = doc.getElementsByTagName("mount");
            for (int i = 0; i < nl.getLength(); ++i) {
                Element ele = (Element) nl.item(i);
                String mp = ele.getAttribute("mountPoint");
                String value = ele.getTextContent();
                MountPoint mountPoint = new MountPoint();
                mountPoint.setFile(f.getPath());
                mountPoint.setMountPoint(mp);
                mountPoint.setProtocol(URI.create(value.replace(" ", "%20")));
                this.mountPoints.put(mp, mountPoint);
                this.mountPointFiles.put(f.getPath(), Long.valueOf(f.getLastModified()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void uninstallMountPoint(File f, FSServiceImpl fs) {
        for (String key : this.mountPoints.keySet()) {
            MountPoint mp = (MountPoint) this.mountPoints.get(key);
            if (mp.getFile().equals(f.getPath())) {
                this.mountPoints.remove(key);
                this.mountPointFiles.remove(f.getPath());
            }
        }
    }*/

    public static MountManager getInstance() {
        if (instance == null) {
            instance = new MountManager();
        }
        return instance;
    }

    public IFileSystem getFileSystem(String path) {
        String tmp = path;

        while (!(tmp.equals("/"))) {
            if (this.mountPoints.containsKey(tmp)) {
                return ((MountPoint) this.mountPoints.get(tmp)).getFileSystem();
            }
            tmp = getParent(tmp);
        }

        if (this.mountPoints.containsKey(tmp)) {
            return ((MountPoint) this.mountPoints.get(tmp)).getFileSystem();
        }
        
        return null;
    }

    public MountPoint getMountPoint(String path) {
        String tmp = path;

        while (!(tmp.equals("/"))) {
            if (this.mountPoints.containsKey(tmp)) {
                return ((MountPoint) this.mountPoints.get(tmp));
            }
            tmp = getParent(tmp);
        }

        if (this.mountPoints.containsKey(tmp)) {
            return ((MountPoint) this.mountPoints.get(tmp));
        }
        return null;
    }

    public List<IPath> getMountPointPath(String path) {
        List paths = new ArrayList();
        for (String p : this.mountPoints.keySet()) {
            String rt = p.replace(path, "");
            if ((p.startsWith(path)) && (rt.indexOf("/") == 0) && (rt.lastIndexOf("/") == 0)) {
                Path pt = new Path();
                pt.path = p;
                pt.fileSystem = ((MountPoint) this.mountPoints.get(p)).getFileSystem();
                paths.add(pt);
            }
        }

        return paths;
    }

    private String getParent(String path) {
        if ("/".equals(path)) {
            return "/";
        }

        int index = path.lastIndexOf("/");
        if (index == 0) {
            return "/";
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path.substring(0, index);
    }
}

/* Location:           E:\company\webosInstall\2013.11.9-1\cc\applications\nisp.webos\nisp.webos.server\program\lib\nisp.webos.server-1.0.1-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.nisp.webos.plugins.filesystem.impl.MountManager
 * JD-Core Version:    0.5.3
 */
