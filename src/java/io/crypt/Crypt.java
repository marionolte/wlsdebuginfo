/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.crypt;

import general.Version;
import io.file.WriteFile;
import java.util.UUID;
//import main.Mos;
import net.tcp.Host;

/**
 *
 * @author SuMario
 */
public class Crypt extends Version {
    final private CryptHigh ch;
    final private CryptLow  cl;
          private CryptHigh hostch;
          private CryptLow  hostcl;
          private CryptLow  custcl=null;
          private CryptHigh custch=null;
          private CryptHigh userch;
          private CryptLow  usercl;
          
    final private UUID uuid;
    private final String Ukey="5fa4a40a-53b4-4f7a-b132-61bd19b79a8e";
    private       String host=Host.getHostname();
    private       String user=getUserKey();
    int maxKeyLen;
    
    private int cryptLevel=0;
    public Crypt() {
        uuid= UUID.fromString(Ukey);
        ch=new CryptHigh(uuid);
        if (ch.getHighAllow()) { 
            cl=null; 
        } else{ 
            cl=new CryptLow(uuid);
        }
        init();
    }
    //public Crypt(Mos m) {
    //    this();
    //}
    
    
    public void setHostKey(String info) {
        if ( info == null || info.isEmpty() ) { return; }
        host = getUUIDCode(info).toString();
        init();
    }
    
    public void setUserKey(String info) {
        if ( info == null || info.isEmpty() ) { return; }
        user = getUUIDCode(info).toString();
        init();
    }
    
    public void setCustomKey(String info) {
        if ( info == null || info.isEmpty() ) { return; }
        if ( cl == null ) {
            custch = new CryptHigh(getUUIDCode(info)) ;
        } else { 
            custcl = new CryptLow(getUUIDCode(info)) ;
        }    
        
    }
    
    public void setCryptLevel(int level) {
        this.cryptLevel=(level>0)?level:0;
    }
    
    
    private void init() {
        if (ch.getHighAllow()) { 
            hostcl=null; 
            usercl=null; 
            hostch=new CryptHigh(getUUIDCode(host));
            userch=new CryptHigh(getUUIDCode(user));
        } else{ 
            hostcl=new CryptLow(getUUIDCode(host));
            usercl=new CryptLow(getUUIDCode(user));
            hostch=null;
            userch=null;
        }
    }
    
    private UUID getUUIDCode(String info) {
        try { 
            return UUID.fromString(info);
        } catch(java.lang.IllegalArgumentException iae) { 
            StringBuilder sw = new StringBuilder();
                          
            byte[] b = getMD5(info).getBytes();  // [8]-[4]-[4]-[12]
            for ( int i=0; i<27; i++) {
                if (i==8 || i==12 || i==16 || i==20) { sw.append("-");}
                if (i< 27 ){
                    if ( i < b.length ) { sw.append( (char)b[i] ); }
                    else { sw.append("1"); }
                }
            }
            
            return UUID.fromString(sw.toString());
        }
    }
    
    private MD5 md5;
    public String getMD5(String info) {
        if ( md5 == null ) { md5=new MD5(); }
        String md = md5.toMD5Hash(info);
        return md;
    }
    
    public boolean isCrypted(String txt) {
        if ( txt == null || txt.isEmpty() ) return false;
        return isCrypted(txt.getBytes());
        
    }
    
    public boolean isCrypted(byte[] b) {
        boolean br=false;
        for (int i=0; i<b.length; i++) {
            if ( ! Base64.isBase64(b[i]) ) {
                return false;
            } 
        } 
        //System.out.println("br last:"+b[ b.length-1 ]);
        if ( b[ b.length-1 ] == 61 ) { br=true; }
        return br;
    }
    
    public String getCrypted(String txt) {
         String out = getUserCrypted(txt);
                out = getHostCrypted(out);
                out = getCustCrypted(out);
         return ( cl == null )? ch.getCrypted(out) : cl.getCrypted(out);
    }
        
