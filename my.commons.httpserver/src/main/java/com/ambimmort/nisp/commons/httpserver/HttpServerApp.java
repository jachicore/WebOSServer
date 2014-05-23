package com.ambimmort.nisp.commons.httpserver;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.ProxyServlet.Transparent;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;

public class HttpServerApp
        implements FileAlterationListener {

    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(HttpServerApp.class);
    private Server server = null;
    private WebAppContext context = null;
    private static HttpServerApp instance = null;
    private FileAlterationMonitor monitor;
    private FileAlterationObserver observer;

    private HttpServerApp() {
        this.observer = new FileAlterationObserver("config");
        this.observer.addListener(this);
        this.monitor = new FileAlterationMonitor(1000L);
        this.monitor.addObserver(this.observer);

        try {
            this.monitor.start();
        } catch (Exception e) {
            this.logger.error(e);
        }
    }

    private void init() {
        this.server = new Server(HttpServerConfig.getInstance().getPort());
        this.context = new WebAppContext();
        this.getContext().setContextPath("/");
        this.getContext().setWelcomeFiles(new String[]{"index.html", "index.jsp"});

        this.getContext().setErrorHandler(new ErrorPageErrorHandler());
        this.getContext().setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        String[] rs = new String[HttpServerConfig.getInstance().getResources().size()];
        for (int i = 0; i < rs.length; ++i) {
            rs[i] = ((String) HttpServerConfig.getInstance().getResources().get(i));
        }

        ResourceCollection resources = new ResourceCollection(rs);
        this.getContext().setBaseResource(resources);
        this.getContext().setAliases(true);
        this.getContext().setAvailable(true);
        int i = 0;
        for (String from : HttpServerConfig.getInstance().getProxies().keySet()) {
            if (from.trim().equals("")) {
                continue;
            }
            ++i;

            System.out.println(from);
            String key = from;

            ServletHolder proxy = new ServletHolder(Transparent.class);

            this.getContext().addServlet(proxy, key + "/*");
            System.out.println(proxy);
            proxy.setEnabled(true);
            proxy.setInitParameter("ProxyTo", (String) HttpServerConfig.getInstance().getProxies().get(from));
            proxy.setInitParameter("Prefix", key);
            proxy.setAsyncSupported(true);
            proxy.setInitOrder(i);
        }
        ServletHolder proxy = new ServletHolder(Transparent.class);
        this.context.addServlet(proxy, "/proxy/*");
        //System.out.println(proxy.getContextPath());
        //context.getServletContext().
        proxy.setEnabled(true);
        proxy.setInitParameter("ProxyTo", "http://www.baidu.com");
        proxy.setInitParameter("Prefix", "/proxy");
        proxy.setAsyncSupported(true);
        proxy.setInitOrder(i + 1);

        this.server.setHandler(this.getContext());
    }

    public void restart(Map<String,ServletHolder> servlets) {
        try {
            this.server.stop();
            init();
            Iterator i = servlets.keySet().iterator();
            for (;i.hasNext();) {
                String key = i.next().toString();
                ServletHolder servlet = servlets.get(key);
                this.context.addServlet(servlet, key+"/*");
            }
            this.server.start();
        } catch (Exception e) {
        }

    }

    public void start() {
        try {
            init();
            this.server.start();
            //restart();
        } catch (Exception ex) {
            this.logger.error(ex);
            ex.printStackTrace();
        }
    }

    public void stop() {
        try {
            this.server.stop();
        } catch (Exception ex) {
            this.logger.error(ex);
        }
    }

    public static HttpServerApp getInstance() {
        if (instance == null) {
            instance = new HttpServerApp();
        }
        return instance;
    }

    public static void main(String[] args) {
        getInstance().start();
        Thread t = new Thread(new AppJob());
        t.start();
    }

    public void onStart(FileAlterationObserver observer) {
    }

    public void onDirectoryCreate(File directory) {
    }

    public void onDirectoryChange(File directory) {
    }

    public void onDirectoryDelete(File directory) {
    }

    public void onFileCreate(File file) {
    }

    public void onFileChange(File file) {
        if (file.getName().startsWith(".")) {
            return;
        }
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(HttpServerApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        HttpServerConfig.getInstance().reload();
        this.logger.info("Configuration Change Detected. Restart now.");
        stop();
        this.logger.info("HttpServer Stopped");
        start();
        this.logger.info("HttpServer Started");
    }

    public void onFileDelete(File file) {
    }

    public void onStop(FileAlterationObserver observer) {
    }

    /**
     * @return the context
     */
    public WebAppContext getContext() {
        return context;
    }
}

/* Location:           C:\Users\c1avie\Desktop\SH\nisp.commons.httpserver\lib\nisp.commons.httpservice-1.0-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.nisp.commons.httpservice.HttpServer
 * JD-Core Version:    0.5.3
 */
