/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package service;

import java.util.HashMap;

/**
 *
 * @author SuMario
 */
public class HttpEnum {
    private static HashMap<String, String> map;
    
    static {
        map = new HashMap<String, String>();
        map.put("100", "Continue");
        map.put("101", "Switching Protocols");
        map.put("102", "Processing");
        
        map.put("200", "OK");
        map.put("201", "Created");
        map.put("202", "Accepted");
        map.put("203", "Non-Authoritative Information");
        map.put("204", "No Content");
        map.put("205", "Reset Content");
        map.put("206", "Partial Content");
        map.put("207", "Multi-Status");
        map.put("208", "Already Reported");
        map.put("226", "IM Used");
        
        map.put("300", "Multiple Choices");
        map.put("301", "Moved Permanently");
        map.put("302", "Moved Temporary");
        map.put("303", "See Other");
        map.put("304", "Not Modified");
        map.put("305", "Use Proxy"); 
        map.put("307", "Temporary Redirect"); 
        map.put("308", "Permanent Redirect");
        
        map.put("401", "Unauthorized");
        map.put("402", "Payment Required");
        map.put("403", "Forbidden");
        map.put("404", "Not Found");
        map.put("405", "Method Not Allowed");
        map.put("406", "Not Acceptable");
        map.put("407", "Proxy Authentication Required");
        map.put("408", "Request Timeout");
        map.put("409", "Conflict");
        map.put("410", "Gone");
        map.put("411", "Length Required");
        map.put("412", "Precondition Failed"); 
        map.put("413", "Payload Too Large");
        map.put("414", "URI Too Long");             //[RFC7231, Section 6.5.12]
        map.put("415", "Unsupported Media Type");   //[RFC7231, Section 6.5.13]
        map.put("416", "Range Not Satisfiable");    //[RFC7233, Section 4.4]
        map.put("417", "Expectation Failed");       //[RFC7231, Section 6.5.14]
        map.put("422", "Unprocessable Entity");     //[RFC4918]
        map.put("423", "Locked");                   //[RFC4918]
        map.put("424", "Failed Dependency");        //[RFC4918]
        map.put("426", "Upgrade Required");         //[RFC7231, Section 6.5.15]
        map.put("428", "Precondition Required");    //[RFC6585]
        map.put("429", "Too Many Requests");        //[RFC6585]
        map.put("431", "Request Header Fields Too Large");  //[RFC6585]
        //432-499Unassigned
        
        map.put("500", "Internal Server Error");    // [RFC7231, Section 6.6.1]
        map.put("501", "Not Implemented");          // [RFC7231, Section 6.6.2]
        map.put("502", "Bad Gateway");              // [RFC7231, Section 6.6.3]
        map.put("503", "Service Unavailable");      // [RFC7231, Section 6.6.4]
        map.put("504", "Gateway Timeout");          // [RFC7231, Section 6.6.5]
        map.put("505", "HTTP Version Not Supported"); //[RFC7231, Section 6.6.6]
        map.put("506", "Variant Also Negotiates");  // [RFC2295]
        map.put("507", "Insufficient Storage");     // [RFC4918]
        map.put("508", "Loop Detected");            // [RFC5842]
        map.put("509", "Unassigned");
        map.put("510", "Not Extended");             //[RFC2774]
        map.put("511", "Network Authentication Required");
    
    }
            
    static public String get(int state) {
        return map.get(""+state);
    }
}
