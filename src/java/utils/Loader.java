/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.net.URL;
import java.net.URLClassLoader;
import javax.crypto.SecretKeyFactory;

/**
 *
 * @author SuMario
 */
public class Loader {
   private static ClassLoader sys;
   private static StringBuilder sw;
   
   public static javax.crypto.SecretKeyFactory getSecretKeyFactoryInstance() { return getSecretKeyFactoryInstance(""); }
   public static javax.crypto.SecretKeyFactory getSecretKeyFactoryInstance(String cryptname) {
       if ( cryptname == null || cryptname.isEmpty() ) { cryptname="PBEWITHMD5ANDDES"; }
       sw = new StringBuilder();
       javax.crypto.SecretKeyFactory kf;
       javax.crypto.SecretKeyFactory kfcl;
       URLClassLoader u;
       String myclass = "javax.crypto.SecretKeyFactory"; 
       try {
        kfcl = (javax.crypto.SecretKeyFactory)  loadClass(sys, myclass);  
        
        if (kfcl == null ) {
            u = new URLClassLoader( new URL[] { new URL("WEB-INF/sunjce_provider.jar") } );
            kfcl = (SecretKeyFactory) loadClass(u,myclass);
        }
        kf = kfcl.getInstance( cryptname , "SunJCE" );
        
       } catch(Exception e) {
           sw.append("ERROR: "+e.getMessage()+"\n");
            e.getStackTrace();
           kf=null;
       } 
       
       return kf;
   }
   
   public String getMessage() {
       return (sw==null) ? "no messages" : sw.toString(); 
   }
   
   static private Object loadClass(ClassLoader c, String name) {
       try {
        return c.loadClass(name);
       }catch(Exception e ) {  return null; }
   } 
   
   static private Object loadClass(URLClassLoader c, String name) {
       try {
        return c.loadClass(name);
       }catch(Exception e ) {  return null; }
   } 
   
   static {
      sys = ClassLoader.getSystemClassLoader();   
   } 
    
}
