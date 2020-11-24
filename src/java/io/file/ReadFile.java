/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package io.file;

import general.Version;
import io.crypt.MD5;
import io.thread.RunnableT;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author SuMario
 */
public class ReadFile extends Version {
     
            File filer;
    private String dir;
    private String file;
    private StringBuilder sb=null;
    public  int debug=0;
    public ReadFile(String dir, String file){
        this(dir+File.separator+file);
    }

    public ReadFile(String nfile){
        this( new File(nfile.replaceAll("^~", System.getProperty("user.home")+File.separator)) );
    }

    public ReadFile(File file) {
        boolean b = file.isFile();
         this.filer= ( b )? getCanonical(file):file;
         this.file = ( b )? file.getName():file.toString();
         this.dir  = ( b )? file.getParent():getPar(file.toString());
         
         //System.out.println("file:"+filer.toString());
    }

    private String getPar(String f) {
        StringBuilder sw = new StringBuilder();
        if ( f.indexOf(File.separator) > 0 ) {
            String[] sp = f.split(File.separator); 
            for ( int i=0; i<sp.length-1; i++  ) {
                if ( sw.capacity() > 0 ) { sw.append(File.separator); }
                if      ( sp[i].matches("~") ) { sw.append(System.getProperty("user.home")); } 
                else if ( sp[i].matches(".") ) { sw.append(System.getProperty("user.dir"));  }
                else { sw.append(sp[i]); }
            }
            
        } else { sw.append(System.getProperty("user.dir")); }
        return sw.toString();
    }
    private File getCanonical(File d) {
        final String sepa="__@@__";
        final ArrayList<String> ar = new ArrayList();
        String[] sp= d.getAbsolutePath().replaceAll("^~", System.getProperty("user.home"))
                                        .replaceAll("^.$", System.getProperty("user.dir")+File.separator )
                                        .replaceAll("^."+File.separator, System.getProperty("user.dir")+File.separator)
                      .split(File.separator);
        for(String s : sp) {
            if ( ! s.isEmpty() ) {
                if      ( s.equals("..")) { ar.remove(ar.size()-1); }
                else if ( s.equals(".") ) {}
                else if ( s.equals("~") ) {}
                else { ar.add(s); }
            }
        }
        
        StringBuilder sw = new StringBuilder(sepa);
        while( ar.size() > 0 ) { sw.append(ar.remove(0)).append(sepa); }
        
        return new File(sw.toString().replaceAll(sepa, File.separator));
    }
    
    
    public void checkLog(){
        if ( sb == null ) { this.sb=this.readOut(); }
    }

    
    public StringBuilder readOut() {
        try{
          String line;
          sb= new StringBuilder();
          BufferedReader rb = new java.io.BufferedReader( new java.io.FileReader(filer) );
          do {
                line=rb.readLine();
                if ( line != null ) {
                    sb.append(line).append("\n");
                }
          } while ( line != null );

          if( sb.length() > 0) { sb.setLength(sb.length()-1); }   // remove last \n

        } catch (Exception e){ }

        return sb;
    }
    
    public StringBuilder readOut(String begin, String end) {
        StringBuilder aw = new StringBuilder();
        Pattern pa = Pattern.compile(begin+"|"+end);
        String sp=readOut().toString();
        Matcher ma = pa.matcher( sp );
        int start=0; boolean f= false;
        while( ma.find(start)) {
            if ( ma.group().matches(begin) ) { f=true; }
            else {
                if ( f ) { 
                    aw.append(sp.substring(start, ma.start())); 
                    if (ma.group().matches(end)) { 
                           f=false; 
                    } else {
                           aw.append(ma.group());
                    }
                } 
            }        
            start=ma.end();
        }
        return aw;
    }
    
    public Pattern readPattern() {
         sb= new StringBuilder();
         String line;
       try {  
         BufferedReader rb = new java.io.BufferedReader( new java.io.FileReader(filer) );
         do {
                line=rb.readLine();
                if ( line != null && ! line.isEmpty() ) {
                   if ( sb.length() > 0 ) {
                       sb.append("|");
                   } 
                   sb.append(line);
                }
         } while ( line != null );
        } catch (Exception e){ }  
        return Pattern.compile(sb.toString());
    }

