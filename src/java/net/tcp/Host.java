/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.tcp;

import static io.lib.IOLib.execReadToString;
import java.util.Random;

/**
 *
 * @author SuMario
 */
public class Host extends TcpHost{
    String host = null; 
    
    public Host(){}
    public Host(String ho) { this(); host = ho; }
    
   /* public static String getHostname() { 
       try { return execReadToString("hostname"); } catch(java.io.IOException io){ return "localhost"; }
    }*/
    
    public static String getSerial() { 
       try { return execReadToString("hostname"); } catch(java.io.IOException io){ return "localhost"; }
    }
    
    public static String getMainMac() {
      String out=null;  
      try { 
          out=execReadToString( (  (isWindows())?"ipconfig /all":"ifconfig -a" ) );
          int ind = out.indexOf("ether "); 
          if ( ind > 0 ) {
              String[] sp = out.substring(ind).split(" ");
              out=sp[1].toUpperCase();
          } else {
              ind = out.toLowerCase().indexOf("physical address");
              if (ind != -1) {
                    ind = out.indexOf(":");
                    if (ind != -1) {
                        out = out.substring(ind + 1).trim();
                    }
              }      
          }       
          //System.out.println("out:"+out+":");
          return out;
      }catch(java.io.IOException ie) {}  
       catch(java.lang.StringIndexOutOfBoundsException sb){}
       catch(java.lang.NullPointerException np){}
      return (out!=null)?out:getRandMac();
    }
    
    static private String randMac=null;
    static private String getRandMac() {
        if ( randMac == null ) {
            String mac = "";
            Random r = new Random();
            for (int i = 0; i < 6; i++) {
                int n = r.nextInt(255);
                mac += String.format("%02x", n);
            }
            randMac = mac;
        }
        return randMac;
    }
    
    public String getHost(){
        return (host==null)? super.getHostname():host;
    }
    public String setHost(String ho){
        host=ho;
        return getHost();
    }
    
    public static void main(String[] args) {
          System.out.println("Hostname:"+getHostname());
          System.out.println("MacMain :"+getMainMac()+":");
    }

   
}
