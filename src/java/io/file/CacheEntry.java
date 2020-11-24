/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.file;

import general.Version;
import java.io.File;

/**
 *
 * @author SuMario
 */
public class CacheEntry extends Version {
    private final Cache ca;
    private final String name;
    private final String location;
    private boolean inMemory=true;
    private long loaded;
    private long taken;
    private long maxalive;
    private String caller;
    private StringBuilder sb=new StringBuilder();
    
    CacheEntry(String loc, String[] sp, Cache ca) throws Exception {
       // #FILE=loaded,lasttaken,maxcached 
       
       try { 
        this.ca=ca;
        this.location = loc;
        this.loaded   = Long.parseLong(sp[0]);
        this.taken    = Long.parseLong(sp[1]);
        this.maxalive = Long.parseLong(sp[2]);
        this.caller   = sp[3];
        
        String[] at = location.split(File.separator);
        name=at[ at.length-1 ];
        printf("CacheEntry(String loc, String[] sp, Cache ca)",2,"location:"+location+":  name:"+getName()+":  loaded:"+loaded+"  taken:"+taken+" maxalive:"+maxalive);
       } catch (RuntimeException e) {
           throw new Exception(e.toString());
       } 
    }
    
    public void update(StringBuilder sb) { 
           this.sb=sb;
           this.loaded=System.currentTimeMillis();
           this.maxalive=this.loaded+ca.getMaxCacheTime();
    }
    
    public StringBuilder read() {
        if ( this.maxalive < System.currentTimeMillis() ) { return null; }
        if (! inMemory) {
            ReadFile f=new ReadFile(location);
                    sb=f.read();
                    inMemory=true;
                   
        }
        return this.sb;
    }
    
    void clean(){
        if ( inMemory ) {
            printf("clean()",2,"entry in memory - time to store on disk - "+location);
            if (maxalive < System.currentTimeMillis() ) {
                ReadFile f=new ReadFile(location);
                         f.create();
                         f.save(sb);
                         sb =new StringBuilder();
                         inMemory=false;
            }
        }
    }
    
    void delete() {
        if ( inMemory ) { inMemory=false; sb =new StringBuilder(); }
        boolean test=true;
        WriteFile f=new WriteFile(location);
        if (test) f.copy( new File (location+".sav") );
        f.delete();
            
    }
    
    public boolean isExpired() {
        return (System.currentTimeMillis() > maxalive) ? true : false;
    }
    
    public String getName() {return name; }
    
    public boolean readAvailable() {
        if ( isExpired() ) { return false; }
        if ( ! inMemory ) { read(); }
        
        return (sb.length() > 0 )?true:false;
    }
}