    public String findInFile(String pat) {
        
        StringBuilder sw=new StringBuilder();
        
        Pattern pa  = Pattern.compile(pat, Pattern.CASE_INSENSITIVE);
        Matcher ma  = pa.matcher("");
        try {
            
         String line;
         BufferedReader rb = new java.io.BufferedReader( new java.io.FileReader(filer) );
         do {
            line=rb.readLine();
            if ( line != null && ! line.isEmpty() ) {
                   ma.reset(line); //reset the input
                   if (ma.find()) {
                        sw.append(line.trim()).append("\n");
                   }                    
            }
         } while ( line != null );
        } catch (Exception e){ }   
        /*int ln=0;
        System.out.println("filer.toPath:"+filer.toPath());
        try {
            BufferedReader reader = Files.newBufferedReader(filer.toPath(), StandardCharsets.UTF_8);
            LineNumberReader lineReader = new LineNumberReader(reader);
           
            String line = null;
            while ((line = lineReader.readLine()) != null) {
                if ( ! line.isEmpty() ) {
                    ma.reset(line); //reset the input
                    if (ma.find()) {
                        ln=lineReader.getLineNumber();
                        sw.append(ln).append(":").append(line.trim()).append("\n");
                    }
                }
            }     
            
        } catch(java.io.IOException io) {
            sw.append("Could not read from file at line:"+ln+" - IOERROR:"+io.getMessage()+"\n");
            io.printStackTrace();
        }*/
        return sw.toString();
    }

    public StringBuilder getStringBuilderRef ( ) { checkLog(); return this.sb; }

    public String getString() { checkLog(); return (sb == null )? "":sb.toString() ; }

    private ArrayList attr ;
    public ArrayList getMap(StringBuilder readOut) {
        final String meth="getMap(StringBuilder readOut)";
        attr = new ArrayList();
        if ( readOut != null ) {
                String[] sp = readOut.toString().split("\\n");
                for ( int i=0; i<sp.length; i++) {
                    if ( ! sp[i].isEmpty() && ! sp[i].startsWith("#")) {
                            String sf[] = sp[i].split("=");
                            String k=sf[0];
                            int    j=k.length();
                            if ( (k.length()+1) < sp[i].length() ) { j++; }
                            
                            String v=sp[i].substring( j );
                            k=k.replaceAll(" ", "");
                            v=v.replaceFirst(" ", "");
                            attr.add( new String[] {k,v}  );

                    }
                }
        }
        return attr;
    }
    
    
    
    public boolean exist() { return this.filer.exists();  }
    public boolean isExist() { return exist();  }
    public boolean isReadableFile()  {  return ( filer.isFile() && filer.canRead()    )? true:false; }
    public boolean isWriteableFile() {  return ( filer.isFile() && filer.canWrite()   )? true:false; }
    public boolean isExecutableFile(){  return ( filer.isFile() && filer.canExecute() )? true:false; }
    
    public boolean isReadableDirectory()  {  return ( filer.isDirectory() && filer.canRead() )? true:false; }
    public boolean isWriteableDirectory() {  return ( filer.isDirectory() && filer.canWrite())? true:false; }
    
    public boolean isAsciiFile() { return ! isBinaryFile(); } 
    public boolean isBinaryFile() {
        byte[] data = new byte[0]; 
        try {
            FileInputStream in = new FileInputStream(filer);
            int si = in.available();
            if(si > 1000) si = 1000;
            data = new byte[si];
            in.read(data);
            in.close();
        } catch(IOException io) {}
        return checkAscii(data,data.length);
    }
    
    boolean checkAscii(byte[] buf,int len) {
            int ascii = 0;
            int other = 0;

            for(int i = 0; i < buf.length; i++) {
                byte b = buf[i];
                if( b < 0x09 ) return true;

                if( b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D ) ascii++;
                else if( b >= 0x20  &&  b <= 0x7E ) ascii++;
                else other++;
            }

            if( other == 0 ) return false;

            return 100 * other / (ascii + other) > 95;
            
    }
    
    synchronized public void setLastModified(long d){ filer.setLastModified(d); }
    synchronized public long getLastModified() { return filer.lastModified(); }
    synchronized public boolean isOlderThanXDays(int days) {
        if ( days < 0 ) { return false; }
        return ( getLastModified() < ( System.currentTimeMillis() - (days*24*60*60*1000L) ));
    }
    
    
    synchronized public boolean isBiggerThan(long size) {
         if( size < 0 ) { return false;}
         return (getSize() > size );
    }
    synchronized public boolean isMatching(String req) {
        if ( req == null || req.isEmpty() ) { return false; }
        return  Pattern.compile(req).matcher(filer.getName()).find();
    }
    synchronized public boolean isCompresssed() {        
        return isMatching("\\.bz2$|\\.gz$|\\.zip$|\\.7z|\\.Z$");
    }
    
    public File getParent() { return filer.getParentFile(); }
    
    public InputStream getInputStream() { 
       try { 
        return new java.io.FileInputStream(filer);  
       }
       catch (FileNotFoundException e) { } 
       return null;
    }
    
