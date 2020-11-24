/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package service;

import comm.Http;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.activation.MimetypesFileTypeMap;
import service.http.Header;

/**
 *
 * @author SuMario
 */
public class Tester extends Version implements Runnable {
    private Thread th;
    private final Protect prot; 
    private ArrayList<Header> map;
    //public int debug=0;
    
    public Tester(Protect prot) {
        this.prot = prot;
        this.map  = new ArrayList<Header>();
        this.debug=prot.debug;
    }

    final private String headerLock="header lock";
    public ArrayList<Header> getHeaders() {
        synchronized ( headerLock ) {
            ArrayList<Header> ret = new ArrayList<Header>();
            if ( map.size() >0 )
                 for(int i=0; i<map.size(); i++) { ret.add(map.get(i)); }
            return ret;
        }
    }
    
    private boolean completed=false;
    private boolean running=false;
    public int count;
    public int poll;
    public synchronized void start() {
        if ( isRunning() && completed ) { return; }
        running=true;
        headerClean();
        th= new Thread(this, "http tester");
        th.start();
        count=poll=0; 
        
    }
    public void reset() { completed=false; closed=true; 
      while(runs) {  try { Thread.sleep(300); }catch(InterruptedException e){} }
      headerClean(); h=null;
    }
    
    private void headerClean() {
      synchronized ( headerLock ) {
           map = new ArrayList<Header>();
      }
    }
    public boolean isCompleted() { return completed; }
    
    public void close() { closed=true; }
    public boolean isClosed() { return closed; }
    public boolean isRunning() { return ( !isClosed() && running)?true:false; }
    private boolean closed=false;
    
    private boolean runs=false;
    
    public boolean isConfigured(){ return prot.isConfigured(); }
    
    public StringBuilder err= new StringBuilder();
    private URL url=null;  
    private Http h = null ;
    @Override
    public void run() {
            count++;
            runs=true;
            closed=false;
            String user = prot.getUser();
            String pass = prot.getPassword();
            int _auth=-1;
            if ( err == null ) { err = new StringBuilder(); }
            synchronized ( headerLock ) {
                        // map = new ArrayList<Header>();
            }
        
        try {
            do { 
              try { Thread.sleep(300); }catch(InterruptedException e) {}
            } while( ! isConfigured() );
            url     = prot.getConnectUrl();
            do {
                 try {
                    if ( h == null ) {  h = new Http(url); h.setFollowRedirect(false); 
                                        h.setDebug(debug); 
                                        h.setUserAgent(prot.getUserAgent());
                                        if ( prot.getProxy() != null ) {
                                            h.setProxy(prot.getProxy());
                                            h.setSSLProxy(prot.getProxy());
                                            h.setProxyUser(prot.getProxyUser());
                                            h.setProxyPassword(prot.getProxyPassword());
                                        }
                                        h.connect(); 
                    } else { 
                                        h.connect(url);
                    } 
                 } catch (java.io.IOException io ) {
                     if ( io.getMessage().contains(": 401"))  {
                        h.status=401;
                        err.append("INFO:  url:"+url+" request authentication "+h.status);
                        System.out.println("INFO: 401 ");
                     } else {
                        h.status=500;  
                        err.append("ERROR:  Exception thrown with message : "+io.getMessage() +"");
                     }   
                 }   
                    poll++;
                    addHea(new Header(h));
                    
                    
                    if (h.status == 200 ) { 
                        URL ur=h.getLocation();
                        if ( ur != null &&  prot.isUserProfile("OAMLdap") ) {
                             err.append("\nERROR: server set 200 with Location to "+ur.toString()+"\nINFO: step over to Location "+ur);
                             url=ur;
                        } else {
                            close(); completed=true; 
                            if ( ur == null ) {
                                err.append("\nINFO: step completed with 200");
                            } else {
                                err.append("\nERROR: server set 200 with Location to "+ur.toString());
                            }    
                        }
                    }
                    else if (h.status == 301 || h.status == 302 ) {  url=h.getLocation(); err.append("\nINFO: redirect to "+url);} 
                    else if (h.status == 401 ) { 
                        _auth++;
                        if ( debug > 0 ) { err.append("auth count:"+_auth+" user:"+prot.getUser()+"\n");}
                        if ( _auth < 3 && ! prot.getUser().isEmpty() ) {
                        //throw new RuntimeException("401 seen");
                            String[] t= h.getHeader(new String[] {"Authenticate", "WWW-Authenticate"} ) ;
                            if ( t == null || t.length == 0 || t[0].isEmpty() ) {
                                err.append("\nERROR: no authentication request found");
                            } else {
                                err.append("\nINFO:  authentication are :"+t[0]+":");
                            }
                            for ( int i=0; i < t.length; i++ ) {
                                String[] sp = t[0].split(":");
                                h.setAuthRealm( t[0].substring(sp[0].length()+2) );
                                h.setAuthRealmHeader(sp[0]);
                            }    
                                h.setAuthentication( getBase64(prot.getUser()+":"+prot.getPassword())); 
                        } else {
                            close();
                            completed=true; err.append("\nERROR: authentication do not work for user:"+prot.getUser()+":");
                        }
                    }
                    else { close(); completed=true; err.append("\nINFO: step completed with unhandled "+h.status); }                   
                
            } while( ! closed );
            
            
        } catch (Exception ex) {
          err.append("ERROR:  Exception thrown with message : "+ex.getMessage() ); 
          StringWriter sw = new StringWriter();
          ex.printStackTrace(new PrintWriter(sw));
          err.append(sw.toString()+"\n\n");
          try { 
              addHea(new Header(h)); 
          }catch(java.io.IOException io) {
              err.append("ERROR:  Exception thrown by adding Header : "+io.getMessage() );
          }
        }
        if ( debug > 0 ) {
            System.out.println("BEGIN RUNNING\n"+err.toString()+"\nEND RUNNING");
        }
        closed=true;
        completed=true;
        runs=false;
    }
    
