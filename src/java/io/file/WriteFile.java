/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.file;

import com.jcraft.jzlib.GZIPInputStream;
import com.jcraft.jzlib.GZIPOutputStream;
import static general.Version.printf;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author SuMario
 */
public class WriteFile extends ReadFile{
    
    public WriteFile(String dir, String file) { super(dir, file);   }    
    public WriteFile(String nfile           ) { super( nfile );     }
    public WriteFile(File file              ) { super(file);        }
    public WriteFile(ReadFile file          ) { super(file.filer);  }
    
    public ReadFile getReadFile() { return (ReadFile) this; }
    
    public boolean append(StringBuilder buf,boolean leave) { return this.append(buf.toString(),leave); }
    public boolean append(String buf, boolean leave) { return this.append(buf.getBytes(),true); }
    public boolean append(StringBuilder buf) { return this.append(buf.toString()); }
    public boolean append(String buf) { return this.append(buf.getBytes(),true); }
    public boolean append(byte[] buf, boolean leave) {
       try {  
           // leave true ; create only new it not exist / false create always new
        OutputStream os = new FileOutputStream(super.filer, leave);
        os.write(buf, 0, buf.length); 
        os.close();
        return true;
       }catch(Exception e) { return false; } 
    }

    public boolean replace(StringBuilder sw) { return replace(sw.toString()); }
    public boolean replace(String s        ) { truncate(); return append(s); }
    
    public boolean delete() { return super.filer.delete(); }
    
    public StringBuilder deleteAfterReadOut() {
        StringBuilder sw=readOut();
        delete();
        return sw;
    }
    
    public String getTime(){
        Calendar now = Calendar.getInstance();
                 now.setTimeInMillis(this.getLastModified());
        StringBuilder sw=new StringBuilder(); 
        sw.append( now.get(Calendar.YEAR) );
        int d = now.get(Calendar.MONTH)+1;
        sw.append( ( (d<10)?"0":""+d)  );
        sw.append( ( now.get(Calendar.DAY_OF_MONTH) < 10 )?"0":"" +now.get(Calendar.DAY_OF_MONTH) ); 
        sw.append("-");
        sw.append( ((now.get(Calendar.HOUR_OF_DAY) < 10 )?"0":"" ) ).append(now.get(Calendar.HOUR_OF_DAY )); 
        sw.append( ((now.get(Calendar.MINUTE)      < 10 )?"0":"" ) ).append(now.get(Calendar.MINUTE ));
        sw.append( ((now.get(Calendar.SECOND)      < 10 )?"0":"" ) ).append(now.get(Calendar.SECOND ));
        return sw.toString();
    }
    
    public boolean rotate() {
        StringBuilder sw=new StringBuilder(getTime());           
        if ( WriteFile.__GZIP ) { sw.append(".gz"); }
        return rotate(filer.getAbsoluteFile().toString()+"."+sw.toString(), WriteFile.__GZIP, 50*1024L, -1L, true);
    }
    
    public boolean backup(Long size) { return backup(size, false); }
    public boolean backup(Long size, boolean zip) {
        StringBuilder sw=new StringBuilder(getTime());
        if ( WriteFile.__GZIP ) { sw.append(".gz"); }
        return rotate(filer.getAbsoluteFile().toString()+"."+sw.toString(), zip, size, -1L, false);
    }
    