    public synchronized boolean create() {
        try {
            this.filer.createNewFile();
        } catch(java.io.IOException io) {
        }
        if (debug >1) System.err.println("file "+getFQDNFileName()+" created :"+this.filer.exists() );
        return this.filer.exists() ? true:false ;
    }
    
    
    public synchronized boolean create(boolean b) {
        if ( b ) {
            if ( ! createParent() ) {  return  false; }
        }
        return create();
    }
    
    public synchronized boolean createParent() {
        if ( filer.getParent() != null ) {
            ReadDir dir = new ReadDir ( filer.getParentFile() );
            if ( ! dir.isDirectory() ) {
                return dir.mkdirs(); 
            }  else {
                return true;
            }
        }
        return false;
    }
    
    public synchronized boolean move(File moveFile){
        boolean r = filer.renameTo(moveFile);
        if (r) {
            this.file = filer.getName();
            this.dir  = filer.getParent();
        }
        return r;
    }
    
    public WriteFile getWriteFile() { return new WriteFile(this.filer); }
    
    long size=0;
    long dsize=0;
    public long getCopyState() { return (dsize==0)? (long) 0 : (long) size * 100 / dsize; }
    
    public synchronized boolean copy(File copyFile){
        boolean b=false; dsize=0;
        String meth="copy(File copyFile)";
        try {
            byte[] buf = new byte[ 64*1024 ];
            
            java.io.InputStream   in = new java.io.FileInputStream(filer);
  
            java.io.OutputStream out = new java.io.FileOutputStream(copyFile);

            int len;
            while ((len = in.read(buf)) > 0){ out.write(buf, 0, len); dsize=+buf.length; }
            in.close();
            out.flush();
            out.close();
            b=true;
        } catch (Exception e) {
            b=false;
        }
        return b;
    }
    
    public String[] getInfo() {
        String[] info= { "execute:"+filer.canExecute(),  
                         "write:"+filer.canWrite(),
                         "read:"+filer.canRead(),
                         "exist:"+filer.exists(),
                         "directory:"+filer.isDirectory(),
                         "file:"+filer.isFile(),
                         "hidden:"+filer.isHidden(),
                         "size:"+filer.length(),
                         "lastmodified:"+filer.lastModified()
        };
        
        return info;
    }

    public File   getFile()         { return this.filer; }
    public String getFQDNFileName() { return this.filer.toString(); }
    public String getFileName()     { return this.filer.getName(); }
    public Long   getSize()         { return this.filer.length();  }
    public String getFQDNName()     { return this.filer.getAbsolutePath()+File.separator+File.separator+this.filer.getName(); }
    
    public String getDirName()      { 
        String[] sp = getFQDNFileName().split(File.separator);
        StringBuilder sw=new StringBuilder();
        if ( ! filer.toString().startsWith(File.separator)) { sw.append("."); }
        for(int i=0; i<sp.length-1; i++ ) {
            sw.append(File.separator).append(sp[i]);
        }
        // System.out.println("FILE DIR:"+sw.toString()+":");
        return sw.toString(); 
    
    }
    
    
   
    public void save(StringBuilder sb) {
        final String func="save(StringBuilder sb)";
        try {
            java.io.OutputStream out = new java.io.FileOutputStream(filer);
            out.write(sb.toString().getBytes());
            out.flush();
            out.close();
        } catch (Exception ex) {
            if ( debug >1)
                System.err.println("storing cachefile ends with Exception : "+ex.toString());
        }
          
    }
    
