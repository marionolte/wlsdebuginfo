/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.file;

import io.buffer.RingBuffer;
import io.thread.RunnableT;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author SuMario
 */
public class LargeReadFile extends ReadFile {
    RingBuffer<String> ring = new RingBuffer<String>(30);
    private LargeReader  lt;
    
    public LargeReadFile(String dir, String  file){ this(dir+File.separator+file); }
    public LargeReadFile(            String nfile){ this( new File(nfile) ); }
    public LargeReadFile(            File   nfile){ super( nfile ); startLargeReader(); }
    
    private void startLargeReader() {
        lt = new LargeReader(this);
        lt.start();
    }
    
    @Override
    public StringBuilder read() {
        final String func=getFunc("read()");
        lt.read=true;
        StringBuilder sw = new StringBuilder();
        int c=0;
        while ( ! ring.isEmpty() || c < ring.getSize() ) {
           printf(func,3,"pop ring - capa ring"+ring.getSize());
            sw.append(ring.pop());
            printf(func,3,"capa:"+sw.capacity()+" ring capa:"+ring.getSize());
            c++;
        }
        return sw;
    }
    
    public boolean hasNext() {
        final String func=getFunc("hasNext()");
        if (lt.hasNotRead() || lt.hasNotCompleted() ) { 
            if ( lt.hasComplete ) {
                startLargeReader();
            }
            lt.read=true;
            if ( lt.hasNotCompleted() ) {
                printf(func,3,"wait on ring");
                while( ring.isEmpty() && ! lt.isClosed() ) { sleep(300); }
                printf(func,3,"ring not empty :"+ring.isEmpty()+" not closed:"+(!lt.isClosed()) );
            }
        }
        printf(func,2,"return "+(! ring.isEmpty()));
        return ( ! ring.isEmpty() );
    }
    
    
    public void close() {
        lt.setClosed();
    }
        
    
}
