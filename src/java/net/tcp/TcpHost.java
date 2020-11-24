/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.tcp;

import comm.Http;
import general.Version;
import io.file.ReadFile;
import static io.lib.IOLib.execReadToString;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;


/**
 *
 * @author SuMario
 */
public abstract class TcpHost extends Version{
    
    //public TcpHost() { this(null,"TcpHost");}
    //public TcpHost(String[] args,String name) { super(args,name); init(); }
    
    private void init() {
        StringBuilder sw = (new ReadFile("/etc/service")).readOut();
    }
    
    public boolean isBoolean(String k) {
         return (k!= null && ( k.toLowerCase().equals("true") || k.toLowerCase().equals("false") ));
    }
    
    public int isInteger(String k) {
        int i=-1;
        try {
          i = Integer.parseInt(k);
          return i;
        } catch(Exception e) {}
        return i;
    }
    
    public boolean isPort(String k) {
        try { 
            int i = isInteger(k);        
            return (i>0 && i<(64*1024-1));
        } catch(Exception e) {}
        return false;
    }
    
    public int getPort(String k) {
        if ( isPort(k) ) return isInteger(k);
        return -1;
    }
    
    public int getDefPort(String k) {
        if ( isPort(k) ) return isInteger(k);
        return -1;
    }
    
    public boolean isHostIp(String k) {
          
       return false; 
    }
    
    
    private HashMap<String,String> map = new HashMap(); 
    private ArrayList<InetAddress> addr=new ArrayList();
    
    public boolean isLocalAddress(String host){
        if ( host == null || host.isEmpty() ) { throw new RuntimeException("NULL is not a valid name for a host"); }
        else if (   host.toLowerCase().equals("loopback") 
                 || host.toLowerCase().equals("localhost")
                 || host.toLowerCase().equals("127.0.0.1")
                 || host.toLowerCase().equals("::1")        ) { return true; }
        
        readHosts();
        if ( map.containsValue(host.toLowerCase())){ 
            System.out.println("host is local");
            return true; }
        
        return false;
    }
    
    private void readHosts() {
        //Pattern pa = Pattern.compile("^"+host+"[\\ ,\\t]", Pattern.CASE_INSENSITIVE);
        if ( ! map.isEmpty() ) { return; }
        ReadFile fa = new ReadFile("/etc/hosts");
        
        for(String s : fa.readOut().toString().split("\n")) {
            if ( s.startsWith("[1-9]") ) {
                 String[] sp =  s.trim().split("[\t, ]");
                 boolean first=true;
                 for (int i=1; i< sp.length; i++) {
                     if ( !sp[i].isEmpty() ) {                         
                         if ( first ) {  map.put(sp[0], sp[i].toLowerCase()); first=false;}
                         map.put(sp[i].toLowerCase(), sp[0]);
                     }
                 }
            }
        }
        
        separateInterfaces();
    }
    
    
    public Enumeration<NetworkInterface> getNetInferfaces() {
        try {
          return NetworkInterface.getNetworkInterfaces();
        } catch(Exception e) {
          return null;  
        } 
    }
    private void separateInterfaces() {
        final String func =getFunc("separateInterfaces()");
        final Enumeration<NetworkInterface> nifs = getNetInferfaces();
        if ( nifs != null ) {
           while (nifs.hasMoreElements()) { 
                NetworkInterface                       nif = nifs.nextElement();
                Enumeration<java.net.InetAddress> addrs = nif.getInetAddresses();
                while ( addrs.hasMoreElements() ) {
                    InetAddress inf = addrs.nextElement();
                    if ( inf.isAnyLocalAddress() ) {
                          String ho = inf.getHostAddress().toLowerCase();  String ip=inf.getHostAddress();
                          System.out.println("host:"+ho+":  ip:"+ip+":");
                          if ( ho == null || ho.isEmpty() ) { ho=ip; }
                          map.put(ho,ip); map.put(ip, ho);
                    }
                }
           }
           
        } else {
          printf(func,1,"no network interfaces found");  
        }
    }
    
    
    public boolean isReachable(String url) throws Exception { return isReachable(new URL(url)); }
    public boolean isReachable(URL    url) throws Exception { 
        Http ht = new Http(url);
             ht.setTimeout(5000);
             ht.connect();
        return ( ht.getResponseCode() <= 500 );
    }
    
    
    public final static String getHostname() { 
       String hostname = "localhost"; 
       try { hostname = InetAddress.getLocalHost().getCanonicalHostName(); return hostname; } catch(Exception e) {}
       try { return execReadToString("hostname"); } catch(java.io.IOException io){ return hostname; }
    }
    
    public final static String getDomainname() { 
       String hostname = getHostname();
       // System.out.println("hostname:"+hostname+" index:"+hostname.indexOf('.') );
       if ( hostname.indexOf(".") > 2 ) {
            String[] sp = hostname.split("\\.");
            return hostname.substring(sp[0].length()+1);
       }
       
       return "";
    }
    
    public final static int getMinPort() { return 1; }

    public final static int getMaxPort() { return 64*1024-1; }
    
}
