/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.buffer;

import general.Version;
import java.util.Iterator;
import java.util.NoSuchElementException;




/**
 *
 * @author SuMario
 */
public class RingBufferObjects<Item> extends Version implements Iterable<Item> {

    private Item[] elements;     // queue elements
    private int number = 0;      // number of elements on queue
    private int first  = 0;      // first element in the queue
    private int last   = 0;      // last element in the queue
    private String lock = null ;

    private RingBufferObjects<Object> ring;

    // contructor for the RingBuffer - array where not generated on default
    public RingBufferObjects(int capacity) { this.elements = ( Item[] ) new Object[capacity];  }

    public RingBufferObjects() { this(16384); }

    public  boolean isEmpty()  { return number == 0;  }
    public  boolean isFull()   { return number == this.elements.length; }
    public  int     getSize()  { return number;       }
    private boolean islocked() { return lock == null; }

    private synchronized void getLock(String s) {
            while( this.islocked() ) {
                //System.out.println("locked :"+lock);
                try {
                   this.wait(500);
                } catch ( InterruptedException l ) {}
            }
            this.lock=s;
    }

    private synchronized void clearLock(String s) {
        if ( this.lock.equals(s)) { lock=null; this.notify(); }
    }

    public synchronized boolean push( Object s ) {
        boolean b = false;
        // umwandlung Object/String Item ???
        Item m = (Item) s; // new String( s );

        this.enqueue(m);
        return b;
    }

    private void enqueue(Item item) {
        if ( this.number == this.elements.length ) { throw new RuntimeException("ring buffer full"); }

        this.elements[last] = item;
        this.last = (this.last + 1) % this.elements.length ;  // set next element wrap-around
        this.number++;
    }

    public Object pop() {
        return( this.dequeue() );
    }

    private Item dequeue() {
        if ( this.isEmpty() ) { throw new RuntimeException("ring buffer empty"); }

        Item item = this.elements[ this.first ];
        this.elements[ this.first ] = null;      // cleanup for gc
        this.number--;
        this.first = ( this.first + 1 ) % this.elements.length; // set next element + wrap around
        return(item);
    }

    

    @Override
    public Iterator<Item> iterator() { return new RingBufferIterator(); }
    
    
    // inner classes

    private class RingBufferIterator implements Iterator<Item> {
        private int i = 0;
        @Override
        public boolean hasNext() { return i < number;  }
        public boolean isEmpty() { return i == 0; }
        
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
