/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.tools;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import java.util.ArrayList;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author SuMario
 */
public class PortScanner {
    public final String host;
    private final int maxport=65365;
    private final int minport=1;
    private int maxScanPort=10000;
    private int minScanPort=1;
    
    public PortScanner(String host) {
        String ho="";
        try {
            URL u = new URL(host);
                ho=u.getHost();
        }catch(MalformedURLException me) {
            ho=host;
        }
        this.host=ho;
    }
    
    private int getPort(String p, int def){
        int ret=def;
        try {
          int a = Integer.parseInt(p);
          if ( a >= minport && a <= maxport ) {  ret=a; } 
        }catch(Exception e) {}  
        return ret;
    }
    
    public int setMaxPort(String p) {  return maxScanPort=getPort(p,maxScanPort);}
    public int setMinPort(String p) {  return minScanPort=getPort(p,minScanPort);}
    public int getMaxPort()         {  return this.maxScanPort; }
    public int getMinPort()         {  return this.minScanPort; }
    public int setMaxScanPort(int p) { return maxScanPort=(p>=minport&&p<=maxport && p>minScanPort)?p:maxScanPort; }
    public int setMinScanPort(int p) { return minScanPort=(p>=minport&&p<=maxport && p<maxScanPort)?p:minScanPort; }
    
    public InetAddress resolve() {
        InetAddress a=null;
        try {
            a =  InetAddress.getByName(host);
        } catch (UnknownHostException ex) {
            a=null;
        }
        
        return a;
    }
    public long getRunTime(){ return endTime-startTime; }
    public StringBuilder getInfo(){ return info; }
    public StringBuilder getError(){ return error; }
    
    StringBuilder info;
    StringBuilder error;
    long startTime=0L;
    long endTime=0L;
    public void test(){
        info=new StringBuilder(); error=new StringBuilder();
        InetAddress a = resolve();        
        if ( a != null ) {
            int d=minScanPort;
            if ( minScanPort > maxScanPort ) { d=maxScanPort; maxScanPort=minScanPort; minScanPort=d; }
            ArrayList<PStest> ar = new ArrayList<PStest>(); int i=minScanPort;  
            startTime=System.currentTimeMillis(); endTime=startTime;
            PStest pt;
            do {
              i=i+25;  
              if (i>maxScanPort){ i=maxScanPort; }
              log("start new thread for min:"+d+" to max:"+i+" to host"); 
              pt = new PStest(d,i,host);
              ar.add(pt); pt.start();
              if ( ar.size() > 200 ) {
                   pt=ar.remove(0);  
                   log("remove one thread ");
                   info.append(pt.getResult().toString());
                   log("info collected");
              }
              d=i+1;
            } while(i<maxScanPort);
            if ( ar.size() > 0 ) {
                do {
                  pt=ar.remove(0);  info.append(pt.getResult().toString()); 
                } while(ar.size() > 0 );
            }
            /*for (int i=minScanPort; i<=maxScanPort; i++ ) {
               try { 
                    PScanner sc=new PScanner(host,i);
                    info.append("INFO: ").append(host).append(" listen on port ").append(i).append("\n");
               }catch(java.io.IOException io) {
                    //info.append("INFO: ").append(host).append(" did not listen on port ").append(i).append("\n");
               }     
            }*/
            endTime=System.currentTimeMillis();
        } else { 
            error.append("ERROR: Host "+host+" are not resolvable");
        }
    }
    
    int debug=0;
    
    public void log(String msg) { if (debug > 0 ) System.out.println(msg); }
    
    public static void main(String[] args) {
          String min="min";  String max="max";
          for( int i=0; i<args.length; i++ ) {
              if      ( args[i].matches("-pmin") ) {  min=args[++i]; }
              else if ( args[i].matches("-pmax") ) {  max=args[++i]; }
              else {
                PortScanner pc=new PortScanner(args[i]);
                pc.setMaxPort(max); pc.setMinPort(min);
                pc.test();
                System.out.println("PortScanner runs against host: "+args[i]+" on ports "+pc.minScanPort+" to "+pc.maxScanPort+" for "+(pc.endTime-pc.startTime)/1000+" seconds and found\n"+pc.info.toString()+pc.error.toString());
              }       
          }
    }
   
    private static class PStest implements Runnable{
        int min=1; int max=65365;  final String ho;
        Thread th ; boolean complete=false;
        private SSLContext sslContext;
        boolean sslPossible=false;
        public PStest(int min, int max, String ho) {
            this.max=max; this.min=min; this.ho=ho;
            th = new Thread(this, "network scan "+min+" to "+max);
            try {
                sslContext = SSLContext.getInstance("TLS");            
                sslContext.init( null, null, new SecureRandom() );
                sslPossible=true;
            }catch(Exception e) {
                sslContext=null;
            }
                
        }
        
        public void start() { complete=false; th.start(); }
        
        public  StringBuilder getResult(){
            do {  try { Thread.sleep(300); }catch(InterruptedException ie) {} } while( ! complete );
            return te;
        }
        
        private StringBuilder te=new StringBuilder();
        
        @Override
        public void run() {
            te=new StringBuilder();
            for (int i=min; i<=max; i++ ) {
              
                PScanner sc=new PScanner(ho,i,this);
                if ( sc.connect ) {
                    te.append("INFO: ").append(ho).append(" listen on port ").append(i);
                    if ( sc.ssl ) { te.append(" (SSL/TLS) "); }
                    te.append("\n");
                }
                   
            }
            complete=true;
        }
        
        synchronized SSLSocket getSSLServer(String ho, int po) throws IOException {
            return (SSLSocket) sslContext.getSocketFactory().createSocket(ho, po);
        }
    }
    private static class PScanner {
        boolean connect=false;
        boolean ssl=false;
        public PScanner(String ho, int po, PStest te) {
            try {
                if ( te.sslPossible ) {
                    Socket so =new Socket(ho, po);
                    connect=true;
                           so.close();
                } else {
                    SSLSocket sso= te.getSSLServer(ho, po); //  .sslContext.getSocketFactory().createSocket(ho, po);
                    connect=true;
                    sso.startHandshake(); 
                    ssl=true;
                }
            } catch(Exception e) { }    
        }
    }
}
