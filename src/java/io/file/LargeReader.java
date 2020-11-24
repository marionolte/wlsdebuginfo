/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.file;

import io.thread.RunnableT;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author SuMario
 */
class LargeReader extends RunnableT{
    final File file;
        boolean read=false;
        boolean hasComplete=false;
        private final LargeReadFile lrFile;
        LargeReader(LargeReadFile l) {
            this.file=l.filer;
            this.debug=l.debug;
            this.lrFile=l;
        }
        @Override
        public void run() {
            final String func=getFunc("run()");
            setRunning(); hasComplete=false;
            FileChannel inChannel=null;
            try {
                printf(func,4,"file channel started");
                while( ! read ) { 
                    if ( this.isClosed() ){  throw new java.io.IOException("isClosed"); }
                        sleep(300); 
                }
                printf(func,4,"read started");
                
                      inChannel = new FileInputStream(file).getChannel();
                ByteBuffer buff = ByteBuffer.allocate(4*1024);

                int re =0; long d=0L; long s=file.length();
                do {
                        wait4Ring();
                        printf(func,3," check if channel isOpen:"+inChannel.isOpen());
                        while ( ! inChannel.isOpen() ) { 
                            if ( this.isClosed() ){  throw new java.io.IOException("isClosed"); }
                            sleep(300); 
                        }
                        re = inChannel.read(buff); d+=re;
                        if ( re > 0 ) {
                            printf(func,3,"read from channel:"+re+" bytes total:"+d+": of "+s);                       
                            StringBuilder sw = new StringBuilder();
                            for ( int i=0; i<re; i++) {
                                sw.append( (char) buff.get(i) );
                            }    
                            lrFile.ring.push(sw.toString());
                        }
                        buff.clear();
                        if ( this.isClosed() ){  throw new java.io.IOException("isClosed"); }
                 } while( re > 0);
                    
                 
           }catch(java.io.IOException io) {
                    printf(func,1,"read with exception "+io.getMessage(),io);
           }
                 
           try{ inChannel.close(); }catch(Exception e) {}
           hasComplete=true;
            
           setRunning();
        }

        private void wait4Ring() {
            final String func=getFunc("wait4Ring()");
            while ( lrFile.ring.isFull() ) { sleep(300);}
            printf(func,3,"ring has space");
        }    
    
        boolean hasNotRead() { return (! read); }

        boolean hasNotCompleted() { return ! hasComplete; }
        
}