    private String getUserCrypted(String txt) {
        if( cryptLevel > 0 && ( (cl==null && userch!=null) || ( cl!=null && usercl!=null) ) ) {
            StringBuilder sw = new StringBuilder();
                          sw.append(
                                     (( cl == null )? userch.getCrypted(getHostCrypted(txt)) 
                                                    : usercl.getCrypted(getHostCrypted(txt)))
                                    );
                          //System.out.println("crypt user sw:"+sw.toString()+":");
                          if ( sw.length() > 4 && ! sw.toString().endsWith("=") ) { sw.append("="); }
            return "<"+user+" md5=\""+getMD5(sw.toString())+"\">"+sw.toString()+"</"+user+">";
            //return "<"+user+">"+sw.toString()+"</"+user+">";
        }
        return txt;
    }
    
    private String getHostCrypted(String txt) {
        if( cryptLevel > 1 ) {
            StringBuilder sw = new StringBuilder();
                          sw.append(
                                       (( cl == null )? hostch.getCrypted(txt) 
                                                      : hostcl.getCrypted(txt))
                                    );
                          if ( ! sw.substring(sw.capacity()-1).equals("=") ) { sw.append("="); }
            return "<"+host+">"+sw.toString()+"</"+host+">";
        }
        return txt;
    }
    
    private String getCustCrypted(String txt) {
        if(  ( custch!=null ) || ( custcl!=null )  ) {
            StringBuilder sw = new StringBuilder();
                          sw.append(
                                   (( custcl == null )? custch.getCrypted(txt) 
                                                      : custcl.getCrypted(txt))
                                    );
                          if ( ! sw.substring(sw.capacity()-1).equals("=") ) { sw.append("="); }
            return sw.toString();
        }
        return txt;
    }
    
    public String getUnCrypted(String info) {
        String out=( cl == null )? ch.getUnCrypted(info) : cl.getUnCrypted(info);
               out=getUnCryptedCust(out);
               out=getUnCryptedHost(out);
               out=getUnCryptedUser(out);
               out=getUnCryptedHost(out);
        return out;
    }
    private String getUnCryptedHost(String info) {
        final String a = "<"+host; final String e="</"+host+">";
        if ( info.startsWith(a) && info.endsWith(e) ) {
                      String[] ab = info.split(">"); 
           String f = info.substring(ab[0].length()+1,info.length()-e.length());
           String md5 = getCryptedMD5(ab[0]);
           if ( md5.isEmpty()  || ( ! md5.isEmpty() && md5.matches(getMD5(f)) )    )  {
                return ( cl == null )? hostch.getUnCrypted(f) 
                                     : hostcl.getUnCrypted(f);  
           // return ( cl == null )? hostch.getUnCrypted(info.substring(a.length(),info.length()-e.length())) 
           //                     : hostcl.getUnCrypted(info.substring(a.length(),info.length()-e.length())); 
           }
        }
        return info;
    }
    private String getUnCryptedUser(String info) {
        final String a = "<"+user; final String e="</"+user+">";
        if ( info.startsWith(a) && info.endsWith(e) ) {
           String[] ab = info.split(">"); 
           String f = info.substring(ab[0].length()+1,info.length()-e.length());
           String md5 = getCryptedMD5(ab[0]);
           if ( md5.isEmpty()  || ( ! md5.isEmpty() && md5.matches(getMD5(f)) )    )  {
                return ( cl == null )? userch.getUnCrypted(f) 
                                     : usercl.getUnCrypted(f); 

                //return ( cl == null )? userch.getUnCrypted(info.substring(a.length(),info.length()-e.length())) 
                //                     : usercl.getUnCrypted(info.substring(a.length(),info.length()-e.length())); 
           }
        }
        return info;
    }
    
    synchronized private String getCryptedMD5(String info) {
        String[] sp = info.split("\"");
        for(int i=0; i< sp.length;i++) {
            if ( sp[i].endsWith("md5=") && i+i < sp.length ) { return sp[++i]; }
        }
        return "";
    }
    
