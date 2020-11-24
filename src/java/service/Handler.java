/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;

/**
 *
 * @author SuMario
 */
public class Handler {
    public int debug=0;

    public  synchronized HashMap<String,String> getPost(InputStream in ) {
         return getPost( new Reader(in) {
             @Override
             public int read(char[] cbuf, int off, int len) throws IOException { return -1; }

             @Override
             public void close() throws IOException { }
         } );    
    }
    public  synchronized HashMap<String,String> getPost(Reader r) {
        return getPost(new BufferedReader(r));
    }    
    public  synchronized HashMap<String,String> getPost(BufferedReader br ) {     
        HashMap<String,String> ret=new HashMap<String,String>();
        //BufferedReader br = new BufferedReader(new Reader (in)) ;
        String line = null; StringBuilder sw=new StringBuilder();
        try {
            while ((line = br.readLine()) != null) {
                if ( sw.length() > 0 ) { sw.append("\n"); }
                sw.append(line);
            }
        } catch(java.io.IOException io ) {}    
        if ( sw.length() > 0 ) {
            int start=0;
            int scan=sw.indexOf("&");
            String[] pair=null;
            while(start < scan ) {
                pair = getPair( sw.substring(start, scan) ); 
                ret.put(pair[0], pair[1]);
                System.out.println("key="+pair[0]+"|  val="+pair[1]+"|");
                start=scan;
                scan=sw.indexOf("&",scan);
            }
            
        }
        return (ret.isEmpty())?null:ret;
    }
    
    private  String[] getPair(String test) {
        String sp[] = test.split("=");
        if ( sp[0].length() < test.length() ) {
            return new String[] { sp[0], test.substring(sp[0].length()+1) };
        } else {
            return new String[] { test, null };
        }
    }
    
}
