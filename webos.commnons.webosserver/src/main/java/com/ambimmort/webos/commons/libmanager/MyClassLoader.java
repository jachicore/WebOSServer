package com.ambimmort.webos.commons.libmanager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import static sun.misc.Launcher.getBootstrapClassPath;
import sun.misc.Resource;
import sun.misc.URLClassPath;

public class MyClassLoader extends ClassLoader {

    private String path = null;
    private Hashtable<String, Class> loaded = new Hashtable();
    public Hashtable<String, URLClassLoader> classloaders = new Hashtable();
    private HashSet<String> loadedJars = new HashSet();
    private ClassLoader _parent = null;
    private String basedir = null;

    public MyClassLoader(ClassLoader parent, String path) {
        super(parent);
        if (parent == null) {
            System.out.println("parent is null!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        this._parent = parent;
        this.basedir = path;
        //System.out.println(parent.getResource("")+"!!!!!");
        ArrayList tl = new ArrayList();
        this.path = path;

        File lib = new File("lib");
        if (lib.isDirectory()) {
            try {
                File[] jars = lib.listFiles();

                for (int i = 0; i < jars.length; ++i) {
                    if (!(this.loadedJars.contains(jars[i].getName()))) {
                        tl.add(jars[i].toURI().toURL());
                        this.loadedJars.add(jars[i].getName());
                    }
                }
            } catch (Error e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        URL[] urls = new URL[tl.size()];
        for (int i = 0; i < urls.length; ++i) {
            urls[i] = ((URL) tl.get(i));
        }
        this.classloaders.put("all", new URLClassLoader(urls, super.getClass().getClassLoader()));
        //System.out.println(super.getClass().getClassLoader().getClass().getName() + "!!!!");
    }

//    @Override
//    public java.net.URL getResource(String name) {
//        URL url = null;
//        if (parent != null) {
//            System.out.println(parent.getClass().getName() + "gerResouce~~~~~~~");
//            url = parent.getResource(name);
//        }
//        if (url == null) {
//            System.out.println("empty");
//            url = findResource(name);//这里
//        }
//        return url;
//    }
//
//    private static URL getBootstrapResource(String name) {
//        URLClassPath ucp = getBootstrapClassPath();
//        Resource res = ucp.getResource(name);
//        return res != null ? res.getURL() : null;
//    }
//
//    private String converName(String name) {
//        StringBuffer sb = new StringBuffer(this.basedir);
//        //name = name.replace('.', File.separatorChar);
//        sb.append(name);
//        //System.out.println(sb.toString()+"############");
//        return sb.toString();
//    }
//
//    @Override
//    protected URL findResource(String name) {
//        try {
//            URL url = super.findResource(name);
//            if (url != null) {
//                return url;
//            }
//            url = PluginsLoader.getInstance().classLoader.findResource(name);
//           // url = new URL(converName(name));
//            //简化处理，所有资源从文件系统中获取
//            System.out.println(url.toURI().toString());
//            return url;
//        } catch (Exception mue) {
//            mue.printStackTrace();
//            return null;
//        }
//    }
    @Override
    public Class loadClass(String name) throws ClassNotFoundException {
        if (this.loaded.containsKey(name)) {
            return ((Class) this.loaded.get(name));
        }
        Class ac = null;
        boolean found = false;
        try {
            //System.out.println(super.getClass().getName() + "****");

            //System.out.println(this._parent.);
            //ac = findLoadedClass(name);
            if (ac == null) {
                ac = this._parent.loadClass(name);
            }

            this.loaded.put(name, ac);
            found = true;
            return ac;
        } catch (ClassNotFoundException ex) {
            found = false;
        } catch (NoClassDefFoundError ex) {
            found = false;
        } catch (Throwable ex) {
            found = false;
        }

        for (URLClassLoader ucl : this.classloaders.values()) {
            try {
                //System.out.println("~~url load:" + name);
                //System.out.println(name + "****");
                ac = ucl.loadClass(name);
                this.loaded.put(name, ac);
                return ac;
            } catch (Throwable e) {
                found = false;
            }
        }
        if (ac == null) {
            //System.out.println(this.loaded.contains(name)+ "!!!!!!!!!!!!!!!!!!!!!!!!");
//            System.out.println(this._parent.getClass().getName());
//            System.out.println(System.getProperty("java.class.path"));
//            System.out.println("!!!!!!!!!!!error classname :" + name);
        }
        return ac;
    }
}

/* Location:           F:\nisp.webos.server-1.0-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.webos.commons.service.MyClassLoader
 * JD-Core Version:    0.5.3
 */
