/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package service.http;

import java.util.HashMap;

/**
 *
 * @author SuMario
 */
public class UserAgent {
    private final HashMap<String,String> map;
    private UserAgent() {
        debug=UserAgent.debug;
        map = new HashMap<String,String>();
    }
    private String get(String k          ) { String f=map.get(k); return (f==null)?"":f; }
    private String set(String k, String v) { map.put(k,v);        return get(k);         }
    
    
    public String getBrowser()        {  return get("browser"); }
    public String getBrowserVersion() {  return get("browserversion"); }
    public String getCompat()         {  return get("compat"); }
    public String getCompatVersion()  {  return get("compatversion"); }
    public String getJSBrowser()      {  return get("jsbrowser"); }
    public String getJSVersion()      {  return get("jsbrowserversion"); }
    
    private void  setBrowser(String s) { 
         log(2, "get to set =>"+s+"<=");
        if ( s==null || s.isEmpty() ) { return; }
        String[] sp = s.split("/");
        if ( sp[0].matches("Gecko") ) { set("jsbrowser", sp[0]); set("jsbrowserversion",sp[1]);  } else {
            log(1,"set "+sp[0]+" version="+sp[1]);
            if ( getCompat().isEmpty()) {
                 set("browser"       , set("compat",       sp[0]));
                 set("browserversion", set("compatversion",sp[1]));
            } else {
                 set("browser"       , sp[0]);
                 set("browserversion", sp[1]);
            }
        }
    }
    
    public String getPlatform()        {  return get("platform"); }
    public String getPlatformArch()    { return ( get("wow").contains("64") )?"64":"32";}
    public String getPlatformVersion() { 
        String f=get("platformversion"); 
        
        if      ( f.matches("NT4.0") ) {f="WindowsNT";}
        else if ( f.matches("NT5.0") ) {f="Windows2000";}
        else if ( f.matches("NT5.1") ) {f="WindowsXP";}
        else if ( f.matches("NT5.2") ) {f="Windows2003";}
        else if ( f.matches("NT6.0") ) {f="Windows2008";}
        else if ( f.matches("NT6.1") ) {f="Windows7";}
        else if ( f.matches("NT6.2") ) {f="Windows8";} 
        else if ( f.matches("NT6.3") ) {f="Windows8.1";}
        else if ( f.matches("NT6.4") ) {f="Windows9";}
        else if ( f.matches("NT6.5") ) {f="Windows10";}
        
        return f;
    
    }
    
    private void setPlatform(String s) {
        if ( getPlatform().isEmpty() ) { set("platform",s); } else {
             if        ( s.startsWith("WOW") ) { set("wow",s); 
             } else if ( s.startsWith("rv:") ) { set("rev",s);  
             } else {
                set("platformversion", get("platformversion")+s );
             }   
        }
    }
    
    public synchronized static UserAgent check(String s) {
        UserAgent ua=new UserAgent();
        
                  ua.log(2, "check useragent ==>"+s+"<==");
                  String[] sp = s.split("[ ,(,),;]");
                  for ( int i=0; i< sp.length; i++ ) { 
                     if ( ! sp[i].isEmpty() ) { 
                        if ( sp[i].contains("/") ) { ua.setBrowser(sp[i]); } else { ua.setPlatform(sp[i]);}
                        ua.log(2,"["+i+"]="+sp[i]+"|");
                     }
                  }
                  ua.log(1, "browser =>"+ua.getBrowser()+"<= version="+ua.getBrowserVersion()+"|\n"
                           +"typ     =>"+ua.getCompat()+"<=  version="+ua.getCompatVersion()+"|\n"
                           +"JScript =>"+ua.getJSBrowser()+"<= version="+ua.getJSVersion()+"|\n"
                           +"Platform =>"+ua.getPlatform()+"<= version="+ua.getPlatformVersion()+"| architectur="+ua.getPlatformArch()+"|\n"
                  );
        
        return ua;
    }
    
    public boolean isEqual(UserAgent ua) {
        boolean b=false;
        if (    ua.getBrowser(        ).equals(this.getBrowser() )
             && ua.getBrowserVersion( ).equals(this.getBrowserVersion())
             && ua.getCompat(         ).equals(this.getCompat())
             && ua.getCompatVersion(  ).equals(this.getCompatVersion())
             && ua.getJSBrowser(      ).equals(this.getJSBrowser())
             && ua.getJSVersion(      ).equals(this.getJSVersion())
             && ua.getPlatform(       ).equals(this.getPlatform())
             && ua.getPlatformArch(   ).equals(this.getPlatformArch())
             && ua.getPlatformVersion().equals(this.getPlatformVersion())
           ) {
           b=true; 
        }
        return b;
    }
    
    public static int debug=0;
    private void log(final int level, String msg) {
       if ( debug >= level  ) {
           if ( level > 0 ) { msg="DEBUG("+level+"/"+ debug +") =>"+msg; }
           System.out.println(msg);
       } 
    }
}