    private void addHea(Header hea) {
        synchronized ( headerLock ) {
                         map.add(hea);
        }
    }
    private String getBase64(String a) { return getBase64( a.getBytes() ); }
    private synchronized String getBase64(byte[] a) { return new String( io.Base64.encodeBase64(a) ) ; }
    
    
    public static void main(String[] args ) throws Exception {
         Protect p =  Protect.getInstance() ;
         
         for ( int i=0; i <args.length; i++) {
              if ( args[i].matches("-u") ) { p.setUser(args[++i]); }
              else if ( args[i].matches("-p") ) { p.setPassword(args[++i]); }
              else if ( args[i].matches("-d") ) { p.debug++; } 
              else if ( args[i].matches("-proxy") ) { p.setProxy(args[++i]); }
              else if ( args[i].matches("-pu")    ) { p.setProxyUser(args[++i]); }
              else if ( args[i].matches("-pw")    ) { p.setProxyPassword(args[++i]); }
              else if ( args[i].matches("-dhttp") ) { Http.setHttpTrace( new BufferedOutputStream( new  FileOutputStream( args[++i] ) )  );}
              else {
                  p.setUrl( args[i]); 
              }
         }
         System.out.println("BEGIN CONFIG\n"+p.err.toString()+"\nEND CONFIG");
         if ( p.getUrl() != null ) {
            Http.debug = p.debug; 
            Tester t = new Tester(p); 
            ArrayList<Header> ar;
                   t.start();

                   int i=0;
                   while ( ! t.isCompleted() ) {
                        ar = t.getHeaders(); i=0;
                        while( ar.size() >0 ) {
                            Header h = ar.remove(0);
                            System.out.println("######### BEGIN HEADER "+(i+1)+" #########  NONE CLOSED");
                            System.out.println(h.toString());
                            System.out.println("########### END HEADER "+(i+1)+" #########  NONE CLOSED");
                            i++;
                        }
                   }

                   ar = t.getHeaders(); i=0;
                   while( ar.size() >0 ) {
                       Header h = ar.remove(0);
                       System.out.println("######### BEGIN HEADER "+(i+1)+" ######### CLOSED");
                       System.out.println(h.toString());
                       System.out.println("########### END HEADER "+(i+1)+" ######### CLOSED");
                       i++;
                   }
         }          
    }
    
    synchronized public byte[] getContext(String url) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        if ( url == null || url.isEmpty() ) {
            return "<HTML><HEAD><TITLE>ERROR - no resource</TITLE></HEAD><BODY>ERROR - no resource </BODY></HTML>\n".getBytes("UTF-8");
        }
        if ( debug >0 && h==null ) {  Http.debug=debug; h=new Http(); }
        Http ht =  h.getClone();
        byte[] b = ht.getByteData(URLDecoder.decode(url, "UTF-8"));
        return  b;
    }
    
    synchronized public String getUrlName(String url) {
        String[] sp = url.split("\\?"); 
                 sp = sp[0].split("\\/");
                 return sp[ sp.length-1];
    }
    
    private  MimetypesFileTypeMap mime = new MimetypesFileTypeMap();
    synchronized public String getMimeTyp(String url) throws UnsupportedEncodingException {
        return mime.getContentType( getUrlName( URLDecoder.decode(url, "UTF-8") ) );
        //return "applicaton/octet-stream; UTF-8";
    } 
}
