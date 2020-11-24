/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io.buffer;

import general.Version;
import java.util.Iterator;
import java.util.NoSuchElementException;



// suppress unchecked warnings in Java 1.5.0_6 and later

/**
 *
 * @author mario
 */
public class RingBuffer<Item> extends Version implements Iterable<Item> {

    // private RingBuffer<String> ring  ;
    private Item[] elements;     // queue elements
    private int number = 0;      // number of elements on queue
    private int first  = 0;      // first element in the queue
    private int last   = 0;      // last element in the queue
    private String lock = null ;
    private RingBuffer<Object> ring;
    private boolean autoInc=true;
    private int capa;
    

    // contructor for the RingBuffer - array where not generated on default
    public RingBuffer(int capacity) { this.capa=capacity; clear(); } //this.elements = ( Item[] ) new Object[capacity];  }

    public RingBuffer() { this(16384); }
    
    public  synchronized boolean clear() { 
        getLock("clear");
            this.elements = ( Item[] ) new Object[capa];
            this.number = 0;      // number of elements on queue
            this.first  = 0;      // first element in the queue
            this.last   = 0;
        clearLock("clear");
        return true;
    }

    public  synchronized boolean isEmpty()  { return number == 0;  }
    public  synchronized boolean isFull()   { return number == this.elements.length; }
    public  synchronized int     getSize()  { return number;       }
    public  synchronized boolean reSize(int newSize) {
        if ( ! this.isEmpty() ) { return false; }
        getLock("resize");
            this.capa=newSize;
        clearLock("resize");
            clear();  
        
        return true;
    }
    public void setAutoIncrease(boolean b) { this.autoInc=b; }
    
    private synchronized boolean islocked() { return (lock == null)?false:true; }

    private final String mylock="RingBuffer sync";
    private synchronized void getLock(String s) {
         final String func="getLock(String s)";
         synchronized (mylock) {   
            while( this.islocked() ) {
                printf(func,5,"locked with:"+lock);
                try {
                   this.wait(500);
                } catch ( InterruptedException l ) {}
            }
            this.lock=s;
         }   
    }

    private synchronized void clearLock(String s) {
        final String func="clearLock(String s)";
        synchronized (mylock) { 
            if ( this.lock.equals(s)) { lock=null; this.notify(); }
        }
    }

    public synchronized boolean push( String s ) { return push( (Object) s );         }
    public synchronized boolean push( Object s ) { 
        if ( ! autoInc && isFull() ) { popObject(); }
        return this.enqueue( (Item) (s) ); 
    }
    

    private synchronized boolean enqueue(Item item) {
        final String func="enqueue(Item item)";
        printf(func,6,"verify RingBuffer length number:"+number+" == element.length:"+this.elements.length+" ?");
        if ( isFull() ) { return false; }

        printf(func,5,"ask for queue lock");
        getLock("queue");
        printf(func,5,"have the queue lock now \nset item to last:"+last);
        
        this.elements[last] = item;
        this.last = (this.last + 1) % this.elements.length ;  // set next element wrap-around
        this.number++;
        
        printf(func,5, "number:"+number+": last:"+last+" updated - size:"+this.getSize()+"\nclear the lock from queue");
        clearLock("queue");
        
        return true;
    }

    public String pop()       { Object o=popObject();   return ( o==null )?"": o.toString(); }
    public Object popObject() { return( (Object) this.dequeue() ); }
    
    private synchronized Item dequeue() {
        final String func="dequeue()";
        printf(func,5,"if empty return ("+this.isEmpty()+")- no extra debug msg");
        if ( this.isEmpty() ) { return null; }

        printf(func,5,"ask for release lock");
        getLock("release");
        printf(func,5,"have the release lock now\nget Item from "+this.first);
        
        Item item = this.elements[ this.first ];
        this.elements[ this.first ] = null;      // cleanup for gc
        this.number--;
        this.first = ( this.first + 1 ) % this.elements.length; // set next element + wrap around
        
        printf(func,5,"number:"+number+": last:"+last+" updated - size:"+this.getSize()+"\nclear the lock from release");
        clearLock("release");
        
        printf(func,5,"return item :"+item.toString()+":");
        return(item);
    }
    
    synchronized public  String getFirst()       { return (String) getFirstObject(); }
    synchronized public  Object getFirstObject() { return          getObject(0); }

    synchronized public  String getLast()        { return (String) getLastObject(); }
    synchronized public  Object getLastObject()  { return          getObject(last-1); }
    synchronized private Object getObject(int pos){
        getLock("pos");
        Object o = (pos >=0 && pos < this.elements.length )?this.elements[pos]:null;                 
        clearLock("pos");    
        return o;
    }
   
    @Override
    public Iterator<Item> iterator() { return new RingBufferIterator(); }

    private class RingBufferIterator extends Version implements Iterator<Item> {
        private int i = 0;
        @Override
        public boolean hasNext() { return i < number;  }
        public boolean isEmpty() { return i == 0; }
        
        RingBufferIterator() { super(); }
        
        @Override
        public void remove()  {
             if ( ! this.isEmpty() ) { throw new NoSuchElementException(); }

             elements[i]=null; i--;
             
        }

        @Override
        public Item next() {
            if ( ! this.hasNext() ) { throw new NoSuchElementException(); }

            return elements[i++];
        }

    }
    
}
