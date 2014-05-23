/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author SH
 */
public class Test {

    private static String getName(String path) {
        String name = path.substring(path.lastIndexOf("/") + 1, path.length());
        name = name.lastIndexOf(".") == -1 ? name : name.substring(0, name.lastIndexOf("."));
        return name;
    }

    public static void main(String[] args) {
        String test = "%2525E5%2525A8%2525B4%2525E5%2525AC%2525AD%2525E7%252598%2525AF";
        //String a = "YW9zOi8vL3N5c3RlbS9kZXNrdG9wcy9kZWZhdWx0L+aymeaBki5qcGc=";
        //"YW9zOi8vL3N5c3RlbS9kZXNrdG9wcy9kZWZhdWx0L+aymeaBki5qcGc="
        //URLDecode(a);
        //URLDecode.
        
        try {
            //String file = java.net.URLDecoder.decode(a, "utf-8");
            //String b = "YW9zOi8vL3N5c3RlbS9kZXNrdG9wcy9kZWZhdWx0L%2BaymeaBki5qcGc%3D";
            String rst = java.net.URLDecoder.decode(java.net.URLDecoder.decode(java.net.URLDecoder.decode(test,"utf-8"),"utf-8"),"utf-8");
            System.out.println(rst);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
//        System.out.println();
////        JSONArray arr = JSONArray.fromObject(test);
////        arr.remove("admin");
//        System.out.println(getName(test));
    }
}
