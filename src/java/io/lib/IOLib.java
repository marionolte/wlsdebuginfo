/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.lib;

import general.Version;
import io.file.ReadFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class IOLib extends Version {
   private static Version v;
   private static  ClassLoader loader;
   
   static {
       v      = new Version() {};
       loader = ClassLoader.getSystemClassLoader();
   } 
   
   static public String execReadToString(String[] execCommand) throws java.io.IOException {
       final String func="IOLib::execReadToString(String[] execCommand)";
       printf(func,3,"comm 0 =>"+execCommand[0]);
       Process proc = Runtime.getRuntime().exec(execCommand);
            System.out.println("proc:"+proc);
            StringBuilder sw=new StringBuilder();
            java.io.InputStream stream; 
            try  {
                stream = proc.getInputStream();
                java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
                while ( s.hasNext() ) {
                    sw.append( (s.hasNext() ? s.next().trim() : ""));
                } 
            }catch(Exception e) {}
            printf(func,3,"stdout:"+sw.toString()+":");
            //java.io.InputStream stream;
            
                stream = proc.getErrorStream();
                java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
                while ( s.hasNext() ) {
                   sw.append( (s.hasNext() ? s.next().trim() : ""));
                }
            printf(func,2,"return:"+sw.toString()+":");
            return sw.toString();
     
   }
   static public String execReadToString(String execCommand) throws java.io.IOException {
       return execReadToString(execCommand.split(" "));
   }
   
   public static String launch(List<String> cmdarray) { // throws IOException,InterruptedException {
        StringBuilder sw = new StringBuilder();
        byte[] buffer = new byte[1024];
        Process process;
        //System.out.println("1");
        ProcessBuilder processBuilder = new ProcessBuilder(cmdarray);
        //System.out.println("1a");
        processBuilder.redirectErrorStream(true);
        //System.out.println("1b");
        try { 
            //System.out.println("command: "+processBuilder.toString());
            process = processBuilder.start();
        } catch (java.io.IOException io) {
            //System.out.println("not accessed");
            return "";
        }
        //System.out.println("1c");
        InputStream in  = process.getInputStream();
        //System.out.println("2");
        while (true) {
           try { 
            int r = in.read(buffer);
            if (r <= 0) {
                break;
            }
            //System.out.write(buffer, 0, r);
            for( int i = 0; i<r; i++ ) { sw.append((char)buffer[i]);}
            
           } catch(java.io.IOException io ) {
               
           }
        }
        //System.out.println("3");
        //process.waitFor();
        //System.out.println("4");
        return sw.toString();
    }
   
   private static final HashMap<String, String> _zipmap = new HashMap<String, String>();
   
   static public void fillJarMap(String  f) throws IOException { fillJarMap(new ZipFile(f)); }
   static public void fillJarMap(File    f) throws IOException { fillJarMap(new ZipFile(f.toString())); }
   static public void fillJarMap(ZipFile f) {
       final String func=v.getFunc("fillJarMap(ZipFile f)");
       Enumeration<? extends ZipEntry> e = f.entries();
       v.printf(func,4,"read Zip:"+f.getName());
       while(e.hasMoreElements()) {
           String fn = f.getName();
           ZipEntry en = e.nextElement();
           _zipmap.put(en.toString(), fn);
       }
       v.printf(func,3,"_zipmap:"+_zipmap);
   }
   
   static public boolean isPackageExist(String pack) {
       String  a = (pack).replaceAll("\\.", "\\/"); if( ! a.endsWith("/") ) { a +="/"; }
       boolean b = (_zipmap.get(a) != null ||  _zipmap.get("/"+a) != null);
       v.printf(v.getFunc("isPackageExist(String pack)"),2," check pack:"+pack+": check with a:"+a+": return:"+b+" "+_zipmap.size()+" "+_zipmap.get(a));
       return b;
   }
   static public String[] getClassFromPackage(String pack) {
       int save=v.debug;
       //v.debug=2;
       final String func=v.getFunc("getClassFromPackage(String pack)");
       v.printf(func,4,"income for pack:"+pack);
       ArrayList<String> ar = new ArrayList();
       if (isPackageExist(pack)) {
            String[] at = pack.split("\\."); 
            v.printf(func,3,"is in pack :"+pack+":");
            String  a = (pack).replaceAll("\\.", "\\/"); if( ! a.endsWith("/") ) { a +="/"; }
            Iterator<String> itter = _zipmap.keySet().iterator();
            while( itter.hasNext() ){
                String ef = itter.next();
                v.printf(func,3,"check ef:"+ef+":");
                if( ef.startsWith(a) || ef.startsWith("/"+a) ) {
                      String[] sp = ef.split("/");
                      if ( at.length+1 == sp.length && ef.endsWith("class")) {
                          v.printf(func,2,"ef:"+ef+ at.length+"=="+sp.length);
                          ar.add(ef.substring(0, ef.length()-6));
                      }
                }
            }
       } else {
           v.printf(func,3,"pack:"+pack+" not exist");
       }
       String[] sp = new String[ar.size()];  for(int i=0; i<sp.length; i++){ sp[i]=ar.get(i); }
       v.printf(func,3,"outgoing for pack:"+pack+":    return:"+sp.length+" elements");
       v.debug=save;
       return sp;
   }
   
   //static public String getFunc(String func){  return "IOLib::"+func;}

    public String getValueFromClass(String cl, String key) {
        try {
          return getIno(loader.loadClass(cl), key);
        } catch (Exception e) { return (String) null;}  
   }
    
    private  String getIno(Class<?> cl, String name) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException  {
        
        Field f = cl.getDeclaredField(name); 
        return (String) ""+f.get(f);
    }
    
    static public HashMap<String,String> scanner(String[] args,final String use) {    
        //v.debug=4;
        String func=v.getFunc("scanner(Sting[] args,final String use)");
        v.printf(func,3," usage |"+use+"|");
        Pattern pa = Pattern.compile("\\]|\\[|<|>");
        Matcher ma = pa.matcher(use);
        int pos=0;
        HashMap<String,String> map = new HashMap();
        while(ma.find(pos)) {
            String msg = use.substring(pos,ma.start());
            v.printf(func,3," find |"+msg+"| pos:"+pos+" to:"+ma.start()+" "+msg.indexOf(" ") );
            if ( msg.indexOf(" ") > 0 ) {
                String va="true";
                String[] sp = msg.split(" ");
                v.printf(func,3," sp[0] |"+sp[0]+"|");
                if ( sp.length>0 && sp[0].startsWith("-") ) {
                    if ( sp.length > 1 ) { 
                        va=use.substring(pos,ma.start()).substring(sp[0].length()+1); 
                    }
                    if( use.substring(ma.end()-1, ma.end()).equals("<") ) {
                        pos=ma.end(); ma.find(pos);
                        va=use.substring(pos,ma.start());
                        v.printf(func,2,"msg after char <  =>:"+va+":");
                    }
                    v.printf(func,2," save |"+sp[0]+"="+v+"|");
                    map.put(sp[0], va);
                    map.put("_default_"+sp[0], va);
                }
            } else {
                v.printf(func,3," msg without spaces |"+msg+"|");
                if ( msg.startsWith("-") ) {
                    map.put(msg, "false");
                }else if ( msg.equals("objectlist")) {
                    map.put(msg, "");
                }
            }
            //map.put("-w", "password");     map.put("_default_-w", "password");
            //map.put("-j", "passwordfile"); map.put("_default_-j", "passwordfile");
            map.put("--help", "help page");  map.put("_default_--help", map.get("--help"));
            
            v.printf(func,3," new pos |"+ma.end()+"| of "+use.length());
            pos=ma.end();
        }
        v.printf(func,3," end map |"+map+"|");
        map.put("_usage_", "false");  map.put("_debug_", "0");
        if (args.length > 0) {
            for(int i=0; i<args.length; i++) {
                v.printf(func,3," property:"+args[i]+":");
                if ( ! args[i].isEmpty() ) {
                    if ( args[i].equals("--help") ) {
                        map.put(args[i], "true");
                        map.put("_usage_", "true");
                    } else if ( args[i].equals("-d") ) { 
                       map.put("_debug_", ""+(Integer.parseInt(map.get("_debug_"))+1));
                    } else if ( args[i].startsWith("-") ) { 
                       if ( map.get(args[i]) != null ) {
                            v.printf(func,2," map:"+args[i]+":");
                            String p=args[i];
                            String va="true";
                            if ( args.length > (i+1) && ! args[i+1].startsWith("-") ) {
                                va=args[++i];
                            }
                            if ( p.equals("-o") && ! map.get(p).equals(map.get("_default_-o"))) {
                                va=map.get(p)+"\n"+v;
                            }
                            v.printf(func,2," map:"+p+"="+v+":");
                            map.put(p, va);
                       } else {
                           map.put("_usage_", "true");
                       }  
                    }
                    else if ( ! args[i].startsWith("-") ){
                       v.printf(func,2," add object List:"+args[i]+":"); 
                       map.put("_objlist_", map.get("_objlist_")+"_@@_"+args[i]);
                    }
                }   
            }
       } else {
            map.put("_usage_", "true"); 
       }     
       return map;     
     }
    
    static public String getMappedValue(final String use, final HashMap<String,String> map) {    
        String func=v.getFunc("getMappedValue(final String use,HashMap<String,String> map)");
        final String f=map.get(use);
        if ( f != null && ! f.equals(map.get("_default_"+use)) ) {
            return f;
        }
        return "";
    }
    
    static public boolean isNumber(String s) {
        try {  
            double d = Double.parseDouble(s);  
            return true;
        }  
        catch(NumberFormatException nfe) { return false; }  
 
    }
    
    static public void getFileDiff(String[] ar) {
        ArrayList<ReadFile> mar = new ArrayList();
        HashMap<String,String> mp = new HashMap();
        for ( String f : ar ) {
            if ( ! mp.containsKey(f) ) {
                ReadFile fr = new ReadFile(f);
                if ( fr.isAsciiFile() ) {

                    System.out.print("INFO: include file "+f+" .. ");
                    fr.diffReady();
                    mar.add(fr);  mp.put(f, f);
                    System.out.println("done");
                } else {
                    System.out.println("WARNING: "+f+" are not an ascii file - skipping");
                }
            } else {
                    System.out.println("INFO: seconds time for "+f+" - skipping ");
            }    
        }
        if ( mar.size() <= 1 ) {
            System.out.println("INFO: minimum count 2 of different files not provided - skipping diff ");
            return;
        }
        ReadFile ref = mar.remove(0);
        for( ReadFile f : mar ) {
             ref.getDiff(f);
        }
   }
     
}   

