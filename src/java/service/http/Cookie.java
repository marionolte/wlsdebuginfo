/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package service.http;

/**
 *
 * @author SuMario
 */
public class Cookie {
     static int debug=0;
     private final String name ;
     private final String value;
     private boolean cook2=false;
     private boolean cookieSet=false;
     private boolean httpOnly=false;
     private boolean httpSecure=false;
     private String  httpPath=null;
     private String  httpDomain=null;
     private Long    expires=0L;
     private boolean error=false;
     private boolean ExpireError=false;
     
     
     Cookie(String cook) {
         this.debug=Cookie.debug;
         // DCCCtxCookie_myhost.example.com:7777=value; httponly; secure; path=/oam/server/auth_cred_submit; domain=.example.com
         String[] sp = cook.split(";");
         String[] fp = sp[0].split("="); 
         name = fp[0]; value = sp[0].substring(name.length()+1);
         if ( sp.length > 1 ) {
            cookieSet=true; 
            for (int i=1; i<sp.length; i++ ) {
                 log(3, " cookie info =>"+sp[i]+"<=");
                 fp = sp[i].split("="); 
                 final String f=fp[0].replaceFirst(" ", "").toLowerCase();
                 if( f.length()+1 < sp[i].length() ) {
                     log(2, " cookie valued =>"+sp[i]+"<=");
                     if      ( f.matches("domain") ) { httpDomain=sp[i].substring(fp[0].length()+1); }
                     else if ( f.matches("path")   ) { httpPath  =sp[i].substring(fp[0].length()+1); }
                     else if ( f.matches("port")   ) { cook2=true;
                     }
                     else if ( f.matches("expires")) {
                         
                     }
                     else if ( f.matches("max-age") ) {
                         try {
                           final String p = sp[i].substring(fp[0].length()+1);
                           log(3, "max-age value =>"+p+"<=");
                           long l = Long.parseLong(p);
                           expires=System.currentTimeMillis()+l;
                         } catch( NumberFormatException nfe ) {
                           error=true;  ExpireError=true;
                         } 
                     }
                 
                 } else {
                     log(2, " cookie flag =>"+sp[i]+"<=");
                     if      ( f.matches("secure")   ) { this.httpSecure=true;}
                     else if ( f.matches("httponly") ) { this.httpOnly=true; }
                 }
            }
         }   
         
         log(2, "Cookie are:"+toString());
     }
     
     public boolean isExpired() {
         if ( error || System.currentTimeMillis() > this.expires ){ return true;}
         return false;    
     }
     @Override
     public String toString() {
         return    ((cookieSet)? ((cook2)?"Set-Cookie2: ":"Set-Cookie: "):"")
                   + name+"="+value+((!cookieSet)?"":""
                   + ((httpOnly)?"; httponly":"")
                   + ((httpSecure)?"; secure":"")
                   + ((httpPath != null )?"; path="+httpPath:"")
                   + ((httpDomain != null )?"; domain="+httpDomain:"")
                 );
     }
    
     private void log(final int level, String msg) {
       if ( debug >= level  ) {
           if ( level > 0 ) { msg="DEBUG("+level+"/"+ debug +") Cookie:: =>"+msg; }
           System.out.println(msg);
       } 
    }
}
