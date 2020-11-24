/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.file;

import general.Version;
import io.crypt.MD5;
import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import net.exception.CloseException;


/**
 *
 * @author SuMario
 */
public class Cache extends Version {
    private ReadDir basedir;
    private static boolean cacheReady=false;
    MessageDigest md = null;
    private static Cache mc=null;
    private HashMap map=new HashMap<String, HashMap>();
    
    
    
    public Cache(String dir) {
        if ( dir.endsWith(File.separator) ) {
            dir=dir.substring(0, dir.length()-1 );
        }
        if ( ! dir.endsWith("pcache_dir") ) {
            dir=dir+File.separator+"pcache_dir";
        }
        
        printf("Cache(String dir)",2,"start cache initialize on :"+dir+":");
        basedir = new ReadDir(dir);
        initialize();
        mc=this;
    }
    
    public static Cache getInstance() {
        if ( mc == null ) {
             throw new RuntimeException("cache not initialized");
        }
        return mc;
    }
    
    private void getFileCache(String ca) {
        if ( ca == null || ca.startsWith(".") ) { return;}
        final String meth="getFileCache(String ca)";
        final String loc=basedir.getFQDNDirName()+File.separator+ca+File.separator;
        ReadFile f = new ReadFile ( loc+"plist.info" );
        ArrayList ar=f.getMap( f.readOut() );
        HashMap lmap=(HashMap) map.get(ca);
        boolean updateNeeded=false;
        if ( ar != null ) {
                for(int i=0; i<ar.size(); i++) {
                    String[] sp=(String[])ar.get(i);
                    sp[0]=loc+sp[0];
                    printf(meth,3,"File:"+sp[0]+":  value:"+sp[1]+":    "+System.currentTimeMillis() );
                    try { 
                        CacheEntry ce = new CacheEntry(sp[0],sp[1].split(","),this);
                        if ( ! ce.isExpired() ) {
                           lmap.put(ce.getName(), ce);
                        } else { 
                           printf(meth,3,"cache entry "+ce.getName()+" expired - removing now"); 
                           ce.delete();
                           updateNeeded=true;
                        }
                    } catch (Exception e) {}    
                }
        }
        //updateNeeded=true;
        if ( updateNeeded ) { updateInfoFile(f,lmap); }
    }
    
    private void updateInfoFile(ReadFile f, HashMap lmap) {
        StringBuilder ab = new StringBuilder();
                      ab.append("#FILE=loaded,lasttaken,maxcached\n");
            printf("updateInfoFile(ReadFile f, HashMap lmap)",2,"cache info file "+f.getFileName()+" updated ");  
            Iterator itter = lmap.keySet().iterator();
            while ( itter.hasNext() ) {
                Object o = itter.next();
                printf("updateInfoFile(ReadFile f, HashMap lmap)",2,"cache info entry:"+o.toString() );
            }
            f.create();
            f.save(ab);
            printf("updateInfoFile(ReadFile f, HashMap lmap)",3,"cache info file "+f.getFileName()+" has now \n|@|\n"+ab+"\n|@|");
            
    }
    
    private void initialize(){
        final String meth="initialize()";
        if ( ! basedir.isExist() ) {  createCache(); }
        try {
            md = MessageDigest.getInstance("MD5");
            String[] sp=basedir.getDirectories();
            for(int i=0; i< sp.length; i++) {
                if (  ! ( sp[i].matches(".") || sp[i].matches("..")) ) { 
                    printf(meth,2,"add now :"+sp[i]+":");
                    map.put(sp[1], new HashMap() );
                    getFileCache(sp[i]); 
                }
            }
        } catch (Exception ex) {
            throw new CloseException(ex.toString());
        }
    }
    
    
    private void createCache() {
        final String meth="createCache()";
        printf(meth,2,"create cache on :"+basedir.getFQDNDirName()+":");
        //DirFilesOp.getInstance("create", new ReadDir(basedir), basedir);
        if ( basedir.create() ) {
            if ( ! basedir.isWritable() ) { throw new RuntimeException("ERROR: cache instance not writable");}
            
        } else {
            throw new RuntimeException("ERROR: cache instance create");
        }
    }
    
