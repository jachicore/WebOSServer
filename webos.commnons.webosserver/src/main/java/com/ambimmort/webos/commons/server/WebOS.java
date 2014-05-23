package com.ambimmort.webos.commons.server;

import com.ambimmort.webos.commons.libmanager.LibLoader;
import com.ambimmort.webos.commons.libmanager.MyClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public final class WebOS {

    private static WebOS instance = null;
    private Server server;
    private ServletContextHandler context;
    private MyClassLoader classLoader = null;

    public static WebOS getInstance() {
        if (instance == null) {
            instance = new WebOS();
        }
        return instance;
    }

    private WebOS() {
        this.server = new Server(8999);

        this.context = new ServletContextHandler(1);
        this.context.setContextPath("/");
        this.classLoader = new MyClassLoader(WebOS.class.getClassLoader(), "lib");
        //this.classLoader2 = new PluginsClassLoader(this.context.getClassLoader(), "plugins");
        this.context.setClassLoader(this.classLoader);
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[]{this.context});
        this.server.setHandler(this.context);
        try {
            //classLoader.loadClass("test.app.ClassLoaderTest").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ServletContextHandler getContext() {
        return this.context;
    }

    public void addServlet(Class<? extends HttpServlet> aClass, String url) {
        this.context.addServlet(new ServletHolder(aClass), url);
    }

    public void start() {
        try {
            LibLoader.getInstance().setClassLoader(this.classLoader);
            LibLoader.getInstance().init();
            this.server.start();
            this.server.join();
        } catch (Exception ex) {
            Logger.getLogger(WebOS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        WebOS app = getInstance();

        app.start();
    }
}

/* Location:           F:\nisp.webos.server-1.0-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.webos.commons.server.WebOS
 * JD-Core Version:    0.5.3
 */
