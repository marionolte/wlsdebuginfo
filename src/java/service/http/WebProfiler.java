/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package service.http;

import java.util.ArrayList;

/**
 *
 * @author SuMario
 */
public class WebProfiler {

    public static synchronized boolean validate(ArrayList<Header> hea, String profile) {
         boolean b=false;
         if ( hea == null || hea.size() == 0 ) { return b; }
         do {
           Header h = hea.remove(0); h.start();
           boolean r=h.validate(); 
           if (r) {
               if ( profile != null ) {           
                if ( profile.equals("oam") ) {
                    log(3, "INFO: test webprofile oam");
                } else {
                    log(2,"INFO: base header check completed without a issue - profile not found or empty");
                }
               } else {
                    log(3,"INFO: base header check completed without a issue");
               } 
           } else {
               log(0,"ERROR: base header errors found with \n" +h.toString()); 
           }
         } while( hea.size() >0 );
       
         return b;
    }
    
    public static int debug=0;
    private static void log(final int level, String msg) {
       if ( WebProfiler.debug >= level  ) {
           if ( level > 0 ) { msg="DEBUG("+level+"/"+ WebProfiler.debug +") WebProfiler:: =>"+msg; }
           System.out.println(msg);
       } 
    }
}
