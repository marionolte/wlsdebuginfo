/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package general;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLDecoder;
import net.tcp.Host;

/**
 *
 * @author SuMario
 */
public abstract class  Version {
    final public static int majorVersion=1;
    final public static int minorVersion=1;
    final public static int patchVersion=3;
    final public static int libVersion=0;
    
    
    /**
     *
     * @return
     */
    public static String getVersion()     { return ""+majorVersion+"."+minorVersion+((patchVersion==0)?"":"."+patchVersion); }
    public static String getFullVersion() { return getVersion()+""+((libVersion==0)?"":"."+libVersion); }
    
    final public static void sleep(int l) {
        try { Thread.sleep(l); } catch(Exception e){}
    }
    
    final public String getFunc(String func){ return this.getClass().getName()+"::"+func; }
    final public String getFunc(){ return getFunc(0); }
    final public String getFunc(int i){ return getFunc(  (new Throwable() ).getStackTrace()[i].getMethodName() ); }
    
    final public int getDebug(){ return debug; }
    final public int setDebug(int i) { debug=(i>=0)?i:debug; return getDebug(); }
    
    static final public Log log=new Log() {
        
        final int InfoLog=1;
        final int DebugLog=2;
        final int WarnLog=3;
        final int ErrorLog=4;
        final int FatalLog=4;
        final int TraceLog=FatalLog;
        
       
        
        
        final public String getFunc(String func){ return this.getClass().getName()+"::"+func; }
        final public String getFunc(){ return getFunc(0); }
        final public String getFunc(int i){ return getFunc(  (new Throwable() ).getStackTrace()[i].getMethodName() ); }
        
        final public int getDebug(){ return debug; }
        final public int setDebug(int i) { debug=(i>=0)?i:debug; return getDebug(); }
        final public boolean isFatalEnabled() { return debug >(FatalLog -1); }
        final public boolean isErrorEnabled() { return debug >(ErrorLog -1); }
        final public boolean isWarnEnabled()  { return debug >(WarnLog  -1); }
        final public boolean isDebugEnabled() { return debug >(DebugLog -1); }
        final public boolean isInfoEnabled()  { return debug >(InfoLog  -1); }
                
        @Override
        public boolean isTraceEnabled() { return isFatalEnabled(); }


        @Override
        public void trace(Object message) {
            if ( isTraceEnabled() ) { printf(getFunc(1),TraceLog,(String)message); }
        }

        @Override
        public void trace(Object message, Throwable t) {
            if ( isTraceEnabled() ) { printf(getFunc(1),TraceLog, (String) message,(Exception)t); }
        }

        @Override
        public void debug(Object message) {
            if ( isDebugEnabled() ) { printf(getFunc(1),DebugLog, (String) message); }
        }

        @Override
        public void debug(Object message, Throwable t) {
            if ( isDebugEnabled() ) { printf(getFunc(1),DebugLog, (String) message, (Exception) t); }
        }

        @Override
        public void info(Object message) {
            if ( isInfoEnabled() ) { printf(getFunc(1),InfoLog, (String) message); }
        }

        @Override
        public void info(Object message, Throwable t) {
            if ( isInfoEnabled() ) { printf(getFunc(1),InfoLog, (String) message, (Exception) t); }
        }

        @Override
        public void warn(Object message) {
            if ( isWarnEnabled() ) { printf(getFunc(1),WarnLog, (String) message); }
        }

        @Override
        public void warn(Object message, Throwable t) {
            if ( isWarnEnabled() ) { printf(getFunc(1),WarnLog, (String) message, (Exception) t); }
        }

        @Override
        public void error(Object message) {
            if ( isErrorEnabled() ) { printf(getFunc(1),ErrorLog, (String) message); }
        }

        @Override
        public void error(Object message, Throwable t) {
            if ( isErrorEnabled() ) { printf(getFunc(1),ErrorLog, (String) message, (Exception) t); }
        }

        @Override
        public void fatal(Object message) {
            if ( isFatalEnabled() ) { printf(getFunc(1),FatalLog, (String) message); }
        }

        @Override
        public void fatal(Object message, Throwable t) {
            if ( isFatalEnabled() ) { printf(getFunc(1),FatalLog, (String) message, (Exception) t); }
        }
    };
    
    public static String jarfile;
    public static int debug=0;
    
