/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package service;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author SuMario
 */
public class Protect {
    public int debug=0;
    
    Protect() {
        
    }
    
    private boolean configured=false;
    private String user  = "";
    private String pass  = "";
    private URL    url   = null;
    
    private URL    proxy  = null;
    private String puser  = "";
    private String ppass  = "";
    private String userAgent=null;
    private boolean useAgent=false;
    private String userProfile="none";
    
    public boolean  isConfigured() { return this.configured; }
    public boolean setConfigured(boolean b){ this.configured=b; return isConfigured(); }
    public void    reset(){ this.configured=false; }
    public String getUser() { return this.user; }
    public String getPassword() { return this.pass; }
    public String getUrl() { return this.url.toString(); }
    public URL    getConnectUrl() throws MalformedURLException { return url;}
    public URL    getProxy(){ return proxy; }
    public String getUserAgent() { return ((useAgent)?userAgent:null); }
    public void   setUserAgent(String ua){ this.userAgent=ua; }
    public void   useUserAgent(boolean b) {this.useAgent=b;}
    public void   setUserProfile(String ua ) { this.userProfile=(ua!=null && ! ua.isEmpty())?ua.toLowerCase():"none"; }
    public String getUserProfile(){ return this.userProfile; }
    public boolean isUserProfile(String ua) { return ( ua !=null && ua.toLowerCase().matches(this.userProfile) )?true:false; }
   
    public void    setUser(    String u) { 
        if ( err == null ) { err=new StringBuilder(); }
        this.user=(u!=null)?u:"";
        err.append("INFO: like to add user "+((!user.isEmpty())?user:"null or empty") );
    }
    public void    setPassword(String u) { 
        if ( err == null ) { err=new StringBuilder(); }
        this.pass=(u!=null)?u:"";
        err.append("INFO: like to add password "+((!pass.isEmpty())?"******":"is null or empty") ); 
    }
    public boolean setUrl(     String u) { 
        boolean b=false;
        if ( err == null ) { err=new StringBuilder(); }
        try {
            err.append("INFO: checking ").append( ((u==null || u.isEmpty())?"NULL":u) ).append("\n");
            URL uri = new URL(u);
            this.url=uri; 
            b=true;
            
        } catch (Exception ex) {  
            err.append("ERROR: url ");
            err.append( ((u!=null && !u.isEmpty())?u:"null") ); 
            err.append(" for user ");
            err.append( (user==null || user.isEmpty())?"NONE":user );
            err.append( (pass==null || pass.isEmpty())?"":" and password ******");
            err.append(" are invalid  error : ").append(ex.getMessage()); 
        } finally {
             return b;
        }
    }
    public boolean setProxy(     String u) { 
        boolean b=false;
        if (   u == null ) { proxy=null; return !b; }
        
        if ( err == null ) { err=new StringBuilder(); }
        try {
            err.append("INFO: checking proxy ").append( ((u==null || u.isEmpty())?"NULL":u) ).append("\n");
            URL uri = new URL(u);
            this.proxy=uri; 
            b=true;
         } catch (Exception ex) { 
            err.append("ERROR: proxy url ").append( ((u!=null && !u.isEmpty())?u:"null") ).append(" - ").append(ex.getMessage()); 
         }  finally {  
            return b;
        }   
    }
    
    public void   setProxyUser(String     u) { this.puser=(u==null)?"":u; }
    public String getProxyUser(            ) { return this.puser; }
    public void   setProxyPassword(String u) { this.ppass=(u==null)?"":u; }
    public String getProxyPassword(        ) { return this.ppass; }
    
    StringBuilder err=null;
    public String getErrorMsg(){ return err.toString(); }
    
    static public Protect getInstance() {
         Protect p = new Protect();
         return p; 
    }
}
