package com.ambimmort.webos.commons.dmc;

import com.ambimmort.webos.commons.libmanager.LibLoader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.http.HttpSession;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

public class DMCInterpretor {

    private static DMCInterpretor instance = null;

    public String doDMCClassService(String className) {
        try {
            Class cl = LibLoader.getInstance().classForName(className);
            if (cl == null) {
                return null;
            }
            JSONObject obj = new JSONObject();
            obj.put("className", cl.getName());
            JSONObject methods = new JSONObject();
            for (Method m : cl.getMethods()) {
                JSONObject mo = new JSONObject();
                mo.put("returnType", m.getReturnType().getName());
                JSONArray ja = new JSONArray();
                for (Class pc : m.getParameterTypes()) {
                    ja.add(pc.getName());
                }
                mo.put("parameterType", ja);
                methods.put(m.getName(), mo);
            }
            obj.put("methods", methods);

            return obj.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
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

    public Object doDMCService(String name, String methodName, String content, HttpSession session) {
        if (content == null) {
            System.out.println("null");
            return "dodmc";
        }
        JSONObject obj = null;
        try {
            obj = JSONObject.fromObject(content);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        String v = new String(Base64.decodeBase64(obj.getString("values")));
        try {
            Class cl = LibLoader.getInstance().classForName(name);
            if (cl == null) {
                return name + "not found";
            }
            Class iHttpSessionAware = LibLoader.getInstance().classForName(IHttpSessionAware.class.getName());
            Class iSessionable = LibLoader.getInstance().classForName(ISessionable.class.getName());
            JSONArray array = obj.getJSONArray("parameterType");
            JSONArray objs;
            try {
                objs = JSONArray.fromObject(v);
            } catch (Exception ex) {
                objs = new JSONArray();
                System.out.println("0"+ex.getMessage());
                return ex.getMessage();
            }

            Class[] pcs = new Class[array.size()];
            Object[] argsvalue = new Object[objs.size()];
            int i;
            for (i = 0; i < array.size(); ++i) {
                pcs[i] = Class.forName(array.getString(i));
            }
            for (i = 0; i < objs.size(); ++i) {
                argsvalue[i] = objs.get(i);
            }
            Method m = null;
            try {
                Object to = cl.newInstance();
                boolean flag = false;
                boolean flag_session = false;

                for (Class cls : cl.getInterfaces()) {
                    if ("com.ambimmort.webos.commons.dmc.IHttpSessionAware".equals(cls.getName())) {
                        flag = true;
                    }
                    if ("com.ambimmort.webos.commons.dmc.ISessionable".equals(cls.getName())) {
                        flag_session = true;
                    }
                }

                if (flag) {
                    m = iHttpSessionAware.getDeclaredMethod("bind", new Class[]{HttpSession.class});
                    m.invoke(to, new Object[]{session});
                }
                if (flag_session) {
                    if (session.getAttribute("username") != null) {
                        m = iSessionable.getDeclaredMethod("injectSession", new Class[]{HttpSession.class});
                        m.invoke(to, new Object[]{session});
                    }
                    else{
                        return null;
                    }

                }
                m = to.getClass().getDeclaredMethod(methodName, pcs);
                m.setAccessible(true);
                Object rt = m.invoke(to, argsvalue);
                if (rt == null) {
                    return "{}";
                }
                return rt;
            } catch (Exception er) {
                er.printStackTrace();
                System.out.println( "1" + er.getMessage() + m.getName() + "methodname:" + methodName + "args:" + pcs.length + ":" + pcs.toString() + " class:" + cl.getName() + " content:" + content);
            } finally {
            }
        } catch (SecurityException ex) {
            Logger.getLogger(DMCService.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println( "2" + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DMCService.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println( "3" + ex.getMessage());
        }
        return null;
    }

    public static DMCInterpretor getInstance() {
        if (instance == null) {
            instance = new DMCInterpretor();
        }
        return instance;
    }
}

/* Location:           F:\nisp.webos.server-1.0-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.webos.commons.dmc.DMCInterpretor
 * JD-Core Version:    0.5.3
 */
