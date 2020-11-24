/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package io.thread;

import general.Version;

/**
 *
 * @author SuMario
 */
public abstract class RunnableT extends Version implements Runnable {
    public String lock="default";
    private Thread  th=null;
    private long _startTime_=0L;
    private long   _endTime_=0L;
    
    public String getName() { return lock; }
    private boolean running=false;
    final public boolean  isRunning() { return running; }
    final public boolean setRunning() { running=(running)?false:true;  
            if( ! running ) { _endTime_=System.currentTimeMillis(); }
            return isRunning(); 
    }
    
    final public long getStartTime(){ return _startTime_; }
    final public long getStopTime() { return   _endTime_; }
    
    private boolean closed=false;
    final public boolean isClosed() { return closed; }
          public void   setClosed() { closed=true; }
    final public void setUnClosed() { closed=false; }
    
    
    final public void start() {        
        //synchronized ( lock ) {
            if ( th !=null && th.isAlive() && isRunning() )  { return; }
            th = new Thread( this, lock);
            th.start();  
            Long d = System.currentTimeMillis()+5000L;
            while( ! th.isAlive() && ! isRunning() && System.currentTimeMillis() < d ) { sleep(100); }
        //}
        _startTime_=System.currentTimeMillis();
        _endTime_=0L;
    }
    
    final public void stop() {
       //synchronized(lock) { 
        if ( th != null && th.isAlive() )  {
            try {
              th.interrupt();
              th.join(100);
            } catch(InterruptedException ie) {} 
        }
        this.th=null;
        this.running=false;
       //} 
    }
    final public Thread getThread() { return th; }
    
    final public void thsleep(long l) {
        synchronized(lock) {
            try { th.sleep(l); }  catch (Exception e) { }
        }
    }
    
    final public void join() {
        setClosed();
        join();
    }
    
    final public void thwakeup() {
        synchronized(lock) {
            try { th.interrupt(); th.notify(); }  catch (Exception e) { }
        }
    }
    final public void thwait() {
        synchronized(th) {
                      try { 
                         th.wait();
                      }  catch (Exception e) {
                         // wake up
                      }
                      
        }      
    }
    
    @Override
    abstract public void run();
    
    
}
