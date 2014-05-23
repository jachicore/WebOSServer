package com.ambimmort.webos.dmcClient.filesystem.utils;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public class CodeUtils {

    public CodeUtils() {
    }

    public synchronized String getEncode(File f) {
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();

        detector.add(new ParsingDetector(false));

        detector.add(JChardetFacade.getInstance());

        detector.add(ASCIIDetector.getInstance());

        detector.add(UnicodeDetector.getInstance());
        Charset charset = null;
        try {
            charset = detector.detectCodepage(new BufferedInputStream(new FileInputStream(f)), 100);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (charset != null) {
            return charset.name();
        }

        return "unknown";
    }

    public synchronized String getEncode(InputStream f) {
        Charset charset = null;
        try {
            CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();

            detector.add(new ParsingDetector(false));

            detector.add(JChardetFacade.getInstance());

            detector.add(ASCIIDetector.getInstance());

            detector.add(UnicodeDetector.getInstance());

            charset = detector.detectCodepage(new BufferedInputStream(f), 100);
        } catch (Throwable ex) {
            ex.printStackTrace();
            return "unknown";
        }
        if (charset != null) {
            return charset.name();
        }

        return "unknown";
    }

    public synchronized String getEncode(String f) {
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();

        detector.add(new ParsingDetector(false));

        detector.add(JChardetFacade.getInstance());

        detector.add(ASCIIDetector.getInstance());

        detector.add(UnicodeDetector.getInstance());
        Charset charset = null;
        try {
            charset = detector.detectCodepage(new BufferedInputStream(new ByteArrayInputStream(f.getBytes())), 100);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (charset != null) {
            return charset.name();
        }
        return "unknown";
    }
}

/* Location:           F:\nisp.webos.server-1.0-SNAPSHOT.jar
 * Qualified Name:     com.ambimmort.dmcClient.filesystem.utils.CodeUtils
 * JD-Core Version:    0.5.3
 */