    public static String getCacheResult(String ask) {
        String ret=null;
        if ( cacheReady ) {
             
        } 
        return ret;
    }
    
    public static void setCache(String ask, String value) {
        if ( cacheReady ) {
            
        }   
    }
    
    
    
    public static void main(String[] args) throws Exception {
        
        Cache cache = new Cache(args[0]);
        
    }

    private long maxcachetime=3*60*1000;  // default 3 minute in milli secounds
    public long getMaxCacheTime() { return maxcachetime; }
    public long setMaxCacheTime(long time) { if(time>0){ this.maxcachetime=time; } return getMaxCacheTime(); }

    public CacheEntry getCacheEntry(String location) { return getCacheEntry(location,false); }
    public CacheEntry getCacheEntry(String location, boolean createIfNotExist ) {
        final String func="getCacheEntry(String location)";
        String a=MD5.toMD5Hash(location).toLowerCase();
        String b=a.substring(0, 4);
        printf(func,2,"check for cache entry "+b+"/"+a+" from location:"+location);
        CacheEntry entry=getMapEntry(a,b);
        if( entry == null && createIfNotExist ) {
            printf(func,2,"create cache entry "+b+"/"+a+" from location:"+location);
            try {
                entry = new CacheEntry(a, new String[] {"-1","-1","-1",location}, this);
                setEntryMap(entry,a,b);
            } catch (Exception ex) {
                printf(func,2,"create cache entry runs in exception "+ex.getMessage()+" for location:"+location);
            }
        }
        return entry;
    }

    private final String mapLocked="MapLock";
    private final String entryLock="EntryLock";
    private final String updateLock="UpdateLock";
    
    private HashMap getMap(String b) {
        synchronized(mapLocked) {
            HashMap a=(HashMap) map.get(b);
            if ( a == null ) { a=new HashMap(); updateMap(b, a);}
            return a;
        }
    }
    private CacheEntry getMapEntry(String a, String b) {
        synchronized(entryLock) {
           return (CacheEntry) ((HashMap) getMap(b)).get(a);
        }
    }

    public void setEntryMap(CacheEntry entry, String a, String b) {
        synchronized(entryLock) {
             HashMap lmap=(HashMap) getMap(b);
             if ( lmap.get(a) == null ) { count++; }
             lmap.put(a, entry);
        }
    }

    public void updateMap(String b, HashMap a) {
        synchronized ( updateLock ) {
             map.put(b, a);
        }
    }
    
    private long count=0;
    private long lastClean=System.currentTimeMillis();
    public void cleanMap() {
        synchronized ( entryLock ) {
            synchronized(mapLocked) {
                synchronized(updateLock) {
                    HashMap nmap = new HashMap();
                    Iterator<String> itter = map.keySet().iterator();
                    count=0;
                    while( itter.hasNext() ) {
                        String m = itter.next();
                        HashMap lmap = (HashMap) map.get(m);
                        HashMap imap = new HashMap();
                        Iterator et = lmap.entrySet().iterator();
                        while ( et.hasNext() ) {
                            String n = (String) et.next();
                            CacheEntry ce = (CacheEntry) lmap.get(n);
                            if ( ce != null && ! ce.isExpired() ) {
                                imap.put(n, ce); count++;
                            }
                        }
                        if ( imap.size() > 0 ) {
                            nmap.put(m, imap);
                        }
                    }
                    if ( nmap.size() > 0 ) { map = nmap; } else { map = new HashMap(); }
                    lastClean=System.currentTimeMillis();
                }
            }
        }
    }
    
    public boolean isEmpty() { synchronized(updateLock) { return  (count >0)?true:false; } }
    public long    getLastClean() { synchronized(updateLock) { return lastClean; } }
    
    
}