    private String getUnCryptedCust(String info) {
        //if ( custcl == null && custch == null ) { return info; }
        if( ( custch !=null ) || ( custcl!=null )  && info.endsWith("=") ) {
            //System.out.println("in cust:"+info+":");
            String out = (( custcl == null )? custch.getUnCrypted(info) 
                                            : custcl.getUnCrypted(info) ); 
            //System.out.println("un cust:"+out+":");
            return out;
        }
        return info;
    }
    
    public byte[] getUnCryptedByte(String info) {
        return ( cl == null )? ch.getUnCryptedByte(info) : cl.getUnCryptedByte(info);
    }
    
    public void runArgs(String[] args) {
        boolean test = false;  String cust=Host.getHostname()+"1234@456789-0";
         
         for ( int i=0; i<args.length; i++ ) {
             if ( args[i].matches("-max") ) {
                    setCryptLevel(Integer.parseInt(args[++i]));
                    
             }
             else if ( args[i].matches("-custkey")   ) { cust=args[++i];     }
             else if ( args[i].matches("-usecustkey")) { setCustomKey(cust); }
             else if ( args[i].matches("-test") ) {
                 int j=args.length;
                 for( j=++i; j<args.length; j++) {
                   String s=args[j];
                   if ( s.matches("\\-d") ){ debug++;  ch.debug++; if(cl!=null){cl.debug++;} } else { 
                     String en = getCrypted(s);
                     String de = getUnCrypted(en);
                     String ma = ( s.equals(de) )?"YES":"NO";
                     log("main(String[] args)",0,"TESTING:"+s+":\nENCODED :"+en+":\nDECODED :"+de+":\nDECODED :"+getUnCrypted(s)+": (income)\nMATCHING:"+ma+"\n");
                   }  
                }
                i=j;
                test=true;
             } 
             else if (args[i].matches("-version")     && ! test ) { System.out.println("Crypt v"+this.getVersion()+" of "+this.getFullVersion()); }
             else if (args[i].matches("-crypt") && ! test ) { 
                WriteFile fa = new WriteFile(args[++i]);
                if ( ! fa.isReadableFile() ) {
                    String s= getCrypted(args[i].replaceAll("==$", "="));
                    System.out.println(s);
                 } else {
                    if ( ! fa.isBinaryFile() )  {
                       String s= fa.readOut().toString();
                              s= getCrypted(s.replaceAll("==$", "="));
                       fa.replace( s+((s.endsWith("="))?"":"=") );
                    } else {
                       System.out.println("WARNING:  do not handle binary files ");
                    }
                }    
             }
             else if (args[i].matches("-uncrypt") && ! test ) { 
                WriteFile fa = new WriteFile(args[++i]);
                if ( ! fa.isReadableFile() ) {
                    String s= getUnCrypted(args[i]);
                    System.out.println(s);
                 } else {
                    if ( ! fa.isBinaryFile() )  {
                       String s= fa.readOut().toString();
                              s=getUnCrypted(s);
                       fa.replace(s);
                    } else {
                        System.out.println("WARNING:  do not handle binary files ");
                    }
                }    
             } else {
                 System.out.println("i="+i+": |"+args[i]+"|");
                 System.out.println(usage(true));
             }
         }
         
    }
    
    public static void main(String[] args) throws Exception {
         Crypt c = new Crypt();
         //System.out.println("USERKEY |"+c.getUserKey()+"|\nHOSTKEY |"+c.getSshHostKey()+"|");
               c.runArgs(args);
    }

    public String usage(boolean b) {
        StringBuilder sw=new StringBuilder();
        if (b) sw.append("usage() : ");
        sw.append("[-max <0..2>] [-custkey <custom key> -usecustkey] <-crypt|-uncrypt> <String|File>");
        return sw.toString();
    }
    
    private void log(String func, int level, String msg) {
        if ( level == 0 ) {
            System.out.println(msg);
        } else {
            if ( level <= debug ) {
                System.out.println("DEBUG["+level+"/"+debug+"] Crypt::"+func+" - "+msg);
            }
        }    
    }
    private void log(String func, int level, String msg, Exception e) {
         log(func,level,msg);
         log(func,level, "Exception trown with "+e.getMessage());
         e.printStackTrace();
    }
}