    public static PrintStream ps=null;
    public static PrintStream pserr=null;
    public synchronized static void println(String msg) { print(msg+"\n"); }
    public synchronized static void println(int lev, String msg ) { 
        if ( lev == 0 ) { 
            println(msg); 
        } else {
            if ( debug >= lev ) println("DEBUG["+lev+"/"+debug+"] "+msg);
        }
    }
    public synchronized static void print(String msg) {
        if ( ps == null ) { System.out.print(msg); } else { ps.print(msg); ps.flush(); }
    }
    public synchronized static void printerr(String msg) {
        if ( pserr == null ) { System.err.println(msg); } else { pserr.print(msg); pserr.flush(); }
    }
    public synchronized static void printf(String msg, String[] sp) {
        if ( ps == null ) {
           if ( sp.length == 0 ) { print(msg); }
           else if ( sp.length == 1 ) { System.out.printf(msg, sp[0]); }
           else if ( sp.length == 2 ) { System.out.printf(msg, sp[0], sp[1]); }
           else if ( sp.length == 3 ) { System.out.printf(msg, sp[0], sp[1], sp[2]); }
           else if ( sp.length == 4 ) { System.out.printf(msg, sp[0], sp[1], sp[2], sp[3]); }
           else if ( sp.length >= 5 ) { System.out.printf(msg, sp[0], sp[1], sp[2], sp[3], sp[4]); }
                
        } else { pserr.print(msg); pserr.flush(); }
    }
    
    public synchronized static void printf(String cName, String meth, int level, String msg ) { printf(cName+"::"+meth,level,msg); }
    public synchronized static void printf(String cName, int level, String msg ) { 
        //println(level, cName+" - "+msg);
        //println(level,cName+" - "+msg+"debug["+level+"/"+debug+"]");
        if ( level <= debug ) {
            if ( msg.contains("\n") ) {
                boolean t=false;
                for(String m: msg.split("\n")) {
                    String n=(t)?"\t":"";
                    println(level, cName+" - "+n+m);
                    t=true;
                }
            } else {
                println(level, cName+" - "+msg);
            }    
        }
    }
    public synchronized static void printf(String cName, int level, String msg , Exception e) { 
        if ( level >= debug ) {
             StringWriter sw = new StringWriter();
             PrintWriter  pw = new PrintWriter(sw);
             e.printStackTrace(pw);
             printf(cName, level, msg+"\n"+sw.toString());
        }
    }
    
    public final String getUserKey()     { return USERKEY; }
    public final String getHostKey()     { return HOSTKEY; }
    
    private static final String OSNAME   ;
    private static final String JAVAHOME ;
    private static final String JAVAVERSION;
    private static final String USERKEY;
    private static final String HOSTKEY;
    
    static public final boolean isWindows() { return OSNAME.contains("win"); } 
    static public final boolean isUnix()    { return !isWindows(); } 
    static public final boolean isMac()     { return (OSNAME.contains("mac")) || (OSNAME.contains("mac")); } 
    static public final boolean isSolaris() { return (OSNAME.contains("sunos")) || (OSNAME.contains("solaris")); } 
    static public final boolean isAIX()     { return OSNAME.contains("aix");}
    static public final boolean isLinux()   { return OSNAME.contains("linux"); }

    
    final public synchronized static String replacePass(String s) {
        StringBuilder sw=new StringBuilder();
        if ( s != null ) {
            for(String f : s.split("\n")) {
                if (sw.length() >0 ) { sw.append("\n"); }
                if      ( f.startsWith("PASSWORD=")       ) { f="PASSWORD=(are set)"; }
                else if ( f.startsWith("SERVERPASSWORD=") ) { f="SERVERPASSWORD=(are set)"; } 
                sw.append(f);
            }
        }
        return sw.toString();
    }
    
    
    
    static public Object o; 
    static {
       o=null;
       try { 
        jarfile = URLDecoder.decode(Version.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
       } catch(Exception e) {} 
       JAVAHOME    = System.getProperty("java.home");
       OSNAME      = System.getProperty("os.name").toLowerCase();
       JAVAVERSION = System.getProperty("java.version");
       USERKEY     = System.getProperty("user.name");
       HOSTKEY     = Host.getHostname(); //System.getProperty("os.name")+System.getProperty("os.version");
       
    }
}
