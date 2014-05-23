package com.ambimmort.webos.commons.libmanager;

import com.ambimmort.webos.commons.server.WebOS;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

public class LibLoader
        implements FileAlterationListener {

    MyClassLoader classLoader;
    private static LibLoader instance = null;
    private Hashtable<String, ServletInfo> servlets;
    private HashSet<String> loadedJarNames;
    private Class _cl;
    private Hashtable<String, Class> allClasses;

    public LibLoader() {
        this.classLoader = null;

        this.servlets = new Hashtable();
        this.loadedJarNames = new HashSet();
        this._cl = null;
        this.allClasses = new Hashtable();
    }

    public List<String> getAllClasses(File file) throws FileNotFoundException, IOException {
        List classNames = new ArrayList();
        ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (!(entry.getName().endsWith(".class"))) {
                continue;
            }
            if (entry.isDirectory()) {
                continue;
            }
            StringBuilder className = new StringBuilder();
            for (String part : entry.getName().split("/")) {
                if (className.length() != 0) {
                    className.append(".");
                }
                className.append(part);
                if (part.endsWith(".class")) {
                    className.setLength(className.length() - ".class".length());
                }
            }
            classNames.add(className.toString());
        }

        return classNames;
    }

    public Class classForName(String name) {
        if (this.allClasses.containsKey(name)) {
            return ((Class) this.allClasses.get(name));
        }
        boolean found = false;
        Class ac = null;
        try {
            ac = this.classLoader.loadClass(name);
            found = true;
        } catch (ClassNotFoundException ex) {
            found = false;
        } catch (NoClassDefFoundError ex) {
            found = false;
        } catch (Throwable ex) {
            found = false;
        }

        if (ac != null) {
            this.allClasses.put(name, ac);
        }
        return ac;
    }

    public boolean classExists(String name) {
        if (!(this.allClasses.containsKey(name))) {
            return (classForName(name) != null);
        }
        return false;
    }

    public static LibLoader getInstance() {
        if (instance == null) {
            instance = new LibLoader();
        }
        return instance;
    }

    public void setClassLoader(MyClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    private void loadSystemJar() {
        File systemDir = new File("lib");
        if (systemDir.exists()) {
            for (File f : systemDir.listFiles()) {
                loadServlets(f);
            }
        }
    }

    public void init() {
        loadSystemJar();
    }

    public Hashtable<String, ServletInfo> getServlets() {
        return this.servlets;
    }

    public void loadServlets(File jarFile) {
        if (this.loadedJarNames.contains(jarFile.getName())) {
            System.out.println("load: -- " + jarFile + " duplicated and igonre");
            return;
        }
        System.out.println("load: -- " + jarFile);
        this.loadedJarNames.add(jarFile.getName());
        try {
            List<String> list = getAllClasses(jarFile);

            for (String cn : list) {
                if ((!(jarFile.getAbsolutePath().startsWith("webos.commons.server"))) && (!(cn.startsWith("com.ambimmort")))&&(!(cn.startsWith("org.pentaho")))) {
                    continue;
                }

                this._cl = getInstance().classForName(cn);
                if (this._cl == null) {
                    continue;
                }

                Annotation wsa = null;
                Annotation pwa = null;
                Annotation boot = null;

                for (Annotation an : this._cl.getDeclaredAnnotations()) {
                    if (an.annotationType().getName().equals("com.ambimmort.webos.commons.servlets.annotation.WebServlet")) {
                        wsa = an;
                    } else if (an.annotationType().getName().equals("com.ambimmort.webos.commons.servlets.annotation.Boot")) {
                        boot = an;
                    } else if (an.annotationType().getName().equals("com.ambimmort.webos.commons.servlets.annotation.Project")) {
                        pwa = an;
                    }
                }

                if (wsa != null) {
                    String url = (String) wsa.annotationType().getDeclaredMethod("url", null).invoke(wsa, null);

                    System.out.println("add servlet [" + this._cl.getName() + "] url=" + url);

                    WebOS.getInstance().addServlet(this._cl, url);
                    ServletInfo si = new ServletInfo();
                    si.setUrl(url);
                    si.setClassName(cn);
                    if (pwa != null) {
                        si.setProject((String) pwa.annotationType().getDeclaredMethod("name", null).invoke(pwa, null));
                        si.setAuthor((String) pwa.annotationType().getDeclaredMethod("author", null).invoke(pwa, null));
                    } else {
                        si.setProject("");
                        si.setAuthor("");
                    }
                    this.servlets.put(url, si);
                    System.out.println("servlet putted : " + url + "\t" + si.getProject());
                }

                if (boot != null) {
                    new Thread(new Runnable() {
                        public void run() {
                            System.out.println("Boot bean detected. Class[" + LibLoader.this._cl.getName() + "]");
                            try {
                                LibLoader.this._cl.newInstance();
                            } catch (InstantiationException ex) {
                                Logger.getLogger(LibLoader.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IllegalAccessException ex) {
                                Logger.getLogger(LibLoader.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }).start();
                }

            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(LibLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LibLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(LibLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(LibLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(LibLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(LibLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(LibLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onStart(FileAlterationObserver fao) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onDirectoryCreate(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onDirectoryChange(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onDirectoryDelete(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onFileCreate(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onFileChange(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onFileDelete(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onStop(FileAlterationObserver fao) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setupJar(File file) {
        if (file.getName().contains("nisp.webos.server")) {
            loadServlets(file);
        }
        if (file.getAbsolutePath().endsWith(".jar")) {
            loadServlets(file);
        }
    }
}

/* Location:           F:\nisp.webos.server-1.0-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.webos.commons.service.LibLoader
 * JD-Core Version:    0.5.3
 */
