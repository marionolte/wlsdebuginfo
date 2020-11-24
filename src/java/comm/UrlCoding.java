/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comm;

import general.Version;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 *
 * @author SuMario
 */
public class UrlCoding extends Version{
    
    
    synchronized public static String encode(String info) throws UnsupportedEncodingException  { return encode(info, "UTF-8"); }
    synchronized public static String encode(String info, String code) throws UnsupportedEncodingException  {
        return URLEncoder.encode(info, code);
    }
    
    synchronized public static String decode(String info) throws UnsupportedEncodingException { return decode(info, "UTF-8"); }
    synchronized public static String decode(String info, String code) throws UnsupportedEncodingException { 
        return URLDecoder.decode(info, code);
    }

}