    public StringBuilder read() {
        final String func="read()";
        StringBuilder ab=new StringBuilder();
        try {
            java.io.BufferedReader is = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(filer)));
            String inputLine;
            while(  ( inputLine = is.readLine() ) != null ) {
                ab.append(inputLine).append("\n");          
            }
            is.close();
        }catch (Exception ex) {
            if ( debug > 1)
            System.err.println("reading file ends with Exception : "+ex.toString());
        } finally {
            return ab;
        }
        
    }
    
    public  HashMap<String, String> getLines(int[] line) {
        HashMap<String, String> imap = new HashMap();
        try {
            java.io.BufferedReader is = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(filer)));
            String inputLine; 
            int li=0; int c=0;
            while(  ( inputLine = is.readLine() ) != null ) { li++;
                if ( li == line[c] ) {
                     imap.put(""+li, inputLine); c++;
                }
            }
            is.close();
        }catch (Exception ex) {
            if ( debug > 1)
                System.err.println("reading file ends with Exception : "+ex.toString());
        }
        for ( int i =0; i< line.length; i++ ) {
            if ( ! imap.containsKey((""+i)) ) { imap.put(""+i, _not); }
        }
        return imap;
    }
    
    private final String _not="__@@NOT@@__";
    
    HashMap<String,HashMap<String,String>> check_mp=new HashMap();
    public void diffReady() {
        final String func="diffReady()";
        MD5 md5 = new MD5();
        if ( check_mp.size() > 1 ) { return; }
        try {
            HashMap<String,String> lines = new HashMap();
            check_mp.put("lines", lines);
            java.io.BufferedReader is = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(filer)));
            String inputLine;
            int line=0;
            while(  ( inputLine = is.readLine() ) != null ) { line++;
                 String m=md5.toMD5Hash(inputLine);
                 if ( lines.containsKey(m) ) {
                      lines.put(m, lines.get(m)+","+line);
                 } else {
                      lines.put(m, ""+line);
                 }
                 printf(func,0,line+":"+m+":"+lines.get(m));
            }
            is.close();
        }catch (Exception ex) {
            if ( debug > 1)
            System.err.println("reading file ends with Exception : "+ex.toString());
        }
    }
    
    public void getDiff(ReadFile f) {
        final String func=getFunc("getDiff(ReadFile f)");
        HashMap<String,String> lines =   check_mp.get("lines");
        HashMap<String,String> line1 = f.check_mp.get("lines");
        ArrayList<String> list = new ArrayList();
        Iterator<String> itter = lines.keySet().iterator();
        while( itter.hasNext() ) {
            final String k = itter.next();
            String[] sp=getLineDiff( ((lines.get(k) !=null )?lines.get(k):""), ((line1.get(k) !=null )?line1.get(k):"" ) );
            printf(func,0,"k="+k+"<= sp list:"+Arrays.toString(sp));
            for ( String s: sp) {
                if ( ! s.isEmpty() && ! list.contains(s) ) { list.add(s); }
            }
        }
                         itter = line1.keySet().iterator();
        while( itter.hasNext() ) {
            final String k = itter.next();
            String[] sp=getLineDiff( ((line1.get(k) !=null )?line1.get(k):""), ((lines.get(k) !=null )?lines.get(k):"" ) );
            for ( String s: sp) {
                if ( ! list.contains(s) ) { list.add(s); }
            }
        }
        int[] sort = new int[ list.size() ];
        int i=0;
        for ( String s : list ) { sort[i] = Integer.parseInt(s); i++; }
        Arrays.sort(sort);
        HashMap<String, String> mp1 =   getLines(sort);
        HashMap<String, String> mp2 = f.getLines(sort);
        
        System.out.println("sort:"+Arrays.toString(sort));
        System.out.println("mp1:"+mp1);
        System.out.println("mp2:"+mp2);
    }
    
    
    private String[] getLineDiff(String a, String b) {
        if ( ! b.matches(a) ) {
            if ( a.isEmpty() ) { return b.split(","); }
            if ( b.isEmpty() ) { return a.split(","); }
            ArrayList<String> ar = new ArrayList();
            String [] sp = a.split(",");
            String [] s1 = b.split(",");
            for (String s : sp ) {
                if ( ! searchSet(s1,s) ) { ar.add(s); }
            }
            for (String s: s1 ) {
                if ( ! searchSet(sp,s) ) { ar.add(s); }
            }
            sp = new String[ ar.size() ];
            for (int i=0; i<sp.length; i++ ) { sp[i]=ar.get(i); }
            return sp;      
        }
        return new String[] {};
    }
    private boolean searchSet(String[] str, String search) {
        Set<String> stringSet = (Set<String>) new HashSet<String>(Arrays.asList(str));     
        return stringSet.contains(search);
    }
    
    StringBuilder errorMsg=new StringBuilder();
    StringBuilder stdoutMsg=new StringBuilder();
    public String getStdError() { return errorMsg.toString();  }
    public String getStdOut(){ return stdoutMsg.toString(); }
    
    
    private ObjectInputStream oin=null;
    public Iterator loadObjects(){ return loadObjects(null); }
    public Iterator loadObjects(Object o) {
        final String func="loadObjects(Object o) - ";
        HashMap m = new HashMap();
        try {
           if ( oin == null ) oin= new ObjectInputStream( new BufferedInputStream( new FileInputStream(this.filer) ) );
           Object obj;
           while ( (obj=oin.readObject() ) != null ) {
                 m.put(obj, obj);
           }
        } catch (ClassNotFoundException ex) {
            println(1,func+"read object error (ClassNotFoundException) - "+ex.getMessage() ); 
            if ( debug >0) ex.printStackTrace(System.err); 
        } catch(IOException io) {
            println(1,func+"read object error (IOException) - "+io.getMessage()); 
            if ( debug >0 ) io.printStackTrace(System.err); 
        } 
        return m.values().iterator();
    }
    
    private ObjectOutputStream oout=null;
    public void storeObjects(Object f) {
        final String func="storeObjects(Object f)";
        try {
            if (oout == null) oout = new ObjectOutputStream( new BufferedOutputStream( new FileOutputStream( this.filer ) ) ); 
            if ( f == null ) { oout.flush(); oout.close(); oout=null; return ; }
            oout.writeObject(f);
        } catch(IOException io) {
           if ( debug >1) { 
           System.err.println("store Object  error - "+io.getMessage()); io.printStackTrace(System.err); }
        }    
    }
    
    
        
   public PrintStream getPrintStream() {
        try {
            create(true);
            OutputStream os = new FileOutputStream(filer, true);
            return new PrintStream( os);
        } catch(FileNotFoundException fn) {
            fn.printStackTrace();
            return null;
        }    
    }
 
    public BufferedOutputStream getOutStream() {
        try {
         create(true);
         OutputStream os = new FileOutputStream(filer, true);
         return new BufferedOutputStream( os );
        } catch(FileNotFoundException fn) {
            fn.printStackTrace();
            return null;
        }     
         
    }
    
    public String getMD5() {
        
        String ret="";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            InputStream fis =  new FileInputStream(filer);
            byte[] buffer = new byte[4096];
            int numRead;
            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    md.update(buffer, 0, numRead);
                }
            } while (numRead != -1);

            fis.close();
            byte[] mdbytes = md.digest();

            //convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mdbytes.length; i++) {
              sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch(Exception e){}
        return ret;
    }
    
    public Properties getProperties() {
        Properties p = new Properties();
                  try { p.load(this.getInputStream()); } 
                  catch( java.io.IOException  io) {} 
                  catch( NullPointerException ne){}
        return p;
    }
    
    private TailTask tailer =null;
    public String tail() {
        if ( tailer == null || (tailer != null && tailer.onError)) { if (tailer!=null){ tailer.setClosed(); } ;tailer = new TailTask(this); tailer.start(); }
        return tailer.read();
    }
    
    public static void main(String[] args) {
        ReadFile f = new ReadFile(args[0]);
        System.out.println("tail file:"+f.getFQDNFileName()+":");
        long d = System.currentTimeMillis()+30000;
        while(d > System.currentTimeMillis() ) {
            String fa=f.tail();
            if( ! fa.isEmpty() ) System.out.println(fa); 
            sleep(100);
        }
        System.out.println("tail completed");
        System.exit(0);
    }
    
    
    private class TailTask extends RunnableT {
        
        private final ReadFile f ;
        
        TailTask(ReadFile f) {
            this.f=f;
            this.debug=f.debug;
        }    
        
        boolean onError=false;
        
        private final ArrayList<String> ar = new ArrayList();
        
        String read() {
            synchronized(lock) {
                if( ar.size() == 0) { return ""; }
                StringBuilder sw = new StringBuilder();               
                while ( ar.size() > 0 ) { sw.append(ar.remove(0)); }
                return sw.toString();
            }    
        }
        
        void updateIO(String io) {
            synchronized(lock) {
                ar.add(io);
            }
            if ( ar.size() > 10 ) {
                 while ( ar.size() > 10 ) { sleep(300); }
            }
            
        }    
        
        @Override
        public void run() {
            setRunning(); 
            onError=false;
            RandomAccessFile raF=null;
            long time = 0;
            String line = "";

            //go to the end of File
            long fSize= f.filer.length();
            try {
                raF = new RandomAccessFile(f.filer, "r");
                while( line != null ) { line = raF.readLine(); }  
                raF.seek(fSize);
            } catch(IOException io){ onError=true; }    
            time = f.filer.lastModified();

            //Now print the additions in the file from now on

            long size=f.getSize();
            while ( ! isClosed() ) {
                if (time != f.filer.lastModified()) {
                    // System.out.println("changed");
                     time = f.filer.lastModified();
                     if ( size > f.getSize() ) { size=0l; }
                     StringBuilder sw = new StringBuilder();
                     try {
                        raF.seek(size);
                        while( (line =raF.readLine() )!= null )  
                        {   
                           sw.append(line.trim()).append("\n");
                        }
                        size=f.getSize();
                     } catch(java.io.IOException io) { onError=true; }
                     //System.out.println("have read:"+sw.toString()+":");
                     updateIO(sw.toString());
                     
                }

                sleep(100);
            }
          
        }
        
    }
               
}