    static boolean __GZIP=false;
    public boolean rotate(String fn, boolean gzip, long minSize, long old, boolean truncate) {
        final String func="rotate(String fn, boolean gzip, long minSize, long old, boolean truncate)";
        boolean b=false;
        printf(func,3,"fn:"+fn+":");
        if ( ((minSize > 0 && this.getSize() > minSize) || ( old > 0 && this.getLastModified() > old ))  && isWriteableFile() ) {
            return rotate(fn,gzip,truncate);
        } else {
            System.out.println("skip rotation for file "+this.getFileName()
                              +" reason for skipping "
                              +" isWritable:"+isWriteableFile()                    
                              +" minSize:"+(this.getSize() > minSize)+"("+(getSize()-minSize)+")"
                              +" file modification delta:"+(this.getLastModified() > old)+" ("+this.getLastModified()+">"+old+"="+(this.getLastModified()-old)+")");
        }   
        return b;
    }
    public boolean rotate(String fn, boolean gzip, boolean truncate){
        final String func=getFunc("rotate(String fn, boolean gzip, boolean truncate)");
        WriteFile toFn = new WriteFile(fn); 
        try {
                GZIPOutputStream _gzip=null;
                    OutputStream  _out=toFn.getOutStream();
                if ( gzip ) { _gzip = new GZIPOutputStream(_out); } 
                
                byte[] buf = new byte[ 64*1024 ];
            
                java.io.InputStream   in = new java.io.FileInputStream(filer);

                int len;
                while ((len = in.read(buf)) > 0){ 
                    if ( gzip ) { 
                        _gzip.write(buf, 0, len);
                    } else {    
                         _out.write(buf, 0, len);
                    } 
                    dsize=+buf.length; 
                }

                if ( gzip ) { _gzip.flush(); _gzip.close(); } 
                _out.flush(); _out.close();
                printf(func,2,"file "+this.getFileName()+" is rotated to "+toFn.getFQDNName() );
                if ( truncate ) {
                    return truncate();
                } else {
                    return delete();
                }    
        } catch(IOException io ) {
                printf(func,1,"file "+this.getFileName()+" rotation error "+io.getMessage());
        }
        return false;
    }
    
    public boolean truncate() {
        final String func=getFunc("truncate()");
        boolean b=false;
        try { 
            FileChannel outChan = new FileOutputStream(filer, true).getChannel();
            outChan.truncate(0L);
            outChan.close();
            printf(func,2,"file "+this.getFQDNFileName()+" is truncated now");
            b=true;
        } catch(IOException io ) {
            System.out.println("file "+this.getFQDNFileName()+" error in truncation - "+io.getMessage());
        }
        return b;
    }
    
    synchronized  public boolean gzip() {
        final String func=getFunc("gzip()");
        boolean b=false;
        WriteFile fn = new WriteFile(this.getFQDNFileName()+".gz");
        try {
                if ( isMatching("\\.gz")) { return b; } 
                b=gzip(fn.getOutStream(), new java.io.FileInputStream(filer) );
                if ( b ) {
                    fn.setLastModified(getLastModified());
                    b=delete();
                    if ( b ) {
                       this.filer=fn.filer;
                    }
                } 
                    
        } catch(IOException io ) {
            printf(func,1,"file "+this.getFileName()+" gzip error "+io.getMessage());
            fn.delete();
        }    
        return b;
    }
    
    synchronized public boolean gzip(java.io.OutputStream out, java.io.InputStream in) {
        final String func=getFunc("gzip(java.io.OutputStream out, java.io.InputStream in)");
        boolean b=false;
        try {
            GZIPOutputStream gzip=new GZIPOutputStream(out);;
                byte[] buf = new byte[ 64*1024 ];
                
                int len;
                while ((len = in.read(buf)) > 0){ 
                     gzip.write(buf, 0, len);
                     dsize=+buf.length; 
                }

                in.close(); gzip.flush(); gzip.close(); 
                b=true;                    
        } catch(IOException io ) {
            printf(func,1,"gzip error "+io.getMessage());
            
        } 
        return b;
    }    
    
