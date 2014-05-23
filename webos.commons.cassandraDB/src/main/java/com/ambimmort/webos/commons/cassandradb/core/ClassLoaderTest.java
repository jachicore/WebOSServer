/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.webos.commons.cassandradb.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author SH
 */
public class ClassLoaderTest {

    private ClassLoader classLoader;

    public ClassLoaderTest() throws Exception {
        System.out.println("int test~~~~~~~~");
        configurePlugins();
    }

    protected final void configurePlugins()
            throws Exception {
        ClassLoader cl = findClassLoader();
        Class c =cl.loadClass("com.amibimort.test.LibTest");
        if (c != null) {
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++Sucess load class:" + c.getName());
        }
        Enumeration enumResources;
        try {
            System.out.println(cl.getClass().getName());
            System.out.println(cl.getResource(""));
            System.out.println(cl.getResource("META-INF/vfs-providers.xml").toURI().toString());
            enumResources = cl.getResources("META-INF/vfs-providers.xml");
        } catch (Exception e) {
            throw new Exception(e);
        }
        while (enumResources.hasMoreElements()) {
            System.out.println("has elements~~~~~~~~");
            URL url = (URL) enumResources.nextElement();
            configure(url);
        }
    }

    private void configure(URL configUri)
            throws Exception {
        InputStream configStream = null;
        try {
            DocumentBuilder builder = createDocumentBuilder();
            configStream = configUri.openStream();
            Element config = builder.parse(configStream).getDocumentElement();

            configure(config);
        } catch (Exception e) {
        } finally {
            if (configStream != null) {
                try {
                    configStream.close();
                } catch (IOException e) {
                    //getLogger().warn(e.getLocalizedMessage(), e);
                }
            }
        }
    }

    private void configure(Element config)
            throws Exception {
        NodeList providers = config.getElementsByTagName("provider");
        int count = providers.getLength();
        for (int i = 0; i < count; ++i) {
            Element provider = (Element) providers.item(i);
            addProvider(provider, false);
        }

    }

    private void addProvider(Element providerDef, boolean isDefault)
            throws Exception {
        String classname = providerDef.getAttribute("class-name");
        System.out.println("SUCCESS INIT Classname" + classname + "!!~~!!!!!!!~~~~~~~~~~~!!!!");
    }

    private DocumentBuilder createDocumentBuilder()
            throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);
        factory.setExpandEntityReferences(true);
        return factory.newDocumentBuilder();
    }

    private ClassLoader findClassLoader() {
        if (this.classLoader != null) {
            return this.classLoader;
        }
        System.out.println("=====================================================");
        System.out.println(Thread.currentThread().getContextClassLoader().getClass().getName());
        System.out.println(super.getClass().getClassLoader().getClass().getName());
        //ClassLoader cl = super.getClass().getClassLoader();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = super.getClass().getClassLoader();
        }
        System.out.println(cl.getClass().getName());
        return cl;
    }
}
