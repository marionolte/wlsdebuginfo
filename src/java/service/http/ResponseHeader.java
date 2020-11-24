/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.http;

import comm.Http;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author SuMario
 */
public class ResponseHeader  {
    public static int debug=0;
    private final StringBuilder body;
    private final int           code;
    private final HashMap<String, String> reqmap;
    private final HashMap<String, String> resmap;
    
    ResponseHeader(Http h) throws IOException {
        body = h.getResponse();
        code = h.getResponseCode();
        reqmap = h.getRequestHeaders();
        resmap = h.getResponseHeaders();
    }
}