    synchronized public void gunzipUntilTxt(String fn) {
        final String func=getFunc("gunzipUntilTxt(String fn)");
        boolean ascii=false;
        ArrayList<GZIPInputStream> ar = new ArrayList<GZIPInputStream>();
        WriteFile f = new WriteFile(fn);
        OutputStream out = f.getOutStream();
        try {
                GZIPInputStream gin = new GZIPInputStream( getInputStream() );
                while ( ! ascii ) {
                        gin.mark(0);
                        byte[] buf = new byte[ 1024 ];
                        int len =gin.read(buf);
                        ascii=checkAscii(buf,len);
                        gin.reset();
                        if( ! ascii ) {
                            ar.add(gin);
                            gin = new GZIPInputStream( gin );
                        }
                }
                
                byte[] buf = new byte[ 64*1024 ];
                int len;
                while ((len = gin.read(buf)) > 0){ 
                     out.write(buf, 0, len);
                     dsize=+buf.length; 
                }
                gin.close(); 
                if ( ar.size() > 0 ) {
                     while ( ar.size() > 0 ) { (ar.remove(ar.size()-1)).close(); }
                }
                out.flush();
                out.close();
        }catch(java.io.IOException io) {
            printf(func,0,"ERROR:"+io.getMessage());
        }
            
        f.setLastModified(getLastModified());
    }
    
    
    synchronized public boolean gunzip() {
        final String func=getFunc("gunzip()");
        boolean b=false;
        WriteFile fn = new WriteFile(this.getFQDNFileName().replaceAll("\\.gz$", ""));
            b=gunzip( fn.getOutStream() , getInputStream());
        if ( b ) {
            fn.setLastModified(getLastModified());
            delete();
            this.filer=fn.filer;
        } else {
            fn.delete();
        }
        return b;
    } 
    
    synchronized public boolean gunzip(java.io.OutputStream out, java.io.InputStream in) {
        try {
            GZIPInputStream gin = new GZIPInputStream( in );
            byte[] buf = new byte[ 64*1024 ];
                
                int len;
                while ((len = gin.read(buf)) > 0){ 
                     out.write(buf, 0, len);
                     dsize=+buf.length; 
                }

                out.flush(); gin.close(); 
            
                return true;
        }catch(java.io.IOException io ) {
        }
        return false;
    }
    
    public boolean Execute() { return Execute(null); }
    public boolean Execute(String[] evp) {
        final String func="Exceute(PrintStream ps)";
        boolean b=false;
        try  {  
            String[] cmd = { this.getFQDNFileName() };
            Runtime rt = Runtime.getRuntime();
            Process p;
            if (evp == null ) {
                p = rt.exec(cmd);
            } else {
                p = rt.exec(cmd,evp);
            }     
            BufferedReader br = new BufferedReader ( new InputStreamReader( p.getInputStream() ) );
            InputStream er = p.getErrorStream();
            if (debug >1) System.err.println("start wait until received");
            while( ! br.ready() ) {
                try { Thread.sleep(1000); } catch(Exception io) {}
            }
            errorMsg=new StringBuilder();
            stdoutMsg=new StringBuilder();
             if (debug >1) System.err.println("STDOUT received information : ");
             String l;
             while( ( l = br.readLine() ) != null) { 
                if (debug >1) System.err.println("STDOUT received:"+l);
                stdoutMsg.append(l).append("\n");
             }
             try { br.close(); }catch(IOException iom) {}
             if (debug >1) System.err.println("STDOUT received completed length="+stdoutMsg.length());
             
             if ( er.available() == 0 ) { b=true; } else {
                while( er.available() >0 ) { 
                    errorMsg.append( (char)er.read() );
                }
                if ( errorMsg.length() > 0 ) { 
                    if (debug >1) System.err.println("STDERR received error :"+errorMsg.toString()+"  length="+stdoutMsg.length());
                } else {
                    b=true;
                }
             }
             try { er.close(); }catch(IOException iom) {}
             if (debug >1) System.err.println("STDERR received completed length="+errorMsg.length());
             
             p.destroy(); 
             if (debug >1) System.err.println("process are destroyed");
        } catch(Exception io) {
            if (debug >1) { System.err.println("Execute runs in error:"+io.getMessage()); io.printStackTrace(System.err); }
        }    
            
        return b;
        
    }
    
    public boolean setExecutable(boolean b) {  filer.setExecutable(b); return filer.canExecute(); }
    public boolean setWritable(  boolean b) {  filer.setWritable(b);   return filer.canWrite();   }
    public boolean setReadable(  boolean b) {  filer.setReadable(b);   return filer.canRead();    }
    
    
    
    public static void main(String[] args) {
        WriteFile.__GZIP=true;
        WriteFile f = new WriteFile(args[0]);
       
        // System.out.println("rotate  file:"+f.getFQDNFileName()+" "+f.rotate()+":");
        f.gunzipUntilTxt(args[1]);
    }
     
    
}
